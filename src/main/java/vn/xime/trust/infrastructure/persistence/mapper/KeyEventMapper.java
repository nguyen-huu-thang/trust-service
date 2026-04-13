package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.event.KeyEvent;
import vn.xime.trust.infrastructure.persistence.entity.KeyEventEntity;

public class KeyEventMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyEvent toDomain(KeyEvent e) {
        return new KeyEvent(
                e.getKid(),
                e.getServiceId(),
                e.getEventType(),
                e.getCreatedAt(),
                e.getMetadata()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyEventEntity toEntity(KeyEvent d) {
        KeyEventEntity e = new KeyEventEntity();

        e.setKid(d.getKid());
        e.setServiceId(d.getServiceId());
        e.setEventType(d.getEventType());
        e.setCreatedAt(d.getCreatedAt());
        e.setMetadata(d.getMetadata());

        return e;
    }
}