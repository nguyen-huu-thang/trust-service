package vn.xime.trust.infrastructure.security;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;

public class PemLoader {

    static {
        // đảm bảo BouncyCastle được register
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    /**
     * Load private key từ PEM file
     * Hỗ trợ:
     *  - PKCS#8 (BEGIN PRIVATE KEY)
     *  - PKCS#1 (BEGIN RSA PRIVATE KEY)
     *  - EC PRIVATE KEY
     */
    public PrivateKey loadPrivateKey(String path) {
        Objects.requireNonNull(path, "Private key path must not be null");

        Path filePath = Path.of(path);

        if (!Files.exists(filePath)) {
            throw new IllegalStateException("Private key file not found: " + path);
        }

        try (Reader reader = new FileReader(filePath.toFile());
            PEMParser pemParser = new PEMParser(reader)) {

            Object object = pemParser.readObject();

            if (object == null) {
                throw new IllegalStateException("Empty PEM file: " + path);
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
                    .setProvider("BC");

            // CASE 1: PKCS#1 hoặc keypair
            if (object instanceof PEMKeyPair pemKeyPair) {
                return converter.getKeyPair(pemKeyPair).getPrivate();
            }

            // CASE 2: PKCS#8
            if (object instanceof PrivateKeyInfo privateKeyInfo) {
                return converter.getPrivateKey(privateKeyInfo);
            }

            throw new IllegalStateException(
                    "Unsupported private key format in file: " + path +
                    " (type: " + object.getClass().getName() + ")"
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read private key from: " + path,
                    e
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse private key from: " + path,
                    e
            );
        }
    }

    /**
     * Load X.509 certificate từ PEM file
     */
    public X509Certificate loadCertificate(String path) {
        Objects.requireNonNull(path, "Certificate path must not be null");

        Path filePath = Path.of(path);

        if (!Files.exists(filePath)) {
            throw new IllegalStateException("Certificate file not found: " + path);
        }

        try (var inputStream = Files.newInputStream(filePath)) {

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(inputStream);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load X509 certificate from: " + path,
                    e
            );
        }
    }
}