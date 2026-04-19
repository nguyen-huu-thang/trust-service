package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.IdService;

@Component
public class CreatePolicyUseCase {

    private final KeyPolicyRepository keyPolicyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyFactory keyPolicyFactory;

    public CreatePolicyUseCase(
            KeyPolicyRepository keyPolicyRepository,
            ServiceRepository serviceRepository,
            KeyPolicyFactory keyPolicyFactory
    ) {
        this.keyPolicyRepository = keyPolicyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyFactory = keyPolicyFactory;
    }

    @Transactional
    public KeyPolicyDto execute(CreateKeyPolicyCommand cmd) {

        // =========================
        // VALIDATE INPUT
        // =========================

        if (cmd.getSignerServiceId() == null || cmd.getSignerServiceId().isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (cmd.getVerifierServiceId() == null || cmd.getVerifierServiceId().isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        if (cmd.getSignerServiceId().equals(cmd.getVerifierServiceId())) {
            throw new IllegalArgumentException("signer and verifier must be different");
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
        // CHECK SERVICE EXISTS
        // =========================

        if (!serviceRepository.existsById(cmd.getSignerServiceId())) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(cmd.getVerifierServiceId())) {
            throw new IllegalStateException("Verifier service not found");
        }

        // =========================
        // CHECK DUPLICATE (QUAN TRỌNG)
        // =========================

        keyPolicyRepository
                .findByPair(cmd.getSignerServiceId(), cmd.getVerifierServiceId())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "KeyPolicy already exists for pair: "
                                    + cmd.getSignerServiceId() + " -> " + cmd.getVerifierServiceId()
                    );
                });

        // =========================
        // BUILD DOMAIN
        // =========================

        KeyPolicy keyPolicy = keyPolicyFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                cmd.getKeyLifetimeSec(),
                cmd.getJwtTtlSec(),
                cmd.getPreloadSec()
        );

        // =========================
        // SAVE
        // =========================

        KeyPolicy saved = keyPolicyRepository.save(keyPolicy);

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