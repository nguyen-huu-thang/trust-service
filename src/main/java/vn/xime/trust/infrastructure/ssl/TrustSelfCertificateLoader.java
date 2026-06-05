package vn.xime.trust.infrastructure.ssl;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import vn.xime.trust.application.dto.response.ServiceCertDto;
import vn.xime.trust.application.usecase.cert.GetCertificatesUseCase;


/**
 * =========================================================
 * TRUST SELF CERTIFICATE LOADER
 * =========================================================
 *
 * Load cert của chính Trust service vào RAM khi startup.
 *
 * Loads Trust service's own certificate into RAM at startup.
 *
 * Responsibilities:
 *
 * - gọi GetCertificatesUseCase để lấy cert active của Trust
 * - wrap raw base64 thành PEM format
 * - cập nhật TrustSelfCertificateResolver
 * - fail fast nếu cert không tìm thấy
 *
 * Call GetCertificatesUseCase to get Trust's active cert.
 * Wrap raw base64 to PEM format.
 * Update TrustSelfCertificateResolver.
 * Fail fast if cert not found.
 *
 * KHÔNG:
 *
 * - truy cập trực tiếp repository hay domain
 * - rotation logic
 * - scheduler logic
 *
 * =========================================================
 * LAYERING
 * =========================================================
 *
 * Luôn đi qua application.usecase — KHÔNG gọi repository hay
 * domain trực tiếp từ infrastructure/ssl.
 *
 * Always go through application.usecase — do NOT call repository
 * or domain directly from infrastructure/ssl.
 *
 * =========================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "trust.self.mtls.enabled", havingValue = "true")
public class TrustSelfCertificateLoader {

    /**
     * =====================================================
     * USE CASE
     * =====================================================
     */
    private final GetCertificatesUseCase getCertificatesUseCase;

    /**
     * =====================================================
     * RESOLVER
     * =====================================================
     */
    private final TrustSelfCertificateResolver trustSelfCertificateResolver;

    /**
     * =====================================================
     * SELF SERVICE ID
     * =====================================================
     *
     * ServiceId của Trust service trong database.
     * Phải khớp với serviceId được admin đăng ký.
     *
     * Trust service's serviceId in database.
     * Must match the serviceId registered by admin.
     *
     * =====================================================
     */
    @Value("${trust.self.service-id}")
    private String selfServiceId;


    /**
     * =====================================================
     * INITIALIZE
     * =====================================================
     *
     * Đọc cert active của Trust service từ database thông qua
     * use case, convert sang PEM và lưu vào RAM cache.
     *
     * Reads Trust service's active cert from database via use
     * case, converts to PEM and stores in RAM cache.
     *
     * =====================================================
     */
    @PostConstruct
    public void initialize() {

        log.info(
                "Loading self certificate for service-id={}",
                selfServiceId
        );

        // =================================================
        // LOAD VIA USE CASE
        // =================================================

        ServiceCertDto cert = getCertificatesUseCase
                .getActiveCertificate(selfServiceId);

        // =================================================
        // VALIDATE PRIVATE KEY
        // =================================================

        if (cert.getPrivateKey() == null || cert.getPrivateKey().isBlank()) {

            throw new IllegalStateException(
                    """

                    ==================================================
                    FATAL SELF CERTIFICATE ERROR
                    ==================================================

                    Private key of self certificate is missing.

                    service-id: %s

                    Private key của self certificate bị thiếu.

                    Kiểm tra: cert được issue đúng chưa?

                    ==================================================
                    """.formatted(selfServiceId)
            );
        }

        // =================================================
        // WRAP PEM
        // =================================================

        String publicCertPem = wrapCertPem(cert.getPublicCert());

        String privateKeyPem = wrapPrivateKeyPem(cert.getPrivateKey());

        // =================================================
        // UPDATE RESOLVER
        // =================================================

        trustSelfCertificateResolver.update(
                new SelfCertificate(publicCertPem, privateKeyPem)
        );

        log.info(
                "Self certificate loaded successfully, expires={}",
                cert.getExpiresAt()
        );
    }


    /**
     * =====================================================
     * WRAP CERTIFICATE PEM
     * =====================================================
     *
     * Trust DB lưu cert dạng raw base64 DER (không có PEM header).
     * Nếu đã có header thì giữ nguyên.
     *
     * Trust DB stores cert as raw base64 DER (no PEM header).
     * If already has header, keep as-is.
     *
     * =====================================================
     */
    private static String wrapCertPem(String value) {

        if (value.startsWith("-----")) {
            return value;
        }

        return "-----BEGIN CERTIFICATE-----\n"
                + value
                + "\n-----END CERTIFICATE-----\n";
    }


    /**
     * =====================================================
     * WRAP PRIVATE KEY PEM (PKCS#8)
     * =====================================================
     *
     * Private key sau khi decrypt là PKCS#8 base64.
     * Nếu đã có header thì giữ nguyên.
     *
     * Private key after decryption is PKCS#8 base64.
     * If already has header, keep as-is.
     *
     * =====================================================
     */
    private static String wrapPrivateKeyPem(String value) {

        if (value.startsWith("-----")) {
            return value;
        }

        return "-----BEGIN PRIVATE KEY-----\n"
                + value
                + "\n-----END PRIVATE KEY-----\n";
    }
}
