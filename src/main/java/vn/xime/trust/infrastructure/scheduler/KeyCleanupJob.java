package vn.xime.trust.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.CleanupExpiredKeys;

@Component
public class KeyCleanupJob {

    private final CleanupExpiredKeys cleanupExpiredKeys;

    public KeyCleanupJob(CleanupExpiredKeys cleanupExpiredKeys) {
        this.cleanupExpiredKeys = cleanupExpiredKeys;
    }

    /**
     * Cleanup không cần chạy quá thường xuyên
     *
     * Ví dụ:
     * - chạy mỗi 6 giờ / 12 giờ / 1 ngày
     *
     * Vì:
     * - key hết hạn vẫn không ảnh hưởng hệ thống ngay
     * - verify logic đã check expires_at
     */
    @Scheduled(fixedDelayString = "${trust.key.cleanup.interval-ms:21600000}")
    public void run() {
        cleanupExpiredKeys.execute();
    }
}