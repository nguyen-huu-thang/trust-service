package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.ScheduleKeyRotationCommand;
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
import vn.xime.trust.domain.service.KeyValidationDomainService;

import java.time.Instant;
import java.util.List;

@Component
public class ScheduleKeyRotationUseCase {

    private final KeyRepository keyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyRepository keyPolicyRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    private final KeyFactory keyFactory;
    private final KeyValidationDomainService validationService;

    public ScheduleKeyRotationUseCase(
            KeyRepository keyRepository,
            ServiceRepository serviceRepository,
            KeyPolicyRepository keyPolicyRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService,
            KeyFactory keyFactory,
            KeyValidationDomainService validationService
    ) {
        this.keyRepository = keyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyRepository = keyPolicyRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.keyFactory = keyFactory;
        this.validationService = validationService;
    }

    @Transactional
    public String execute(ScheduleKeyRotationCommand cmd) {

        Instant now = Instant.now();

        // =========================
        // VALIDATE SERVICE
        // =========================

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
        // VALIDATE ACTIVATE TIME (STRICT)
        // =========================

        if (cmd.getActivateAt() == null) {
            throw new IllegalArgumentException("activateAt is required for rotation");
        }

        Instant activateAt = cmd.getActivateAt();

        if (!activateAt.isAfter(now)) {
            throw new IllegalArgumentException("activateAt must be in the future");
        }

        // =========================
        // CALCULATE EXPIRES
        // =========================

        Instant expiresAt = activateAt
                .plusSeconds(policy.getKeyLifetimeSeconds())
                .plusSeconds(policy.getJwtTtlSeconds());

        // =========================
        // LOAD EXISTING KEYS
        // =========================

        List<Key> existingKeys =
                keyRepository.findBySignerAndVerifier(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                );

        // =========================
        // VALIDATE KEY CHAIN
        // =========================

        validationService.validateNewKey(
                existingKeys,
                activateAt,
                expiresAt,
                policy,
                now
        );

        // =========================
        // GENERATE KEY PAIR
        // =========================


        var pair = keyGenerator.generate("RSA", 2048);

        String encryptedPrivateKey =
                encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // BUILD DOMAIN
        // =========================

        Key key = keyFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                pair.getPublicKey(),
                encryptedPrivateKey,
                KeyAlgorithm.RSA,
                2048,
                activateAt,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        keyRepository.save(key);

        return IdService.toBase62(key.getId());
    }
}