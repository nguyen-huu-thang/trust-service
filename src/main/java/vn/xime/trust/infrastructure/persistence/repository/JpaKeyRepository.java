package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaKeyRepository extends JpaRepository<KeyEntity, Long> {

    /**
     * Lấy tất cả key của service (chưa bị delete)
     */
    List<KeyEntity> findByServiceNameAndIsDeletedFalse(String serviceName);

    /**
     * Lấy key theo kid
     */
    Optional<KeyEntity> findByKidAndIsDeletedFalse(String kid);

    /**
     * Lấy key dùng để SIGN
     * (activate_at gần nhất <= now)
     */
    @Query("""
    SELECT k FROM KeyEntity k
    WHERE k.serviceName = :serviceName
    AND k.isDeleted = false
    AND k.activateAt <= :now
    ORDER BY k.activateAt DESC
    LIMIT 1
    """)
    Optional<KeyEntity> findKeyForSign(String serviceName, Instant now);

    /**
     * Lấy NEXT key (preload)
     */
    @Query("""
    SELECT k FROM KeyEntity k
    WHERE k.serviceName = :serviceName
    AND k.isDeleted = false
    AND k.activateAt > :now
    ORDER BY k.activateAt ASC
    LIMIT 1
    """)
    Optional<KeyEntity> findNextKey(String serviceName, Instant now);

    /**
     * Lấy key dùng để VERIFY
     */
    @Query("""
    SELECT k FROM KeyEntity k
    WHERE k.serviceName = :serviceName
    AND k.isDeleted = false
    AND (k.expiresAt IS NULL OR k.expiresAt > :now)
    """)
    List<KeyEntity> findKeysForVerify(String serviceName, Instant now);

}
