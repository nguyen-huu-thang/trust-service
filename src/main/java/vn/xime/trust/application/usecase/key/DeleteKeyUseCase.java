package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.DeleteKeyCommand;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.IdService;


@Component
public class DeleteKeyUseCase {

    private final KeyRepository keyRepository;

    public DeleteKeyUseCase(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    @Transactional
    public void execute(DeleteKeyCommand cmd) {

        // =========================
        // LOAD KEY
        // =========================

        Key key = keyRepository.findById(IdService.fromString(cmd.getId()))
                .orElseThrow(() -> new IllegalStateException("Key not found"));

        // =========================
        // ALREADY DELETED
        // =========================

        if (key.isDeleted()) {
            return; // idempotent
        }

        // =========================
        // DELETE (SOFT)
        // =========================

        Key deleted = key.markDeleted();

        keyRepository.save(deleted);
    }
}