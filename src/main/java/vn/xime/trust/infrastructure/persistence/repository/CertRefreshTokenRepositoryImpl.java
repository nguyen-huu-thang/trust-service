package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertRefreshTokenMapper;

import java.time.Instant;
import java.util.Optional;

@Repository
public class CertRefreshTokenRepositoryImpl implements CertRefreshTokenRepository {

    private final JpaCertRefreshTokenRepository repo;

    public CertRefreshTokenRepositoryImpl(JpaCertRefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    public CertRefreshToken save(CertRefreshToken token) {
        var entity = CertRefreshTokenMapper.toEntity(token);
        var saved = repo.save(entity);
        return CertRefreshTokenMapper.toDomain(saved);
    }

    @Override
    public Optional<CertRefreshToken> findByTokenHash(String tokenHash) {
        return repo.findByTokenHash(tokenHash)
                .map(CertRefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<CertRefreshToken> findValidToken(
            String tokenHash,
            Id boundCertId,
            Instant now
    ) {
        return repo.findValidToken(
                        tokenHash,
                        boundCertId.toBytes(),
                        now
                )
                .map(CertRefreshTokenMapper::toDomain);
    }
}