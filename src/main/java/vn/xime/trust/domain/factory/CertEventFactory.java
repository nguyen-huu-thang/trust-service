package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.CertEvent;
import vn.xime.trust.domain.model.CertEventType;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;
import java.util.Map;

public class CertEventFactory {

    public CertEvent create(
            String serviceId,
            Id certId,
            CertEventType eventType,
            Map<String, Object> metadata
    ) {
        // =========================
        // VALIDATE (nhẹ)
        // =========================

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (certId == null) {
            throw new IllegalArgumentException("certId is required");
        }

        if (eventType == null) {
            throw new IllegalArgumentException("eventType is required");
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();
        Instant now = Instant.now();

        Map<String, Object> safeMetadata =
                metadata == null ? null : Map.copyOf(metadata);

        return new CertEvent(
                id,
                serviceId,
                certId,
                eventType,
                now,
                safeMetadata
        );
    }
}