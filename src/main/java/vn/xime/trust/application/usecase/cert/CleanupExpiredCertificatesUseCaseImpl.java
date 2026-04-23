package vn.xime.trust.application.usecase.cert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.port.in.CleanupExpiredCertificatesUseCase;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.domain.service.CertificateLifecycleService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CleanupExpiredCertificatesUseCaseImpl implements CleanupExpiredCertificatesUseCase {

    private final CertificateRepository certificateRepository;
    private final CertificateLifecycleService lifecycleService;

    @Override
    @Transactional
    public void execute() {

        Instant now = Instant.now();

        List<Certificate> certs = certificateRepository.findAllNotDeleted();

        List<Certificate> toSoftDelete = new ArrayList<>();
        List<Certificate> toHardDelete = new ArrayList<>();

        for (Certificate cert : certs) {

            try {

                // =========================
                // HARD DELETE (PRIORITY)
                // =========================

                if (lifecycleService.shouldBeHardDeleted(cert, now)) {
                    toHardDelete.add(cert);
                    continue;
                }

                // =========================
                // SOFT DELETE
                // =========================

                if (lifecycleService.shouldBeDeleted(cert, now)) {
                    toSoftDelete.add(cert);
                }

            } catch (Exception e) {
                log.error(
                        "Failed to cleanup cert id={} service={}",
                        cert.getId(),
                        cert.getServiceId(),
                        e
                );
            }
        }

        // =========================
        // APPLY SOFT DELETE
        // =========================

        for (Certificate cert : toSoftDelete) {

            // ⚠️ bạn đã có is_deleted → nên dùng markDeleted()
            certificateRepository.save(cert.markDeleted());
        }

        // =========================
        // APPLY HARD DELETE
        // =========================

        if (!toHardDelete.isEmpty()) {

            certificateRepository.deleteAllByIds(
                    toHardDelete.stream()
                            .map(Certificate::getId)
                            .toList()
            );

            log.info(
                    "Hard deleted {} certificates",
                    toHardDelete.size()
            );
        }
    }
}