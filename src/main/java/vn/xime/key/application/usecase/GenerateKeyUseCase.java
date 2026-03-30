package vn.xime.key.application.usecase;

import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.application.port.KeyGenerator;
import vn.xime.key.domain.key.*;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class GenerateKeyUseCase {

    private final KeyRepository keyRepository;
    private final KeyGenerator keyGenerator;
    private final KeyEncryptionService encryptionService;

    public GenerateKeyUseCase(
            KeyRepository keyRepository,
            KeyGenerator keyGenerator,
            KeyEncryptionService encryptionService
    ) {
        this.keyRepository = keyRepository;
        this.keyGenerator = keyGenerator;
        this.encryptionService = encryptionService;
    }

    // =========================
    // MAIN LOGIC
    // =========================

    public Key execute(String serviceName) {

        // 1. Check existing CURRENT
        Optional<Key> currentOpt = keyRepository.findCurrent(serviceName);

        // 2. Generate RSA key pair
        KeyPair keyPair = keyGenerator.generate(2048);

        String publicKey = encodePublicKey(keyPair);
        String privateKeyRaw = encodePrivateKey(keyPair);

        // 3. Encrypt private key
        String privateKeyEncrypted = encryptionService.encrypt(privateKeyRaw);

        // 4. Build Key domain object
        Key newKey = buildKey(
                serviceName,
                publicKey,
                privateKeyEncrypted,
                currentOpt.isEmpty()
        );

        // 5. Save
        keyRepository.save(newKey);

        return newKey;
    }

    // =========================
    // BUILD KEY
    // =========================

    private Key buildKey(
            String serviceName,
            String publicKey,
            String privateKeyEncrypted,
            boolean isFirstKey
    ) {

        Instant now = Instant.now();

        KeyStatus status = isFirstKey
                ? KeyStatus.CURRENT
                : KeyStatus.NEXT;

        return new Key(
                generateKid(serviceName),
                serviceName,
                publicKey,
                privateKeyEncrypted,
                KeyAlgorithm.RSA,
                2048,
                status,
                now,
                isFirstKey ? now : null, // CURRENT activate ngay
                null,                   // chưa cần expires
                false
        );
    }

    // =========================
    // HELPER
    // =========================

    private String generateKid(String serviceName) {
        return serviceName + "-" + UUID.randomUUID();
    }

    private String encodePublicKey(KeyPair keyPair) {
        return java.util.Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());
    }

    private String encodePrivateKey(KeyPair keyPair) {
        return java.util.Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());
    }
}