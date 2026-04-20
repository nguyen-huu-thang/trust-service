package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.UpdateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.mapper.KeyPolicyMapper;

@Component
public class UpdatePolicyUseCase {

    private final KeyPolicyRepository repository;
    private final KeyPolicyMapper mapper;

    public UpdatePolicyUseCase(KeyPolicyRepository repository, KeyPolicyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public KeyPolicyDto execute(UpdateKeyPolicyCommand cmd) {

        // =========================
        // VALIDATE INPUT
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        if (cmd.getAlgorithm() == null || cmd.getAlgorithm().isBlank()) {
            throw new IllegalArgumentException("algorithm is required");
        }

        if (cmd.getKeySize() <= 0) {
            throw new IllegalArgumentException("keySize must be > 0");
        }

        if (cmd.getKeyLifetimeSec() <= 0) {
            throw new IllegalArgumentException("keyLifetime must be > 0");
        }

        if (cmd.getJwtTtlSec() <= 0) {
            throw new IllegalArgumentException("jwtTtl must be > 0");
        }

        if (cmd.getPreloadSec() < 0) {
            throw new IllegalArgumentException("preload must be >= 0");
        }

        // =========================
        // PARSE ALGORITHM
        // =========================

        KeyAlgorithm algorithm;
        try {
            algorithm = KeyAlgorithm.valueOf(cmd.getAlgorithm().trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid algorithm: " + cmd.getAlgorithm());
        }

        // =========================
        // DOMAIN RULE VALIDATION
        // =========================

        if (cmd.getKeyLifetimeSec() <
                cmd.getJwtTtlSec() + cmd.getPreloadSec()) {
            throw new IllegalArgumentException(
                    "keyLifetime must be >= jwtTtl + preload"
            );
        }

        // =========================
        // LOAD EXISTING
        // =========================

        KeyPolicy existing = repository.findById(IdService.fromString(cmd.getId()))
                .orElseThrow(() ->
                        new IllegalStateException("KeyPolicy not found: " + cmd.getId())
                );

        // =========================
        // BUILD UPDATED DOMAIN
        // =========================

        KeyPolicy updated = existing.updated(
                algorithm,
                cmd.getKeySize(),
                cmd.getKeyLifetimeSec(),
                cmd.getJwtTtlSec(),
                cmd.getPreloadSec()
        );

        // =========================
        // SAVE
        // =========================

        KeyPolicy saved = repository.save(updated);

        // =========================
        // RETURN DTO
        // =========================

        return mapper.toDto(saved);
    }
}