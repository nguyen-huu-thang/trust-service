package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Shard {

    private final String id;
    private final String serviceId;

    private final String host;
    private final Integer port;

    private final ShardStatus status;

    private final Instant createdAt;

    public Shard(
            String id,
            String serviceId,
            String host,
            Integer port,
            ShardStatus status,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);

        this.host = host;
        this.port = port;
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isActive() {
        return status == ShardStatus.ACTIVE;
    }

    public boolean isAvailable() {
        return status == ShardStatus.ACTIVE;
    }

    public void ensureActive() {
        if (!isActive()) {
            throw new IllegalStateException("Shard is not active: " + id);
        }
    }

    // =========================
    // GETTERS
    // =========================

    public String getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public ShardStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}