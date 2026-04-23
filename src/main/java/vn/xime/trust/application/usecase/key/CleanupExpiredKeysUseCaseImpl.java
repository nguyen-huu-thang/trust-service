package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.port.in.CleanupExpiredKeysUseCase;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CleanupExpiredKeysUseCaseImpl implements CleanupExpiredKeysUseCase {

    private final KeyRepository keyRepository;
    private final KeyLifecycleDomainService keyLifecycleDomainService;

    public CleanupExpiredKeysUseCaseImpl(
            KeyRepository keyRepository,
            KeyLifecycleDomainService keyLifecycleDomainService
    ) {
        this.keyRepository = keyRepository;
        this.keyLifecycleDomainService = keyLifecycleDomainService;
    }

    @Override
    @Transactional
    public void execute() {

        Instant now = Instant.now();

        // =========================
        // LOAD ALL NON-DELETED
        // =========================

        List<Key> keys = keyRepository.findAllNotDeleted();

        List<Key> toSoftDelete = new ArrayList<>();
        List<Key> toHardDelete = new ArrayList<>();

        for (Key key : keys) {

            // =========================
            // HARD DELETE (ưu tiên)
            // =========================

            if (keyLifecycleDomainService.shouldBeHardDeleted(key, now)) {
                toHardDelete.add(key);
                continue;
            }

            // =========================
            // SOFT DELETE
            // =========================

            if (keyLifecycleDomainService.shouldBeDeleted(key, now)) {
                toSoftDelete.add(key);
            }
        }

        // =========================
        // APPLY SOFT DELETE
        // =========================

        for (Key key : toSoftDelete) {
            keyRepository.save(key.markDeleted());
        }

        // =========================
        // APPLY HARD DELETE
        // =========================

        if (!toHardDelete.isEmpty()) {
            keyRepository.deleteAllByIds(
                    toHardDelete.stream()
                            .map(Key::getId)
                            .toList()
            );
        }
    }
}