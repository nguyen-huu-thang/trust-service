package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
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
    public List<KeyAccessLog> findByServiceId(String serviceId) {
        return repo.findByServiceIdOrderByRequestedAtDesc(serviceId)
                .stream()
                .map(KeyAccessLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<KeyAccessLog> findByKid(String kid) {
        return repo.findByKidOrderByRequestedAtDesc(kid)
                .stream()
                .map(KeyAccessLogMapper::toDomain)
                .toList();
    }
}