package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateSelectionService;
import vn.xime.trust.domain.service.CertificateValidationService;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.AdminCertDto;
import vn.xime.trust.application.dto.response.ServiceCertDto;
import vn.xime.trust.application.mapper.CertificateMapper;
import vn.xime.trust.application.port.out.KeyEncryptionService;

import java.time.Instant;
import java.util.List;

@Component
public class GetCertificatesUseCase {

    private final CertificateRepository certificateRepository;
    private final CertificateMapper mapper;
    private final KeyEncryptionService encryptionService;

    private final CertificateSelectionService selectionService;
    private final CertificateValidationService validationService;

    public GetCertificatesUseCase(
            CertificateRepository certificateRepository,
            CertificateMapper mapper,
            KeyEncryptionService encryptionService,
            CertificateSelectionService selectionService,
            CertificateValidationService validationService
    ) {
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
        this.encryptionService = encryptionService;
        this.selectionService = selectionService;
        this.validationService = validationService;
    }

    // ==================================================
    // GET BY ID (ADMIN)
    // ==================================================

    public AdminCertDto getById(String certId) {

        Id id = IdService.fromString(certId);

        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Certificate not found"));

        return mapper.toAdminDto(cert);
    }

    // ==================================================
    // LIST BY SERVICE (ADMIN)
    // ==================================================

    public List<AdminCertDto> listByService(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        return certificateRepository.findByServiceId(serviceId)
                .stream()
                .map(mapper::toAdminDto)
                .toList();
    }

    // ==================================================
    // GET ACTIVE CERT (RUNTIME - mTLS)
    // ==================================================

    // hàm này hiện tại không cần lắm.
    // vì logic bây giờ chỉ trả token quan bootstrap hoặc rotate để đảm bảo vấn đề bảo mật.
    
    public ServiceCertDto getActiveCertificate(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }

        Instant now = Instant.now();

        // =========================
        // LOAD
        // =========================

        List<Certificate> certs = certificateRepository.findValidCertificates(serviceId, now);

        // =========================
        // DOMAIN: SELECT
        // =========================

        Certificate current = selectionService.getCurrentCertificate(certs, now);

        // =========================
        // DOMAIN: VALIDATE
        // =========================

        validationService.validateActive(current, now);

        // =========================
        // DECRYPT PRIVATE KEY
        // =========================

        String decryptedPrivateKey = null;

        if (current.getPrivateKeyEncrypted() != null) {
            decryptedPrivateKey = encryptionService.decrypt(
                    current.getPrivateKeyEncrypted()
            );
        }

        // =========================
        // MAP
        // =========================

        return mapper.toServiceDto(current, decryptedPrivateKey);
    }
}