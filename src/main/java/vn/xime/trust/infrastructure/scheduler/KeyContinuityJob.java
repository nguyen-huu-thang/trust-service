package vn.xime.trust.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.EnsureKeyContinuity;

@Component
public class KeyContinuityJob {

    private final EnsureKeyContinuity ensureKeyContinuity;

    public KeyContinuityJob(EnsureKeyContinuity ensureKeyContinuity) {
        this.ensureKeyContinuity = ensureKeyContinuity;
    }

    /**
     * ⚠️ fixedDelay (KHÔNG dùng fixedRate)
     *
     * - fixedDelay: chạy sau khi job trước hoàn thành → an toàn
     * - fixedRate: có thể chạy chồng → dễ tạo duplicate key
     *
     * Interval phải << preload_seconds
     *
     * Ví dụ:
     * preload = 1 ngày
     * → job chạy mỗi 1 giờ
     */
    @Scheduled(fixedDelayString = "${trust.key.rotation.interval-ms:3600000}")
    public void run() {
        ensureKeyContinuity.execute();
    }
}