package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.key.infrastructure.persistence.entity.KeyPolicyEntity;

import java.util.Optional;

public interface JpaKeyPolicyRepository extends JpaRepository<KeyPolicyEntity, Long> {

    Optional<KeyPolicyEntity> findByServiceName(String serviceName);

}