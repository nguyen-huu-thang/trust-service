package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.CertificateLifecycleService;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class GenerateCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final ServiceRepository serviceRepository;

    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    private final CertificateFactory certificateFactory;
    private final CertificateLifecycleService lifecycleService;
    private final CertificateSelectionService selectionService;

    public GenerateCertificateUseCase(
            CertificateRepository certificateRepository,
            ServiceRepository serviceRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService,
            CertificateFactory certificateFactory,
            CertificateLifecycleService lifecycleService,
            CertificateSelectionService selectionService
    ) {
        this.certificateRepository = certificateRepository;
        this.serviceRepository = serviceRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
        this.certificateFactory = certificateFactory;
        this.lifecycleService = lifecycleService;
        this.selectionService = selectionService;
    }

    @Transactional
    public Certificate generate(String serviceId) {

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
        // LOAD CERTS
        // =========================

        List<Certificate> certs = certificateRepository.findByServiceId(serviceId);

        // =========================
        // FIND LATEST CERT
        // =========================

        Optional<Certificate> latestOpt = selectionService.findLatestCertificate(certs);

        if (latestOpt.isPresent()) {

            Certificate latest = latestOpt.get();

            // nếu chưa cần rotate → dùng lại cert cũ
            if (!lifecycleService.shouldIssueNewCert(latest, now)) {
                return latest;
            }
        }

        // =========================
        // GENERATE KEY PAIR
        // =========================

        var pair = keyGenerator.generate(
                "RSA",
                2048
        );

        String encryptedPrivateKey = encryptionService.encrypt(pair.getPrivateKey());

        // =========================
        // CALCULATE EXPIRES
        // =========================

        Instant expiresAt = lifecycleService.calculateExpiresAt(now);

        // =========================
        // BUILD CERT
        // =========================

        Certificate cert = certificateFactory.create(
                serviceId,
                pair.getPublicKey(), // sai nha
                encryptedPrivateKey,
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        certificateRepository.save(cert);

        return cert;
    }

    public String createByAdmin( String serviceId) {
        return generate(serviceId).getId().toString();

    }
}