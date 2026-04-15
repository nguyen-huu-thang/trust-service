package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyRepository extends JpaRepository<KeyEntity, Long> {

    Optional<KeyEntity> findByKid(String kid);

    // 🔥 signing queries
    List<KeyEntity> findBySignerServiceId(String signerServiceId);

    List<KeyEntity> findBySignerServiceIdAndIsDeletedFalse(String signerServiceId);

    // 🔥 trust pair
    List<KeyEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );
}