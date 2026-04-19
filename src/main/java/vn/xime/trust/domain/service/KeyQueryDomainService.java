package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Key;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KeyQueryDomainService {

    public Optional<Key> findSigningKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .filter(k -> k.canSign(now))
                .max(Comparator.comparing(Key::getActivateAt));
    }

    public Optional<Key> findNextKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .filter(k -> k.getActivateAt().isAfter(now))
                .min(Comparator.comparing(Key::getActivateAt));
    }

    public List<Key> findVerifyKeys(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> !k.isDeleted())
                .filter(k -> k.canVerify(now))
                .toList();
    }
}