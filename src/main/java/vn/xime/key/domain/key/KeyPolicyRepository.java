package vn.xime.key.domain.key;

import java.util.Optional;

/**
 * Domain Repository: KeyPolicyRepository
 *
 * =========================
 * Vai trò:
 * =========================
 * * Load policy theo service
 */
public interface KeyPolicyRepository {

    Optional<KeyPolicy> findByService(String serviceName);

}