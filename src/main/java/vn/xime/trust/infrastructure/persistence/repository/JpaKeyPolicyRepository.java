package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.policy.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyPolicyMapper;

import java.util.Optional;

@Repository
public class JpaKeyPolicyRepository implements KeyPolicyRepository {

    private final SpringDataKeyPolicyRepository repo;

    public JpaKeyPolicyRepository(SpringDataKeyPolicyRepository repo) {
        this.repo = repo;
    }

    @Override
    public KeyPolicy save(String serviceId, KeyPolicy policy) {
        var entity = KeyPolicyMapper.toEntity(policy, serviceId);
        var saved = repo.save(entity);
        return KeyPolicyMapper.toDomain(saved);
    }

    @Override
    public Optional<KeyPolicy> findByServiceId(String serviceId) {
        return repo.findByServiceId(serviceId)
                .map(KeyPolicyMapper::toDomain);
    }

    @Override
    public boolean exists(String serviceId) {
        return repo.existsByServiceId(serviceId);
    }
}