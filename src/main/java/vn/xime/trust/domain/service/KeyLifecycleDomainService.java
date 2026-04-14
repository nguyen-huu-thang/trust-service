package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class KeyLifecycleDomainService {

    // =========================
    // VERIFY
    // =========================

    public List<Key> getKeysForVerify(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.canVerify(now))
                .toList();
    }

    // =========================
    // SIGN
    // =========================

    public Key getKeyForSign(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.canSign(now))
                .max(Comparator.comparing(Key::getActivateAt))
                .orElseThrow(() ->
                        new IllegalStateException("No active key for signing")
                );
    }

    // =========================
    // NEXT (PRELOAD)
    // =========================

    public Key getNextKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.getActivateAt().isAfter(now))
                .min(Comparator.comparing(Key::getActivateAt))
                .orElse(null);
    }

    // =========================
    // CLEANUP RULE
    // =========================

    public boolean shouldBeDeleted(Key key, Instant now) {
        return key.isExpiredAt(now);
    }

    // =========================
    // COMMON FILTER
    // =========================

    public List<Key> filterNotDeleted(List<Key> keys) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .toList();
    }
}