package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.ServiceTrust;
import vn.xime.trust.infrastructure.persistence.entity.ServiceTrustEntity;

public class ServiceTrustMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static ServiceTrust toDomain(ServiceTrustEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("ServiceTrustEntity must not be null");
        }

        requireNonNull(e.getSignerServiceId(), "signerServiceId");
        requireNonNull(e.getVerifierServiceId(), "verifierServiceId");
        requireNonNull(e.getKeyLifetimeSeconds(), "keyLifetimeSeconds");
        requireNonNull(e.getJwtTtlSeconds(), "jwtTtlSeconds");
        requireNonNull(e.getPreloadSeconds(), "preloadSeconds");
        requireNonNull(e.getAutoRotate(), "autoRotate");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new ServiceTrust(
                e.getSignerServiceId(),
                e.getVerifierServiceId(),
                e.getKeyLifetimeSeconds(),
                e.getJwtTtlSeconds(),
                e.getPreloadSeconds(),
                e.getAutoRotate(),
                e.getLastRotatedAt(),
                e.getNextRotationAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static ServiceTrustEntity toEntity(ServiceTrust d) {

        if (d == null) {
            throw new IllegalArgumentException("ServiceTrust must not be null");
        }

        ServiceTrustEntity e = new ServiceTrustEntity();

        e.setSignerServiceId(d.getSignerServiceId());
        e.setVerifierServiceId(d.getVerifierServiceId());
        e.setKeyLifetimeSeconds(d.getKeyLifetimeSeconds());
        e.setJwtTtlSeconds(d.getJwtTtlSeconds());
        e.setPreloadSeconds(d.getPreloadSeconds());
        e.setAutoRotate(d.isAutoRotate());
        e.setLastRotatedAt(d.getLastRotatedAt());
        e.setNextRotationAt(d.getNextRotationAt());
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