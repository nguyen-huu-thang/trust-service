package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

public class CertRefreshTokenMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static CertRefreshToken toDomain(CertRefreshToken e) {
        return new CertRefreshToken(
                e.getServiceId(),
                e.getTokenHash(),
                e.getBoundKid(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                e.getUsedAt(),
                e.getIssuedBy()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertRefreshTokenEntity toEntity(CertRefreshToken d) {
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
}