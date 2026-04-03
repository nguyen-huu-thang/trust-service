package vn.xime.key.domain.key;

import java.time.Instant;

/**
 * Domain Model: KeyPolicy
 *
 * =========================
 * Vai trò:
 * =========================
 * * Cấu hình rotation cho từng service
 *
 * =========================
 * Không chứa logic phức tạp
 * =========================
 */
public class KeyPolicy {

    private final String serviceName;

    private final long keyLifetimeSeconds;
    private final long jwtTtlSeconds;
    private final long preloadSeconds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public KeyPolicy(
            String serviceName,
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
            long preloadSeconds,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.serviceName = serviceName;
        this.keyLifetimeSeconds = keyLifetimeSeconds;
        this.jwtTtlSeconds = jwtTtlSeconds;
        this.preloadSeconds = preloadSeconds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getServiceName() {
        return serviceName;
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