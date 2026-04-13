package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

public class KeyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Key toDomain(KeyEntity e) {
        return new Key(
                e.getKid(),
                e.getServiceId(),
                e.getPublicKey(),
                e.getPrivateKeyEncrypted(),
                KeyAlgorithm.valueOf(e.getAlgorithm()),
                e.getKeySize(),
                e.getCreatedAt(),
                e.getActivateAt(),
                e.getExpiresAt(),
                Boolean.TRUE.equals(e.getIsDeleted())
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyEntity toEntity(Key d) {
        KeyEntity e = new KeyEntity();

        e.setKid(d.getKid());
        e.setServiceId(d.getServiceId());
        e.setPublicKey(d.getPublicKey());
        e.setPrivateKeyEncrypted(d.getPrivateKeyEncrypted());
        e.setAlgorithm(d.getAlgorithm().name());
        e.setKeySize(d.getKeySize());
        e.setCreatedAt(d.getCreatedAt());
        e.setActivateAt(d.getActivateAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setIsDeleted(d.isDeleted());

        return e;
    }
}