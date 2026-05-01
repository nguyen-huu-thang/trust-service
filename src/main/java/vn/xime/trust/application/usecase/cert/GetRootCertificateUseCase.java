package vn.xime.trust.application.usecase.cert;

import java.security.cert.X509Certificate;
import java.util.Base64;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.port.out.CertificateAuthoritySigner;


@Component
public class GetRootCertificateUseCase {

    private final CertificateAuthoritySigner caSigner;

    public GetRootCertificateUseCase(CertificateAuthoritySigner caSigner) {
        this.caSigner = caSigner;
    }

    /**
     * Trả về Root CA certificate (PEM format)
     */
    public String getRootCertificate() {

        X509Certificate caCert = caSigner.getCaCertificate();

        return toPem(caCert);
    }

    // =========================
    // Helpers
    // =========================

    private String toPem(X509Certificate cert) {
        try {
            byte[] der = cert.getEncoded();

            String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'})
                    .encodeToString(der);

            return "-----BEGIN CERTIFICATE-----\n"
                    + base64
                    + "\n-----END CERTIFICATE-----";

        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode CA certificate", e);
        }
    }
}