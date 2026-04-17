package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class CertEvent {

    private final Id id;

    private final String serviceId;
    private final Id certId;

    private final CertEventType eventType;

    private final Instant createdAt;

    private final Map<String, Object> metadata;

    public CertEvent(
            Id id,
            String serviceId,
            Id certId,
            CertEventType eventType,
            Instant createdAt,
            Map<String, Object> metadata
    ) {
        this.id = Objects.requireNonNull(id);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.certId = Objects.requireNonNull(certId);
        this.eventType = Objects.requireNonNull(eventType);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.metadata = metadata != null ? Map.copyOf(metadata) : null;
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Id getCertId() {
        return certId;
    }

    public CertEventType getEventType() {
        return eventType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}