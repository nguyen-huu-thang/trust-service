package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyRepository extends JpaRepository<KeyEntity, byte[]> {

    // ❗ byte[] không dùng derived query được ổn định → dùng query
    @Query("SELECT k FROM KeyEntity k WHERE k.id = :id")
    Optional<KeyEntity> findByIdBytes(@Param("id") byte[] id);

    // 🔥 signing
    List<KeyEntity> findBySignerServiceId(String signerServiceId);

    List<KeyEntity> findBySignerServiceIdAndIsDeletedFalse(String signerServiceId);

    List<KeyEntity> findByVerifierServiceIdAndIsDeletedFalse(String verifierServiceId);

    // 🔥 trust pair
    List<KeyEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );
}