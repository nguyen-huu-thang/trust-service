package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;

import java.time.Instant;

public class KeyFactory {

    public Key create(
        String kid,
        String signerServiceId,
        String verifierServiceId,
        String publicKey,
        String privateKeyEncrypted,
        KeyAlgorithm algorithm,
        int keySize,
        Instant activateAt,
        Instant expiresAt
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (kid == null || kid.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }
        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }
        if (publicKey == null || publicKey.isBlank()) {
            throw new IllegalArgumentException("publicKey is required");
        }
        if (privateKeyEncrypted == null || privateKeyEncrypted.isBlank()) {
            throw new IllegalArgumentException("privateKeyEncrypted is required");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm is required");
        }
        if (keySize <= 0) {
            throw new IllegalArgumentException("keySize must be greater than 0");
        }
        if (activateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }
        if (expiresAt.isBefore(activateAt)) {
            throw new IllegalArgumentException("expiresAt must be after activateAt");
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        return new Key(
                kid,
                signerServiceId,
                verifierServiceId,
                publicKey,
                privateKeyEncrypted,
                algorithm,
                keySize,
                Instant.now(),
                activateAt,
                expiresAt,
                false
        );
    }
}