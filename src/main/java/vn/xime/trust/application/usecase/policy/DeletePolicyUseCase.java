package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.service.IdService;

@Component
public class DeletePolicyUseCase {

    private final KeyPolicyRepository repository;

    public DeletePolicyUseCase(KeyPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void deleteById(String id) {

        // =========================
        // VALIDATE
        // =========================

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        Id policyId = IdService.fromString(id);

        // =========================
        // CHECK EXIST
        // =========================

        repository.findById(policyId)
                .orElseThrow(() ->
                        new IllegalStateException("KeyPolicy not found: " + id)
                );

        // =========================
        // DELETE
        // =========================

        repository.deleteById(policyId);
    }
}