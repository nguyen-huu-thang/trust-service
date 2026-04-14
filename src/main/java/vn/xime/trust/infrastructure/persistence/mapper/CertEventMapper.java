package vn.xime.trust.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.xime.trust.domain.model.CertEvent;
import vn.xime.trust.domain.model.CertEventType;
import vn.xime.trust.infrastructure.persistence.entity.CertEventEntity;

import java.util.Map;

public class CertEventMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================
    // Entity -> Domain
    // =========================

    public static CertEvent toDomain(CertEventEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("CertEventEntity must not be null");
        }

        requireNonNull(e.getEventType(), "eventType");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new CertEvent(
                e.getServiceId(),
                e.getKid(),
                mapEventType(e.getEventType()),
                e.getCreatedAt(),
                deserializeMetadata(e.getMetadata())
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertEventEntity toEntity(CertEvent d) {

        if (d == null) {
            throw new IllegalArgumentException("CertEvent must not be null");
        }

        CertEventEntity e = new CertEventEntity();

        e.setServiceId(d.getServiceId());
        e.setKid(d.getKid());
        e.setEventType(d.getEventType().name());
        e.setCreatedAt(d.getCreatedAt());
        e.setMetadata(serializeMetadata(d.getMetadata()));

        return e;
    }

    // =========================
    // Helpers
    // =========================

    private static CertEventType mapEventType(String type) {
        try {
            return CertEventType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid event type: " + type);
        }
    }

    private static Map<String, Object> deserializeMetadata(String json) {
        if (json == null) return null;

        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse metadata JSON", e);
        }
    }

    private static String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null) return null;

        try {
            return OBJECT_MAPPER.writeValueAsString(metadata);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize metadata", e);
        }
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}