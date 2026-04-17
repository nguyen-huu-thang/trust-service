package vn.xime.trust.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyEvent;
import vn.xime.trust.domain.model.KeyEventType;
import vn.xime.trust.infrastructure.persistence.entity.KeyEventEntity;

import java.util.Arrays;
import java.util.Map;

public class KeyEventMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyEvent toDomain(KeyEventEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("KeyEventEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getEventType(), "eventType");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new KeyEvent(
                toId(e.getId()),
                toNullableId(e.getKeyId()),
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                mapEventType(e.getEventType()),
                e.getCreatedAt(),
                deserializeMetadata(e.getMetadata())
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyEventEntity toEntity(KeyEvent d) {

        if (d == null) {
            throw new IllegalArgumentException("KeyEvent must not be null");
        }

        KeyEventEntity e = new KeyEventEntity();

        e.setId(toBytes(d.getId()));
        e.setKeyId(toNullableBytes(d.getKeyId()));
        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());
        e.setEventType(d.getEventType().name());
        e.setCreatedAt(d.getCreatedAt());
        e.setMetadata(serializeMetadata(d.getMetadata()));

        return e;
    }

    // =========================
    // ID MAPPING
    // =========================

    private static Id toId(byte[] bytes) {
        return new Id(copy(bytes));
    }

    private static Id toNullableId(byte[] bytes) {
        return bytes == null ? null : toId(bytes);
    }

    private static byte[] toBytes(Id id) {
        return copy(id.toBytes());
    }

    private static byte[] toNullableBytes(Id id) {
        return id == null ? null : toBytes(id);
    }

    private static byte[] copy(byte[] src) {
        return src == null ? null : Arrays.copyOf(src, src.length);
    }

    // =========================
    // ENUM
    // =========================

    private static KeyEventType mapEventType(String eventType) {
        try {
            return KeyEventType.valueOf(eventType.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid eventType: " + eventType);
        }
    }

    // =========================
    // JSONB
    // =========================

    private static Map<String, Object> deserializeMetadata(String json) {
        if (json == null) return null;

        try {
            return OBJECT_MAPPER.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize metadata", e);
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