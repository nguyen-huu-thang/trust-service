package vn.xime.trust.application.port.in;

public interface EnsureKeyContinuity {

    /**
     * Được gọi bởi scheduler
     * Đảm bảo timeline key không bị thiếu
     */
    void execute();
}