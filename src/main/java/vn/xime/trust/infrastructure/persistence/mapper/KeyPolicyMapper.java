package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

public class KeyPolicyMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static KeyPolicy toDomain(KeyPolicyEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("KeyPolicyEntity must not be null");
        }

        requireNonNull(e.getServiceId(), "serviceId");
        requireNonNull(e.getKeyLifetimeSeconds(), "keyLifetimeSeconds");
        requireNonNull(e.getJwtTtlSeconds(), "jwtTtlSeconds");
        requireNonNull(e.getPreloadSeconds(), "preloadSeconds");

        return new KeyPolicy(
                e.getServiceId(),
                e.getKeyLifetimeSeconds(),
                e.getJwtTtlSeconds(),
                e.getPreloadSeconds(),
                e.getCreatedAt(),   // nullable OK
                e.getUpdatedAt()    // nullable OK
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static KeyPolicyEntity toEntity(KeyPolicy d) {

        if (d == null) {
            throw new IllegalArgumentException("KeyPolicy must not be null");
        }

        KeyPolicyEntity e = new KeyPolicyEntity();

        e.setServiceId(d.getServiceId());
        e.setKeyLifetimeSeconds(d.getKeyLifetimeSeconds());
        e.setJwtTtlSeconds(d.getJwtTtlSeconds());
        e.setPreloadSeconds(d.getPreloadSeconds());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());

        return e;
    }

    // =========================
    // Helpers
    // =========================

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}