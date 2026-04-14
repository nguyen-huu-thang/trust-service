package vn.xime.trust.application.usecase;

import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.port.out.KeyGenerator;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyAlgorithm;
import vn.xime.trust.domain.repository.KeyRepository;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GenerateKeyUseCase {

    private static final int KEY_SIZE = 2048;

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

    public Key execute(String serviceId) {

        Instant now = Instant.now();

        // 1. Load keys
        List<Key> keys = keyRepository.findByServiceId(serviceId);

        // 2. Find latest (max activateAt)
        Key latestKey = keys.stream()
                .filter(k -> !k.isDeleted())
                .max(Comparator.comparing(Key::getActivateAt))
                .orElse(null);

        // 3. Generate keypair
        KeyPair keyPair = keyGenerator.generate(KEY_SIZE);

        String publicKey = encodePublicKey(keyPair);
        String privateKeyRaw = encodePrivateKey(keyPair);

        String privateKeyEncrypted = encryptionService.encrypt(privateKeyRaw);

        // 4. Decide activateAt
        Instant activateAt = (latestKey == null)
                ? now
                : latestKey.getActivateAt().plus(KEY_ACTIVATION_DELAY);

        // 5. Create new key
        Key newKey = new Key(
                generateKid(serviceId),
                serviceId,
                publicKey,
                privateKeyEncrypted,
                KeyAlgorithm.RSA,
                KEY_SIZE,
                now,
                activateAt,
                null,
                false
        );

        // 6. Update old key expiresAt
        if (latestKey != null) {

            Instant expiresAt = activateAt.plus(JWT_TTL);

            Key updatedOldKey = new Key(
                    latestKey.getKid(),
                    latestKey.getServiceId(),
                    latestKey.getPublicKey(),
                    latestKey.getPrivateKeyEncrypted(),
                    latestKey.getAlgorithm(),
                    latestKey.getKeySize(),
                    latestKey.getCreatedAt(),
                    latestKey.getActivateAt(),
                    expiresAt,
                    latestKey.isDeleted()
            );

            keyRepository.save(updatedOldKey);
        }

        // 7. Save new key
        keyRepository.save(newKey);

        return newKey;
    }

    private String generateKid(String serviceId) {
        return serviceId + "-" + UUID.randomUUID();
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