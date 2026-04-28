package vn.xime.trust.infrastructure.crypto;


import org.bouncycastle.asn1.x509.*;


import java.security.PublicKey;

/**
 * Factory tạo X.509 Extensions cho mTLS certificate.
 *
 * Bao gồm:
 *  - Subject Alternative Name (SPIFFE ID)
 *  - KeyUsage
 *  - ExtendedKeyUsage
 *  - BasicConstraints
 */
public class X509ExtensionsFactory {

    public Extensions createExtensions(
            String serviceId,
            String spiffeId,
            PublicKey publicKey
    ) {

        try {
            ExtensionsGenerator generator = new ExtensionsGenerator();

            // =========================
            // 1. Subject Alternative Name (SAN)
            // =========================
            GeneralName san = new GeneralName(
                    GeneralName.uniformResourceIdentifier,
                    spiffeId
            );

            GeneralNames subjectAltNames = new GeneralNames(san);

            generator.addExtension(
                    Extension.subjectAlternativeName,
                    false,
                    subjectAltNames
            );

            // =========================
            // 2. Key Usage
            // =========================
            int keyUsageBits = KeyUsage.digitalSignature;

            // Nếu là RSA thì thêm keyEncipherment
            if ("RSA".equalsIgnoreCase(publicKey.getAlgorithm())) {
                keyUsageBits |= KeyUsage.keyEncipherment;
            }

            generator.addExtension(
                    Extension.keyUsage,
                    true, // critical
                    new KeyUsage(keyUsageBits)
            );

            // =========================
            // 3. Extended Key Usage (EKU)
            // =========================
            KeyPurposeId[] eku = new KeyPurposeId[]{
                    KeyPurposeId.id_kp_clientAuth,
                    KeyPurposeId.id_kp_serverAuth
            };

            generator.addExtension(
                    Extension.extendedKeyUsage,
                    false,
                    new ExtendedKeyUsage(eku)
            );

            // =========================
            // 4. Basic Constraints
            // =========================
            generator.addExtension(
                    Extension.basicConstraints,
                    true, // critical
                    new BasicConstraints(false) // leaf cert (not CA)
            );

            // =========================
            // 5. Subject Key Identifier (optional nhưng nên có)
            // =========================
            SubjectPublicKeyInfo spki =
                    SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

            SubjectKeyIdentifier ski =
                    new SubjectKeyIdentifier(spki.getEncoded());

            generator.addExtension(
                    Extension.subjectKeyIdentifier,
                    false,
                    ski
            );

            return generator.generate();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create X509 extensions", e);
        }
    }
}