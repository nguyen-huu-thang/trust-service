package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KeyLifecycleDomainService {

    // 🔥 3 YEARS
    private static final long HARD_DELETE_RETENTION_SECONDS =
            60L * 60 * 24 * 365 * 3;

    // =========================
    // COMMON FILTER
    // =========================

    private List<Key> filterActive(List<Key> keys) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .toList();
    }

    // =========================
    // SIGN
    // =========================

    public Key getKeyForSign(List<Key> keys, Instant now) {
        return filterActive(keys).stream()
                .filter(k -> k.canSign(now))
                .max(Comparator.comparing(Key::getActivateAt))
                .orElseThrow(() ->
                        new IllegalStateException("No active key for signing")
                );
    }

    // =========================
    // OPTIONAL: SIGN (SAFE)
    // =========================

    public Optional<Key> findKeyForSign(List<Key> keys, Instant now) {
        return filterActive(keys).stream()
                .filter(k -> k.canSign(now))
                .max(Comparator.comparing(Key::getActivateAt));
    }

    // =========================
    // VERIFY
    // =========================

    public List<Key> getKeysForVerify(List<Key> keys, Instant now) {
        return filterActive(keys).stream()
                .filter(k -> k.canVerify(now))
                .toList();
    }

    // =========================
    // NEXT (PRELOAD)
    // =========================

    public Optional<Key> getNextKey(List<Key> keys, Instant now) {
        return filterActive(keys).stream()
                .filter(k -> k.getActivateAt().isAfter(now))
                .min(Comparator.comparing(Key::getActivateAt));
    }

    // =========================
    // SOFT DELETE RULE
    // =========================

    public boolean shouldBeDeleted(Key key, Instant now) {
        return key.isExpiredAt(now);
    }

    // =========================
    // HARD DELETE RULE (NEW)
    // =========================

    public boolean shouldBeHardDeleted(Key key, Instant now) {

        // chỉ xét key đã expired
        if (!key.isExpiredAt(now)) {
            return false;
        }

        // expires_at + 3 years
        return key.getExpiresAt()
                .plusSeconds(HARD_DELETE_RETENTION_SECONDS)
                .isBefore(now);
    }

    // =========================
    // OPTIONAL HELPERS
    // =========================

    public List<Key> getAllActive(List<Key> keys) {
        return filterActive(keys);
    }
}