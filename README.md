# Trust Service

**English** | [Tiếng Việt](README-vn.md)

> Cryptographic infrastructure and trust authority for the Xime Base Platform — managing JWT signing keys, X.509 certificates for mTLS, and service identity.

---

Trust Service is the **security infrastructure layer** of the Xime Base Platform. It acts as the internal Root of Trust — providing cryptographic keys for JWT authentication and X.509 certificates for mutual TLS between services.

```
Application Services (social, ecommerce, SaaS, AI)
               ↓ verify JWT locally using cached public keys
          Identity Service ← fetches signing key from Trust Service
               ↓
          Trust Service   ← CA, JWT key lifecycle, mTLS cert lifecycle
               ↓
          PostgreSQL
```

---

## What Trust Service Does

Trust Service manages two independent security systems:

**JWT Key System**
- Generate and manage lifecycle of asymmetric signing key pairs (RSA/EC)
- Distribute public keys to services for local JWT verification
- Time-based key rotation — no event trigger required
- Backward compatibility: old keys remain valid until expiry

**Service Certificate System (mTLS)**
- Operate as internal Certificate Authority (CA) for the platform
- Issue X.509 leaf certificates to services (SAN = service_id)
- Manage certificate lifecycle: issuance, rotation, and cleanup
- Enable mutual TLS (mTLS) between all Base Platform services

## What Trust Service Does NOT Do

- Does not authenticate users
- Does not participate in runtime request handling
- Does not implement business logic
- Does not revoke JWTs in real time (short TTL design instead)

Trust Service is **control-plane only** — never in the hot path of a request.

---

## Resilience Design

If Trust Service is temporarily unavailable:

```
JWT verification  → still works (services use cached public keys)
mTLS connections  → still work  (services use existing certificates)
JWT signing       → still works (Identity Service uses cached signing key)
```

Trust Service is only needed when rotating keys, issuing new certs, or bootstrapping a new service.

---

## Key Design Decisions

### Time-Based Key Lifecycle

Keys carry no CURRENT/NEXT/OLD status. Time determines behavior:

```
current key = max(activate_at ≤ now)
next key    = min(activate_at > now)
```

### Append-Only Key Chain

Key rotation means extending the timeline, not replacing keys. Invariants:
- `new.activate_at` strictly after `last.activate_at`
- No duplicate `activate_at` within a (signer, verifier) pair
- Rotation interval ≥ policy minimum

### CA Abstraction

```
DEV  → load private key from dev-keys/ (file system)
PROD → delegate signing to HSM/KMS (key never leaves the device)
```

Application code sees only `CertificateAuthoritySigner` — no environment-specific logic in use cases.

---

## Quick Start

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

HTTP: `8081` | gRPC: `9090`

---

## Architecture

Trust Service follows **Hexagonal Architecture** with DDD tactical patterns, built on Spring Boot 4 / Java 25:

```
src/main/java/
├── api/              ← Input adapters (gRPC external/internal, REST)
├── application/      ← Use cases, services, ports, DTOs, mappers
├── domain/           ← Models, factories, repositories (interfaces), domain services
└── infrastructure/   ← JPA persistence, crypto, scheduler, security
```

Domain services are plain Java (no Spring annotations). Repository interfaces live in `domain/repository/` and are implemented in `infrastructure/persistence/`.

---

## Documentation

| Document | Description |
|---|---|
| [Overview](docs/en/overview.md) | Role, two systems, position in Base Platform |
| [Architecture](docs/en/architecture.md) | Layer structure, DDD patterns, directory layout |
| [Key System](docs/en/key-system.md) | JWT key lifecycle, rotation policy, scheduler |
| [Certificate & mTLS](docs/en/certificate-mtls.md) | X.509 design, CA abstraction, cert rotation |
| [API Reference](docs/en/api.md) | gRPC proto definitions, usage rules |
| [Integration](docs/en/integration.md) | How Identity Service and other services use Trust Service |

---

## Base Platform Services

| Service | Role |
|---|---|
| `trust-service` | **Trust infrastructure — CA, mTLS, JWT signing keys** |
| `identity-service` | Authentication infrastructure — JWT, refresh tokens |
| `user-service` | Human Identity Domain Service |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Notification delivery |
| `payment-service` | Payment processing |

---

## Project Status

Trust Service is in **active development**. The JWT key system and certificate/mTLS system are implemented. Scheduled jobs for key continuity and certificate lifecycle are operational.

---

## License

MIT
