package vn.xime.trust.application.usecase;

import java.util.List;

import vn.xime.trust.application.dto.request.GetKeysRequestDto;
import vn.xime.trust.application.dto.response.GetKeysResponseDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;

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

    // =====================================================
    // MAIN USE CASE (GetKeys)
    // =====================================================

    public GetKeysResponseDto getKeys(GetKeysRequestDto request) {

        List<Key> keys = keyRepository.findAllByService(request.getService())
                .stream()
                .filter(k -> !k.isDeleted())
                .toList();

        // =========================
        // Identity → cần private key
        // =========================
        if (request.isIncludePrivate()) {

            List<PrivateKeyDto> privateKeys = keys.stream()
                    .map(this::toPrivateDto)
                    .toList();

            return new GetKeysResponseDto(
                    null,
                    privateKeys
            );
        }

        // =========================
        // Verify service → chỉ public key
        // =========================
        List<PublicKeyDto> publicKeys = keys.stream()
                .map(this::toPublicDto)
                .toList();

        return new GetKeysResponseDto(
                publicKeys,
                null
        );
    }

    // =====================================================
    // MAPPER
    // =====================================================

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