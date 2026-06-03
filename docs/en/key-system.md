# Key System

**English** | [Tiếng Việt](../vn/key-system.md)

---

## Purpose

The JWT Key System manages the full lifecycle of asymmetric key pairs used to sign and verify JSON Web Tokens across the Xime Base Platform.

```
Trust Service
   ↓ generate RSA/EC key pair
   ↓ encrypt private key at rest (AES)
   ↓ store in PostgreSQL
   ↓
Identity Service ← fetches signing key (private) via gRPC
   ↓ signs JWT access tokens
   ↓
All other services ← fetch public keys via gRPC, cache locally
   ↓ verify JWT without calling Trust Service at runtime
```

---

## Core Design: Time-Based Lifecycle

Keys have **no CURRENT/NEXT/OLD status field**. Time determines which key is active:

```
current key = the key with max(activate_at) where activate_at ≤ now
next key    = the key with min(activate_at) where activate_at > now
```

This eliminates race conditions from status updates and simplifies queries.

---

## Key Chain Invariants

Every key belongs to a **(signer_service_id, verifier_service_id)** pair. The chain for each pair must satisfy four invariants at all times:

| Invariant | Rule |
|---|---|
| **Append-only** | `new.activate_at` must be strictly after `last.activate_at` |
| **No duplicate** | No two keys in the same pair can share the same `activate_at` |
| **Rotation interval** | `expires_at - activate_at ≥ policy.rotation_interval_seconds` |
| **No past activation** | `activate_at` cannot be in the past (5-second clock skew tolerance) |

These are enforced in `KeyValidationDomainService` and in `KeyFactory` before any key is persisted.

---

## Key Generation Flow

```
GenerateKeyUseCase
  ├── KeyPolicyDomainService.calculateActivateAt()    ← place after last key
  ├── KeyPolicyDomainService.calculateExpiresAt()     ← apply rotation interval
  ├── KeyValidationDomainService.validateNewKey()     ← enforce 4 invariants
  ├── KeyGenerator.generate(algorithm, keySize)       ← infrastructure
  ├── KeyEncryptionService.encrypt(privateKey)        ← AES encrypt at rest
  ├── KeyFactory.create(...)                          ← domain aggregate
  └── KeyRepository.save(key)
```

---

## Key Rotation: Extending the Timeline

Rotation does **not** replace existing keys. It appends a new key to the timeline:

```
Before rotation:
  Key A  [activate: T+0, expires: T+30d]   ← current
  Key B  [activate: T+25d, expires: T+55d] ← next (preloaded)

After rotation (new key added):
  Key A  [activate: T+0, expires: T+30d]
  Key B  [activate: T+25d, expires: T+55d] ← will become current
  Key C  [activate: T+50d, expires: T+80d] ← new next key
```

Old keys are never deleted while there are tokens that could still be valid. Services hold all unexpired public keys and verify by `kid` (key ID in JWT header).

---

## Preload Window

The scheduler checks whether a "next key" exists within the preload window:

```
preload window = [now + rotation_interval - preload_margin, now + rotation_interval + buffer]
```

Two cases trigger pre-generation:

**Case 1 — No future key exists**
```
Timeline: [Key A] ← current, nothing after
Action:   Generate Key B starting after Key A expires
```

**Case 2 — Next key exists but is too close to current**
```
Timeline: [Key A (current)] [Key B (activates soon, no buffer)]
Action:   Generate Key C after Key B with proper interval
```

---

## Key Policy

Each **(signer, verifier)** pair has an associated `KeyPolicy`:

| Field | Description |
|---|---|
| `rotation_interval_seconds` | Minimum lifetime of each key |
| `preload_margin_seconds` | How far ahead to generate the next key |
| `algorithm` | `RSA` or `EC` |
| `key_size` | 2048 / 4096 (RSA) or 256 / 384 (EC) |

---

## Private Key Security

Private keys are never stored in plaintext:

```
KeyGenerator.generate()
   → raw RSA/EC PrivateKey (in memory only)
   → KeyEncryptionService.encrypt(privateKey)  ← AES-256, key from application.yml
   → store encrypted bytes in PostgreSQL
```

The AES encryption key is loaded from `key.encryption.secret` in `application.yml` (Base64-encoded, managed externally in production).

---

## Key Distribution

| Consumer | API | Data received |
|---|---|---|
| Identity Service | `GetSigningKey` | Private key (encrypted transport), activate_at, expires_at |
| All services | `GetPublicKeys` | Public key (PEM), kid, activate_at, expires_at |

`GetPublicKeys` returns **all unexpired public keys** for a verifier. Services cache these keys and re-fetch periodically — not on every request.

---

## Key Cleanup

`KeyCleanupJob` hard-deletes keys that are past their retention period:

```
delete key where expires_at < now - retention_period
```

Retention period is long enough to cover any valid JWT's remaining TTL at expiry time. A key is only cleaned up after no JWT it signed could still be in circulation.
