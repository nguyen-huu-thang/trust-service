# Key System

[English](../en/key-system.md) | **Tiếng Việt**

---

## Mục đích

JWT Key System quản lý toàn bộ lifecycle của các cặp khóa bất đối xứng dùng để ký và verify JSON Web Token trên Xime Base Platform.

```
Trust Service
   ↓ tạo cặp khóa RSA/EC
   ↓ mã hóa private key at rest (AES)
   ↓ lưu vào PostgreSQL
   ↓
Identity Service ← lấy signing key (private) qua gRPC
   ↓ ký JWT access token
   ↓
Các service khác ← lấy public key qua gRPC, cache local
   ↓ verify JWT mà không cần gọi Trust Service ở runtime
```

---

## Thiết kế cốt lõi: Lifecycle theo thời gian

Key **không có trường status CURRENT/NEXT/OLD**. Thời gian quyết định key nào đang active:

```
current key = key có max(activate_at) mà activate_at ≤ now
next key    = key có min(activate_at) mà activate_at > now
```

Thiết kế này loại bỏ race condition từ việc update status và đơn giản hóa query.

---

## Bất biến của Key Chain

Mỗi key thuộc về một cặp **(signer_service_id, verifier_service_id)**. Chain của mỗi cặp phải đảm bảo 4 bất biến mọi lúc:

| Bất biến | Quy tắc |
|---|---|
| **Append-only** | `new.activate_at` phải strictly sau `last.activate_at` |
| **Không trùng** | Không hai key nào trong cùng cặp có cùng `activate_at` |
| **Rotation interval** | `expires_at - activate_at ≥ policy.rotation_interval_seconds` |
| **Không kích hoạt quá khứ** | `activate_at` không được ở quá khứ (dung sai clock skew 5 giây) |

Các bất biến này được enforce trong `KeyValidationDomainService` và trong `KeyFactory` trước khi bất kỳ key nào được lưu.

---

## Luồng Generate Key

```
GenerateKeyUseCase
  ├── KeyPolicyDomainService.calculateActivateAt()    ← đặt sau key cuối cùng
  ├── KeyPolicyDomainService.calculateExpiresAt()     ← áp dụng rotation interval
  ├── KeyValidationDomainService.validateNewKey()     ← enforce 4 bất biến
  ├── KeyGenerator.generate(algorithm, keySize)       ← infrastructure
  ├── KeyEncryptionService.encrypt(privateKey)        ← mã hóa AES at rest
  ├── KeyFactory.create(...)                          ← domain aggregate
  └── KeyRepository.save(key)
```

---

## Key Rotation: Mở rộng Timeline

Rotation **không** thay thế key hiện có. Nó thêm key mới vào cuối timeline:

```
Trước rotation:
  Key A  [activate: T+0, expires: T+30d]   ← current
  Key B  [activate: T+25d, expires: T+55d] ← next (đã tạo trước)

Sau rotation (thêm key mới):
  Key A  [activate: T+0, expires: T+30d]
  Key B  [activate: T+25d, expires: T+55d] ← sẽ trở thành current
  Key C  [activate: T+50d, expires: T+80d] ← next key mới
```

Key cũ không bao giờ bị xóa khi vẫn còn token có thể còn hợp lệ. Các service giữ tất cả public key chưa hết hạn và verify theo `kid` (key ID trong JWT header).

---

## Preload Window

Scheduler kiểm tra xem "next key" có tồn tại trong preload window không:

```
preload window = [now + rotation_interval - preload_margin, now + rotation_interval + buffer]
```

Hai trường hợp trigger việc tạo trước key:

**Trường hợp 1 — Không có key tương lai**
```
Timeline: [Key A] ← current, không có gì sau đó
Action:   Tạo Key B bắt đầu sau khi Key A hết hạn
```

**Trường hợp 2 — Next key tồn tại nhưng quá gần current**
```
Timeline: [Key A (current)] [Key B (sắp active, không có buffer)]
Action:   Tạo Key C sau Key B với interval phù hợp
```

---

## Key Policy

Mỗi cặp **(signer, verifier)** có một `KeyPolicy` đi kèm:

| Trường | Mô tả |
|---|---|
| `rotation_interval_seconds` | Thời gian sống tối thiểu của mỗi key |
| `preload_margin_seconds` | Bao xa trước khi tạo next key |
| `algorithm` | `RSA` hoặc `EC` |
| `key_size` | 2048 / 4096 (RSA) hoặc 256 / 384 (EC) |

---

## Bảo mật Private Key

Private key không bao giờ được lưu dạng plaintext:

```
KeyGenerator.generate()
   → RSA/EC PrivateKey thô (chỉ trong bộ nhớ)
   → KeyEncryptionService.encrypt(privateKey)  ← AES-256, key từ application.yml
   → lưu bytes đã mã hóa vào PostgreSQL
```

AES encryption key được load từ `key.encryption.secret` trong `application.yml` (Base64-encoded, quản lý bên ngoài trên production).

---

## Phân phối Key

| Người nhận | API | Dữ liệu nhận |
|---|---|---|
| Identity Service | `GetSigningKey` | Private key (encrypted transport), activate_at, expires_at |
| Tất cả service | `GetPublicKeys` | Public key (PEM), kid, activate_at, expires_at |

`GetPublicKeys` trả về **tất cả public key chưa hết hạn** của một verifier. Các service cache toàn bộ và refresh định kỳ — không phải mỗi request.

---

## Key Cleanup

`KeyCleanupJob` hard-delete key đã qua retention period:

```
xóa key khi: expires_at < now - retention_period
```

Retention period đủ dài để không còn JWT nào được ký bởi key đó còn trong lưu hành. Key chỉ bị xóa khi chắc chắn không có token hợp lệ nào phụ thuộc vào nó.
