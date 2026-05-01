package vn.xime.trust.application.usecase.cert;

import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;

import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.application.port.out.CertificateIssuer;
import vn.xime.trust.application.port.out.KeyEncryptionService;



@Component
public class GenerateCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final KeyGenerator keyGenerator;
    private final CertificateIssuer certificateIssuer;
    private final KeyEncryptionService encryptionService;
    private final CertificateFactory certificateFactory;

    public GenerateCertificateUseCase(
            CertificateRepository certificateRepository,
            KeyGenerator keyGenerator,
            CertificateIssuer certificateIssuer,
            KeyEncryptionService encryptionService,
            CertificateFactory certificateFactory
    ) {
        this.certificateRepository = certificateRepository;
        this.keyGenerator = keyGenerator;
        this.certificateIssuer = certificateIssuer;
        this.encryptionService = encryptionService;
        this.certificateFactory = certificateFactory;
    }

    public Certificate serviceBootstrap(String serviceId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        return generate(serviceId, expiresAt);
    }

    public Certificate rotateCert(String serviceId, Instant expiresAt) {
        return generate(serviceId, expiresAt);
    }

    @Transactional
    private Certificate generate(String serviceId, Instant expiresAt) {

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        Instant now = Instant.now();

        // =========================
        // 1. Generate KeyPair
        // =========================

        var pair = keyGenerator.generate("EC", 256);

        // =========================
        // 2. Issue Certificate (ALL crypto hidden)
        // =========================
        
        var issued = certificateIssuer.issue(
                new CertificateIssuer.IssueCommand(
                        serviceId,
                        "spiffe://localhost/" + serviceId,
                        pair.getPublicKey(),
                        now,
                        expiresAt
                )
        );

        // =========================
        // 3. Build Domain Object
        // =========================
        Certificate cert = certificateFactory.create(
                serviceId,
                issued.certificate(),   // đã là Base64/PEM
                encryptionService.encrypt(pair.getPrivateKey()),
                expiresAt
        );

        // =========================
        // 4. Save
        // =========================
        certificateRepository.save(cert);

        return cert;
    }
}