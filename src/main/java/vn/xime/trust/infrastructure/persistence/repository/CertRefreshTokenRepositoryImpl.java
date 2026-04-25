package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertRefreshTokenMapper;

import java.time.Instant;
import java.util.List;
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
    public Optional<CertRefreshToken> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(CertRefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<CertRefreshToken> findByTokenHash(String tokenHash) {
        return repo.findByTokenHashAndDeletedFalse(tokenHash)
                .map(CertRefreshTokenMapper::toDomain);
    }

    @Override
    public Optional<CertRefreshToken> findUsableToken(String tokenHash, Instant now) {
        return repo.findUsableToken(tokenHash, now)
                .map(CertRefreshTokenMapper::toDomain);
    }

    // =========================
    // CLEANUP
    // =========================

    @Override
    public List<CertRefreshToken> findAllNotDeleted() {
        return repo.findByDeletedFalse()
                .stream()
                .map(CertRefreshTokenMapper::toDomain)
                .toList();
    }

    @Override
    public List<CertRefreshToken> findAllDeleted() {
        return repo.findByDeletedTrue()
                .stream()
                .map(CertRefreshTokenMapper::toDomain)
                .toList();
    }

    // =========================
    // HARD DELETE
    // =========================

    @Override
    public void deleteAllByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<byte[]> rawIds = ids.stream()
                .map(Id::toBytes)
                .toList();

        repo.deleteByIdIn(rawIds);
    }
}