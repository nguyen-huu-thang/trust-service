package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.service.IdService;

@Component
public class KeyMapper {

    public KeyResponseDto toDto(Key key, boolean includePrivate) {
        return new KeyResponseDto(
                IdService.toBase62(key.getId()),
                key.getSignerServiceId(),
                key.getVerifierServiceId(),
                key.getPublicKey(),
                includePrivate ? key.getPrivateKeyEncrypted() : null,
                key.getAlgorithm().name(),
                key.getKeySize(),
                key.getActivateAt(),
                key.getExpiresAt()
        );
    }
}