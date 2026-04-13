package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ServiceMapper;

import java.util.Optional;

@Repository
public class JpaServiceRepository implements ServiceRepository {

    private final SpringDataServiceRepository repo;

    public JpaServiceRepository(SpringDataServiceRepository repo) {
        this.repo = repo;
    }

    @Override
    public Service save(Service service) {
        var entity = ServiceMapper.toEntity(service);
        var saved = repo.save(entity);
        return ServiceMapper.toDomain(saved);
    }

    @Override
    public Optional<Service> findById(String id) {
        return repo.findById(id)
                .map(ServiceMapper::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return repo.existsById(id);
    }
}