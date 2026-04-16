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

        requireNonNull(e.getSignerServiceId(), "signerServiceId");
        requireNonNull(e.getVerifierServiceId(), "verifierServiceId");
        requireNonNull(e.getKeyLifetimeSeconds(), "keyLifetimeSeconds");
        requireNonNull(e.getJwtTtlSeconds(), "jwtTtlSeconds");
        requireNonNull(e.getPreloadSeconds(), "preloadSeconds");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new KeyPolicy(
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                e.getKeyLifetimeSeconds(),
                e.getJwtTtlSeconds(),
                e.getPreloadSeconds(),
                e.getCreatedAt(),
                e.getUpdatedAt()
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

        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());
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