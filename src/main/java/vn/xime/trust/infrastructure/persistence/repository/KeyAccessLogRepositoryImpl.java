package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAccessLog;
import vn.xime.trust.domain.repository.KeyAccessLogRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyAccessLogMapper;

import java.util.List;

@Repository
public class KeyAccessLogRepositoryImpl implements KeyAccessLogRepository {

    private final JpaKeyAccessLogRepository repo;

    public KeyAccessLogRepositoryImpl(JpaKeyAccessLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(KeyAccessLog log) {
        var entity = KeyAccessLogMapper.toEntity(log);
        repo.save(entity);
    }

    @Override
    public List<KeyAccessLog> findBySignerServiceId(String signerServiceId) {
        return repo.findBySignerServiceIdOrderByRequestedAtDesc(signerServiceId)
                .stream()
                .map(KeyAccessLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<KeyAccessLog> findByKeyId(Id keyId) {
        return repo.findByKeyIdOrderByRequestedAtDesc(keyId.toBytes())
                .stream()
                .map(KeyAccessLogMapper::toDomain)
                .toList();
    }
}