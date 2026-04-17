package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAccessAction;
import vn.xime.trust.domain.model.KeyAccessLog;
import vn.xime.trust.infrastructure.persistence.entity.KeyAccessLogEntity;

import java.util.Arrays;

public class KeyAccessLogMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyAccessLog toDomain(KeyAccessLogEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("KeyAccessLogEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getAction(), "action");
        requireNonNull(e.getRequestedAt(), "requestedAt");

        return new KeyAccessLog(
                toId(e.getId()),
                toNullableId(e.getKeyId()),
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                mapAction(e.getAction()),
                Boolean.TRUE.equals(e.getIncludePrivate()),
                e.getRequestedAt(),
                e.getIpAddress(),
                Boolean.TRUE.equals(e.getSuccess()),
                e.getErrorMessage()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyAccessLogEntity toEntity(KeyAccessLog d) {

        if (d == null) {
            throw new IllegalArgumentException("KeyAccessLog must not be null");
        }

        KeyAccessLogEntity e = new KeyAccessLogEntity();

        e.setId(toBytes(d.getId()));
        e.setKeyId(toNullableBytes(d.getKeyId()));
        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());
        e.setAction(d.getAction().name());
        e.setIncludePrivate(d.isIncludePrivate());
        e.setRequestedAt(d.getRequestedAt());
        e.setIpAddress(d.getIpAddress());
        e.setSuccess(d.isSuccess());
        e.setErrorMessage(d.getErrorMessage());

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
    // Helpers
    // =========================

    private static KeyAccessAction mapAction(String action) {
        try {
            return KeyAccessAction.valueOf(action.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}