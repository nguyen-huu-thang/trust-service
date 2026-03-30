package vn.xime.key.domain.key.service;

import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyRepository;
import vn.xime.key.domain.key.KeyStatus;

import java.time.Instant;
import java.util.Optional;

public class KeyLifecycleManager {

    private final KeyRepository keyRepository;

    public KeyLifecycleManager(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    /**
     * Lấy current key (đơn giản cho MVP)
     */
    public Key getCurrentKey(String serviceName) {
        return keyRepository.findCurrent(serviceName)
                .orElseThrow(() -> new IllegalStateException(
                        "No CURRENT key found for service: " + serviceName
                ));
    }

    /**
     * Promote NEXT -> CURRENT (future dùng)
     */
    public void promoteNext(String serviceName) {
        Optional<Key> currentOpt = keyRepository.findCurrent(serviceName);
        Optional<Key> nextOpt = keyRepository.findNext(serviceName);

        if (currentOpt.isEmpty() || nextOpt.isEmpty()) {
            throw new IllegalStateException("Cannot promote key");
        }

        Key current = currentOpt.get();
        Key next = nextOpt.get();

        current.markAsOld();
        next.markAsCurrent();

        keyRepository.save(current);
        keyRepository.save(next);
    }

    /**
     * Check active key theo thời gian (future)
     */
    public boolean shouldActivate(Key key, Instant now) {
        return key.getActivateAt() != null && !now.isBefore(key.getActivateAt());
    }
}