package vn.xime.trust.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.in.EnsureKeyContinuityUseCase;

@Component
public class KeyContinuityJob {

    private final EnsureKeyContinuityUseCase ensureKeyContinuityUseCase;

    public KeyContinuityJob(EnsureKeyContinuityUseCase ensureKeyContinuityUseCase) {
        this.ensureKeyContinuityUseCase = ensureKeyContinuityUseCase;
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
        ensureKeyContinuityUseCase.execute();
    }
}