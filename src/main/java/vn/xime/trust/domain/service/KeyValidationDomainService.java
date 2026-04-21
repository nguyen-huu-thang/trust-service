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
        // BASIC VALIDATION
        // =========================

        if (newActivateAt == null) {
            throw new IllegalArgumentException("activateAt is required");
        }

        if (newExpiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        if (!newExpiresAt.isAfter(newActivateAt)) {
            throw new IllegalArgumentException("expiresAt must be after activateAt");
        }

        // small clock skew tolerance
        if (newActivateAt.isBefore(now.minusSeconds(5))) {
            throw new IllegalArgumentException("activateAt cannot be in the past");
        }

        // =========================
        // FILTER + SORT
        // =========================

        List<Key> keys = existingKeys.stream()
                .filter(k -> !k.isDeleted())
                .sorted(Comparator.comparing(Key::getActivateAt))
                .toList();

        if (keys.isEmpty()) {
            return; // first key always valid
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
        // APPEND-ONLY RULE
        // =========================

        // Key mới phải luôn nằm SAU key cuối cùng
        Key last = keys.get(keys.size() - 1);

        if (!newActivateAt.isAfter(last.getActivateAt())) {
            throw new IllegalStateException(
                    "New key must be after the last key"
            );
        }

        // =========================
        // ROTATION INTERVAL RULE
        // =========================

        if (newExpiresAt.isBefore(newActivateAt.plusSeconds(policy.getRotationIntervalSeconds()))) {
            throw new IllegalStateException(
                    "New key's expiresAt cannot be before its activateAt plus rotation interval"
            );
        }
    }
}