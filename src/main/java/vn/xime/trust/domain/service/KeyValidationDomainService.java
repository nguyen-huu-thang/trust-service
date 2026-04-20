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

        if (!newExpiresAt.isAfter(newActivateAt)) {
            throw new IllegalArgumentException("expiresAt must be after activateAt");
        }

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
        // FIND PREV / NEXT
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
        // PRELOAD RULE
        // =========================

        Instant minActivate = now.plusSeconds(policy.getPreloadSeconds());

        if (newActivateAt.isBefore(minActivate)) {
            throw new IllegalStateException(
                    "Key must respect preload window"
            );
        }

        // =========================
        // VERIFY GUARANTEE (CRITICAL)
        // =========================

        // previous key must still verify JWT issued before rotation
        if (prev != null) {
            Instant minRequiredExpire = newActivateAt.plusSeconds(policy.getJwtTtlSeconds());

            if (prev.getExpiresAt().isBefore(minRequiredExpire)) {
                throw new IllegalStateException(
                        "Previous key expires too early → JWT verification may fail"
                );
            }
        }

        // =========================
        // FUTURE COLLISION
        // =========================

        if (next != null) {

            // new key must activate before next key
            if (!newActivateAt.isBefore(next.getActivateAt())) {
                throw new IllegalStateException(
                        "New key conflicts with next key activation"
                );
            }

            // new key must not break next key verify window
            Instant minExpireForNext = next.getActivateAt().plusSeconds(policy.getJwtTtlSeconds());

            if (newExpiresAt.isBefore(minExpireForNext)) {
                throw new IllegalStateException(
                        "New key expires too early → next key verification may fail"
                );
            }
        }

        // =========================
        // OPTIONAL: STRICT MODE
        // =========================
        // Nếu bạn muốn KHÔNG cho insert giữa, bật lại:
        //
        // if (prev != null && next != null) {
        //     throw new IllegalStateException("Cannot insert key between existing keys");
        // }
    }
}