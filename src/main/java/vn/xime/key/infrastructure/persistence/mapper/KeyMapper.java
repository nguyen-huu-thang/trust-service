package vn.xime.key.infrastructure.persistence.mapper;

import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyAlgorithm;
import vn.xime.key.domain.key.KeyStatus;
import vn.xime.key.infrastructure.persistence.entity.KeyEntity;

public class KeyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Key toDomain(KeyEntity entity) {
        if (entity == null) return null;

        return new Key(
                entity.getKid(),
                entity.getServiceName(),
                entity.getPublicKey(),
                entity.getPrivateKeyEncrypted(),
                KeyAlgorithm.valueOf(entity.getAlgorithm()),
                entity.getKeySize(),
                KeyStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getActivateAt(),
                entity.getExpiresAt(),
                entity.isDeleted()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyEntity toEntity(Key domain) {
        if (domain == null) return null;

        KeyEntity entity = new KeyEntity();

        entity.setKid(domain.getKid());
        entity.setServiceName(domain.getServiceName());

        entity.setPublicKey(domain.getPublicKey());
        entity.setPrivateKeyEncrypted(domain.getPrivateKeyEncrypted());

        entity.setAlgorithm(domain.getAlgorithm().name());
        entity.setKeySize(domain.getKeySize());

        entity.setStatus(domain.getStatus().name());

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setActivateAt(domain.getActivateAt());
        entity.setExpiresAt(domain.getExpiresAt());

        entity.setDeleted(domain.isDeleted());

        return entity;
    }
}