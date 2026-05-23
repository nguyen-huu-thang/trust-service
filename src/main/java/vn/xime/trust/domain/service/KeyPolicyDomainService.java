package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;

import java.time.Instant;

public class KeyPolicyDomainService {

    // =========================
    // DEFAULT CONSTANTS
    // =========================

    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long THREE_MONTHS = 90L * ONE_DAY;

    // =========================
    // RESULT OBJECT (RESOLVED PARAMS)
    // =========================

    public static class ResolvedPolicyParams {
        public final KeyAlgorithm algorithm;
        public final int keySize;
        public final long keyLifetime;
        public final long rotationInterval;
        public final long preload;

        public ResolvedPolicyParams(
                KeyAlgorithm algorithm,
                int keySize,
                long keyLifetime,
                long rotationInterval,
                long preload
        ) {
            this.algorithm = algorithm;
            this.keySize = keySize;
            this.keyLifetime = keyLifetime;
            this.rotationInterval = rotationInterval;
            this.preload = preload;
        }
    }

    // =========================
    // MAIN ENTRY: RESOLVE + VALIDATE
    // =========================

    public ResolvedPolicyParams resolveAndValidate(
            String signerServiceId,
            String verifierServiceId,
            String rawAlgorithm,
            int keySize,
            Long keyLifetime,
            Long rotationInterval,
            Long preload
    ) {

        // =========================
        // BASIC DOMAIN VALIDATION
        // =========================

        if (signerServiceId.equals(verifierServiceId)) {
            throw new IllegalStateException("signer and verifier must be different");
        }

        if (keySize <= 0) {
            throw new IllegalStateException("keySize must be > 0");
        }

        KeyAlgorithm algorithm = parseAlgorithm(rawAlgorithm);

        long finalRotation;
        long finalPreload;
        long finalLifetime;

        // =========================
        // INVALID CASE
        // =========================

        if (keyLifetime != null && rotationInterval == null) {
            throw new IllegalStateException(
                    "rotationInterval is required when keyLifetime is provided"
            );
        }

        // =========================
        // CASE 1: NO INPUT
        // =========================

        if (rotationInterval == null && keyLifetime == null && preload == null) {

            finalRotation = THREE_MONTHS;
            finalPreload = ONE_HOUR;
            finalLifetime = finalRotation * 2;
        }

        // =========================
        // CASE 2: ONLY ROTATION
        // =========================

        else if (rotationInterval != null && keyLifetime == null) {

            if (rotationInterval < ONE_DAY) {
                throw new IllegalStateException(
                        "rotationInterval must be >= 1 day"
                );
            }

            finalRotation = rotationInterval;
            finalPreload = ONE_HOUR;
            finalLifetime = finalRotation * 2;
        }

        // =========================
        // CASE 3: FULL / PARTIAL INPUT
        // =========================

        else {

            if (rotationInterval == null) {
                throw new IllegalStateException("rotationInterval is required");
            }

            if (rotationInterval <= 0) {
                throw new IllegalStateException("rotationInterval must be > 0");
            }

            finalRotation = rotationInterval;

            finalPreload = (preload != null) ? preload : ONE_HOUR;

            if (finalPreload < 0) {
                throw new IllegalStateException("preload must be >= 0");
            }

            if (keyLifetime == null) {
                throw new IllegalStateException("keyLifetime is required");
            }

            if (keyLifetime <= 0) {
                throw new IllegalStateException("keyLifetime must be > 0");
            }

            finalLifetime = keyLifetime;
        }

        // =========================
        // FINAL DOMAIN RULE 🔥
        // =========================

        if (finalLifetime < finalRotation + finalPreload) {
            throw new IllegalStateException(
                    "keyLifetime must be >= rotationInterval + preload"
            );
        }

        return new ResolvedPolicyParams(
                algorithm,
                keySize,
                finalLifetime,
                finalRotation,
                finalPreload
        );
    }

    // =========================
    // POLICY VALIDATION (EXISTING POLICY)
    // =========================

    public void validatePolicy(KeyPolicy policy) {

        if (policy.getKeyLifetimeSeconds() <= 0) {
            throw new IllegalStateException("key_lifetime must be > 0");
        }

        if (policy.getRotationIntervalSeconds() <= 0) {
            throw new IllegalStateException("rotation_interval must be > 0");
        }

        if (policy.getPreloadSeconds() < 0) {
            throw new IllegalStateException("preload must be >= 0");
        }

        if (policy.getKeyLifetimeSeconds() < policy.getRotationIntervalSeconds()) {
            throw new IllegalStateException(
                    "key_lifetime must be >= rotation_interval"
            );
        }
    }

    // =========================
    // ACTIVATE TIME VALIDATION
    // =========================

    public void validateActivateAt(
            Instant activateAt,
            Instant now
    ) {

        if (activateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }

        if (activateAt.isBefore(now)) {
            throw new IllegalArgumentException("activateAt must be in the future");
        }
    }

    // =========================
    // TIMELINE CALCULATION
    // =========================

    public Instant calculateNextActivateAt(
            Instant lastActivateAt,
            KeyPolicy policy
    ) {

        if (lastActivateAt == null) {
            throw new IllegalArgumentException("lastActivateAt is required");
        }

        return lastActivateAt.plusSeconds(
                policy.getRotationIntervalSeconds()
        );
    }

    // =========================
    // EXPIRES TIME
    // =========================

    public Instant calculateExpiresAt(
            Instant activateAt,
            KeyPolicy policy
    ) {

        if (activateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }

        return activateAt.plusSeconds(
                policy.getKeyLifetimeSeconds()
        );
    }

    // =========================
    // ALGORITHM PARSING
    // =========================

    public KeyAlgorithm parseAlgorithm(String raw) {
        try {
            return KeyAlgorithm.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid algorithm: " + raw);
        }
    }
}