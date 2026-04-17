package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

import java.time.Instant;
import java.util.Optional;

public interface JpaCertRefreshTokenRepository extends JpaRepository<CertRefreshTokenEntity, byte[]> {

    Optional<CertRefreshTokenEntity> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(Instant now);

    // 🔥 CORE SECURITY QUERY
    @Query("""
        SELECT t FROM CertRefreshTokenEntity t
        WHERE t.tokenHash = :tokenHash
          AND t.usedAt IS NULL
          AND t.expiresAt > :now
          AND t.boundCertId = :boundCertId
    """)
    Optional<CertRefreshTokenEntity> findValidToken(
            @Param("tokenHash") String tokenHash,
            @Param("boundCertId") byte[] boundCertId,
            @Param("now") Instant now
    );
}