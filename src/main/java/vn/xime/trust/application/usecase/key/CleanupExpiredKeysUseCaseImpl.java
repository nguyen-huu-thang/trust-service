package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.port.in.CleanupExpiredKeysUseCase;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
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
        // LOAD CANDIDATES
        // =========================

        List<Key> keys = keyRepository.findAllNotDeleted();

        for (Key key : keys) {

            // =========================
            // DOMAIN RULE
            // =========================

            if (!keyLifecycleDomainService.shouldBeDeleted(key, now)) {
                continue;
            }

            // =========================
            // MARK DELETED
            // =========================

            Key deletedKey = key.markDeleted();

            keyRepository.save(deletedKey);
        }
    }
}