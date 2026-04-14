package vn.xime.trust.application.usecase;

import vn.xime.trust.application.dto.request.GetKeysRequestDto;
import vn.xime.trust.application.dto.response.GetKeysResponseDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;

import java.time.Instant;
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

    public GetKeysResponseDto getKeys(GetKeysRequestDto request) {

        Instant now = Instant.now();

        // 1. Load keys
        List<Key> keys = keyRepository.findByServiceId(request.getService())
                .stream()
                .filter(k -> !k.isDeleted())
                .toList();

        // =========================
        // PRIVATE KEY (Identity only)
        // =========================
        if (request.isIncludePrivate()) {

            // ⚠️ SECURITY CHECK (bắt buộc)
        //     if (!request.isInternalCall()) {
        //         throw new SecurityException("Private key access denied");
        //     }

            List<PrivateKeyDto> privateKeys = keys.stream()
                    .filter(k -> !k.isExpiredAt(now)) // chỉ key còn valid
                    .map(this::toPrivateDto)
                    .toList();

            return new GetKeysResponseDto(null, privateKeys);
        }

        // =========================
        // PUBLIC KEY (Verify services)
        // =========================
        List<PublicKeyDto> publicKeys = keys.stream()
                .filter(k -> !k.isExpiredAt(now)) // chỉ key còn verify được
                .map(this::toPublicDto)
                .toList();

        return new GetKeysResponseDto(publicKeys, null);
    }

    // =========================
    // MAPPER
    // =========================

    private PublicKeyDto toPublicDto(Key key) {
        return new PublicKeyDto(
                key.getKid(),
                key.getPublicKey(),
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }

    private PrivateKeyDto toPrivateDto(Key key) {

        String decryptedPrivateKey = encryptionService.decrypt(
                key.getPrivateKeyEncrypted()
        );

        return new PrivateKeyDto(
                key.getKid(),
                key.getPublicKey(),
                decryptedPrivateKey,
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }
}