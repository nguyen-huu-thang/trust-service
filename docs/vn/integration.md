# Tích Hợp

[English](../en/integration.md) | **Tiếng Việt**

---

## Tổng quan

Trust Service tương tác với platform theo hai cách riêng biệt:

| Tích hợp | Chiều | Thời điểm |
|---|---|---|
| Phân phối JWT key | Trust → Identity Service | Bootstrap + refresh định kỳ |
| Cấp mTLS certificate | Trust → tất cả service | Bootstrap + cert rotation |

Trust Service **không bao giờ được gọi ở request time**. Mọi tích hợp đều là startup operation hoặc background refresh cycle.

---

## Tích hợp với Identity Service

### Lấy JWT Signing Key

Identity Service là **người dùng duy nhất** của private signing key. Nó lấy signing key một lần khi khởi động và refresh định kỳ.

```
Identity Service startup
      ↓
  GetSigningKey(signer_service_id="identity-service")
      ↓
  Trust Service trả về: private key, kid, activate_at, expires_at
      ↓
  Identity Service cache key trong bộ nhớ
      ↓
  Identity Service ký tất cả JWT bằng cached private key
```

Luồng key refresh:

```
Identity Service (background job, mỗi N phút)
      ↓
  GetSigningKey(...)
      ↓
  Trust Service trả về current + key sắp tới
      ↓
  Identity Service cập nhật key cache
```

Nếu key cache cũ và Trust Service không khả dụng, Identity Service tiếp tục ký bằng cached key cho đến khi hết hạn. Đây là thiết kế có chủ đích.

### Identity Service KHÔNG làm gì

- Không tự tạo signing key — Trust Service sở hữu key lifecycle
- Không rotate key — key rotation là trách nhiệm của Trust Service
- Không lưu trạng thái key lâu dài — nó là consumer, không phải owner

---

## Tất cả Service: JWT Verification

Mọi service cần verify JWT đều lấy public key từ Trust Service:

```
Service startup
      ↓
  GetPublicKeys(verifier_service_id="identity-service")
      ↓
  Trust Service trả về: tất cả public key chưa hết hạn (kid, PEM, activate_at, expires_at)
      ↓
  Service cache key set trong bộ nhớ
      ↓
  Request đến với JWT
      ↓
  Extract kid từ JWT header
      ↓
  Tìm key trong cache local theo kid
      ↓
  Verify signature local — KHÔNG gọi Trust Service
```

Các service **không bao giờ gọi Trust Service để verify JWT**. Verification luôn là local dùng cached public key. Điều này quan trọng cho scalability và resilience.

Public key refresh:

```
Service (background job, mỗi 15 phút)
      ↓
  GetPublicKeys(...)
      ↓
  Cập nhật local key cache
```

---

## Tất cả Service: mTLS Setup

### Bootstrap lần đầu

Khi service mới được deploy lần đầu:

```
1. Service gọi GetRootCertificate
      → Trust Service trả về Root CA cert (PEM)
      → Service pin Root CA vào trust store

2. Platform admin gọi BootstrapCert (internal API)
      → Trust Service cấp chứng chỉ X.509 đầu tiên cho service này
      → Trả về cert (PEM) + initial refresh token

3. Service load cert + private key vào TLS configuration
      → Tất cả kết nối outbound và inbound giờ dùng mTLS
```

### Cert Rotation

Service rotate cert trước khi hết hạn (~100 ngày sau khi cấp):

```
Service phát hiện cert gần đến rotation window
      ↓
Service tạo cặp khóa mới (trong bộ nhớ, không gửi lên Trust Service)
      ↓
Service gọi RotateCertificate
  → token_id + refresh_token (one-time)
  → new public key (PEM)
  → qua kết nối mTLS hiện tại (chứng minh đang nắm cert hiện tại)
      ↓
Trust Service xác thực token + cấp cert mới
      ↓
Service load cert + cặp khóa mới vào TLS configuration
      ↓
Service lưu refresh token mới cho lần rotation tiếp theo
```

### Xác thực kết nối

Trên mỗi kết nối mTLS, service nhận thực hiện xác thực danh tính hai lớp:

```
Kết nối mTLS đến
      ↓
1. TLS layer:    xác thực cert chain với Root CA đã pin
2. App layer:    extract service_id từ cert SAN
3. App layer:    so sánh cert.service_id == request.metadata.service_id
```

Xác thực hai lớp (cert + request metadata) cung cấp defense against misconfigured proxy và tăng observability — request mang explicit caller identity ngay cả sau khi TLS kết thúc tại proxy.

---

## Không có Runtime Dependency

```
Trust Service
  ↓ cung cấp key và cert (chỉ tại thời điểm setup)

Tất cả service dùng:
  - cached public key  → verify JWT mà không cần Trust Service
  - cert hiện có       → mTLS mà không cần Trust Service
```

Trust Service có thể bị tắt để maintenance, upgrade hoặc rotation operation mà không ảnh hưởng đến user-facing traffic. Platform tiếp tục hoạt động dựa trên cached cryptographic material.

Trust Service downtime chỉ ngăn:
- Tạo signing key mới
- Cấp service certificate mới
- Rotate certificate hiện có

---

## Tóm tắt tích hợp

```
Trust Service
      │
      ├── Identity Service
      │     ├── GetSigningKey (startup + định kỳ)
      │     └── GetPublicKeys (startup + định kỳ)
      │
      ├── Tất cả service (mỗi service)
      │     ├── GetRootCertificate (một lần lúc startup)
      │     ├── BootstrapCert qua admin (một lần lúc deploy)
      │     ├── GetPublicKeys (startup + định kỳ)
      │     └── RotateCertificate (mỗi ~100 ngày)
      │
      └── Platform admin
            ├── Đăng ký service
            ├── Quản lý key policy
            └── Bootstrap cert đầu tiên
```
