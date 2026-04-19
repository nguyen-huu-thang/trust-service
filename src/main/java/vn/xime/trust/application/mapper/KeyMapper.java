package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.service.IdService;

@Component
public class KeyMapper {

    public KeyResponseDto toResponseDto(Key key) {
        return new KeyResponseDto(
                IdService.toString(key.getId()),
                key.getSignerServiceId(),
                key.getVerifierServiceId(),
                key.getAlgorithm().name(),
                key.getKeySize(),
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }

    public PrivateKeyDto toPrivateKeyDto(Key key, String decryptedPrivateKey) {
        return new PrivateKeyDto(
                IdService.toString(key.getId()),
                key.getSignerServiceId(),
                key.getAlgorithm().name(),
                key.getKeySize(),
                decryptedPrivateKey,
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }

    public PublicKeyDto toPublicKeyDto(Key key) {
        return new PublicKeyDto(
                IdService.toString(key.getId()),
                key.getVerifierServiceId(),
                key.getAlgorithm().name(),
                key.getKeySize(),
                key.getPublicKey(),
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }
}