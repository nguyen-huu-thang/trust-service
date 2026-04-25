package vn.xime.trust.application.usecase.cert;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.CertificateStatus;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateValidationService;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.request.RevokeCertificateCommand;


@Component
public class RevokeCertificateUseCase {

    private final CertificateRepository certificateRepository;
    private final CertificateValidationService validationService;

    public RevokeCertificateUseCase(
            CertificateRepository certificateRepository,
            CertificateValidationService validationService
    ) {
        this.certificateRepository = certificateRepository;
        this.validationService = validationService;
    }

    @Transactional
    public String execute(RevokeCertificateCommand cmd) {

        // Instant now = Instant.now();

        // =========================
        // VALIDATION
        // =========================

        if (cmd.getCertId() == null) {
            throw new IllegalArgumentException("certificateId is required");
        }

        // reason optional (admin/audit), nhưng nếu có thì validate nhẹ
        if (cmd.getReason() != null && cmd.getReason().length() > 500) {
            throw new IllegalArgumentException("reason too long");
        }

        // =========================
        // LOAD
        // =========================

        Certificate cert = certificateRepository.findById(IdService.fromString(cmd.getCertId()))
                .orElseThrow(() ->
                        new IllegalStateException("Certificate not found")
                );

        // =========================
        // DOMAIN VALIDATION
        // =========================

        validationService.validateRevoke(cert);

        // =========================
        // IDEMPOTENT CHECK
        // =========================

        if (cert.getStatus() == CertificateStatus.REVOKED) {
            return cert.getId().toString();
        }

        // =========================
        // DOMAIN STATE CHANGE
        // =========================

        Certificate revoked = cert.markRevoked();

        // =========================
        // SAVE
        // =========================

        certificateRepository.save(revoked);

        // =========================
        // FUTURE
        // =========================
        
        // sau thêm logic gửi tới đồng loạt các service khác để thông báo về việc certificate bị revoked.

        return revoked.getId().toString();
    }
}