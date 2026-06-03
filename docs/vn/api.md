# API Reference

[English](../en/api.md) | **Tiếng Việt**

---

## Tổng quan

Trust Service cung cấp hai nhóm gRPC API:

| Nhóm | Mục đích |
|---|---|
| **External** | Service-to-service runtime: phân phối key, rotate cert, bootstrap trust |
| **Internal** | Admin operation: đăng ký service, quản lý key, cert, policy, shard |

Proto file nằm tại `src/main/proto/`:
- `external/` — public client API
- `internal/` — admin service definition

---

## External API

### KeyDistributionService

```protobuf
service KeyDistributionService {
  // Lấy public key để VERIFY JWT (cache lại, verify theo kid)
  rpc GetPublicKeys(GetPublicKeysRequest) returns (GetPublicKeysResponse);

  // Lấy signing key để SIGN JWT (chỉ dành cho Identity Service)
  rpc GetSigningKey(GetSigningKeyRequest) returns (GetSigningKeyResponse);
}

message GetPublicKeysRequest {
  string verifier_service_id = 1;
}

message GetPublicKeysResponse {
  repeated PublicKeyDto keys = 1;   // tất cả public key chưa hết hạn
}

message GetSigningKeyRequest {
  string signer_service_id = 1;
}

message GetSigningKeyResponse {
  repeated SigningKeyDto keys = 1;  // current + key sắp tới
}

message PublicKeyDto {
  string id = 1;                    // KSUID (dùng làm kid trong JWT header)
  string verifier_service_id = 2;
  string algorithm = 3;             // RSA hoặc EC
  int32  key_size = 4;
  string public_key = 5;            // định dạng PEM
  int64  activate_at = 6;           // Unix timestamp (ms)
  int64  expires_at = 7;
}

message SigningKeyDto {
  string id = 1;
  string signer_service_id = 2;
  string verifier_service_id = 3;
  string algorithm = 4;
  int32  key_size = 5;
  string private_key = 6;           // chỉ trả cho Identity Service — encrypted trong transit
  int64  activate_at = 7;
  int64  expires_at = 8;
}
```

### CertificateService

```protobuf
service CertificateService {
  // Rotate chứng chỉ (dùng khi cert gần đến ngưỡng rotation)
  rpc RotateCertificate(RotateCertificateRequest) returns (RotateCertificateResponse);
}

message RotateCertificateRequest {
  string token_id = 1;       // ID của refresh token one-time
  string refresh_token = 2;  // giá trị token one-time
  string new_public_key = 3; // PEM — cặp khóa mới do service tự tạo
  string service_id = 4;
}

message RotateCertificateResponse {
  CertificateDto certificate = 1;
  string next_refresh_token = 2;    // token cho lần rotate tiếp theo
  string next_refresh_token_id = 3;
  int64  issued_at = 4;
  int64  expires_at = 5;
}

message CertificateDto {
  string id = 1;
  string service_id = 2;
  string public_cert = 3;   // định dạng PEM — chứng chỉ X.509 đầy đủ
}
```

### TrustService

```protobuf
service TrustService {
  // Lấy Root CA certificate (bootstrap trust store, verify mTLS cert)
  rpc GetRootCertificate(GetRootCertificateRequest) returns (GetRootCertificateResponse);
}

message GetRootCertificateRequest {}

message GetRootCertificateResponse {
  string root_cert = 1;     // định dạng PEM
  string fingerprint = 2;   // SHA-256 fingerprint (optional, để pin)
  int64  expires_at = 3;
}
```

### HealthService

```protobuf
service HealthService {
  rpc CheckLiveness(CheckLivenessRequest) returns (CheckLivenessResponse);
  rpc CheckReadiness(CheckReadinessRequest) returns (CheckReadinessResponse);
  rpc GetServiceInfo(GetServiceInfoRequest) returns (GetServiceInfoResponse);
}
```

---

## Internal API (Admin)

Internal API được dùng bởi platform operator và automated tooling để quản lý trạng thái Trust Service. Không được gọi ở runtime bởi các service khác.

```
KeyAdminService       ← tạo / kiểm tra key thủ công
CertAdminService      ← bootstrap cert đầu tiên cho service mới
ServiceAdminService   ← đăng ký / hủy đăng ký service ID
PolicyAdminService    ← tạo / cập nhật key policy
ShardAdminService     ← đăng ký shard
```

Chỉ truy cập được từ internal network. Yêu cầu mTLS + admin role.

---

## Quy tắc sử dụng API

### JWT Key Distribution

- Service phải **cache** public key local — không gọi `GetPublicKeys` mỗi request
- `GetPublicKeys` trả về tất cả public key chưa hết hạn — cache tất cả để đảm bảo backward compatibility
- `GetSigningKey` chỉ dành cho Identity Service — service khác không được gọi
- Refresh public key theo lịch (ví dụ: mỗi 15 phút), không phải khi cache miss từng request

### Quản lý Certificate

```
Bootstrap service mới:
  1. Admin gọi CertAdminService.BootstrapCert (internal API)
  2. Service nhận cert + initial refresh token

Cert rotation (do service tự khởi xướng):
  1. Service phát hiện cert gần đến ngưỡng (hoặc được lifecycle check thông báo)
  2. Service gọi RotateCertificate với mTLS hiện tại + refresh token
  3. Service nhận cert mới + refresh token mới
  4. Service cập nhật TLS configuration

Bootstrap trust store (lần đầu khởi động):
  1. Service gọi GetRootCertificate (unauthenticated)
  2. Service pin Root CA vào trust store
```

### Khi nào gọi Trust Service

| Tình huống | Có nên gọi không? |
|---|---|
| Verify JWT | Không — dùng cached public key |
| Ký JWT | Không (chỉ Identity Service, và chỉ để refresh key) |
| Service-to-service request | Không — mTLS dùng cert đã có |
| Lần đầu khởi động (trust store) | Có — gọi `GetRootCertificate` |
| Cert gần đến ngưỡng rotation | Có — gọi `RotateCertificate` |
| Periodic key refresh | Có — gọi `GetPublicKeys` theo lịch |
| **Request path thông thường** | **Không bao giờ** |
