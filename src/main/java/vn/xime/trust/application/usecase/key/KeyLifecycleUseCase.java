package vn.xime.trust.application.usecase.key;

import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;

import java.time.Instant;
import java.util.List;

public class KeyLifecycleUseCase {

    private final KeyRepository keyRepository;
    private final KeyLifecycleDomainService lifecycle;

    public KeyLifecycleUseCase(
            KeyRepository keyRepository,
            KeyLifecycleDomainService lifecycle
    ) {
        this.keyRepository = keyRepository;
        this.lifecycle = lifecycle;
    }

    // // =========================
    // // LOAD KEYS
    // // =========================

    // public List<Key> loadKeys(String serviceId) {
    //     List<Key> keys = keyRepository.findByServiceId(serviceId);
    //     return lifecycle.filterNotDeleted(keys);
    // }

    // // =========================
    // // SIGN
    // // =========================

    // public Key getKeyForSign(String serviceId, Instant now) {
    //     List<Key> keys = loadKeys(serviceId);
    //     return lifecycle.getKeyForSign(keys, now);
    // }

    // // =========================
    // // VERIFY
    // // =========================

    // public List<Key> getKeysForVerify(String serviceId, Instant now) {
    //     List<Key> keys = loadKeys(serviceId);
    //     return lifecycle.getKeysForVerify(keys, now);
    // }

    // // =========================
    // // NEXT
    // // =========================

    // public Key getNextKey(String serviceId, Instant now) {
    //     List<Key> keys = loadKeys(serviceId);
    //     return lifecycle.getNextKey(keys, now);
    // }

    // // =========================
    // // CLEANUP
    // // =========================

    // public void cleanupExpiredKeys(String serviceId, Instant now) {
    //     List<Key> keys = keyRepository.findByServiceId(serviceId);

    //     keys.stream()
    //             .filter(k -> !k.isDeleted())
    //             .filter(k -> lifecycle.shouldBeDeleted(k, now))
    //             .forEach(k -> {
    //                 Key deleted = k.markDeleted();
    //                 keyRepository.save(deleted);
    //             });
    // }
}