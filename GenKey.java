import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class GenKey {

    public static void main(String[] args) throws Exception {

        // System.out.println("=== AES ===");
        // System.out.println(generateAesKey());

        // System.out.println("\n=== RSA ===");
        // KeyPair rsa = generateRsaKeyPair();
        // System.out.println("Public:  " + encode(rsa.getPublic().getEncoded()));
        // System.out.println("Private: " + encode(rsa.getPrivate().getEncoded()));

        // System.out.println("\n=== EC ===");
        // KeyPair ec = generateEcKeyPair();
        // System.out.println("Public:  " + encode(ec.getPublic().getEncoded()));
        // System.out.println("Private: " + encode(ec.getPrivate().getEncoded()));

        // System.out.println("\n=== RSA PEM ===");
        // System.out.println(publicKeyToPem(rsa.getPublic()));
        // System.out.println(privateKeyToPem(rsa.getPrivate()));

        // System.out.println("\n=== EC PEM ===");
        // System.out.println(publicKeyToPem(ec.getPublic()));
        // System.out.println(privateKeyToPem(ec.getPrivate()));

        
        // 🔥 bật cái này để generate CA
        generateDevCa();
        System.out.println("✅ Generated CA in ./dev-keys/");
    }

    // =========================
    // 🔥 CA GENERATOR (QUAN TRỌNG)
    // =========================
    public static void generateDevCa() throws Exception {

        // 1. ensure folder
        java.nio.file.Path dir = java.nio.file.Path.of("./dev-keys");
        java.nio.file.Files.createDirectories(dir);

        // 2. generate EC key
        KeyPair keyPair = generateEcKeyPair();

        // 3. create self-signed cert
        X509Certificate cert = createSelfSignedCa(keyPair);

        // 4. write files
        writeToFile("./dev-keys/ca-private-key.pem", privateKeyToPem(keyPair.getPrivate()));
        writeToFile("./dev-keys/ca-cert.pem", certToPem(cert));
    }

    private static X509Certificate createSelfSignedCa(KeyPair keyPair) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Instant now = Instant.now();

        X500Name subject = new X500Name("CN=Trust Root CA, O=Platform, OU=Trust Service");

        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                subject,
                new BigInteger(128, new SecureRandom()),
                Date.from(now),
                Date.from(now.plusSeconds(3650L * 24 * 3600)), // 10 years
                subject,
                org.bouncycastle.asn1.x509.SubjectPublicKeyInfo.getInstance(
                        keyPair.getPublic().getEncoded()
                )
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA")
                .build(keyPair.getPrivate());

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(builder.build(signer));
    }

    private static String certToPem(X509Certificate cert) throws Exception {
        return toPem("CERTIFICATE", cert.getEncoded());
    }

    private static void writeToFile(String path, String content) throws Exception {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        }
    }

    // =========================
    // AES
    // =========================
    public static String generateAesKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 128 / 192 / 256
        SecretKey key = keyGen.generateKey();
        return encode(key.getEncoded());
    }

    // =========================
    // RSA
    // =========================
    public static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // 2048 / 3072 / 4096
        return keyGen.generateKeyPair();
    }

    // =========================
    // EC (ECDSA)
    // =========================
    public static KeyPair generateEcKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");

        // curve phổ biến nhất cho TLS / mTLS
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1"); // aka P-256

        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public static String publicKeyToPem(PublicKey publicKey) {
        return toPem("PUBLIC KEY", publicKey.getEncoded());
    }

    public static String privateKeyToPem(PrivateKey privateKey) {
        return toPem("PRIVATE KEY", privateKey.getEncoded());
    }

    // =========================
    // Utils
    // =========================
    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static String toPem(String type, byte[] derBytes) {
        String base64 = Base64.getEncoder().encodeToString(derBytes);

        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN ").append(type).append("-----\n");

        int index = 0;
        while (index < base64.length()) {
            pem.append(base64, index, Math.min(index + 64, base64.length()));
            pem.append("\n");
            index += 64;
        }

        pem.append("-----END ").append(type).append("-----");
        return pem.toString();
    }
}

// ======================================================
// 0. MỤC TIÊU
// ------------------------------------------------------

// Tạo Root CA (private key + certificate) phục vụ DEV
// mà không cần tích hợp vào project Maven.

// Output:
//   ./dev-keys/ca-private-key.pem
//   ./dev-keys/ca-cert.pem

// ======================================================
// 1. CHUẨN BỊ
// ------------------------------------------------------

// Tạo thư mục:

//   lib/

// Tải 3 thư viện BouncyCastle:

//   bcprov-jdk18on-1.78.1.jar
//   bcpkix-jdk18on-1.78.1.jar
//   bcutil-jdk18on-1.78.1.jar

// Có thể dùng PowerShell:

//   Invoke-WebRequest <url> -OutFile lib/<file>.jar

// (Lưu ý: phải cùng version)

// ======================================================
// 2. VIẾT FILE JAVA
// ------------------------------------------------------

// Tạo file:

//   GenKey.java

// Chức năng:

//   - generate EC key pair
//   - tạo self-signed CA certificate
//   - ghi ra PEM file

// ======================================================
// 3. COMPILE & RUN
// ------------------------------------------------------

// Windows:

//   javac -cp "lib/*" GenKey.java
//   java  -cp ".;lib/*" GenKey

// Linux / Mac:

//   javac -cp "lib/*" GenKey.java
//   java  -cp ".:lib/*" GenKey

// ======================================================
// 4. KẾT QUẢ
// ------------------------------------------------------

// Sau khi chạy:

//   dev-keys/
//     ├── ca-private-key.pem
//     └── ca-cert.pem

// ======================================================
// 5. LỖI THƯỜNG GẶP
// ------------------------------------------------------

// [1] package org.bouncycastle... does not exist
// → thiếu classpath (quên -cp "lib/*")

// [2] NoClassDefFoundError: EdECObjectIdentifiers
// → thiếu bcutil jar

// [3] VSCode vẫn lỗi
// → Code Runner không dùng classpath
// → phải config hoặc chạy bằng terminal

// ======================================================
// 6. LƯU Ý
// ------------------------------------------------------

// - Chỉ dùng cho DEV
// - Không dùng CA này cho production
// - Production phải dùng HSM / KMS

// ======================================================
// END
// ======================================================