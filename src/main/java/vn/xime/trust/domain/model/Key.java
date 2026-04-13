package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Key {

    private final String kid;
    private final String serviceId;

    private final String publicKey;
    private final String privateKeyEncrypted;

    private final KeyAlgorithm algorithm;
    private final int keySize;

    private final Instant createdAt;
    private final Instant activateAt;
    private final Instant expiresAt;

    private final boolean deleted;

    public Key(
            String kid,
            String serviceId,
            String publicKey,
            String privateKeyEncrypted,
            KeyAlgorithm algorithm,
            int keySize,
            Instant createdAt,
            Instant activateAt,
            Instant expiresAt,
            boolean deleted
    ) {
        if (expiresAt.isBefore(activateAt)) {
            throw new IllegalArgumentException("expiresAt must be after activateAt");
        }

        this.kid = Objects.requireNonNull(kid);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.publicKey = Objects.requireNonNull(publicKey);
        this.privateKeyEncrypted = Objects.requireNonNull(privateKeyEncrypted);
        this.algorithm = Objects.requireNonNull(algorithm);
        this.keySize = keySize;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.activateAt = Objects.requireNonNull(activateAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.deleted = deleted;
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean canSign(Instant now) {
        return !deleted && !now.isBefore(activateAt);
    }

    public boolean canVerify(Instant now) {
        return !deleted && now.isBefore(expiresAt);
    }

    public boolean isActiveAt(Instant now) {
        return canSign(now);
    }

    public boolean isExpiredAt(Instant now) {
        return !now.isBefore(expiresAt);
    }

    // =========================
    // GETTERS
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceId() {
        return serviceId;
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
}