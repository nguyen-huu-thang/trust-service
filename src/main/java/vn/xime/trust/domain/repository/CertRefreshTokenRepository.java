package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;
import java.util.Optional;

public interface CertRefreshTokenRepository {

    CertRefreshToken save(CertRefreshToken token);

    Optional<CertRefreshToken> findByTokenHash(String tokenHash);

    Optional<CertRefreshToken> findValidToken(
            String tokenHash,
            Id boundCertId,
            Instant now
    );
}