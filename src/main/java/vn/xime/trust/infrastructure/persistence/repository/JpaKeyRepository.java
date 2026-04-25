package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyRepository extends JpaRepository<KeyEntity, byte[]> {

    @Query("SELECT k FROM KeyEntity k WHERE k.id = :id")
    Optional<KeyEntity> findByIdBytes(@Param("id") byte[] id);

    // =========================
    // SIGNING
    // =========================

    List<KeyEntity> findBySignerServiceId(String signerServiceId);

    List<KeyEntity> findBySignerServiceIdAndIsDeletedFalse(String signerServiceId);

    List<KeyEntity> findByVerifierServiceIdAndIsDeletedFalse(String verifierServiceId);

    // =========================
    // TRUST PAIR
    // =========================

    List<KeyEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );

    // =========================
    // CLEANUP
    // =========================

    List<KeyEntity> findByIsDeletedFalse();

    List<KeyEntity> findByIsDeletedTrue();

    // 🔥 batch delete
    void deleteByIdIn(List<byte[]> ids);
}