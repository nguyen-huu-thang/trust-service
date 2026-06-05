package vn.xime.trust.infrastructure.ssl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;


/**
 * =========================================================
 * TRUST SELF CERTIFICATE RESOLVER
 * =========================================================
 *
 * RAM cache cho cert của chính Trust service.
 *
 * In-memory cache for Trust service's own certificate.
 *
 * Responsibilities:
 *
 * - cache SelfCertificate trong RAM
 * - cung cấp fast lookup cho GrpcExternalServerCredentialsProvider
 * - expose update method cho TrustSelfCertificateLoader
 *
 * Cache current SelfCertificate in RAM.
 * Provide fast lookup for GrpcExternalServerCredentialsProvider.
 * Expose update method for TrustSelfCertificateLoader.
 *
 * KHÔNG:
 *
 * - giao tiếp với database
 * - giao tiếp với use case
 * - scheduler logic
 *
 * =========================================================
 */
@Component
public class TrustSelfCertificateResolver {

    /**
     * =====================================================
     * CURRENT SELF CERTIFICATE
     * =====================================================
     *
     * Cert PEM-ready, được load một lần lúc startup.
     *
     * PEM-ready cert, loaded once at startup.
     *
     * =====================================================
     */
    private final AtomicReference<SelfCertificate> certificate =
            new AtomicReference<>();


    /**
     * =====================================================
     * RESOLVE
     * =====================================================
     */
    public Optional<SelfCertificate> resolve() {

        return Optional.ofNullable(
                certificate.get()
        );
    }


    /**
     * =====================================================
     * UPDATE
     * =====================================================
     */
    public void update(SelfCertificate selfCertificate) {

        certificate.set(selfCertificate);
    }


    /**
     * =====================================================
     * CLEAR
     * =====================================================
     */
    public void clear() {

        certificate.set(null);
    }
}
