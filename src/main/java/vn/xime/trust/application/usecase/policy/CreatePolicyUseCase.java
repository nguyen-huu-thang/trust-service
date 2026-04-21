package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.application.mapper.KeyPolicyMapper;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.KeyPolicyDomainService;

@Component
public class CreatePolicyUseCase {

    private final KeyPolicyRepository keyPolicyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyFactory keyPolicyFactory;
    private final KeyPolicyMapper mapper;
    private final KeyPolicyDomainService domainService;

    public CreatePolicyUseCase(
            KeyPolicyRepository keyPolicyRepository,
            ServiceRepository serviceRepository,
            KeyPolicyFactory keyPolicyFactory,
            KeyPolicyMapper mapper,
            KeyPolicyDomainService domainService
    ) {
        this.keyPolicyRepository = keyPolicyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyFactory = keyPolicyFactory;
        this.mapper = mapper;
        this.domainService = domainService;
    }

    @Transactional
    public KeyPolicyDto execute(CreateKeyPolicyCommand cmd) {

        // =========================
        // BASIC VALIDATION (application-level)
        // =========================

        if (cmd.getSignerServiceId() == null || cmd.getSignerServiceId().isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (cmd.getVerifierServiceId() == null || cmd.getVerifierServiceId().isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        if (cmd.getAlgorithm() == null || cmd.getAlgorithm().isBlank()) {
            throw new IllegalArgumentException("algorithm is required");
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
        // CHECK DUPLICATE POLICY
        // =========================

        keyPolicyRepository
                .findByPair(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                )
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "KeyPolicy already exists for pair: "
                                    + cmd.getSignerServiceId()
                                    + " -> "
                                    + cmd.getVerifierServiceId()
                    );
                });

        // =========================
        // DOMAIN: RESOLVE + VALIDATE 🔥
        // =========================

        var params = domainService.resolveAndValidate(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                cmd.getAlgorithm(),
                cmd.getKeySize(),
                cmd.getKeyLifetimeSec(),
                cmd.getRotationIntervalSeconds(),
                cmd.getPreloadSec()
        );

        // =========================
        // DOMAIN: BUILD ENTITY
        // =========================

        KeyPolicy keyPolicy = keyPolicyFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                params.algorithm,
                params.keySize,
                params.keyLifetime,
                params.rotationInterval,
                params.preload
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