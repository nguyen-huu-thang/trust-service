package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.KeyPolicy;

import java.time.Instant;

public class KeyPolicyDomainService {

    // =========================
    // POLICY VALIDATION
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

        // 🔥 CRITICAL RULE
        if (policy.getKeyLifetimeSeconds()
                < policy.getRotationIntervalSeconds() + policy.getPreloadSeconds()) {

            throw new IllegalStateException(
                    "Invalid policy: key_lifetime must be >= rotation_interval + preload"
            );
        }
    }

    // =========================
    // ACTIVATE TIME
    // =========================

    public Instant resolveActivateAt(
            Instant requestedActivateAt,
            KeyPolicy policy,
            Instant now
    ) {

        if (requestedActivateAt != null) {
            return requestedActivateAt;
        }

        // default = preload
        return now.plusSeconds(policy.getPreloadSeconds());
    }

    // =========================
    // VALIDATE ACTIVATE TIME
    // =========================

    public void validateActivateAt(
            Instant activateAt,
            KeyPolicy policy,
            Instant now
    ) {

        if (activateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }

        if (!activateAt.isAfter(now)) {
            throw new IllegalArgumentException("activateAt must be in the future");
        }

        Instant minActivate = now.plusSeconds(policy.getPreloadSeconds());

        if (activateAt.isBefore(minActivate)) {
            throw new IllegalArgumentException(
                    "activateAt must be >= now + preload_seconds"
            );
        }
    }

    // =========================
    // EXPIRES TIME
    // =========================

    public Instant calculateExpiresAt(
            Instant activateAt,
            KeyPolicy policy
    ) {
        return activateAt.plusSeconds(policy.getKeyLifetimeSeconds());
    }
}