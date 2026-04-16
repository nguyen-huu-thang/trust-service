package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetKeysQuery;
import vn.xime.trust.application.dto.response.KeyDto;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
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

    public List<KeyDto> execute(GetKeysQuery query) {

        List<Key> keys = keyRepository.findBySignerAndVerifier(
                query.getSignerServiceId(),
                query.getVerifierServiceId()
        );

        Instant now = Instant.now();

        return lifecycleService
                .filterNotDeleted(keys)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private KeyDto toDto(Key k) {
        return new KeyDto(
                k.getKid(),
                k.getPublicKey(),
                k.getAlgorithm().name(),
                k.getKeySize(),
                k.getActivateAt(),
                k.getExpiresAt()
        );
    }
}