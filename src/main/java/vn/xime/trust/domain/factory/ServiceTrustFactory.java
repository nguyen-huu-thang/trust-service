package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.ServiceTrust;

import java.time.Instant;

public class ServiceTrustFactory {

    public ServiceTrust create(
        String signerServiceId,
        String verifierServiceId,
        long keyLifetimeSeconds,
        long jwtTtlSeconds,
        long preloadSeconds

    ) {


        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================


        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }
        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }
        if (signerServiceId.equals(verifierServiceId)) {
            throw new IllegalArgumentException("signer and verifier must be different");
        }
        if (keyLifetimeSeconds <= 0) {
            throw new IllegalArgumentException("keyLifetime must be greater than 0");
        }
        if (jwtTtlSeconds <= 0) {
            throw new IllegalArgumentException("jwtTtl must be greater than 0");
        }
        if (preloadSeconds < 0) {
            throw new IllegalArgumentException("preloadSeconds must be greater than or equal to 0");
        }


        return new ServiceTrust(
        signerServiceId,
        verifierServiceId,
        keyLifetimeSeconds,
        jwtTtlSeconds,
        preloadSeconds,
        true,
        null,
        null,
        Instant.now(),
        null
        );
    }
}