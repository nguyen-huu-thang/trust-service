package vn.xime.trust.application.service.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.port.in.CleanupExpiredKeys;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CleanupExpiredKeysImpl implements CleanupExpiredKeys {

    private final KeyRepository keyRepository;
    private final KeyLifecycleDomainService lifecycle;

    public CleanupExpiredKeysImpl(
            KeyRepository keyRepository,
            KeyLifecycleDomainService lifecycle
    ) {
        this.keyRepository = keyRepository;
        this.lifecycle = lifecycle;
    }

    @Override
    @Transactional
    public void execute() {

        Instant now = Instant.now();

        // =========================
        // 1. SOFT DELETE PHASE
        // =========================

        List<Key> activeKeys = keyRepository.findAllNotDeleted();

        List<Key> toSoftDelete = new ArrayList<>();

        for (Key key : activeKeys) {
            if (lifecycle.shouldBeDeleted(key, now)) {
                toSoftDelete.add(key.markDeleted());
            }
        }

        for (Key key : toSoftDelete) {
            keyRepository.save(key);
        }

        // =========================
        // 2. HARD DELETE PHASE
        // =========================

        List<Key> deletedKeys = keyRepository.findAllDeleted();

        List<Key> toHardDelete = new ArrayList<>();

        for (Key key : deletedKeys) {
            if (lifecycle.shouldBeHardDeleted(key, now)) {
                toHardDelete.add(key);
            }
        }

        if (!toHardDelete.isEmpty()) {
            keyRepository.deleteAllByIds(
                    toHardDelete.stream()
                            .map(Key::getId)
                            .toList()
            );
        }
    }
}