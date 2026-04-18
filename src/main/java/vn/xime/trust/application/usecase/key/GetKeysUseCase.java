package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetKeysRequestDto;
import vn.xime.trust.application.dto.response.KeysResponseDto;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;
import vn.xime.trust.domain.service.IdService;

import java.util.List;

@Component
public class GetKeysUseCase {

    private final KeyRepository keyRepository;
    private final KeyLifecycleDomainService lifecycleService;

    public GetKeysUseCase(
            KeyRepository keyRepository,
            KeyLifecycleDomainService lifecycleService
    ) {
        this.keyRepository = keyRepository;
        this.lifecycleService = lifecycleService;
    }

    public List<KeysResponseDto> execute(GetKeysRequestDto query) {

        List<Key> keys = keyRepository.findBySignerAndVerifier(
                query.getSignerServiceId(),
                query.getVerifierServiceId()
        );

        return lifecycleService
                .filterNotDeleted(keys)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private KeysResponseDto toDto(Key k) {
        return new KeysResponseDto(
                IdService.toBase62(k.getId()),
                k.getPublicKey(),
                k.getAlgorithm().name(),
                k.getKeySize(),
                k.getActivateAt(),
                k.getExpiresAt()
        );
    }
}