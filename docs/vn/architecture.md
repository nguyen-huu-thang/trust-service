# Kiến Trúc

[English](../en/architecture.md) | **Tiếng Việt**

---

## Tổng quan tầng

Trust Service theo **Hexagonal Architecture** (Ports and Adapters) với DDD tactical pattern, xây dựng trên Spring Boot 4 / Java 25.

```
External Clients (gRPC, REST)
        ↓
   Adapter Layer (api/)          ← nhận request, ánh xạ sang use case input
        ↓
 Application Layer (application/) ← use case, application service, orchestration
        ↓
   Domain Layer (domain/)        ← domain model thuần túy, không phụ thuộc framework
        ↑
Infrastructure Layer (infrastructure/) ← JPA, crypto, scheduler, security
```

Domain layer không biết gì về Spring, JPA hay gRPC. Infrastructure layer không biết gì về use case logic. Dependency luôn hướng vào trong, về phía domain.

---

## Cây thư mục

```
src/main/java/com/xime/trust/
│
├── api/                                   ← adapter layer
│   ├── grpc/
│   │   ├── external/                      ← gRPC handler cho external client
│   │   │   ├── KeyDistributionGrpcService
│   │   │   ├── CertificateGrpcService
│   │   │   ├── TrustGrpcService
│   │   │   └── HealthGrpcService
│   │   ├── internal/                      ← gRPC handler cho admin operation
│   │   │   ├── KeyAdminGrpcService
│   │   │   ├── CertAdminGrpcService
│   │   │   └── ServiceAdminGrpcService
│   │   └── mapper/                        ← DTO ↔ Protobuf mapper (MapStruct)
│   └── rest/
│
├── application/
│   ├── usecase/                           ← orchestrate: validate → domain → repo → return
│   │   ├── key/
│   │   │   ├── GenerateKeyUseCase
│   │   │   └── GetSigningKeyUseCase
│   │   ├── cert/
│   │   │   ├── BootstrapCertUseCase
│   │   │   └── RotateCertificateUseCase
│   │   └── service/
│   │       └── RegisterServiceUseCase
│   ├── service/                           ← application service (Spring bean)
│   ├── port/                              ← inbound / outbound port interface
│   │   └── out/
│   │       └── CertificateAuthoritySigner ← điểm abstraction quan trọng
│   ├── dto/                               ← input/output DTO
│   └── mapper/                            ← Domain ↔ DTO mapper (MapStruct)
│
├── domain/
│   ├── model/                             ← aggregate root, value object
│   │   ├── Key
│   │   ├── Certificate
│   │   ├── ServiceRegistration
│   │   └── KeyPolicy
│   ├── factory/                           ← enforce invariant tại thời điểm tạo
│   │   ├── KeyFactory
│   │   └── CertificateFactory
│   ├── repository/                        ← collection abstraction (interface)
│   │   ├── KeyRepository
│   │   ├── CertificateRepository
│   │   ├── KeyPolicyRepository
│   │   └── CertRefreshTokenRepository
│   └── service/                           ← plain Java domain service (không có Spring)
│       ├── KeyValidationDomainService
│       ├── KeyPolicyDomainService
│       └── CertificateLifecycleDomainService
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/                        ← JPA entity (đặt tên *Entity)
    │   ├── mapper/                        ← Entity ↔ Domain mapper (MapStruct)
    │   └── repository/                    ← implement domain repository interface
    │       └── *RepositoryImpl
    ├── crypto/
    │   ├── KeyGenerator                   ← tạo cặp khóa RSA/EC
    │   ├── KeyEncryptionService           ← mã hóa AES private key at rest
    │   └── x509/
    │       ├── X509CertificateBuilder
    │       ├── X509CertificateAssembler
    │       └── X509ExtensionsFactory
    ├── security/
    │   ├── FileSystemCaSigner             ← DEV: load key từ dev-keys/
    │   └── HsmCaSigner                    ← PROD: ủy quyền cho HSM/KMS
    ├── scheduler/
    │   ├── KeyContinuityJob               ← 1h: đảm bảo timeline key không thiếu
    │   ├── KeyCleanupJob                  ← 6h: hard-delete key hết hạn
    │   ├── CertificateLifecycleJob        ← 1h: phát hiện và trigger rotate cert
    │   └── CertificateCleanupJob          ← 6h: hard-delete cert hết retention
    └── config/
        └── CaSignerConfig                 ← chọn FileSystem hay HSM theo profile
```

---

## DDD Tactical Pattern

### Domain Service là Plain Java

Domain service trong `domain/service/` là **POJO thuần túy** — không có `@Service`, `@Component` hay bất kỳ annotation Spring nào.

```java
public class KeyValidationDomainService {
    public void validateNewKey(Key newKey, List<Key> existingKeys) {
        // validation logic thuần túy — không có Spring, không có DB
    }
}
```

Chúng được khởi tạo thủ công trong `@Configuration` class hoặc dùng `new` trực tiếp khi cần.

### Invariant được enforce tại thời điểm tạo

Domain invariant được enforce trong constructor và factory method, không phải trong use case:

```java
// KeyFactory enforce append-only chain ngay khi tạo
public Key create(SignerId signer, VerifierId verifier, Instant activateAt, ...) {
    validation.validateNewKey(activateAt, existingKeys);  // ném exception nếu vi phạm
    return new Key(...);
}
```

### Repository interface đặt trong Domain

Repository interface nằm ở `domain/repository/` — không phải `application/port/out/`. Điều này theo quy ước DDD tactical, coi repository là collection abstraction thuộc về domain aggregate.

```
domain/repository/KeyRepository           ← interface
infrastructure/persistence/repository/
    KeyRepositoryImpl                     ← JPA implementation
    JpaKeyRepository                      ← Spring Data JPA
```

---

## Quy tắc đặt tên

| Loại | Pattern | Ví dụ |
|---|---|---|
| JPA Entity | `*Entity` | `KeyEntity` |
| Repository impl | `*RepositoryImpl` | `KeyRepositoryImpl` |
| JPA Repository | `Jpa*Repository` | `JpaKeyRepository` |
| Use Case | `*UseCase` | `GenerateKeyUseCase` |
| Domain Service | `*DomainService` | `KeyValidationDomainService` |
| gRPC Service | `*GrpcService` | `KeyAdminGrpcService` |

---

## Tầng Mapper

Toàn bộ mapping được thực hiện bằng **MapStruct** annotation processor — không viết mapper thủ công:

```
Entity ↔ Domain model     (infrastructure/persistence/mapper)
Domain model ↔ DTO        (application/mapper)
DTO ↔ Protobuf            (api/grpc/mapper)
```

---

## Luồng Use Case

```
HTTP/gRPC Request
      ↓
  gRPC Handler      → ánh xạ proto message sang input DTO
      ↓
  Use Case          → validate → gọi domain service → gọi factory → lưu → trả output DTO
      ↓
  Domain Service    → validation thuần túy (không có I/O)
      ↓
  Factory           → enforce invariant, tạo aggregate
      ↓
  Repository        → được resolve sang JPA implementation qua Spring DI
      ↓
  PostgreSQL
```

---

## Scheduled Job

| Job | Interval | Chức năng |
|---|---|---|
| `KeyContinuityJob` | 1h | Đảm bảo key timeline không bao giờ bị trống — tạo trước key tiếp theo |
| `KeyCleanupJob` | 6h | Hard-delete key đã qua retention period |
| `CertificateLifecycleJob` | 1h | Phát hiện cert gần đến ngưỡng rotation (~100 ngày) |
| `CertificateCleanupJob` | 6h | Hard-delete cert đã qua retention 5 năm |
