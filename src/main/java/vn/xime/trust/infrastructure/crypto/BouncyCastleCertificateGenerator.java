package vn.xime.trust.infrastructure.crypto;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import vn.xime.trust.application.port.out.CertGenerator;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class BouncyCastleCertificateGenerator implements CertGenerator {

    private final PrivateKey caPrivateKey;
    private final X509Certificate caCertificate;

    @Override
    public GeneratedCertificate generate(
            String serviceId,
            KeyPair keyPair,
            Instant expiresAt
    ) {

        try {

            Instant now = Instant.now();

            X500Principal subject = new X500Principal("CN=" + serviceId);

            var builder = new JcaX509v3CertificateBuilder(
                    caCertificate,
                    BigInteger.valueOf(System.currentTimeMillis()),
                    Date.from(now),
                    Date.from(expiresAt),
                    subject,
                    keyPair.getPublic()
            );

            // 🔥 TODO: add SAN (service_id)
            // Extension SAN rất quan trọng, bạn nên add sau

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                    .build(caPrivateKey);

            X509CertificateHolder holder = builder.build(signer);

            X509Certificate cert = new JcaX509CertificateConverter()
                    .getCertificate(holder);

            return new GeneratedCertificate(
                    toPem(cert),
                    toPem(keyPair.getPrivate())
            );

        } catch (Exception e) {
            throw new RuntimeException("Cannot generate certificate", e);
        }
    }

    private String toPem(Object obj) {
        try (var writer = new java.io.StringWriter();
             var pemWriter = new org.bouncycastle.openssl.jcajce.JcaPEMWriter(writer)) {

            pemWriter.writeObject(obj);
            pemWriter.flush();

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}