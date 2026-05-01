package vn.xime.trust.application.usecase.key;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import vn.xime.trust.application.port.in.EnsureKeyContinuityUseCase;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class EnsureKeyContinuityUseCaseImpl implements EnsureKeyContinuityUseCase {

    private final KeyPolicyRepository keyPolicyRepository;
    private final KeyRepository keyRepository;

    private final GenerateKeyUseCase generateKeyUseCase;
    private final KeyLifecycleDomainService keyLifecycleDomainService;

    public EnsureKeyContinuityUseCaseImpl(
        KeyPolicyRepository keyPolicyRepository,
        KeyRepository keyRepository,
        GenerateKeyUseCase generateKeyUseCase,
        KeyLifecycleDomainService keyLifecycleDomainService
    ) {
        this.keyPolicyRepository = keyPolicyRepository;
        this.keyRepository = keyRepository;
        this.generateKeyUseCase = generateKeyUseCase;
        this.keyLifecycleDomainService = keyLifecycleDomainService;
    }

    @Override
    @Transactional
    public void execute() {

        Instant now = Instant.now();

        // =========================
        // LOAD ALL POLICIES
        // =========================

        List<KeyPolicy> policies = keyPolicyRepository.findAll();

        for (KeyPolicy policy : policies) {

            handlePolicy(policy, now);
        }
    }

    // =========================================================
    // CORE LOGIC PER POLICY
    // =========================================================

    private void handlePolicy(KeyPolicy policy, Instant now) {

        // =========================
        // LOAD KEYS
        // =========================

        List<Key> keys = keyRepository.findBySignerAndVerifier(
            policy.getSignerServiceId(),
            policy.getVerifierServiceId()
        );

        // =========================
        // FILTER + SORT
        // =========================

        List<Key> activeKeys = keyLifecycleDomainService.getAllActive(keys)
            .stream()
            .sorted(Comparator.comparing(Key::getActivateAt))
            .toList();

        if (activeKeys.isEmpty()) {
            // chưa có key nào → bỏ qua (admin phải tạo key đầu tiên)
            return;
        }

        Key lastKey = activeKeys.get(activeKeys.size() - 1);

        // =========================
        // FUTURE KEYS
        // =========================

        List<Key> futureKeys = activeKeys.stream()
            .filter(k -> k.getActivateAt().isAfter(now))
            .toList();

        int futureCount = futureKeys.size();

        // =====================================================
        // CASE 1: KHÔNG CÓ NEXT KEY
        // =====================================================

        if (futureCount == 0) {

            Instant newActivateAt = lastKey.getActivateAt()
                .plusSeconds(policy.getRotationIntervalSeconds());

            generate(policy, newActivateAt);
            return;
        }

        // =====================================================
        // CASE 2: CÓ NEXT KEY
        // =====================================================

        Key nextKey = futureKeys.get(0);

        long timeToNext = nextKey.getActivateAt().getEpochSecond() - now.getEpochSecond();

        long preload = policy.getPreloadSeconds();

        // =====================================================
        // CASE 2.1: CHƯA VÀO PRELOAD WINDOW
        // =====================================================

        if (timeToNext > preload) {
            // chỉ cần >= 1 future key → OK
            return;
        }

        // =====================================================
        // CASE 2.2: ĐANG TRONG PRELOAD WINDOW
        // =====================================================

        if (futureCount < 2) {

            Instant newActivateAt = lastKey.getActivateAt()
                .plusSeconds(policy.getRotationIntervalSeconds());

            generate(policy, newActivateAt);
        }
    }

    // =========================================================
    // GENERATE HELPER
    // =========================================================

    private void generate(KeyPolicy policy, Instant activateAt) {
        try {
            generateKeyUseCase.generate(policy, activateAt);
        } catch (IllegalStateException e) {
            log.error("có lẽ một dịch vụ đã bị xóa:", e);
        }
    }
}