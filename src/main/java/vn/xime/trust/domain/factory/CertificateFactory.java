package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;

public class CertificateFactory {

    public Certificate create(
            String serviceId,
            String publicCert,
            String privateKeyEncrypted,
            Instant expiresAt
    ) {
        // =========================
        // VALIDATE
        // =========================

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (publicCert == null || publicCert.isBlank()) {
            throw new IllegalArgumentException("publicCert is required");
        }

        if (privateKeyEncrypted == null || privateKeyEncrypted.isBlank()) {
            throw new IllegalArgumentException("privateKeyEncrypted is required");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();
        Instant now = Instant.now();

        if (expiresAt.isBefore(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        return new Certificate(
                id,
                serviceId,
                publicCert,
                privateKeyEncrypted,
                now,               // issuedAt
                expiresAt,
                CertificateStatus.ACTIVE
        );
    }
}