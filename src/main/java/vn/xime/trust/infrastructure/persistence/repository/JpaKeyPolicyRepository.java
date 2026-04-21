package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import vn.xime.trust.infrastructure.persistence.entity.KeyPolicyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyPolicyRepository extends JpaRepository<KeyPolicyEntity, byte[]> {

    @Query("SELECT k FROM KeyPolicyEntity k WHERE k.id = :id")
    Optional<KeyPolicyEntity> findByIdBytes(@Param("id") byte[] id);

    Optional<KeyPolicyEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );

    List<KeyPolicyEntity> findBySignerServiceId(String signerServiceId);

    List<KeyPolicyEntity> findByVerifierServiceId(String verifierServiceId);

    List<KeyPolicyEntity> findAll();

    @Modifying
    @Query("DELETE FROM KeyPolicyEntity k WHERE k.id = :id")
    int deleteByIdBytes(@Param("id") byte[] id);
}