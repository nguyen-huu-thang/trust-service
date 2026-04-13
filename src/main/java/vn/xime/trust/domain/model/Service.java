package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Service {

    private final String id;
    private final String name;
    private final String tenant;
    private final ServiceStatus status;

    private final Instant createdAt;

    public Service(
            String id,
            String name,
            String tenant,
            ServiceStatus status,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.tenant = tenant;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isActive() {
        return status == ServiceStatus.ACTIVE;
    }

    public void ensureActive() {
        if (!isActive()) {
            throw new IllegalStateException("Service is not active");
        }
    }

    // =========================
    // GETTERS
    // =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTenant() {
        return tenant;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}