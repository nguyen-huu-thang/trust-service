package vn.xime.trust.infrastructure.ssl;


/**
 * =========================================================
 * SELF CERTIFICATE
 * =========================================================
 *
 * Cert của Trust service, đã sẵn sàng dùng cho TLS server.
 *
 * Certificate of Trust service, ready for TLS server use.
 *
 * Được tạo một lần lúc startup bởi TrustSelfCertificateLoader.
 * Created once at startup by TrustSelfCertificateLoader.
 *
 * =========================================================
 */
public record SelfCertificate(

        String publicCertificatePem,

        String privateKeyPem
) {
}
