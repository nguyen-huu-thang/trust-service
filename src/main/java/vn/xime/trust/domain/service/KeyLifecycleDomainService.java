package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KeyLifecycleDomainService {

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
    // CLEANUP RULE
    // =========================

    public boolean shouldBeDeleted(Key key, Instant now) {
        return key.isExpiredAt(now);
    }

    // =========================
    // OPTIONAL HELPERS
    // =========================

    public List<Key> getAllActive(List<Key> keys) {
        return filterActive(keys);
    }
}