package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class KeyPolicy {

    private final Id id;

    private final String signerServiceId;
    private final String verifierServiceId;

    private final long keyLifetimeSeconds;
    private final long jwtTtlSeconds;
    private final long preloadSeconds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public KeyPolicy(
            Id id,
            String signerServiceId,
            String verifierServiceId,
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
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

        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.jwtTtlSeconds = jwtTtlSeconds;
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

    public long getKeyLifetimeSeconds() {
        return keyLifetimeSeconds;
    }

    public long getJwtTtlSeconds() {
        return jwtTtlSeconds;
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

    public KeyPolicy updated(
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
            long preloadSeconds
    ) {
        return new KeyPolicy(
                this.id,
                this.signerServiceId,
                this.verifierServiceId,
                keyLifetimeSeconds,
                jwtTtlSeconds,
                preloadSeconds,
                this.createdAt,
                Instant.now() // update timestamp
        );
    }
}