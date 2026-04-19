package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.UpdateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.service.IdService;

import java.time.Instant;

@Component
public class UpdatePolicyUseCase {

    private final KeyPolicyRepository repository;

    public UpdatePolicyUseCase(KeyPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public KeyPolicyDto execute(UpdateKeyPolicyCommand cmd) {

        // =========================
        // VALIDATE INPUT
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("id is required");
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
        // DOMAIN RULE VALIDATION
        // =========================

        if (cmd.getJwtTtlSec() > cmd.getKeyLifetimeSec()) {
            throw new IllegalArgumentException("jwtTtl must be <= keyLifetime");
        }

        if (cmd.getPreloadSec() > cmd.getKeyLifetimeSec()) {
            throw new IllegalArgumentException("preload must be <= keyLifetime");
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

        KeyPolicy updated = new KeyPolicy(
                existing.getId(), // giữ nguyên ID
                existing.getSignerServiceId(),
                existing.getVerifierServiceId(),
                cmd.getKeyLifetimeSec(),
                cmd.getJwtTtlSec(),
                cmd.getPreloadSec(),
                existing.getCreatedAt(),
                Instant.now() // update timestamp
        );

        // =========================
        // SAVE
        // =========================

        KeyPolicy saved = repository.save(updated);

        // =========================
        // RETURN DTO
        // =========================

        return toDto(saved);
    }

    // =========================
    // Mapper
    // =========================

    private KeyPolicyDto toDto(KeyPolicy p) {
        return new KeyPolicyDto(
                IdService.toString(p.getId()),
                p.getSignerServiceId(),
                p.getVerifierServiceId(),
                p.getKeyLifetimeSeconds(),
                p.getJwtTtlSeconds(),
                p.getPreloadSeconds(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}