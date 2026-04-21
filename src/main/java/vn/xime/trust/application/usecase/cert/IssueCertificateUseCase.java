package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.CertificateLifecycleService;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;

import java.time.Instant;

@Component
public class IssueCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final ServiceRepository serviceRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    private final CertificateFactory certificateFactory;
    private final CertificateLifecycleService lifecycleService;

    public IssueCertificateUseCase(
            CertificateRepository certificateRepository,
            ServiceRepository serviceRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService,
            CertificateFactory certificateFactory,
            CertificateLifecycleService lifecycleService
    ) {
        this.certificateRepository = certificateRepository;
        this.serviceRepository = serviceRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.certificateFactory = certificateFactory;
        this.lifecycleService = lifecycleService;
    }

    @Transactional
    public String execute(String serviceId) {

        Instant now = Instant.now();

        // =========================
        // APPLICATION VALIDATION
        // =========================

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        if (!serviceRepository.existsById(serviceId)) {
            throw new IllegalStateException("Service not found");
        }

        // =========================
        // DOMAIN: CHECK INITIAL ISSUE
        // =========================

        // load latest cert
        Certificate latest = selectionService.findLatestCertificate(certs);

        if (latest != null && !lifecycleService.shouldIssueNewCert(latest, now)) {
            return latest.getId();
        }

        // =========================
        // INFRA: GENERATE KEY PAIR
        // =========================

        var pair = keyGenerator.generate(
                "RSA",   // MVP: hardcode, sau này policy hóa
                2048
        );

        String encryptedPrivateKey =
                encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // DOMAIN: CALCULATE EXPIRES
        // =========================

        Instant expiresAt = lifecycleService.calculateExpiresAt(now);

        // =========================
        // DOMAIN: BUILD CERT
        // =========================

        Certificate cert = certificateFactory.create(
                serviceId,
                pair.getPublicKey(),
                encryptedPrivateKey,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        certificateRepository.save(cert);

        return cert.getId().toString();
    }
}