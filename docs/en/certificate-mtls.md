# Certificate & mTLS

**English** | [Tiếng Việt](../vn/certificate-mtls.md)

---

## Purpose

The Service Certificate System manages X.509 certificates that enable mutual TLS (mTLS) between all services in the Xime Base Platform. Trust Service operates as the internal Certificate Authority (CA) for the platform.

```
Trust Service (CA)
   ↓ issues X.509 leaf cert to each service
   ↓ cert contains SAN = service_id
   ↓
Service A ──mTLS──→ Service B
                        ↓
               verify cert chain against Root CA
                        ↓
               extract service_id from SAN
                        ↓
               compare service_id with request metadata
```

---

## X.509 Certificate Structure

```
Certificate
  tbsCertificate     ← all data to be signed
    issuer           ← Trust Service CA
    subject          ← service identity
    serial number
    validity         ← notBefore / notAfter
    extensions
      SAN            ← service_id (URI format)
      KeyUsage       ← digitalSignature, keyEncipherment (RSA)
      ExtendedKeyUsage ← clientAuth, serverAuth
      BasicConstraints ← CA=false (leaf certificate)
  signatureAlgorithm
  signatureValue     ← SIGN(DER(tbsCertificate))
```

**Important**: The signature is `SIGN(DER(tbsCertificate))` — the entire TBS structure is signed as DER bytes, not a header+body+signature pattern.

---

## CA Abstraction: CertificateAuthoritySigner

The application layer never touches the CA private key directly. All signing is done through a single abstraction:

```
Application / Domain layer
   → knows only: "there exists a mechanism to sign bytes"
   → calls: CertificateAuthoritySigner.sign(byte[])
   → receives: signature bytes

Infrastructure layer
   → decides: load key from file? call HSM? call KMS?
```

This design means the same use case code works in DEV (file-based key) and PROD (HSM/KMS) without modification.

| Environment | Implementation | CA key location |
|---|---|---|
| DEV | `FileSystemCaSigner` | `dev-keys/` (local file) |
| PROD | `HsmCaSigner` | HSM/KMS (key never leaves device) |

The active implementation is selected via `CaSignerConfig` using Spring profiles.

---

## Certificate Issuance Flow

```
Step 1 — Build TBSCertificate
  issuer:     Trust Service CA DN
  subject:    service CN
  serial:     random BigInteger
  validity:   notBefore = now, notAfter = now + ~1 year
  extensions: SAN (service_id), KeyUsage, ExtendedKeyUsage, BasicConstraints

Step 2 — Sign
  input:  DER(tbsCertificate)
  output: signature bytes
  via:    CertificateAuthoritySigner (file or HSM)

Step 3 — Assemble
  combine: tbsCertificate + signatureAlgorithm + signatureValue
  output:  complete X509Certificate (PEM format)
```

The service generates its own key pair before bootstrap. Only the CSR-equivalent data (public key + identity) is sent to Trust Service.

---

## Certificate Lifecycle

| Event | Timing |
|---|---|
| Rotation trigger | ~100 days after issuance |
| Certificate lifetime | ~1 year (notAfter) |
| Hard delete | 5 years after expiry (audit trail retention) |

`CertificateLifecycleJob` runs every hour and detects certs approaching the rotation threshold. When detected, it flags them for rotation. The service must then call `RotateCertificate` to receive a new cert.

---

## Cert Rotation Flow

Certificate rotation requires two proofs of possession:

```
Service (current mTLS connection)
   → sends: refresh_token_id + refresh_token (one-time) + new public key
   → Trust Service verifies:
       token exists in database
       token.used_at IS NULL           ← not yet used
       token.expires_at > now          ← not expired
       token.bound_kid == current cert ← bound to current cert
   → Trust Service issues new cert + new refresh token
   → Trust Service marks old token as used
```

The **one-time refresh token** prevents replay attacks. The **mTLS binding** ensures only the current cert holder can rotate.

---

## mTLS Identity Verification

When Service B receives a request from Service A:

```
Service A ──mTLS──→ Service B
                        ↓
1. TLS handshake: verify cert chain against Root CA
2. Extract service_id from cert SAN
3. Compare cert.service_id == request.service_id (metadata)
4. Compare cert.shard_id == request.shard_id (if applicable)
```

Two-layer identity comparison (cert + payload) prevents spoofing and provides observability — the request carries explicit identity even after TLS terminates at a proxy.

---

## Trust Store Bootstrap

When a new service starts:

```
1. Service calls GetRootCertificate (unauthenticated, plaintext)
2. Service pins Root CA certificate in its trust store
3. Admin calls BootstrapCert (internal API) to issue first cert
4. Service receives cert + initial refresh token
5. Service starts accepting and initiating mTLS connections
```

After bootstrap, cert rotation is autonomous (service-initiated via refresh token).

---

## Security Principles

- CA private key is never exposed outside `CertificateAuthoritySigner`
- Private key material is never serialized or logged
- Leaf certs are identified by SAN, not CN
- Refresh tokens are one-time and bound to the current cert
- CA signing key and service TLS key are completely separate
