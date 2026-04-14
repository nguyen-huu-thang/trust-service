package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

public class CertificateMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Certificate toDomain(CertificateEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("CertificateEntity must not be null");
        }

        requireNonNull(e.getKid(), "kid");
        requireNonNull(e.getServiceId(), "serviceId");
        requireNonNull(e.getPublicCert(), "publicCert");
        requireNonNull(e.getPrivateKeyEncrypted(), "privateKeyEncrypted");
        requireNonNull(e.getIssuedAt(), "issuedAt");
        requireNonNull(e.getExpiresAt(), "expiresAt");
        requireNonNull(e.getStatus(), "status");

        return new Certificate(
                e.getKid(),
                e.getServiceId(),
                e.getPublicCert(),
                e.getPrivateKeyEncrypted(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                mapStatus(e.getStatus())
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static CertificateEntity toEntity(Certificate d) {

        if (d == null) {
            throw new IllegalArgumentException("Certificate must not be null");
        }

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

    // =========================
    // Helpers
    // =========================

    private static CertificateStatus mapStatus(String status) {
        try {
            return CertificateStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid certificate status: " + status);
        }
    }

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}