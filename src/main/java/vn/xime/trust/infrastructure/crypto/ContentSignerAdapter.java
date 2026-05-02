package vn.xime.trust.infrastructure.crypto;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import vn.xime.trust.application.port.out.CertificateAuthoritySigner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

/**
 * Adapter chuyển CertificateAuthoritySigner → ContentSigner (BouncyCastle)
 */
public class ContentSignerAdapter implements ContentSigner {

    private final CertificateAuthoritySigner caSigner;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final AlgorithmIdentifier algorithmIdentifier;

    private ContentSignerAdapter(
            CertificateAuthoritySigner caSigner,
            String algorithm
    ) {
        this.caSigner = caSigner;
        this.algorithmIdentifier =
                new DefaultSignatureAlgorithmIdentifierFinder().find(algorithm);
    }

    public static ContentSigner from(CertificateAuthoritySigner caSigner) {
        String algorithm = resolveAlgorithm(caSigner.getCaCertificate());
        return new ContentSignerAdapter(caSigner, algorithm);
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return algorithmIdentifier;
    }

    @Override
    public OutputStream getOutputStream() {
        return buffer;
    }

    @Override
    public byte[] getSignature() {
        byte[] dataToSign = buffer.toByteArray();
        return caSigner.sign(dataToSign);
    }

    /**
     * Resolve thuật toán ký từ CA certificate
     */
    private static String resolveAlgorithm(X509Certificate caCert) {
        String keyAlgo = caCert.getPublicKey().getAlgorithm();

        return switch (keyAlgo) {
            case "RSA" -> "SHA256withRSA";
            case "EC", "ECDSA"  -> "SHA256withECDSA";
            default -> throw new IllegalStateException(
                    "Unsupported CA key algorithm: " + keyAlgo
            );
        };
    }
}