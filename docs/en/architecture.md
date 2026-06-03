# Architecture

**English** | [Tiếng Việt](../vn/architecture.md)

---

## Layered Overview

Trust Service follows **Hexagonal Architecture** (Ports and Adapters) with DDD tactical patterns, built on Spring Boot 4 / Java 25.

```
External Clients (gRPC, REST)
        ↓
   Adapter Layer (api/)          ← receives requests, maps to use case inputs
        ↓
 Application Layer (application/) ← use cases, application services, orchestration
        ↓
   Domain Layer (domain/)        ← pure business model, no framework dependencies
        ↑
Infrastructure Layer (infrastructure/) ← JPA, crypto, scheduler, security
```

The domain layer has no knowledge of Spring, JPA, or gRPC. The infrastructure layer has no knowledge of use case logic. Dependencies always point inward toward the domain.

---

## Directory Structure

```
src/main/java/com/xime/trust/
│
├── api/                                   ← adapter layer
│   ├── grpc/
│   │   ├── external/                      ← gRPC handlers for external clients
│   │   │   ├── KeyDistributionGrpcService
│   │   │   ├── CertificateGrpcService
│   │   │   ├── TrustGrpcService
│   │   │   └── HealthGrpcService
│   │   ├── internal/                      ← gRPC handlers for admin operations
│   │   │   ├── KeyAdminGrpcService
│   │   │   ├── CertAdminGrpcService
│   │   │   └── ServiceAdminGrpcService
│   │   └── mapper/                        ← DTO ↔ Protobuf mappers (MapStruct)
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
│   ├── service/                           ← application services (Spring beans)
│   ├── port/                              ← inbound / outbound port interfaces
│   │   └── out/
│   │       └── CertificateAuthoritySigner ← key abstraction point
│   ├── dto/                               ← input/output DTOs
│   └── mapper/                            ← Domain ↔ DTO mappers (MapStruct)
│
├── domain/
│   ├── model/                             ← aggregate roots, value objects
│   │   ├── Key
│   │   ├── Certificate
│   │   ├── ServiceRegistration
│   │   └── KeyPolicy
│   ├── factory/                           ← enforce invariants at creation time
│   │   ├── KeyFactory
│   │   └── CertificateFactory
│   ├── repository/                        ← collection abstractions (interfaces)
│   │   ├── KeyRepository
│   │   ├── CertificateRepository
│   │   ├── KeyPolicyRepository
│   │   └── CertRefreshTokenRepository
│   └── service/                           ← plain Java domain services (no Spring)
│       ├── KeyValidationDomainService
│       ├── KeyPolicyDomainService
│       └── CertificateLifecycleDomainService
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/                        ← JPA entities (*Entity naming)
    │   ├── mapper/                        ← Entity ↔ Domain mappers (MapStruct)
    │   └── repository/                    ← implements domain repository interfaces
    │       └── *RepositoryImpl
    ├── crypto/
    │   ├── KeyGenerator                   ← RSA/EC key pair generation
    │   ├── KeyEncryptionService           ← AES encrypt private key at rest
    │   └── x509/
    │       ├── X509CertificateBuilder
    │       ├── X509CertificateAssembler
    │       └── X509ExtensionsFactory
    ├── security/
    │   ├── FileSystemCaSigner             ← DEV: load key from dev-keys/
    │   └── HsmCaSigner                    ← PROD: delegate to HSM/KMS
    ├── scheduler/
    │   ├── KeyContinuityJob               ← 1h: ensure key timeline is never empty
    │   ├── KeyCleanupJob                  ← 6h: hard-delete expired keys
    │   ├── CertificateLifecycleJob        ← 1h: detect and rotate expiring certs
    │   └── CertificateCleanupJob          ← 6h: hard-delete certs past retention
    └── config/
        └── CaSignerConfig                 ← choose FileSystem vs HSM by profile
```

---

## DDD Tactical Patterns

### Domain Services are Plain Java

Domain services in `domain/service/` are **pure POJOs** — no `@Service`, no `@Component`, no Spring dependencies.

```java
public class KeyValidationDomainService {
    public void validateNewKey(Key newKey, List<Key> existingKeys) {
        // pure validation logic — no Spring, no DB
    }
}
```

They are instantiated manually in a `@Configuration` class or via `new` when needed.

### Invariants Enforced at Creation

Domain invariants are enforced inside constructors and factory methods, not in use cases:

```java
// KeyFactory enforces append-only chain at creation time
public Key create(SignerId signer, VerifierId verifier, Instant activateAt, ...) {
    validation.validateNewKey(activateAt, existingKeys);  // throws if violated
    return new Key(...);
}
```

### Repository Interfaces in Domain

Repository interfaces live in `domain/repository/` — not in `application/port/out/`. This follows DDD tactical convention where repository is a collection abstraction owned by the domain aggregate.

```
domain/repository/KeyRepository           ← interface
infrastructure/persistence/repository/
    KeyRepositoryImpl                     ← JPA implementation
    JpaKeyRepository                      ← Spring Data JPA
```

---

## Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| JPA Entity | `*Entity` | `KeyEntity` |
| Repository impl | `*RepositoryImpl` | `KeyRepositoryImpl` |
| JPA Repository | `Jpa*Repository` | `JpaKeyRepository` |
| Use Case | `*UseCase` | `GenerateKeyUseCase` |
| Domain Service | `*DomainService` | `KeyValidationDomainService` |
| gRPC Service | `*GrpcService` | `KeyAdminGrpcService` |

---

## Mapper Layer

All mapping is done with **MapStruct** annotation processor — no manual mappers:

```
Entity ↔ Domain model     (infrastructure/persistence/mapper)
Domain model ↔ DTO        (application/mapper)
DTO ↔ Protobuf            (api/grpc/mapper)
```

---

## Use Case Flow

```
HTTP/gRPC Request
      ↓
  gRPC Handler      → maps proto message to input DTO
      ↓
  Use Case          → validate → call domain service → call factory → save → return output DTO
      ↓
  Domain Service    → pure validation (no I/O)
      ↓
  Factory           → enforce invariants, create aggregate
      ↓
  Repository        → resolved to JPA implementation via Spring DI
      ↓
  PostgreSQL
```

---

## Scheduled Jobs

| Job | Interval | Function |
|---|---|---|
| `KeyContinuityJob` | 1h | Ensure key timeline never runs out — pre-generate next key |
| `KeyCleanupJob` | 6h | Hard-delete keys past retention period |
| `CertificateLifecycleJob` | 1h | Detect certs approaching rotation threshold (~100 days) |
| `CertificateCleanupJob` | 6h | Hard-delete certs past 5-year retention |
