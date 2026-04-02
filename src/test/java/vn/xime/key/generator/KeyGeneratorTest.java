package vn.xime.key.generator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.application.port.KeyGenerator;
import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyAlgorithm;
import vn.xime.key.domain.key.KeyRepository;
import vn.xime.key.domain.key.KeyStatus;

import java.security.KeyPair;
import java.time.Instant;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@SpringBootTest
class KeyGeneratorTest {

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private KeyEncryptionService encryptionService;

    @Autowired
    private KeyRepository keyRepository;

    @Test
    void generateKey() {
        String serviceName = "identity-service";

        // =========================
        // 1. Generate key pair
        // =========================
        KeyPair keyPair = keyGenerator.generate(2048);

        // 👉 encode tại đây (đúng design)
        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        // =========================
        // 2. Encrypt private key
        // =========================
        String encryptedPrivateKey = encryptionService.encrypt(privateKey);

        // =========================
        // 3. Set time
        // =========================
        Instant now = Instant.now();

        Instant activateAt = now;
        Instant expiresAt = now.plus(Duration.ofHours(2));

        // =========================
        // 4. Create entity
        // =========================
        Key key = new Key(
                UUID.randomUUID().toString(),
                serviceName,
                publicKey,
                encryptedPrivateKey,
                KeyAlgorithm.RSA,
                2048,
                KeyStatus.CURRENT,
                now,
                activateAt,
                expiresAt,
                false
        );

        // =========================
        // 5. Save DB
        // =========================
        keyRepository.save(key);

        System.out.println("=== GENERATED KEY ===");
        System.out.println("kid: " + key.getKid());
        System.out.println("activateAt: " + activateAt);
        System.out.println("expiresAt: " + expiresAt);
    }

    @Test
    void generateNextKey() {
        String serviceName = "identity-service";

        KeyPair keyPair = keyGenerator.generate(2048);

        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        String encryptedPrivateKey = encryptionService.encrypt(privateKey);

        Instant now = Instant.now();

        Instant activateAt = now.plus(Duration.ofMinutes(10)); // KEY TƯƠNG LAI
        Instant expiresAt = now.plus(Duration.ofHours(3));

        Key key = new Key(
                UUID.randomUUID().toString(),
                serviceName,
                publicKey,
                encryptedPrivateKey,
                KeyAlgorithm.RSA,
                2048,
                KeyStatus.NEXT,
                now,
                activateAt,
                expiresAt,
                false
        );

        keyRepository.save(key);

        System.out.println("=== GENERATED NEXT KEY ===");
    }
}