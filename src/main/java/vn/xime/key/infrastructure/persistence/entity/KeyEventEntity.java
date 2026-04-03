package vn.xime.key.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "key_events",
    indexes = {
        @Index(name = "idx_key_events_service_time", columnList = "service_name, created_at")
    }
)
public class KeyEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Key Info
    // =========================

    @Column(name = "kid")
    private String kid;

    @Column(name = "service_name")
    private String serviceName;

    // =========================
    // Event
    // =========================

    /**
     * CREATED / ROTATED / DELETED / EMERGENCY_ROTATION
     */
    @Column(name = "event_type")
    private String eventType;

    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * JSON metadata (PostgreSQL JSONB)
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}