package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertRefreshTokenMapper;

import java.time.Instant;
import java.util.Optional;

@Repository
public class JpaCertRefreshTokenRepository implements CertRefreshTokenRepository {

    private final SpringDataCertRefreshTokenRepository repo;

    public JpaCertRefreshTokenRepository(SpringDataCertRefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    public CertRefreshToken save(CertRefreshToken token) {
        var entity = CertRefreshTokenMapper.toEntity(token);
        var saved = repo.save(entity);
        return CertRefreshTokenMapper.toDomain(saved);
    }

    @Override
    public Optional<CertRefreshToken> findValidToken(String tokenHash) {
        return repo.findByTokenHashAndUsedAtIsNull(tokenHash)
                .map(CertRefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<CertRefreshToken> findByTokenHash(String tokenHash) {
        return repo.findByTokenHash(tokenHash)
                .map(CertRefreshTokenMapper::toDomain);
    }

    @Override
    public void deleteExpired(Instant now) {
        repo.deleteByExpiresAtBefore(now);
    }
}