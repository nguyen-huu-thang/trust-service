package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyPolicyMapper;

import java.util.Optional;

@Repository
public class KeyPolicyRepositoryImpl implements KeyPolicyRepository {

    private final JpaKeyPolicyRepository repo;

    public KeyPolicyRepositoryImpl(JpaKeyPolicyRepository repo) {
        this.repo = repo;
    }

    @Override
    public KeyPolicy save(KeyPolicy policy) {
        var entity = KeyPolicyMapper.toEntity(policy);
        var saved = repo.save(entity);
        return KeyPolicyMapper.toDomain(saved);
    }

    @Override
    public Optional<KeyPolicy> findByServiceId(String serviceId) {
        return repo.findByServiceId(serviceId)
                .map(KeyPolicyMapper::toDomain);
    }
}