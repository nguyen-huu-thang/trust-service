package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.service.CertificateValidationService;
import vn.xime.trust.application.dto.response.CertificateResponseDto;
import vn.xime.trust.application.mapper.CertificateMapper;
import vn.xime.trust.application.port.out.KeyEncryptionService;

import java.time.Instant;
import java.util.List;

@Component
public class GetCertificatesUseCase {

    private final CertificateRepository certificateRepository;
    private final CertificateMapper certificateMapper;
    private final KeyEncryptionService encryptionService;

    private final CertificateSelectionService selectionService;
    private final CertificateValidationService validationService;

    public GetCertificatesUseCase(
            CertificateRepository certificateRepository,
            CertificateMapper certificateMapper,
            KeyEncryptionService encryptionService,
            CertificateSelectionService selectionService,
            CertificateValidationService validationService
    ) {
        this.certificateRepository = certificateRepository;
        this.certificateMapper = certificateMapper;
        this.encryptionService = encryptionService;
        this.selectionService = selectionService;
        this.validationService = validationService;
    }

    // ==================================================
    // GET BY ID (ADMIN)
    // ==================================================

    public CertificateResponseDto getById(Id id) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Certificate not found"));

        return toDto(cert);
    }

    // ==================================================
    // LIST BY SERVICE (ADMIN)
    // ==================================================

    public List<CertificateResponseDto> listByService(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        return certificateRepository.findByServiceId(serviceId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ==================================================
    // GET ACTIVE CERT (RUNTIME - mTLS)
    // ==================================================

    public CertificateResponseDto getActiveCertificate(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        Instant now = Instant.now();

        // =========================
        // LOAD
        // =========================

        List<Certificate> certs =
                certificateRepository.findValidCertificates(serviceId, now);

        // =========================
        // DOMAIN: SELECT
        // =========================

        Certificate current = selectionService.getCurrentCertificate(certs, now);

        // =========================
        // DOMAIN: VALIDATE
        // =========================

        validationService.validateActive(current, now);

        // =========================
        // MAP
        // =========================

        return toDto(current);
    }

    // ==================================================
    // INTERNAL HELPER
    // ==================================================

    private CertificateResponseDto toDto(Certificate cert) {

        // ⚠️ decrypt private key nếu cần (internal/admin)
        String decryptedPrivateKey = null;

        if (cert.getPrivateKeyEncrypted() != null) {
            decryptedPrivateKey = encryptionService.decrypt(
                    cert.getPrivateKeyEncrypted()
            );
        }

        return certificateMapper.toResponseDto(cert, decryptedPrivateKey);
    }
}