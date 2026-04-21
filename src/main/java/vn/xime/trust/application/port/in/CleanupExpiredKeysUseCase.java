package vn.xime.trust.application.port.in;

public interface CleanupExpiredKeysUseCase {

    /**
     * Được gọi bởi scheduler
     * Xóa (mark deleted) các key đã hết hạn
     */
    void execute();
}