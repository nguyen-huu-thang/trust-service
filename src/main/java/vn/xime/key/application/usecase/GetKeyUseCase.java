package vn.xime.key.application.usecase;

import vn.xime.key.application.port.KeyEncryptionService;
import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyRepository;

import java.util.List;

public class GetKeyUseCase {

    private final KeyRepository keyRepository;
    private final KeyEncryptionService encryptionService;

    public GetKeyUseCase(
            KeyRepository keyRepository,
            KeyEncryptionService encryptionService
    ) {
        this.keyRepository = keyRepository;
        this.encryptionService = encryptionService;
    }

    // =========================
    // 1. Dùng cho Identity (cần private key)
    // =========================

    public Key getCurrentKeyWithPrivate(String serviceName) {

        Key key = keyRepository.findCurrent(serviceName)
                .orElseThrow(() -> new IllegalStateException(
                        "No CURRENT key for service: " + serviceName
                ));

        // decrypt private key trước khi trả
        String decryptedPrivateKey = encryptionService.decrypt(
                key.getPrivateKeyEncrypted()
        );

        // ⚠️ tạo object mới (không mutate domain)
        return new Key(
                key.getKid(),
                key.getServiceName(),
                key.getPublicKey(),
                decryptedPrivateKey, // trả raw private key
                key.getAlgorithm(),
                key.getKeySize(),
                key.getStatus(),
                key.getCreatedAt(),
                key.getActivateAt(),
                key.getExpiresAt(),
                key.isDeleted()
        );
    }

    // =========================
    // 2. Dùng cho service verify JWT
    // =========================

    public List<Key> getPublicKeys(String serviceName) {
        return keyRepository.findPublicKeys(serviceName);
        // CURRENT + OLD
    }
}