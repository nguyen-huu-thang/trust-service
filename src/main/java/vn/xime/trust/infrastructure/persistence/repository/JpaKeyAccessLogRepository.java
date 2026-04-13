package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.KeyAccessLog;
import vn.xime.trust.domain.repository.KeyAccessLogRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyAccessLogMapper;

import java.util.List;

@Repository
public class JpaKeyAccessLogRepository implements KeyAccessLogRepository {

    private final SpringDataKeyAccessLogRepository repo;

    public JpaKeyAccessLogRepository(SpringDataKeyAccessLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public KeyAccessLog save(KeyAccessLog log) {
        var entity = KeyAccessLogMapper.toEntity(log);
        var saved = repo.save(entity);
        return KeyAccessLogMapper.toDomain(saved);
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