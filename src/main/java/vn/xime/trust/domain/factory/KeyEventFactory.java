package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyEvent;
import vn.xime.trust.domain.model.KeyEventType;

import java.time.Instant;
import java.util.Map;

public class KeyEventFactory {

    public KeyEvent create(
            Id keyId,
            String signerServiceId,
            String verifierServiceId,
            KeyEventType eventType,
            Map<String, Object> metadata
    ) {
        // =========================
        // VALIDATE (nhẹ)
        // =========================

        if (keyId == null) {
            throw new IllegalArgumentException("keyId is required");
        }

        if (eventType == null) {
            throw new IllegalArgumentException("eventType is required");
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();
        Instant now = Instant.now();

        // defensive copy metadata (tránh bị sửa từ ngoài)
        Map<String, Object> safeMetadata =
                metadata == null ? null : Map.copyOf(metadata);

        return new KeyEvent(
                id,
                keyId,
                signerServiceId,
                verifierServiceId,
                eventType,
                now,
                safeMetadata
        );
    }
}