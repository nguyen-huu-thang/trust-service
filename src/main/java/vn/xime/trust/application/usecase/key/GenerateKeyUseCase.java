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
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;

import java.time.Instant;

@Component
public class GenerateKeyUseCase {

    private final KeyRepository keyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyPolicyRepository keyPolicyRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    private final KeyFactory keyFactory;

    public GenerateKeyUseCase(
        KeyRepository keyRepository,
        ServiceRepository serviceRepository,
        KeyPolicyRepository keyPolicyRepository,
        KeyGenerator keyGenerator,
        KeyEncryptionService encryptionService,
        KeyFactory keyFactory
    ) {
        this.keyRepository = keyRepository;
        this.serviceRepository = serviceRepository;
        this.keyPolicyRepository = keyPolicyRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.keyFactory = keyFactory;
    }

    @Transactional
    public String execute(GenerateKeyCommand cmd) {

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
        // GENERATE KEY PAIR
        // =========================

        var pair = keyGenerator.generate(
                cmd.getAlgorithm(),
                cmd.getKeySize()
        );

        String encryptedPrivateKey =
                encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // TIME CALCULATION (CORE LOGIC)
        // =========================

        Instant activateAt =
                cmd.getActivateAt() != null ? cmd.getActivateAt() : now;

        Instant expiresAt = activateAt
                .plusSeconds(policy.getKeyLifetimeSeconds())
                .plusSeconds(policy.getJwtTtlSeconds());

        // =========================
        // BUILD DOMAIN
        // =========================

        Key key = keyFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                pair.getPublicKey(),
                encryptedPrivateKey,
                KeyAlgorithm.valueOf(cmd.getAlgorithm()),
                cmd.getKeySize(),
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