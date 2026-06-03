# Overview

**English** | [Tiếng Việt](../vn/overview.md)

---

## What is Trust Service?

Trust Service is the **cryptographic infrastructure and trust authority** of the Xime Base Platform.

Its role is to be the single source of truth for all cryptographic material used across the platform — JWT signing keys for user authentication and X.509 certificates for service-to-service mTLS. No other service generates or owns these materials.

```
Application Services
  identity-service → signs JWT using key from Trust Service
  user-service     → communicates via mTLS cert from Trust Service
  data-service     → communicates via mTLS cert from Trust Service
         ↓  (all security credentials originate here)
    Trust Service  ← Root of Trust
         ↓
  PostgreSQL
```

---

## Position in Base Platform

The Xime Base Platform is divided into two layers:

### Base Platform (core services)

Generic, reusable infrastructure services built once and shared across all applications:

| Service | Role |
|---|---|
| `trust-service` | **Trust infrastructure — CA, mTLS, JWT signing keys** |
| `identity-service` | Authentication — JWT issuance, refresh tokens |
| `user-service` | Human Identity Domain — credentials, account state |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Notification delivery |
| `payment-service` | Payment processing |

### Application Layer (business services)

Application-specific logic that relies on Base Platform:

- **Social Network**: post-service, comment-service, media-service
- **Ecommerce**: product-service, order-service
- **SaaS / AI**: workspace-service, dataset-service, ai-agent-service

Trust Service serves all of these indirectly — through the security foundation they depend on.

---

## Two Independent Systems

Trust Service manages two completely independent security systems that share no data or logic:

### A. JWT Key System

Used for user authentication across the platform:

```
Trust Service
   → generates RSA/EC key pairs
   → Identity Service fetches signing key (private)
   → Identity Service signs JWT
   → All services fetch public keys to verify JWT locally
```

Key rotation is **time-based**. When the current key approaches expiry, Trust Service pre-generates the next key. Services cache all unexpired public keys so they can verify tokens issued with older keys.

### B. Service Trust System (mTLS)

Used for authenticated communication between services:

```
Trust Service
   → operates as internal CA
   → issues X.509 cert to each service (SAN = service_id)
   → services present cert on every connection
   → receiving service verifies cert against Root CA
```

Cert rotation uses a **one-time refresh token** bound to the current cert. The service presents both the token and its current mTLS connection to prove possession before receiving a new cert.

---

## Design Philosophy

| Question | Answer |
|---|---|
| What is Trust Service? | Cryptographic infrastructure — the Root of Trust |
| What does it manage? | JWT signing key pairs + X.509 service certificates |
| Does it authenticate users? | No — that is Identity Service's responsibility |
| Is it in the request path? | No — control-plane only |
| What if it goes down? | JWT verify and mTLS still work (cached keys/certs) |
| Who is the CA? | Trust Service itself, via `CertificateAuthoritySigner` |

---

## Resilience Model

Trust Service is a **control-plane service**, not a data-plane service. This distinction is architecturally critical:

```
Trust Service DOWN
      ↓
JWT verification  → continues (cached public keys in each service)
mTLS connections  → continue  (existing certs still valid)
JWT signing       → continues (Identity Service has cached signing key)
      ↓
Only fails when:
  - rotating a key (no new next key generated)
  - issuing a new cert (new service cannot get a cert)
  - rotating a cert (cert rotation window is missed)
```

Services are designed to tolerate hours of Trust Service unavailability with no impact on user-facing traffic.

---

## What Trust Service Is NOT

- Not an identity provider — it does not issue or validate user tokens
- Not a runtime security gateway — it does not intercept requests
- Not a secrets manager — it manages cryptographic key lifecycle, not application secrets
- Not a certificate revocation service — revocation is handled by cert expiry and rotation cadence
