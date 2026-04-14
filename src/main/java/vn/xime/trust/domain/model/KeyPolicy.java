package vn.xime.trust.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class KeyPolicy {

    private final String serviceId;

    private final long keyLifetimeSeconds;
    private final long jwtTtlSeconds;
    private final long preloadSeconds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public KeyPolicy(
            String serviceId,
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
            long preloadSeconds,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.serviceId = Objects.requireNonNull(serviceId);

        if (keyLifetimeSeconds <= 0) {
            throw new IllegalArgumentException("keyLifetimeSeconds must be > 0");
        }
        if (jwtTtlSeconds <= 0) {
            throw new IllegalArgumentException("jwtTtlSeconds must be > 0");
        }
        if (preloadSeconds < 0) {
            throw new IllegalArgumentException("preloadSeconds must be >= 0");
        }

        if (keyLifetimeSeconds < jwtTtlSeconds) {
            throw new IllegalArgumentException(
                "keyLifetimeSeconds must be >= jwtTtlSeconds"
            );
        }

        if (preloadSeconds >= keyLifetimeSeconds) {
            throw new IllegalArgumentException(
                "preloadSeconds must be < keyLifetimeSeconds"
            );
        }

        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.jwtTtlSeconds = jwtTtlSeconds;
        this.preloadSeconds = preloadSeconds;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public Duration keyLifetime() {
        return Duration.ofSeconds(keyLifetimeSeconds);
    }

    public Duration jwtTtl() {
        return Duration.ofSeconds(jwtTtlSeconds);
    }

    public Duration preloadWindow() {
        return Duration.ofSeconds(preloadSeconds);
    }

    /**
     * thời điểm nên tạo key tiếp theo
     */
    public Instant calculateNextActivationTime(Instant currentActivateAt) {
        return currentActivateAt.plusSeconds(keyLifetimeSeconds - preloadSeconds);
    }

    /**
     * validate policy hợp lý với JWT
     */
    public boolean isValidForJwtSafety() {
        return keyLifetimeSeconds >= jwtTtlSeconds;
    }

    // =========================
    // GETTERS
    // =========================

    public String getServiceId() {
        return serviceId;
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
}