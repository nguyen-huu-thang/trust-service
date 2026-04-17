package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateTrustCommand;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.factory.KeyPolicyFactory;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.KeyPolicyRepository;

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
    public KeyPolicy execute(CreateTrustCommand cmd) {

        // =========================
        // VALIDATE
        // =========================

        if (cmd.getSignerServiceId() == null || cmd.getSignerServiceId().isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (cmd.getVerifierServiceId() == null || cmd.getVerifierServiceId().isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        if (!serviceRepository.existsById(cmd.getSignerServiceId())) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(cmd.getVerifierServiceId())) {
            throw new IllegalStateException("Verifier service not found");
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

        keyPolicyRepository.save(keyPolicy);

        return keyPolicy;
    }
}