package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

public class CertificateMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Certificate toDomain(Certificate e) {
        return new Certificate(
                e.getKid(),
                e.getServiceId(),
                e.getPublicCert(),
                e.getPrivateKeyEncrypted(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                CertificateStatus.valueOf(e.getStatus())
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertificateEntity toEntity(Certificate d) {
        CertificateEntity e = new CertificateEntity();

        e.setKid(d.getKid());
        e.setServiceId(d.getServiceId());
        e.setPublicCert(d.getPublicCert());
        e.setPrivateKeyEncrypted(d.getPrivateKeyEncrypted());
        e.setIssuedAt(d.getIssuedAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setStatus(d.getStatus().name());

        return e;
    }
}