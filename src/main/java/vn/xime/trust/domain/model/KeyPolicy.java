package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class KeyPolicy {

    private final Id id;

    private final String signerServiceId;
    private final String verifierServiceId;

    private final KeyAlgorithm algorithm;
    private final int keySize;

    private final long keyLifetimeSeconds;
    private final long rotationIntervalSeconds;
    private final long preloadSeconds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public KeyPolicy(
            Id id,
            String signerServiceId,
            String verifierServiceId,
            KeyAlgorithm algorithm,
            int keySize,
            long keyLifetimeSeconds,
            long rotationIntervalSeconds,
            long preloadSeconds,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (signerServiceId.equals(verifierServiceId)) {
            throw new IllegalArgumentException("signer and verifier must be different");
        }

        this.id = Objects.requireNonNull(id);
        this.signerServiceId = Objects.requireNonNull(signerServiceId);
        this.verifierServiceId = Objects.requireNonNull(verifierServiceId);

        this.algorithm = Objects.requireNonNull(algorithm);

        if (keySize <= 0) {
            throw new IllegalArgumentException("keySize must be > 0");
        }
        this.keySize = keySize;

        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.rotationIntervalSeconds = rotationIntervalSeconds;
        this.preloadSeconds = preloadSeconds;

        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
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

    public KeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public long getKeyLifetimeSeconds() {
        return keyLifetimeSeconds;
    }

    public long getRotationIntervalSeconds() {
        return rotationIntervalSeconds;
    }

    public long getPreloadSeconds() {
        return preloadSeconds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // =========================
    // UPDATE
    // =========================

    public KeyPolicy updated(
            KeyAlgorithm algorithm,
            int keySize,
            long keyLifetimeSeconds,
            long rotationIntervalSeconds,
            long preloadSeconds
    ) {
        return new KeyPolicy(
                this.id,
                this.signerServiceId,
                this.verifierServiceId,
                algorithm,
                keySize,
                keyLifetimeSeconds,
                rotationIntervalSeconds,
                preloadSeconds,
                this.createdAt,
                Instant.now()
        );
    }
}