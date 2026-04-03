package vn.xime.key.infrastructure.persistence.mapper;

import vn.xime.key.domain.key.KeyPolicy;
import vn.xime.key.infrastructure.persistence.entity.KeyPolicyEntity;

public class KeyPolicyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyPolicy toDomain(KeyPolicyEntity entity) {
        if (entity == null) return null;

        return new KeyPolicy(
                entity.getServiceName(),
                entity.getKeyLifetimeSeconds(),
                entity.getJwtTtlSeconds(),
                entity.getPreloadSeconds(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyPolicyEntity toEntity(KeyPolicy domain) {
        if (domain == null) return null;

        KeyPolicyEntity entity = new KeyPolicyEntity();

        entity.setServiceName(domain.getServiceName());
        entity.setKeyLifetimeSeconds(domain.getKeyLifetimeSeconds());
        entity.setJwtTtlSeconds(domain.getJwtTtlSeconds());
        entity.setPreloadSeconds(domain.getPreloadSeconds());

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}