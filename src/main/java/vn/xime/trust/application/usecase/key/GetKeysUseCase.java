package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.application.port.out.KeyEncryptionService;
import vn.xime.trust.application.mapper.KeyMapper;


import java.util.List;

@Component
public class GetKeysUseCase {

    private final KeyRepository keyRepository;
    private final KeyMapper keyMapper;
    private final KeyEncryptionService keyEncryptionService;


    public GetKeysUseCase(
            KeyRepository keyRepository,
            KeyMapper keyMapper,
            KeyEncryptionService keyEncryptionService
    ) {
        this.keyRepository = keyRepository;
        this.keyMapper = keyMapper;
        this.keyEncryptionService = keyEncryptionService;
    }

    // ==================================================
    // ADMIN
    // ==================================================

    public KeyResponseDto getById(Id id) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        Key key = keyRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Key not found"));

        return keyMapper.toResponseDto(key);
    }

    public List<KeyResponseDto> getBySigner(String signerServiceId) {
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        return keyRepository.findBySignerServiceId(signerServiceId)
                .stream()
                .map(keyMapper::toResponseDto)
                .toList();
    }

    public List<KeyResponseDto> getBySignerAndVerifier(
            String signerServiceId,
            String verifierServiceId
    ) {
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        return keyRepository.findBySignerAndVerifier(
                        signerServiceId,
                        verifierServiceId
                )
                .stream()
                .map(keyMapper::toResponseDto)
                .toList();
    }

    // ==================================================
    // SIGNING (PRIVATE KEY)
    // ==================================================

    public List<PrivateKeyDto> getActiveForSigning(String signerServiceId) {
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        return keyRepository.findActiveKeysBySigner(signerServiceId)
                .stream()
                .map(key -> {

                    if (key.getPrivateKeyEncrypted() == null) {
                        throw new IllegalStateException("Private key is missing");
                    }

                    String decrypted = keyEncryptionService.decrypt(
                            key.getPrivateKeyEncrypted()
                    );

                    return keyMapper.toPrivateKeyDto(key, decrypted);
                })
                .toList();
    }

    // ==================================================
    // VERIFYING (PUBLIC KEY)
    // ==================================================

    public List<PublicKeyDto> getActiveForVerifier(String verifierServiceId) {
        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        return keyRepository.findActiveKeysByVerifier(verifierServiceId)
                .stream()
                .map(keyMapper::toPublicKeyDto)
                .toList();
    }
}