package vn.xime.trust.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.EnsureCertificateLifecycleUseCase;

/**
 * Scheduler đảm bảo lifecycle của certificate
 *
 * chạy định kỳ:
 * - kiểm tra service nào cần rotate
 * - tạo cert mới nếu cần
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateLifecycleJob {

    private final EnsureCertificateLifecycleUseCase useCase;

    /**
     * ví dụ chạy mỗi 1 giờ
     *
     * ⚠️ interval phải nhỏ hơn rotation window
     */
    @Scheduled(fixedDelayString = "${trust.cert.lifecycle.interval-ms:3600000}")
    public void run() {

        try {
            log.info("[CertificateLifecycleJob] start");

            useCase.execute();

            log.info("[CertificateLifecycleJob] done");

        } catch (Exception e) {
            log.error("[CertificateLifecycleJob] error", e);
        }
    }
}