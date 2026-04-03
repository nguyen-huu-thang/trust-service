package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.key.domain.key.KeyPolicy;
import vn.xime.key.domain.key.KeyPolicyRepository;
import vn.xime.key.infrastructure.persistence.mapper.KeyPolicyMapper;

import java.util.Optional;

@Repository
public class KeyPolicyRepositoryImpl implements KeyPolicyRepository {

    private final JpaKeyPolicyRepository jpaRepository;

    public KeyPolicyRepositoryImpl(JpaKeyPolicyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<KeyPolicy> findByService(String serviceName) {
        return jpaRepository
                .findByServiceName(serviceName)
                .map(KeyPolicyMapper::toDomain);
    }
}