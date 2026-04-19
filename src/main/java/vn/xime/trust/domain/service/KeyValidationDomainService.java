package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyPolicy;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class KeyValidationDomainService {

    public void validateNewKey(
            List<Key> existingKeys,
            Instant newActivateAt,
            Instant newExpiresAt,
            KeyPolicy policy,
            Instant now
    ) {

        // =========================
        // BASIC
        // =========================

        if (newActivateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }

        if (newExpiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        if (newExpiresAt.isBefore(newActivateAt)) {
            throw new IllegalArgumentException("expiresAt must be after activateAt");
        }

        // =========================
        // ACTIVATE TIME VALIDATION
        // =========================

        if (newActivateAt.isBefore(now.minusSeconds(5))) {
            throw new IllegalArgumentException("activateAt cannot be in the past");
        }

        // =========================
        // FILTER VALID KEYS
        // =========================

        List<Key> keys = existingKeys.stream()
                .filter(k -> !k.isDeleted())
                .sorted(Comparator.comparing(Key::getActivateAt))
                .toList();

        if (keys.isEmpty()) {
            return; // first key → always valid
        }

        // =========================
        // DUPLICATE ACTIVATE_AT
        // =========================

        boolean duplicate = keys.stream()
                .anyMatch(k -> k.getActivateAt().equals(newActivateAt));

        if (duplicate) {
            throw new IllegalStateException("Duplicate activateAt for key");
        }

        // =========================
        // FIND NEIGHBORS
        // =========================

        Key prev = null;
        Key next = null;

        for (Key k : keys) {
            if (k.getActivateAt().isBefore(newActivateAt)) {
                prev = k;
            } else if (k.getActivateAt().isAfter(newActivateAt)) {
                next = k;
                break;
            }
        }

        // =========================
        // ORDERING (NO BACK INSERT)
        // =========================

        if (next != null && prev != null) {
            // new key is inserted between → dangerous
            throw new IllegalStateException("Cannot insert key between existing keys");
        }

        // =========================
        // VERIFY GUARANTEE (CRITICAL)
        // =========================

        if (prev != null) {
            Instant minRequiredExpire = newActivateAt.plusSeconds(policy.getJwtTtlSeconds());

            if (prev.getExpiresAt().isBefore(minRequiredExpire)) {
                throw new IllegalStateException(
                        "Previous key expires too early → JWT verification may fail"
                );
            }
        }

        // =========================
        // PRELOAD RULE (RECOMMENDED)
        // =========================

        if (prev != null) {
            Instant minActivate = now.plusSeconds(policy.getPreloadSeconds());

            if (newActivateAt.isBefore(minActivate)) {
                throw new IllegalStateException(
                        "Key must be scheduled before preload window"
                );
            }
        }

        // =========================
        // FUTURE COLLISION
        // =========================

        if (next != null) {
            if (!newActivateAt.isBefore(next.getActivateAt())) {
                throw new IllegalStateException(
                        "New key conflicts with next key activation"
                );
            }
        }
    }
}