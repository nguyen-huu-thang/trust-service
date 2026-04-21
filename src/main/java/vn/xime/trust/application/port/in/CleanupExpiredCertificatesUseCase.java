package vn.xime.trust.application.port.in;

/**
 * Dọn dẹp cert hết hạn
 *
 * - mark deleted hoặc revoke
 * - tránh phình DB
 */
public interface CleanupExpiredCertificatesUseCase {

    void execute();
}