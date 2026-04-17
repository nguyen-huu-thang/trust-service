package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyPolicyRepository extends JpaRepository<KeyPolicyEntity, byte[]> {

    Optional<KeyPolicyEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );

    List<KeyPolicyEntity> findBySignerServiceId(String signerServiceId);

    List<KeyPolicyEntity> findByVerifierServiceId(String verifierServiceId);
}