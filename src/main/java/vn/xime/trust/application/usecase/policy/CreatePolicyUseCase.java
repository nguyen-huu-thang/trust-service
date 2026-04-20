package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.application.mapper.KeyPolicyMapper;

@Component
public class CreatePolicyUseCase {

    private final KeyPolicyRepository keyPolicyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyFactory keyPolicyFactory;
    private final KeyPolicyMapper mapper;

    public CreatePolicyUseCase(
            KeyPolicyRepository keyPolicyRepository,
            ServiceRepository serviceRepository,
            KeyPolicyFactory keyPolicyFactory,
            KeyPolicyMapper mapper
    ) {
        this.keyPolicyRepository = keyPolicyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyFactory = keyPolicyFactory;
        this.mapper = mapper;
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

        // 🔥 NEW
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
        // CHECK SERVICE EXISTS
        // =========================

        if (!serviceRepository.existsById(cmd.getSignerServiceId())) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(cmd.getVerifierServiceId())) {
            throw new IllegalStateException("Verifier service not found");
        }

        // =========================
        // CHECK DUPLICATE
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
                algorithm,
                cmd.getKeySize(),
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

        return mapper.toDto(saved);
    }
}