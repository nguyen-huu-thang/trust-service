# Trust Service

[English](README.md) | **Tiếng Việt**

> Hạ tầng mã hóa và cơ quan tin cậy cho Xime Base Platform — quản lý JWT signing key, chứng chỉ X.509 cho mTLS và định danh service.

---

Trust Service là **tầng hạ tầng bảo mật** của Xime Base Platform. Nó đóng vai trò Root of Trust nội bộ — cung cấp khóa mã hóa cho JWT authentication và chứng chỉ X.509 cho mutual TLS giữa các service.

```
Application Services (mạng xã hội, thương mại điện tử, SaaS, AI)
               ↓ verify JWT local bằng public key đã cache
          Identity Service ← lấy signing key từ Trust Service
               ↓
          Trust Service   ← CA, JWT key lifecycle, mTLS cert lifecycle
               ↓
          PostgreSQL
```

---

## Trust Service làm gì

Trust Service quản lý hai hệ thống bảo mật độc lập:

**JWT Key System**
- Generate và quản lý lifecycle của cặp khóa bất đối xứng (RSA/EC)
- Phân phối public key đến các service để verify JWT local
- Rotation theo thời gian — không cần event trigger
- Backward compatibility: key cũ vẫn verify được token cũ cho đến khi hết hạn

**Service Certificate System (mTLS)**
- Vận hành như Certificate Authority (CA) nội bộ của platform
- Cấp chứng chỉ X.509 leaf cho các service (SAN = service_id)
- Quản lý lifecycle chứng chỉ: cấp, rotate và dọn dẹp
- Cho phép mutual TLS (mTLS) giữa tất cả service trong Base Platform

## Trust Service KHÔNG làm gì

- Không authenticate user
- Không tham gia vào runtime request handling
- Không implement business logic
- Không revoke JWT ngay lập tức (dùng short TTL thay thế)

Trust Service là **control-plane** — không bao giờ nằm trong hot path của request.

---

## Thiết kế Resilience

Nếu Trust Service tạm thời không khả dụng:

```
JWT verification  → vẫn hoạt động (service dùng cached public key)
mTLS connections  → vẫn hoạt động (service dùng cert đã có)
JWT signing       → vẫn hoạt động (Identity Service dùng cached signing key)
```

Trust Service chỉ cần thiết khi rotate key, cấp cert mới hoặc bootstrap service mới.

---

## Quyết định thiết kế quan trọng

### Key Lifecycle theo thời gian

Key không có trạng thái CURRENT/NEXT/OLD. Thời gian quyết định hành vi:

```
current key = max(activate_at ≤ now)
next key    = min(activate_at > now)
```

### Append-Only Key Chain

Rotation key nghĩa là mở rộng timeline, không thay thế key. Bất biến cần đảm bảo:
- `new.activate_at` phải sau `last.activate_at` (strictly)
- Không có hai key cùng `activate_at` trong một cặp (signer, verifier)
- Rotation interval ≥ minimum theo policy

### CA Abstraction

```
DEV  → load private key từ dev-keys/ (file system)
PROD → ủy quyền ký cho HSM/KMS (key không rời thiết bị)
```

Application code chỉ thấy `CertificateAuthoritySigner` — không có logic phụ thuộc môi trường trong use case.

---

## Chạy nhanh

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

HTTP: `8081` | gRPC: `9090`

---

## Kiến trúc

Trust Service theo **Hexagonal Architecture** với DDD tactical patterns, xây dựng trên Spring Boot 4 / Java 25:

```
src/main/java/
├── api/              ← Input adapters (gRPC external/internal, REST)
├── application/      ← Use case, service, port, DTO, mapper
├── domain/           ← Model, factory, repository (interface), domain service
└── infrastructure/   ← JPA persistence, crypto, scheduler, security
```

Domain service là Plain Java (không có Spring annotation). Repository interface nằm ở `domain/repository/` và được implement ở `infrastructure/persistence/`.

---

## Tài liệu

| Tài liệu | Mô tả |
|---|---|
| [Tổng quan](docs/vn/overview.md) | Vai trò, hai hệ thống, vị trí trong Base Platform |
| [Kiến trúc](docs/vn/architecture.md) | Cấu trúc tầng, DDD pattern, cây thư mục |
| [Key System](docs/vn/key-system.md) | JWT key lifecycle, rotation policy, scheduler |
| [Certificate & mTLS](docs/vn/certificate-mtls.md) | Thiết kế X.509, CA abstraction, cert rotation |
| [API Reference](docs/vn/api.md) | gRPC proto definition, quy tắc sử dụng |
| [Tích hợp](docs/vn/integration.md) | Identity Service và các service khác tích hợp Trust Service như thế nào |

---

## Các Service trong Base Platform

| Service | Vai trò |
|---|---|
| `trust-service` | **Trust infrastructure — CA, mTLS, JWT signing key** |
| `identity-service` | Authentication infrastructure — JWT, refresh token |
| `user-service` | Human Identity Domain Service |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Gửi thông báo |
| `payment-service` | Thanh toán |

---

## Trạng thái dự án

Trust Service đang trong **giai đoạn phát triển tích cực**. JWT key system và certificate/mTLS system đã được implement. Các scheduled job cho key continuity và certificate lifecycle đang hoạt động.

---

## Giấy phép

MIT
