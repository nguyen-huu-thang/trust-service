package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_events",
        indexes = {
                @Index(
                        name = "idx_key_events_service_time",
                        columnList = "service_id,created_at DESC"
                )
        }
)
public class KeyEventEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Reference (NO RELATION)
    // =========================

    /**
     * Không dùng ManyToOne
     * → event phải độc lập
     */
    @Column(name = "kid", length = 100)
    private String kid;

    @Column(name = "service_id", length = 100)
    private String serviceId;

    // =========================
    // Event
    // =========================

    @Column(name = "event_type", length = 50)
    private String eventType;

    // =========================
    // Time
    // =========================

    @Column(
            name = "created_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    // =========================
    // Metadata (JSONB)
    // =========================

    /**
     * Lưu JSON dạng string (simple & safe)
     * Có thể nâng cấp sang Map + converter sau
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // =========================
    // Lifecycle hooks
    // =========================

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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