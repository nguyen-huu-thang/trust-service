# Certificate & mTLS

[English](../en/certificate-mtls.md) | **Tiếng Việt**

---

## Mục đích

Service Certificate System quản lý chứng chỉ X.509 cho phép mutual TLS (mTLS) giữa tất cả service trong Xime Base Platform. Trust Service vận hành như Certificate Authority (CA) nội bộ của platform.

```
Trust Service (CA)
   ↓ cấp chứng chỉ X.509 leaf cho từng service
   ↓ cert chứa SAN = service_id
   ↓
Service A ──mTLS──→ Service B
                        ↓
               xác thực cert chain với Root CA
                        ↓
               extract service_id từ SAN
                        ↓
               so sánh service_id với request metadata
```

---

## Cấu trúc chứng chỉ X.509

```
Certificate
  tbsCertificate     ← toàn bộ dữ liệu cần ký
    issuer           ← Trust Service CA
    subject          ← service identity
    serial number
    validity         ← notBefore / notAfter
    extensions
      SAN            ← service_id (định dạng URI)
      KeyUsage       ← digitalSignature, keyEncipherment (RSA)
      ExtendedKeyUsage ← clientAuth, serverAuth
      BasicConstraints ← CA=false (leaf certificate)
  signatureAlgorithm
  signatureValue     ← SIGN(DER(tbsCertificate))
```

**Quan trọng**: Signature là `SIGN(DER(tbsCertificate))` — toàn bộ cấu trúc TBS được ký dưới dạng DER bytes, không phải pattern header+body+signature thông thường.

---

## CA Abstraction: CertificateAuthoritySigner

Application layer không bao giờ trực tiếp đụng đến CA private key. Toàn bộ việc ký đi qua một abstraction duy nhất:

```
Application / Domain layer
   → chỉ biết: "có một cơ chế ký bytes"
   → gọi: CertificateAuthoritySigner.sign(byte[])
   → nhận lại: signature bytes

Infrastructure layer
   → quyết định: load key từ file? gọi HSM? gọi KMS?
```

Thiết kế này cho phép cùng một use case code hoạt động ở DEV (key từ file) và PROD (HSM/KMS) mà không cần sửa.

| Môi trường | Implementation | Vị trí CA key |
|---|---|---|
| DEV | `FileSystemCaSigner` | `dev-keys/` (file local) |
| PROD | `HsmCaSigner` | HSM/KMS (key không rời thiết bị) |

Implementation được chọn qua `CaSignerConfig` dùng Spring profile.

---

## Luồng cấp chứng chỉ

```
Bước 1 — Build TBSCertificate
  issuer:     Trust Service CA DN
  subject:    service CN
  serial:     random BigInteger
  validity:   notBefore = now, notAfter = now + ~1 năm
  extensions: SAN (service_id), KeyUsage, ExtendedKeyUsage, BasicConstraints

Bước 2 — Sign
  input:  DER(tbsCertificate)
  output: signature bytes
  qua:    CertificateAuthoritySigner (file hoặc HSM)

Bước 3 — Assemble
  ghép: tbsCertificate + signatureAlgorithm + signatureValue
  output: X509Certificate hoàn chỉnh (định dạng PEM)
```

Service tự tạo cặp khóa của mình trước khi bootstrap. Chỉ public key và thông tin identity được gửi lên Trust Service.

---

## Lifecycle chứng chỉ

| Sự kiện | Thời điểm |
|---|---|
| Trigger rotation | ~100 ngày sau khi cấp |
| Hết hạn (lifetime) | ~1 năm (notAfter) |
| Hard delete | 5 năm sau khi hết hạn (giữ audit trail) |

`CertificateLifecycleJob` chạy mỗi giờ và phát hiện cert gần đến ngưỡng rotation. Khi phát hiện, nó đánh dấu để rotate. Service sau đó phải gọi `RotateCertificate` để nhận cert mới.

---

## Luồng Cert Rotation

Cert rotation yêu cầu hai bằng chứng quyền sở hữu:

```
Service (kết nối mTLS hiện tại)
   → gửi: refresh_token_id + refresh_token (one-time) + new public key
   → Trust Service xác thực:
       token tồn tại trong database
       token.used_at IS NULL           ← chưa sử dụng
       token.expires_at > now          ← chưa hết hạn
       token.bound_kid == current cert ← gắn với cert hiện tại
   → Trust Service cấp cert mới + refresh token mới
   → Trust Service đánh dấu token cũ là đã dùng
```

**Refresh token one-time** ngăn replay attack. **mTLS binding** đảm bảo chỉ người nắm cert hiện tại mới có thể rotate.

---

## Xác thực danh tính mTLS

Khi Service B nhận request từ Service A:

```
Service A ──mTLS──→ Service B
                        ↓
1. TLS layer:    xác thực cert chain với Root CA đã pin
2. App layer:    extract service_id từ cert SAN
3. App layer:    so sánh cert.service_id == request.service_id (metadata)
4. App layer:    so sánh cert.shard_id == request.shard_id (nếu có)
```

Xác thực hai lớp (cert + payload) chống spoofing và tăng observability — request mang explicit identity ngay cả sau khi TLS kết thúc tại proxy.

---

## Bootstrap Trust Store

Khi service mới khởi động lần đầu:

```
1. Service gọi GetRootCertificate (unauthenticated, plaintext)
2. Service pin Root CA certificate vào trust store
3. Admin gọi BootstrapCert (internal API) để cấp cert đầu tiên
4. Service nhận cert + initial refresh token
5. Service bắt đầu chấp nhận và khởi tạo kết nối mTLS
```

Sau bootstrap, cert rotation tự chủ (service tự khởi xướng qua refresh token).

---

## Nguyên tắc bảo mật

- CA private key không bao giờ bị expose ra ngoài `CertificateAuthoritySigner`
- Key material không bao giờ được serialize hay log
- Leaf cert được nhận dạng qua SAN, không phải CN
- Refresh token là one-time và gắn với cert hiện tại
- CA signing key và service TLS key hoàn toàn độc lập nhau
