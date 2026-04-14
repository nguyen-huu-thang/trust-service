package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KeyDomainService {

    // key dùng để SIGN
    public Optional<Key> findSigningKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.canSign(now))
                .max(Comparator.comparing(Key::getActivateAt));
    }

    // key tiếp theo (preload)
    public Optional<Key> findNextKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.getActivateAt().isAfter(now))
                .min(Comparator.comparing(Key::getActivateAt));
    }

    // tất cả key có thể VERIFY
    public List<Key> findVerifyKeys(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.canVerify(now))
                .toList();
    }

    public List<Key> filterNotDeleted(List<Key> keys) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .toList();
    }
}