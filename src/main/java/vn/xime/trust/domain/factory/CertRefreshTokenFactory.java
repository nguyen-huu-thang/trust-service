package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;

public class CertRefreshTokenFactory {

    public CertRefreshToken create(
        String serviceId,
        String shardId,
        boolean isBootstrap,
        Instant expiresAt
    ) {
        // =========================
        // VALIDATE
        // =========================

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
                null,
                serviceId,
                shardId,
                isBootstrap,
                now,        // issuedAt
                expiresAt,
                null,       // usedAt
                false       // isDeleted
        );
    }
}