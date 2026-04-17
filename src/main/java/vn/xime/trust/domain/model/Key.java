package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Key {

    private final Id id;

    // 🔥 relationship
    private final String signerServiceId;
    private final String verifierServiceId;

    private final String publicKey;
    private final String privateKeyEncrypted;

    private final KeyAlgorithm algorithm;
    private final int keySize;

    private final Instant createdAt;
    private final Instant activateAt;
    private final Instant expiresAt;

    private final boolean deleted;

    public Key(
            Id id,
            String signerServiceId,
            String verifierServiceId,
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

        this.id = Objects.requireNonNull(id);
        this.signerServiceId = Objects.requireNonNull(signerServiceId);
        this.verifierServiceId = Objects.requireNonNull(verifierServiceId);
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

    public Id getId() {
        return id;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
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

    // =========================
    // STATE CHANGE
    // =========================

    public Key markDeleted() {
        return new Key(
                id,
                signerServiceId,
                verifierServiceId,
                publicKey,
                privateKeyEncrypted,
                algorithm,
                keySize,
                createdAt,
                activateAt,
                expiresAt,
                true
        );
    }
}