package vn.xime.trust.application.port.in;

public interface CleanupExpiredKeys {

    /**
     * Được gọi bởi scheduler
     * Xóa (mark deleted) các key đã hết hạn
     */
    void execute();
}