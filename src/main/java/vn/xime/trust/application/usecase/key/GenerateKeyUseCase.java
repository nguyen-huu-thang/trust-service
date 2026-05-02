package vn.xime.trust.application.usecase.key;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.factory.KeyFactory;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.KeyPolicyDomainService;
import vn.xime.trust.domain.service.KeyValidationDomainService;

import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;



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

    public Key generate(Service signer, Service verifier) {

        Instant now = Instant.now();

        // =========================
        // VALIDATE SERVICE
        // =========================

        if (signer == null) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (verifier == null) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        // =========================
        // LOAD POLICY
        // =========================

        KeyPolicy policy = keyPolicyRepository
            .findByPair(
                signer.getId(),
                verifier.getId()
            )
            .orElseThrow(() -> new IllegalStateException("KeyPolicy not found"));
        
        // =========================
        // RESOLVE ACTIVATE TIME
        // =========================

        Instant activateAt;

        activateAt = now;

        return execute(
        signer.getId(),
        verifier.getId(),
        policy,
        activateAt,
        now
        );
    }
    
    public Key generate(KeyPolicy policy, Instant activateAt) {
        
        Instant now = Instant.now();

        String signerServiceId = policy.getSignerServiceId();
        String verifierServiceId = policy.getVerifierServiceId();

        if (!serviceRepository.existsById(signerServiceId)) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(verifierServiceId)) {
            throw new IllegalStateException("Verifier service not found");
        }

        return execute(
        signerServiceId,
        verifierServiceId,
        policy,
        activateAt,
        now
        );
    }

    @Transactional
    private Key execute(
        String signerServiceId,
        String verifierServiceId,
        KeyPolicy policy,
        Instant activateAt,
        Instant now
    ) {

        // =========================
        // DOMAIN: VALIDATE POLICY
        // =========================

        policyDomainService.validatePolicy(policy);

        // =========================
        // DOMAIN: VALIDATE ACTIVATE TIME 🔥 (NEW)
        // =========================

        policyDomainService.validateActivateAt(
            activateAt,
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
                signerServiceId,
                verifierServiceId
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

        String encryptedPrivateKey = encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // DOMAIN: BUILD KEY
        // =========================

        Key key = keyFactory.create(
            signerServiceId,
            verifierServiceId,
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

        return key;
    }
}