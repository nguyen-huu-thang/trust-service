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

        KeyAlgorithm algorithm = resolveAlgorithm(cmd, existingKeys);

        int keySize = resolveKeySize(cmd, existingKeys);

        var pair = keyGenerator.generate(
                algorithm.name(),
                keySize
        );

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
                algorithm,
                keySize,
                activateAt,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        keyRepository.save(key);

        return IdService.toBase62(key.getId());
    }

    // =========================
    // INTERNAL LOGIC
    // =========================

    private KeyAlgorithm resolveAlgorithm(
            ScheduleKeyRotationCommand cmd,
            List<Key> existingKeys
    ) {
        if (cmd.getAlgorithm() != null) {
            return KeyAlgorithm.valueOf(cmd.getAlgorithm().toUpperCase());
        }

        // fallback → dùng key gần nhất
        return existingKeys.stream()
                .filter(k -> !k.isDeleted())
                .max((a, b) -> a.getActivateAt().compareTo(b.getActivateAt()))
                .map(Key::getAlgorithm)
                .orElseThrow(() -> new IllegalStateException("No existing key to infer algorithm"));
    }

    private int resolveKeySize(
            ScheduleKeyRotationCommand cmd,
            List<Key> existingKeys
    ) {
        if (cmd.getKeySize() > 0) {
            return cmd.getKeySize();
        }

        return existingKeys.stream()
                .filter(k -> !k.isDeleted())
                .max((a, b) -> a.getActivateAt().compareTo(b.getActivateAt()))
                .map(Key::getKeySize)
                .orElseThrow(() -> new IllegalStateException("No existing key to infer key size"));
    }
}