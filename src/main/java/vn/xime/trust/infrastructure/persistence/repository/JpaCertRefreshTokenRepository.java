package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.CertRefreshTokenEntity;

import java.time.Instant;
import java.util.Optional;

public interface JpaCertRefreshTokenRepository extends JpaRepository<CertRefreshTokenEntity, Long> {

    Optional<CertRefreshTokenEntity> findByTokenHash(String tokenHash);

    Optional<CertRefreshTokenEntity> findByTokenHashAndUsedAtIsNull(String tokenHash);

    Optional<CertRefreshTokenEntity> findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(String tokenHash, Instant now);

    boolean existsByTokenHash(String tokenHash);

    // optional: phục vụ cleanup / audit
    void deleteByExpiresAtBefore(Instant now);
}