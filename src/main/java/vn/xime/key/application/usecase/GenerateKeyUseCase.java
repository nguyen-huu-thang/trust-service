package vn.xime.key.application.usecase;

import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.application.port.KeyGenerator;
import vn.xime.key.domain.key.*;

import java.security.KeyPair;
import java.time.Instant;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GenerateKeyUseCase {

    private static final int KEY_SIZE = 2048;

    // ⚠️ config nên external sau
    private static final Duration KEY_ACTIVATION_DELAY = Duration.ofMinutes(5);
    private static final Duration JWT_TTL = Duration.ofHours(1);

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

    // =====================================================
    // MAIN LOGIC
    // =====================================================

    public Key execute(String serviceName) {

        Instant now = Instant.now();

        // 1. Load all existing keys
        List<Key> existingKeys = keyRepository.findAllByService(serviceName);

        // 2. Find latest key (the one with highest activateAt)
        Key latestKey = existingKeys.stream()
                .filter(k -> !k.isDeleted())
                .filter(k -> k.getActivateAt() != null)
                .max(Comparator.comparing(Key::getActivateAt))
                .orElse(null);

        // 3. Generate new key pair
        KeyPair keyPair = keyGenerator.generate(KEY_SIZE);

        String publicKey = encodePublicKey(keyPair);
        String privateKeyRaw = encodePrivateKey(keyPair);

        // 4. Encrypt private key
        String privateKeyEncrypted = encryptionService.encrypt(privateKeyRaw);

        // 5. Decide activateAt
        Instant activateAt;

        if (latestKey == null) {
            // first key → active ngay
            activateAt = now;
        } else {
            // schedule key mới
            activateAt = latestKey.getActivateAt().plus(KEY_ACTIVATION_DELAY);
        }

        // 6. Build new key
        Key newKey = new Key(
                generateKid(serviceName),
                serviceName,
                publicKey,
                privateKeyEncrypted,
                KeyAlgorithm.RSA,
                KEY_SIZE,
                KeyStatus.NEXT, // metadata only
                now,
                activateAt,
                null,
                false
        );

        // 7. Update previous key (set expiresAt)
        if (latestKey != null) {
            Instant expiresAt = activateAt.plus(JWT_TTL);

            Key updatedOldKey = new Key(
                    latestKey.getKid(),
                    latestKey.getServiceName(),
                    latestKey.getPublicKey(),
                    latestKey.getPrivateKeyEncrypted(),
                    latestKey.getAlgorithm(),
                    latestKey.getKeySize(),
                    latestKey.getStatus(),
                    latestKey.getCreatedAt(),
                    latestKey.getActivateAt(),
                    expiresAt,
                    latestKey.isDeleted()
            );

            keyRepository.save(updatedOldKey);
        }

        // 8. Save new key
        keyRepository.save(newKey);

        return newKey;
    }

    // =====================================================
    // HELPER
    // =====================================================

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