package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaCertRefreshTokenRepository extends JpaRepository<CertRefreshTokenEntity, byte[]> {

    // =========================
    // ID (KHÔNG filter deleted)
    // =========================

    @Query("SELECT t FROM CertRefreshTokenEntity t WHERE t.id = :id")
    Optional<CertRefreshTokenEntity> findByIdBytes(@Param("id") byte[] id);

    // =========================
    // SECURITY
    // =========================

    Optional<CertRefreshTokenEntity> findByTokenHashAndIsDeletedFalse(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    @Query("""
        SELECT t FROM CertRefreshTokenEntity t
        WHERE t.tokenHash = :tokenHash
          AND t.isDeleted = false
          AND t.usedAt IS NULL
          AND t.expiresAt > :now
    """)
    Optional<CertRefreshTokenEntity> findUsableToken(
            @Param("tokenHash") String tokenHash,
            @Param("now") Instant now
    );

    // =========================
    // CLEANUP
    // =========================

    List<CertRefreshTokenEntity> findByIsDeletedFalse();

    List<CertRefreshTokenEntity> findByIsDeletedTrue();

    void deleteByIdIn(List<byte[]> ids);
}