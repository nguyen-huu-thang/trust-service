package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;

import java.time.Instant;

public class KeyPolicyFactory {

    public KeyPolicy create(
            String signerServiceId,
            String verifierServiceId,
            KeyAlgorithm algorithm,
            int keySize,
            long keyLifetimeSeconds,
            long jwtTtlSeconds,
            long preloadSeconds
    ) {

        // =========================
        // VALIDATE
        // =========================

        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm is required");
        }

        if (keySize <= 0) {
            throw new IllegalArgumentException("keySize must be > 0");
        }

        if (keyLifetimeSeconds <= 0) {
            throw new IllegalArgumentException("keyLifetimeSeconds must be > 0");
        }

        if (jwtTtlSeconds <= 0) {
            throw new IllegalArgumentException("jwtTtlSeconds must be > 0");
        }

        if (preloadSeconds < 0) {
            throw new IllegalArgumentException("preloadSeconds must be >= 0");
        }

        // 🔥 domain rule (quan trọng)
        if (keyLifetimeSeconds < jwtTtlSeconds + preloadSeconds) {
            throw new IllegalArgumentException(
                    "keyLifetime must be >= jwtTtl + preload"
            );
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();
        Instant now = Instant.now();

        return new KeyPolicy(
                id,
                signerServiceId,
                verifierServiceId,
                algorithm,
                keySize,
                keyLifetimeSeconds,
                jwtTtlSeconds,
                preloadSeconds,
                now,
                null
        );
    }
}