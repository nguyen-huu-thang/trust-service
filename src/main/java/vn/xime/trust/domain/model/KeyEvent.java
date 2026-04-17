package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class KeyEvent {

    private final Id id;

    private final Id keyId;
    private final String signerServiceId;
    private final String verifierServiceId;

    private final KeyEventType eventType;

    private final Instant createdAt;

    private final Map<String, Object> metadata;

    public KeyEvent(
            Id id,
            Id keyId,
            String signerServiceId,
            String verifierServiceId,
            KeyEventType eventType,
            Instant createdAt,
            Map<String, Object> metadata
    ) {
        this.id = Objects.requireNonNull(id);
        this.keyId = Objects.requireNonNull(keyId);
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.eventType = Objects.requireNonNull(eventType);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.metadata = metadata;
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
    return id;
    }

    public Id getKeyId() {
        return keyId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
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