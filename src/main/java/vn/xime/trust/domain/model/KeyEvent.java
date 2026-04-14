package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class KeyEvent {

    private final String kid;
    private final String serviceId;

    private final KeyEventType eventType;

    private final Instant createdAt;

    private final Map<String, Object> metadata;

    public KeyEvent(
            String kid,
            String serviceId,
            KeyEventType eventType,
            Instant createdAt,
            Map<String, Object> metadata
    ) {
        this.kid = kid;
        this.serviceId = serviceId;
        this.eventType = Objects.requireNonNull(eventType);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.metadata = metadata;
    }

    // =========================
    // GETTERS
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public KeyEventType getEventType() {
        return eventType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}