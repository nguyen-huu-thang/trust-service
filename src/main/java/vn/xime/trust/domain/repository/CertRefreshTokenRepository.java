package vn.xime.trust.domain.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;


public interface CertRefreshTokenRepository {

    CertRefreshToken save(CertRefreshToken token);

    Optional<CertRefreshToken> findById(Id id);

    Optional<CertRefreshToken> findByTokenHash(String tokenHash);

    Optional<CertRefreshToken> findUsableToken(String tokenHash, Instant now);

    List<CertRefreshToken> findAllNotDeleted();

    List<CertRefreshToken> findAllDeleted();

    void deleteAllByIds(List<Id> ids);
}