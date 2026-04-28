package vn.xime.trust.infrastructure.crypto;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

import java.security.cert.X509Certificate;
import java.security.Security;

/**
 * Assembler tạo X509Certificate hoàn chỉnh từ:
 *  - TBSCertificate (builder)
 *  - ContentSigner (đã gắn với CA private key)
 *
 * Trách nhiệm:
 *  - KHÔNG biết private key
 *  - KHÔNG build TBS
 *  - chỉ assemble + convert
 */
public class X509CertificateAssembler {

    public X509CertificateAssembler() {
        // đảm bảo BC provider có mặt
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    /**
     * Assemble certificate hoàn chỉnh
     *
     * @param builder X509v3CertificateBuilder (TBS)
     * @param signer  ContentSigner (đã cấu hình thuật toán + private key)
     * @return X509Certificate (Java)
     */
    public X509Certificate assemble(
            X509v3CertificateBuilder builder,
            ContentSigner signer
    ) {
        try {
            // ===== Build certificate (sign happens here internally) =====
            X509CertificateHolder holder = builder.build(signer);

            // ===== Convert sang Java X509Certificate =====
            return new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(holder);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to assemble X509Certificate", e);
        }
    }
}