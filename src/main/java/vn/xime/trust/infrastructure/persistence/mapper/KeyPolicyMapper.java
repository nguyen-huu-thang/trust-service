package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.policy.KeyPolicy;
import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

public class KeyPolicyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyPolicy toDomain(KeyPolicyEntity e) {
        return new KeyPolicy(
                e.getKeyLifetimeSeconds(),
                e.getJwtTtlSeconds(),
                e.getPreloadSeconds()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyPolicyEntity toEntity(KeyPolicy d, String serviceId) {
        KeyPolicyEntity e = new KeyPolicyEntity();

        e.setServiceId(serviceId);
        e.setKeyLifetimeSeconds(d.getKeyLifetimeSeconds());
        e.setJwtTtlSeconds(d.getJwtTtlSeconds());
        e.setPreloadSeconds(d.getPreloadSeconds());

        return e;
    }
}