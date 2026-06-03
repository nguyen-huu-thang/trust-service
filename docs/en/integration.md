# Integration

**English** | [Tiếng Việt](../vn/integration.md)

---

## Overview

Trust Service interacts with the platform in two distinct ways:

| Integration | Direction | When |
|---|---|---|
| JWT key distribution | Trust → Identity Service | Bootstrap + periodic refresh |
| mTLS certificate issuance | Trust → all services | Bootstrap + cert rotation |

Trust Service is **never called at request time**. All integrations are either startup operations or background refresh cycles.

---

## Identity Service Integration

### JWT Signing Key Fetch

Identity Service is the **only** consumer of private signing keys. It fetches the signing key once at startup and refreshes it periodically.

```
Identity Service startup
      ↓
  GetSigningKey(signer_service_id="identity-service")
      ↓
  Trust Service returns: private key, kid, activate_at, expires_at
      ↓
  Identity Service caches key in memory
      ↓
  Identity Service signs all JWTs with cached private key
```

Key refresh flow:

```
Identity Service (background job, every N minutes)
      ↓
  GetSigningKey(...)
      ↓
  Trust Service returns current + upcoming key(s)
      ↓
  Identity Service updates key cache
```

If the key cache is stale and Trust Service is unavailable, Identity Service continues signing with its cached key until expiry. This is by design.

### What Identity Service Does NOT Do

- Does not generate signing keys — Trust Service owns key lifecycle
- Does not rotate keys — key rotation is Trust Service's responsibility
- Does not store key state permanently — it is a consumer, not an owner

---

## All Services: JWT Verification

Every service that needs to verify JWTs fetches public keys from Trust Service:

```
Service startup
      ↓
  GetPublicKeys(verifier_service_id="identity-service")
      ↓
  Trust Service returns: all unexpired public keys (kid, PEM, activate_at, expires_at)
      ↓
  Service caches key set in memory
      ↓
  Inbound request with JWT
      ↓
  Extract kid from JWT header
      ↓
  Find key in local cache by kid
      ↓
  Verify signature locally — NO call to Trust Service
```

Services **never call Trust Service to verify a JWT**. Verification is always local using the cached public key. This is critical for scalability and resilience.

Public key refresh:

```
Service (background job, every 15 minutes)
      ↓
  GetPublicKeys(...)
      ↓
  Update local key cache
```

---

## All Services: mTLS Setup

### First Bootstrap

When a new service is deployed for the first time:

```
1. Service calls GetRootCertificate
      → Trust Service returns Root CA cert (PEM)
      → Service pins Root CA in its trust store

2. Platform admin calls BootstrapCert (internal API)
      → Trust Service issues first X.509 cert for this service
      → Returns cert (PEM) + initial refresh token

3. Service loads cert + private key into its TLS configuration
      → All outbound and inbound connections now use mTLS
```

### Cert Rotation

Services rotate their certs before expiry (~100 days after issuance):

```
Service detects cert approaching rotation window
      ↓
Service generates new key pair (in memory, never sent to Trust Service)
      ↓
Service calls RotateCertificate
  → token_id + refresh_token (one-time)
  → new public key (PEM)
  → over existing mTLS connection (proves current cert possession)
      ↓
Trust Service verifies token validity + issues new cert
      ↓
Service loads new cert + key pair into TLS configuration
      ↓
Service stores new refresh token for next rotation
```

### Connection Authentication

On every mTLS connection, the receiving service performs two-layer identity verification:

```
Incoming mTLS connection
      ↓
1. TLS layer:    verify cert chain against pinned Root CA
2. App layer:    extract service_id from cert SAN
3. App layer:    compare cert.service_id == request.metadata.service_id
```

The two-layer check (cert + request metadata) provides defense against misconfigured proxies and improves observability — the request carries explicit caller identity beyond the TLS layer.

---

## No Runtime Dependency

```
Trust Service
  ↓ provides keys and certs (setup time only)

All services use:
  - cached public keys  → verify JWT without Trust Service
  - existing certs      → mTLS without Trust Service
```

This means Trust Service can be taken offline for maintenance, upgrades, or rotation operations without affecting user-facing traffic. The platform continues to operate on cached cryptographic material.

Trust Service downtime only prevents:
- Generating new signing keys
- Issuing new service certificates
- Rotating existing certificates

---

## Integration Summary

```
Trust Service
      │
      ├── Identity Service
      │     ├── GetSigningKey (startup + periodic)
      │     └── GetPublicKeys (startup + periodic)
      │
      ├── All services (each)
      │     ├── GetRootCertificate (once at startup)
      │     ├── BootstrapCert via admin (once on deployment)
      │     ├── GetPublicKeys (startup + periodic)
      │     └── RotateCertificate (every ~100 days)
      │
      └── Platform admin
            ├── Register services
            ├── Manage key policies
            └── Bootstrap first certs
```
