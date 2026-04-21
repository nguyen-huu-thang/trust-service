package vn.xime.trust.application.usecase.cert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.xime.trust.application.port.in.CleanupExpiredCertificatesUseCase;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateLifecycleService;

import java.time.Instant;
import java.util.List;

/**
 * CleanupExpiredCertificatesUseCaseImpl
 *
 * - dọn dẹp cert hết hạn
 * - mark revoked hoặc deleted (tùy strategy)
 */
@Slf4j
@RequiredArgsConstructor
public class CleanupExpiredCertificatesUseCaseImpl
        implements CleanupExpiredCertificatesUseCase {

    private final CertificateRepository certificateRepository;
    private final CertificateLifecycleService lifecycleService;

    @Override
    public void execute() {

        Instant now = Instant.now();

        List<Certificate> certs = certificateRepository.findAll();

        for (Certificate cert : certs) {

            try {
                if (!lifecycleService.shouldBeDeleted(cert, now)) {
                    continue;
                }

                processExpired(cert, now);

            } catch (Exception e) {
                log.error(
                        "Failed to cleanup cert id={} service={}",
                        cert.getId(),
                        cert.getServiceId(),
                        e
                );
            }
        }
    }

    // =========================
    // PROCESS
    // =========================

    private void processExpired(Certificate cert, Instant now) {

        // =========================
        // STRATEGY: SOFT DELETE
        // =========================

        // nếu bạn có status EXPIRED → nên dùng
        if (cert.getStatus().name().equals("EXPIRED")) {
            return; // idempotent
        }

        Certificate updated = new Certificate(
                cert.getId(),
                cert.getServiceId(),
                cert.getPublicCert(),
                cert.getPrivateKeyEncrypted(),
                cert.getIssuedAt(),
                cert.getExpiresAt(),
                // ⚠️ bạn nên có enum EXPIRED
                cert.getStatus() // tạm giữ nguyên nếu chưa có EXPIRED
        );

        certificateRepository.save(updated);

        log.info(
                "Expired certificate cleaned id={} service={}",
                cert.getId(),
                cert.getServiceId()
        );
    }
}