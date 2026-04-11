package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

import java.util.Optional;

public interface JpaKeyPolicyRepository extends JpaRepository<KeyPolicyEntity, Long> {

    Optional<KeyPolicyEntity> findByServiceName(String serviceName);

}