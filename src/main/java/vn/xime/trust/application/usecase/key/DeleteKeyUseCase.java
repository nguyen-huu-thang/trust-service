package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.DeleteKeyCommand;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.IdService;

import java.time.Instant;

@Component
public class DeleteKeyUseCase {

    private final KeyRepository keyRepository;

    public DeleteKeyUseCase(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    @Transactional
    public void execute(DeleteKeyCommand cmd) {

        Instant now = Instant.now();

        // =========================
        // LOAD KEY
        // =========================

        Key key = keyRepository.findById(cmd.getId())
                .orElseThrow(() -> new IllegalStateException("Key not found"));

        // =========================
        // ALREADY DELETED
        // =========================

        if (key.isDeleted()) {
            return; // idempotent
        }

        // =========================
        // VALIDATE DELETE RULE
        // =========================

        // ❌ không cho delete khi còn verify
        if (key.getExpiresAt().isAfter(now)) {
            throw new IllegalStateException(
                    "Cannot delete key before expiresAt (still used for verification)"
            );
        }

        // =========================
        // DELETE (SOFT)
        // =========================

        Key deleted = key.markDeleted();

        keyRepository.save(deleted);
    }
}