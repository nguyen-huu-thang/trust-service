package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "cert_events",
        indexes = {
                @Index(
                        name = "idx_cert_events_service_time",
                        columnList = "service_id,created_at DESC"
                ),
                @Index(
                        name = "idx_cert_events_cert",
                        columnList = "cert_id"
                )
        }
)
public class CertEventEntity {

    // =========================
    // ID (KSUID)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // Reference (NO RELATION)
    // =========================

    @Column(name = "service_id", length = 20)
    private String serviceId;

    // 🔥 thay kid → cert_id
    @Column(name = "cert_id", columnDefinition = "BYTEA")
    private byte[] certId;

    // =========================
    // Event
    // =========================

    @Column(name = "event_type", length = 50)
    private String eventType;

    // =========================
    // Time
    // =========================

    @Column(name = "created_at")
    private Instant createdAt;

    // =========================
    // Metadata (JSONB)
    // =========================

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // =========================
    // Getter / Setter
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public byte[] getCertId() {
        return certId;
    }

    public void setCertId(byte[] certId) {
        this.certId = certId;
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