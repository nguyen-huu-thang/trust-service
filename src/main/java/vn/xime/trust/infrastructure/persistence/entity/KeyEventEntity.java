package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_events",
        indexes = {
                @Index(
                        name = "idx_key_events_signer_time",
                        columnList = "signer_service_id,created_at DESC"
                ),
                @Index(
                        name = "idx_key_events_key",
                        columnList = "key_id"
                )
        }
)
public class KeyEventEntity {

    // =========================
    // ID (KSUID)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // REFERENCE (NO FK)
    // =========================

    @Column(name = "key_id", columnDefinition = "BYTEA")
    private byte[] keyId;

    @Column(name = "signer_service_id", length = 20)
    private String signerServiceId;

    @Column(name = "verifier_service_id", length = 20)
    private String verifierServiceId;

    // =========================
    // EVENT
    // =========================

    @Column(name = "event_type", length = 50)
    private String eventType;

    // =========================
    // TIME
    // =========================

    @Column(name = "created_at")
    private Instant createdAt;

    // =========================
    // METADATA (JSONB)
    // =========================

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getKeyId() {
        return keyId;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public void setSignerServiceId(String signerServiceId) {
        this.signerServiceId = signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public void setVerifierServiceId(String verifierServiceId) {
        this.verifierServiceId = verifierServiceId;
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