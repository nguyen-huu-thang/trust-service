package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.event.CertEvent;
import vn.xime.trust.infrastructure.persistence.entity.CertEventEntity;

public class CertEventMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static CertEvent toDomain(CertEvent e) {
        return new CertEvent(
                e.getServiceId(),
                e.getKid(),
                e.getEventType(),
                e.getCreatedAt(),
                e.getMetadata()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertEventEntity toEntity(CertEvent d) {
        CertEventEntity e = new CertEventEntity();

        e.setServiceId(d.getServiceId());
        e.setKid(d.getKid());
        e.setEventType(d.getEventType());
        e.setCreatedAt(d.getCreatedAt());
        e.setMetadata(d.getMetadata());

        return e;
    }
}