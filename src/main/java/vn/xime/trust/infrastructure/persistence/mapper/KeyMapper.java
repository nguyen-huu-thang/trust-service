package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.key.Key;
import vn.xime.trust.domain.key.KeyAlgorithm;
import vn.xime.trust.domain.key.KeyStatus;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

public class KeyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Key toDomain(KeyEntity entity) {
        if (entity == null) return null;

        KeyAlgorithm algorithm = entity.getAlgorithm() != null
                ? KeyAlgorithm.valueOf(entity.getAlgorithm())
                : null;

        KeyStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = KeyStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException ignored) {
                // fallback: ignore invalid status
            }
        }

        return new Key(
                entity.getKid(),
                entity.getServiceName(),
                entity.getPublicKey(),
                entity.getPrivateKeyEncrypted(),
                algorithm,
                entity.getKeySize(),
                status,
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

        entity.setAlgorithm(domain.getAlgorithm() != null
                ? domain.getAlgorithm().name()
                : null);

        entity.setKeySize(domain.getKeySize());

        entity.setStatus(domain.getStatus() != null
                ? domain.getStatus().name()
                : null);

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setActivateAt(domain.getActivateAt());
        entity.setExpiresAt(domain.getExpiresAt());

        entity.setDeleted(domain.isDeleted());

        return entity;
    }
}