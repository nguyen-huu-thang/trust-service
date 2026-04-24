package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.factory.CertificateFactory;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.application.port.out.CertGenerator;

import java.time.Instant;

@Component
public class GenerateCertificateUseCase {

    private final CertificateRepository certificateRepository;

    private final CertGenerator certGenerator;

    private final CertificateFactory certificateFactory;

    public GenerateCertificateUseCase(
            CertificateRepository certificateRepository,
            CertGenerator certGenerator,
            CertificateFactory certificateFactory
    ) {
        this.certificateRepository = certificateRepository;
        this.certGenerator = certGenerator;
        this.certificateFactory = certificateFactory;
    }

    public Certificate serviceBootstrap( String serviceId) {
        Instant now = Instant.now();
        // cộng thêm 1 giờ
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

        // đã đảm bảo service tồn tại và đang active ở layer trên, nên không check ở đây nữa.
        
        // =========================
        // GENERATE
        // =========================

        var generatedCert = certGenerator.generate(serviceId, expiresAt);

        // =========================
        // BUILD CERT
        // =========================

        Certificate cert = certificateFactory.create(
                serviceId,
                generatedCert.publicCertPem(),
                generatedCert.privateKeyPem(),
                expiresAt
        );

        // =========================
        // SAVE
        // =========================

        certificateRepository.save(cert);

        return cert;
    }
}