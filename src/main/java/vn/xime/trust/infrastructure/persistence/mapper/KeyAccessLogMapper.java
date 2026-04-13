package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.KeyAccessLog;
import vn.xime.trust.infrastructure.persistence.entity.KeyAccessLogEntity;

public class KeyAccessLogMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyAccessLog toDomain(KeyAccessLogEntity e) {
        return new KeyAccessLog(
                e.getKid(),
                e.getServiceId(),
                e.getAction(),
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
        KeyAccessLogEntity e = new KeyAccessLogEntity();

        e.setKid(d.getKid());
        e.setServiceId(d.getServiceId());
        e.setAction(d.getAction());
        e.setIncludePrivate(d.isIncludePrivate());
        e.setRequestedAt(d.getRequestedAt());
        e.setIpAddress(d.getIpAddress());
        e.setSuccess(d.isSuccess());
        e.setErrorMessage(d.getErrorMessage());

        return e;
    }
}