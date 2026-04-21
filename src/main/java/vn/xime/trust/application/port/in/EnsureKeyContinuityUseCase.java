package vn.xime.trust.application.port.in;

public interface EnsureKeyContinuityUseCase {

    /**
     * Được gọi bởi scheduler
     * Đảm bảo timeline key không bị thiếu
     */
    void execute();
}