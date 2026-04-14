package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

public class CertRefreshTokenMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static CertRefreshToken toDomain(CertRefreshTokenEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("CertRefreshTokenEntity must not be null");
        }

        requireNonNull(e.getServiceId(), "serviceId");
        requireNonNull(e.getTokenHash(), "tokenHash");
        requireNonNull(e.getBoundKid(), "boundKid");
        requireNonNull(e.getIssuedAt(), "issuedAt");
        requireNonNull(e.getExpiresAt(), "expiresAt");

        return new CertRefreshToken(
                e.getServiceId(),
                e.getTokenHash(),
                e.getBoundKid(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                e.getUsedAt(),      // nullable OK
                e.getIssuedBy()     // nullable OK
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertRefreshTokenEntity toEntity(CertRefreshToken d) {

        if (d == null) {
            throw new IllegalArgumentException("CertRefreshToken must not be null");
        }

        CertRefreshTokenEntity e = new CertRefreshTokenEntity();

        e.setServiceId(d.getServiceId());
        e.setTokenHash(d.getTokenHash());
        e.setBoundKid(d.getBoundKid());
        e.setIssuedAt(d.getIssuedAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setUsedAt(d.getUsedAt());
        e.setIssuedBy(d.getIssuedBy());

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