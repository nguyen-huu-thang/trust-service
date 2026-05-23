package vn.xime.trust.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.CleanupExpiredCertificates;

/**
 * Scheduler dọn dẹp cert hết hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateCleanupJob {

    private final CleanupExpiredCertificates cleanup;

    /**
     * ví dụ chạy mỗi 6 giờ
     */
    @Scheduled(fixedDelayString = "${trust.cert.cleanup.interval-ms:21600000}")
    public void run() {

        try {
            log.info("[CertificateCleanupJob] start");

            cleanup.execute();

            log.info("[CertificateCleanupJob] done");

        } catch (Exception e) {
            log.error("[CertificateCleanupJob] error", e);
        }
    }
}