package vn.xime.trust.infrastructure.security;

import vn.xime.trust.application.port.out.CertificateAuthoritySigner;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;


public class FileSystemCaSigner implements CertificateAuthoritySigner {

    private static final String DEFAULT_PRIVATE_KEY_PATH = "./dev-keys/ca-private-key.pem";
    private static final String DEFAULT_CERT_PATH = "./dev-keys/ca-cert.pem";

    private final PrivateKey privateKey;
    private final X509Certificate caCertificate;
    private final SignatureService signatureService;

    /**
     * Constructor dùng default path (DEV mode)
     */
    public FileSystemCaSigner(
            PemLoader pemLoader,
            SignatureService signatureService
    ) {
        this(
            DEFAULT_PRIVATE_KEY_PATH,
            DEFAULT_CERT_PATH,
            pemLoader,
            signatureService
        );
    }

    /**
     * Constructor cho phép override path (dành cho future / test / prod mock)
     */
    public FileSystemCaSigner(
            String privateKeyPath,
            String certPath,
            PemLoader pemLoader,
            SignatureService signatureService
    ) {
        Objects.requireNonNull(pemLoader, "pemLoader must not be null");
        Objects.requireNonNull(signatureService, "signatureService must not be null");

        try {
            this.privateKey = pemLoader.loadPrivateKey(privateKeyPath);
            this.caCertificate = pemLoader.loadCertificate(certPath);
            this.signatureService = signatureService;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize FileSystemCaSigner. " +
                    "Check dev-keys path and PEM format.",
                    e
            );
        }
    }

    /**
     * Ký dữ liệu (DER của tbsCertificate)
     */
    @Override
    public byte[] sign(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data to sign must not be null or empty");
        }

        return signatureService.sign(
                data,
                privateKey
        );
    }

    /**
     * Trả về CA certificate (issuer)
     */
    @Override
    public X509Certificate getCaCertificate() {
        return caCertificate;
    }
}