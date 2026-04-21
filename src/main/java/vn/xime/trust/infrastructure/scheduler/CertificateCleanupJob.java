package vn.xime.trust.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.CleanupExpiredCertificatesUseCase;

/**
 * Scheduler dọn dẹp cert hết hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateCleanupJob {

    private final CleanupExpiredCertificatesUseCase useCase;

    /**
     * ví dụ chạy mỗi 6 giờ
     */
    @Scheduled(fixedDelayString = "${trust.cert.cleanup.interval-ms:21600000}")
    public void run() {

        try {
            log.info("[CertificateCleanupJob] start");

            useCase.execute();

            log.info("[CertificateCleanupJob] done");

        } catch (Exception e) {
            log.error("[CertificateCleanupJob] error", e);
        }
    }
}