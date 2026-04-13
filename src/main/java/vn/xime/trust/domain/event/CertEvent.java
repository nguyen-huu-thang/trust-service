package vn.xime.trust.domain.event;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class CertEvent {

    private final String serviceId;
    private final String kid;

    private final CertEventType eventType;

    private final Instant createdAt;

    private final Map<String, Object> metadata;

    public CertEvent(
            String serviceId,
            String kid,
            CertEventType eventType,
            Instant createdAt,
            Map<String, Object> metadata
    ) {
        this.serviceId = serviceId;
        this.kid = kid;
        this.eventType = Objects.requireNonNull(eventType);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.metadata = metadata;
    }

    // =========================
    // GETTERS
    // =========================

    public String getServiceId() {
        return serviceId;
    }

    public String getKid() {
        return kid;
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