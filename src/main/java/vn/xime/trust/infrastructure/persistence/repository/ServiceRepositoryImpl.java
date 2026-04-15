package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.PlatformService;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ServiceMapper;

import java.util.Optional;

@Repository
public class ServiceRepositoryImpl implements ServiceRepository {

    private final JpaServiceRepository repo;

    public ServiceRepositoryImpl(JpaServiceRepository repo) {
        this.repo = repo;
    }

    @Override
    public PlatformService save(PlatformService service) {
        var entity = ServiceMapper.toEntity(service);
        var saved = repo.save(entity);
        return ServiceMapper.toDomain(saved);
    }

    @Override
    public Optional<PlatformService> findById(String id) {
        return repo.findById(id)
                .map(ServiceMapper::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return repo.existsById(id);
    }

    @Override
    public java.util.List<PlatformService> findAll() {
        return repo.findAll().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }
}