package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.GenerateKeyCommand;
import vn.xime.trust.application.port.out.*;
import vn.xime.trust.domain.factory.KeyFactory;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.model.ServiceTrust;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ServiceTrustRepository;
import vn.xime.trust.domain.service.KeyPolicyDomainService;

import java.time.Instant;

@Component
public class GenerateKeyUseCase {

    private final KeyRepository keyRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceTrustRepository trustRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;
    private final IdGenerator idGenerator;

    private final KeyFactory keyFactory;
    private final KeyPolicyDomainService policyService;
    private final Clock clock;

    public GenerateKeyUseCase(
            KeyRepository keyRepository,
            ServiceRepository serviceRepository,
            ServiceTrustRepository trustRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService,
            IdGenerator idGenerator,
            KeyFactory keyFactory,
            KeyPolicyDomainService policyService,
            Clock clock
    ) {
        this.keyRepository = keyRepository;
        this.serviceRepository = serviceRepository;
        this.trustRepository = trustRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.idGenerator = idGenerator;
        this.keyFactory = keyFactory;
        this.policyService = policyService;
        this.clock = clock;
    }

    @Transactional
    public String execute(GenerateKeyCommand cmd) {

        Instant now = clock.now();

        // =========================
        // VALIDATE
        // =========================

        if (!serviceRepository.existsById(cmd.getSignerServiceId())) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(cmd.getVerifierServiceId())) {
            throw new IllegalStateException("Verifier service not found");
        }

        ServiceTrust trust = trustRepository
                .findBySignerAndVerifier(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                )
                .orElseThrow(() -> new IllegalStateException("Trust not found"));

        // =========================
        // GENERATE KEY
        // =========================

        var pair = keyGenerator.generate(
                cmd.getAlgorithm(),
                cmd.getKeySize()
        );

        String encryptedPrivateKey =
                encryptionService.encrypt(pair.getPrivateKey());

        String kid = idGenerator.generateKid();

        // =========================
        // TIME CALCULATION
        // =========================

        Instant activateAt =
                cmd.getActivateAt() != null ? cmd.getActivateAt() : now;

        Instant expiresAt = policyService.calculateExpiresAt(
                activateAt,
                trust.getKeyLifetimeSec()
        );

        // =========================
        // BUILD DOMAIN
        // =========================

        Key key = keyFactory.create(
                kid,
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                pair.getPublicKey(),
                encryptedPrivateKey,
                KeyAlgorithm.valueOf(cmd.getAlgorithm()),
                cmd.getKeySize(),
                now,
                activateAt,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        keyRepository.save(key);

        return kid;
    }
}