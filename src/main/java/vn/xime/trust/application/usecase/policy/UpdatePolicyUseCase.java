package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.UpdateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.application.mapper.KeyPolicyMapper;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.domain.service.KeyPolicyDomainService;

@Component
public class UpdatePolicyUseCase {

    private final KeyPolicyRepository repository;
    private final KeyPolicyMapper mapper;
    private final KeyPolicyDomainService domainService;

    public UpdatePolicyUseCase(
            KeyPolicyRepository repository,
            KeyPolicyMapper mapper,
            KeyPolicyDomainService domainService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.domainService = domainService;
    }

    @Transactional
    public KeyPolicyDto execute(UpdateKeyPolicyCommand cmd) {

        // =========================
        // BASIC VALIDATION
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        // =========================
        // LOAD EXISTING
        // =========================

        KeyPolicy existing = repository.findById(
                IdService.fromString(cmd.getId())
        ).orElseThrow(() ->
                new IllegalStateException("KeyPolicy not found: " + cmd.getId())
        );

        // =========================
        // MERGE INPUT (PARTIAL UPDATE) 🔥
        // =========================

        String algorithmRaw =
                (cmd.getAlgorithm() != null && !cmd.getAlgorithm().isBlank())
                        ? cmd.getAlgorithm()
                        : existing.getAlgorithm().name();

        int keySize =
                (cmd.getKeySize() != null)
                        ? cmd.getKeySize()
                        : existing.getKeySize();

        Long rotation =
                (cmd.getRotationIntervalSeconds() != null)
                        ? cmd.getRotationIntervalSeconds()
                        : existing.getRotationIntervalSeconds();

        Long lifetime =
                (cmd.getKeyLifetimeSec() != null)
                        ? cmd.getKeyLifetimeSec()
                        : existing.getKeyLifetimeSeconds();

        Long preload =
                (cmd.getPreloadSec() != null)
                        ? cmd.getPreloadSec()
                        : existing.getPreloadSeconds();

        // =========================
        // DOMAIN: RESOLVE + VALIDATE 🔥
        // =========================

        var params = domainService.resolveAndValidate(
                existing.getSignerServiceId(),
                existing.getVerifierServiceId(),
                algorithmRaw,
                keySize,
                lifetime,
                rotation,
                preload
        );

        // =========================
        // BUILD UPDATED DOMAIN
        // =========================

        KeyPolicy updated = existing.updated(
                params.algorithm,
                params.keySize,
                params.keyLifetime,
                params.rotationInterval,
                params.preload
        );

        // =========================
        // SAVE
        // =========================

        KeyPolicy saved = repository.save(updated);

        return mapper.toDto(saved);
    }
}