package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.GenerateKeyCommand;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.domain.factory.KeyFactory;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.domain.service.KeyPolicyDomainService;
import vn.xime.trust.domain.service.KeyValidationDomainService;

import java.time.Instant;
import java.util.List;

@Component
public class GenerateKeyUseCase {

    private final KeyRepository keyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyRepository keyPolicyRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    private final KeyFactory keyFactory;

    private final KeyValidationDomainService validationService;
    private final KeyPolicyDomainService policyDomainService;

    public GenerateKeyUseCase(
            KeyRepository keyRepository,
            ServiceRepository serviceRepository,
            KeyPolicyRepository keyPolicyRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService,
            KeyFactory keyFactory,
            KeyValidationDomainService validationService,
            KeyPolicyDomainService policyDomainService
    ) {
        this.keyRepository = keyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyRepository = keyPolicyRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.keyFactory = keyFactory;
        this.validationService = validationService;
        this.policyDomainService = policyDomainService;
    }

    @Transactional
    public String execute(GenerateKeyCommand cmd) {

        Instant now = Instant.now();

        // =========================
        // VALIDATE SERVICE (application-level)
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

        // =========================
        // LOAD POLICY
        // =========================

        KeyPolicy policy = keyPolicyRepository
                .findByPair(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                )
                .orElseThrow(() -> new IllegalStateException("KeyPolicy not found"));

        // =========================
        // DOMAIN: VALIDATE POLICY
        // =========================

        policyDomainService.validatePolicy(policy);

        // =========================
        // DOMAIN: RESOLVE ACTIVATE TIME
        // =========================

        Instant activateAt = policyDomainService.resolveActivateAt(
                cmd.getActivateAt(),
                policy,
                now
        );

        // =========================
        // DOMAIN: VALIDATE ACTIVATE TIME 🔥 (NEW)
        // =========================

        policyDomainService.validateActivateAt(
                activateAt,
                policy,
                now
        );

        // =========================
        // DOMAIN: CALCULATE EXPIRES
        // =========================

        Instant expiresAt = policyDomainService.calculateExpiresAt(
                activateAt,
                policy
        );

        // =========================
        // LOAD EXISTING KEYS
        // =========================

        List<Key> existingKeys =
                keyRepository.findBySignerAndVerifier(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                );

        // =========================
        // DOMAIN: VALIDATE KEY CHAIN
        // =========================

        validationService.validateNewKey(
                existingKeys,
                activateAt,
                expiresAt,
                policy,
                now
        );

        // =========================
        // INFRA: GENERATE KEY
        // =========================

        KeyAlgorithm algorithm = policy.getAlgorithm();
        int keySize = policy.getKeySize();

        var pair = keyGenerator.generate(
                algorithm.name(),
                keySize
        );

        String encryptedPrivateKey =
                encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // DOMAIN: BUILD KEY
        // =========================

        Key key = keyFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                pair.getPublicKey(),
                encryptedPrivateKey,
                algorithm,
                keySize,
                activateAt,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        keyRepository.save(key);

        return IdService.toString(key.getId());
    }
}