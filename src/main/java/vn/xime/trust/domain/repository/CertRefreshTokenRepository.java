package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.util.Optional;

public interface CertRefreshTokenRepository {

    CertRefreshToken save(CertRefreshToken token);

    Optional<CertRefreshToken> findById(Id id);
}