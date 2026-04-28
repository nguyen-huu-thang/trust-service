package vn.xime.trust.infrastructure.crypto;

import java.util.Objects;
import java.util.Base64;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;

import vn.xime.trust.application.port.out.CertificateIssuer;
import vn.xime.trust.application.port.out.CertificateAuthoritySigner;



/**
 * Default implementation của CertificateIssuer.
 *
 * Orchestrate toàn bộ flow:
 *  - build TBS
 *  - sign
 *  - assemble
 *  - encode
 *
 * Application layer KHÔNG biết các bước này.
 */
public class DefaultCertificateIssuer implements CertificateIssuer {

    private final CertificateAuthoritySigner caSigner;
    private final X509CertificateBuilder tbsBuilder;
    private final X509CertificateAssembler assembler;

    public DefaultCertificateIssuer(
            CertificateAuthoritySigner caSigner,
            X509CertificateBuilder tbsBuilder,
            X509CertificateAssembler assembler
    ) {
        this.caSigner = Objects.requireNonNull(caSigner);
        this.tbsBuilder = Objects.requireNonNull(tbsBuilder);
        this.assembler = Objects.requireNonNull(assembler);
    }

    @Override
    public IssuedCertificate issue(IssueCommand command) {

        validate(command);

        // =========================
        // 1. Resolve issuer (từ CA cert)
        // =========================

        X509Certificate caCert = caSigner.getCaCertificate();

        X500Name issuer = X500Name.getInstance(
                caCert.getSubjectX500Principal().getEncoded()
        );

        // =========================
        // 2. Build TBSCertificate
        // =========================

        X509v3CertificateBuilder tbs = tbsBuilder.build(
                command.serviceId(),
                command.spiffeId(),
                decodePublicKey(command.publicKey()),
                command.notBefore(),
                command.notAfter(),
                issuer
        );

        // =========================
        // 3. Create ContentSigner
        // =========================

        ContentSigner contentSigner = ContentSignerAdapter.from(caSigner);

        // =========================
        // 4. Assemble Certificate
        // =========================

        X509Certificate certificate = assembler.assemble(tbs, contentSigner);

        // =========================
        // 5. Encode
        // =========================

        String certBase64 = encodeBase64(certificate);

        // =========================
        // 6. Serial number (optional)
        // =========================

        String serial = certificate.getSerialNumber().toString();

        return new IssuedCertificate(certBase64, serial);
    }

    // =========================
    // Helpers
    // =========================

    private void validate(IssueCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("command must not be null");
        }
        if (cmd.serviceId() == null || cmd.serviceId().isBlank()) {
            throw new IllegalArgumentException("serviceId is required");
        }
        if (cmd.publicKey() == null || cmd.publicKey().isBlank()) {
            throw new IllegalArgumentException("publicKey is required");
        }
        if (cmd.notBefore() == null || cmd.notAfter() == null) {
            throw new IllegalArgumentException("validity is required");
        }
        if (cmd.notAfter().isBefore(cmd.notBefore())) {
            throw new IllegalArgumentException("notAfter must be after notBefore");
        }
    }

    private String encodeBase64(X509Certificate cert) {
        try {
            byte[] der = cert.getEncoded();
            return Base64.getEncoder().encodeToString(der);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode certificate", e);
        }
    }

    /**
     * Decode public key từ Base64 → PublicKey
     */
    private java.security.PublicKey decodePublicKey(String publicKey) {
        byte[] bytes = Base64.getDecoder().decode(publicKey);

        try {
            return java.security.KeyFactory.getInstance("EC")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(bytes));
        } catch (Exception ignored) {
            try {
                return java.security.KeyFactory.getInstance("RSA")
                        .generatePublic(new java.security.spec.X509EncodedKeySpec(bytes));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to decode public key", e);
            }
        }
    }
}