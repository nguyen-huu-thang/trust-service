package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

import java.util.Arrays;

public class CertificateMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Certificate toDomain(CertificateEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("CertificateEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getServiceId(), "serviceId");
        requireNonNull(e.getPublicCert(), "publicCert");
        requireNonNull(e.getPrivateKeyEncrypted(), "privateKeyEncrypted");
        requireNonNull(e.getIssuedAt(), "issuedAt");
        requireNonNull(e.getExpiresAt(), "expiresAt");
        requireNonNull(e.getStatus(), "status");

        return new Certificate(
                toId(e.getId()),
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

        e.setId(toBytes(d.getId()));
        e.setServiceId(d.getServiceId());
        e.setPublicCert(d.getPublicCert());
        e.setPrivateKeyEncrypted(d.getPrivateKeyEncrypted());
        e.setIssuedAt(d.getIssuedAt());
        e.setExpiresAt(d.getExpiresAt());
        e.setStatus(d.getStatus().name());

        return e;
    }

    // =========================
    // ID mapping
    // =========================

    private static Id toId(byte[] bytes) {
        return new Id(copy(bytes));
    }

    private static byte[] toBytes(Id id) {
        return copy(id.toBytes());
    }

    private static byte[] copy(byte[] src) {
        return src == null ? null : Arrays.copyOf(src, src.length);
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