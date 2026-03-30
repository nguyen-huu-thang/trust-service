package vn.xime.key.domain.key;

import java.time.Instant;
import java.util.Objects;

public class Key {

    private final String kid;
    private final String serviceName;

    private final String publicKey;
    private final String privateKeyEncrypted;

    private final KeyAlgorithm algorithm;
    private final int keySize;

    private KeyStatus status;

    private final Instant createdAt;
    private final Instant activateAt;
    private final Instant expiresAt;

    private boolean deleted;

    public Key(
            String kid,
            String serviceName,
            String publicKey,
            String privateKeyEncrypted,
            KeyAlgorithm algorithm,
            int keySize,
            KeyStatus status,
            Instant createdAt,
            Instant activateAt,
            Instant expiresAt,
            boolean deleted
    ) {
        this.kid = kid;
        this.serviceName = serviceName;
        this.publicKey = publicKey;
        this.privateKeyEncrypted = privateKeyEncrypted;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.status = status;
        this.createdAt = createdAt;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
        this.deleted = deleted;
    }

    // =========================
    // Business Logic
    // =========================

    public boolean isCurrent() {
        return this.status == KeyStatus.CURRENT;
    }

    public boolean isNext() {
        return this.status == KeyStatus.NEXT;
    }

    public boolean isOld() {
        return this.status == KeyStatus.OLD;
    }

    public boolean isActive(Instant now) {
        return (activateAt == null || !now.isBefore(activateAt))
                && (expiresAt == null || now.isBefore(expiresAt));
    }

    public void markAsCurrent() {
        this.status = KeyStatus.CURRENT;
    }

    public void markAsOld() {
        this.status = KeyStatus.OLD;
    }

    public void markDeleted() {
        this.deleted = true;
    }

    // =========================
    // Getter
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public KeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public KeyStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getActivateAt() {
        return activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // =========================
    // Equality (based on kid)
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key key)) return false;
        return Objects.equals(kid, key.kid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kid);
    }
}