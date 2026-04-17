package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;

public class CertRefreshTokenFactory {

    public CertRefreshToken create(
            String serviceId,
            String tokenHash,
            Id boundCertId,
            Instant expiresAt,
            String issuedBy
    ) {
        // =========================
        // VALIDATE
        // =========================

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash is required");
        }

        if (boundCertId == null) {
            throw new IllegalArgumentException("boundCertId is required");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        Instant now = Instant.now();

        if (expiresAt.isBefore(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();

        return new CertRefreshToken(
                id,
                serviceId,
                tokenHash,
                boundCertId,
                now,        // issuedAt
                expiresAt,
                null,       // usedAt
                issuedBy
        );
    }
}