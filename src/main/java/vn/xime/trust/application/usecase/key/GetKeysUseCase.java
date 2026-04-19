package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetKeysRequestDto;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.mapper.KeyMapper;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class GetKeysUseCase {

    private final KeyRepository keyRepository;
    private final KeyLifecycleDomainService lifecycleService;
    private final KeyMapper keyMapper;

    public GetKeysUseCase(
            KeyRepository keyRepository,
            KeyLifecycleDomainService lifecycleService,
            KeyMapper keyMapper
    ) {
        this.keyRepository = keyRepository;
        this.lifecycleService = lifecycleService;
        this.keyMapper = keyMapper;
    }

    public KeyResponseDto execute(GetKeysRequestDto query) {

        Instant now = Instant.now();

        // =========================
        // LOAD DATA
        // =========================

        List<Key> keys;

        if (query.getId() != null) {
            // get by id
            Optional<Key> key = keyRepository.findById(query.getId());

            keys = key.map(List::of).orElse(List.of());
        } else {
            keys = keyRepository.findBySignerAndVerifier(
                    query.getSignerServiceId(),
                    query.getVerifierServiceId()
            );
        }

        // =========================
        // FILTER
        // =========================

        keys = keys.stream()
                .filter(k -> query.isIncludeDeleted() || !k.isDeleted())
                .filter(k -> k.getExpiresAt().isAfter(now)) // 🔥 CRITICAL
                .toList();

        // =========================
        // PAGINATION (simple)
        // =========================

        int limit = query.getLimit() > 0 ? query.getLimit() : 50;

        List<Key> page = keys.stream()
                .limit(limit)
                .toList();

        // =========================
        // MAP DTO
        // =========================

        List<KeyResponseDto> result = page.stream()
                .map(k -> keyMapper.toDto(k, query.isIncludePrivate()))
                .toList();

        return new KeyResponseDto(
                result,
                null // TODO: cursor
        );
    }
}