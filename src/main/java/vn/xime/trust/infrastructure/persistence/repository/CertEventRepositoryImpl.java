package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.trust.domain.model.CertEvent;
import vn.xime.trust.domain.repository.CertEventRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertEventMapper;

import java.util.List;

@Repository
public class CertEventRepositoryImpl implements CertEventRepository {

    private final JpaCertEventRepository repo;

    public CertEventRepositoryImpl(JpaCertEventRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(CertEvent event) {
        var entity = CertEventMapper.toEntity(event);
        repo.save(entity);
    }

    @Override
    public List<CertEvent> findByServiceId(String serviceId) {
        return repo.findByServiceIdOrderByCreatedAtDesc(serviceId)
                .stream()
                .map(CertEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<CertEvent> findByKid(String kid) {
        return repo.findByKidOrderByCreatedAtDesc(kid)
                .stream()
                .map(CertEventMapper::toDomain)
                .toList();
    }
}