package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class ServiceTrust {

    private final String signerServiceId;
    private final String verifierServiceId;

    private final long keyLifetimeSeconds;
    private final long jwtTtlSeconds;
    private final long preloadSeconds;

    private final boolean autoRotate;

    private final Instant lastRotatedAt;
    private final Instant nextRotationAt;

    private final Instant createdAt;
    private final Instant updatedAt;

    public ServiceTrust(
            String signerServiceId,
            String verifierServiceId,
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
            long preloadSeconds,
            boolean autoRotate,
            Instant lastRotatedAt,
            Instant nextRotationAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (signerServiceId.equals(verifierServiceId)) {
            throw new IllegalArgumentException("signer and verifier must be different");
        }

        this.signerServiceId = Objects.requireNonNull(signerServiceId);
        this.verifierServiceId = Objects.requireNonNull(verifierServiceId);

        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.jwtTtlSeconds = jwtTtlSeconds;
        this.preloadSeconds = preloadSeconds;

        this.autoRotate = autoRotate;

        this.lastRotatedAt = lastRotatedAt;
        this.nextRotationAt = nextRotationAt;

        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }

    // =========================
    // GETTERS
    // =========================

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public long getKeyLifetimeSeconds() {
        return keyLifetimeSeconds;
    }

    public long getJwtTtlSeconds() {
        return jwtTtlSeconds;
    }

    public long getPreloadSeconds() {
        return preloadSeconds;
    }

    public boolean isAutoRotate() {
        return autoRotate;
    }

    public Instant getLastRotatedAt() {
        return lastRotatedAt;
    }

    public Instant getNextRotationAt() {
        return nextRotationAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}