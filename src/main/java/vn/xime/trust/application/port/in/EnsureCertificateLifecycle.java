package vn.xime.trust.application.port.in;

/**
 * Entry point cho scheduler
 *
 * Chức năng:
 * - đảm bảo mỗi service có cert hợp lệ
 * - tạo cert mới nếu cần (theo policy)
 *
 * ⚠️ implement sau
 */
public interface EnsureCertificateLifecycle {

    void execute();
}