package vn.xime.trust.infrastructure.crypto;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;

/**
 * Builder tạo TBSCertificate (X.509 v3).
 *
 * Trách nhiệm:
 *  - build cấu trúc certificate (tbsCertificate)
 *  - KHÔNG ký
 *  - KHÔNG dùng private key
 */
public class X509CertificateBuilder {

    private final X509ExtensionsFactory extensionsFactory;
    private final X509NameFactory nameFactory;

    public X509CertificateBuilder(
            X509ExtensionsFactory extensionsFactory,
            X509NameFactory nameFactory
    ) {
        this.extensionsFactory = extensionsFactory;
        this.nameFactory = nameFactory;
    }

    /**
     * Build X509v3CertificateBuilder (chưa ký)
     */
    public X509v3CertificateBuilder build(
            String serviceId,
            String spiffeId,
            PublicKey publicKey,
            Instant notBefore,
            Instant notAfter,
            X500Name issuer
    ) {

        // ===== Subject =====
        X500Name subject = nameFactory.createSubject(serviceId);

        // ===== Serial Number =====
        BigInteger serialNumber = generateSerialNumber();

        // ===== Validity =====
        Date startDate = Date.from(notBefore);
        Date endDate = Date.from(notAfter);

        // ===== Core Builder =====
        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                issuer,
                serialNumber,
                startDate,
                endDate,
                subject,
                org.bouncycastle.asn1.x509.SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())
        );

        // ===== Extensions =====
        Extensions extensions = extensionsFactory.createExtensions(
                serviceId,
                spiffeId,
                publicKey
        );

        Enumeration<?> extensionOids = extensions.oids();
        while (extensionOids.hasMoreElements()) {
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) extensionOids.nextElement();
            Extension extension = extensions.getExtension(oid);
            try {
                builder.addExtension(
                        oid,
                        extension.isCritical(),
                        extension.getParsedValue()
                );
            } catch (Exception e) {
                throw new IllegalStateException("Failed to add extension: " + oid, e);
            }
        }

        return builder;
    }

    /**
     * Sinh serial number (random 128-bit)
     */
    private BigInteger generateSerialNumber() {
        return new BigInteger(128, new SecureRandom()).abs();
    }
}
