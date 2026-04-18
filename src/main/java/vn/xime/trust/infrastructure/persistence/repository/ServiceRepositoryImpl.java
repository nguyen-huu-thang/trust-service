package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ServiceMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ServiceRepositoryImpl implements ServiceRepository {

    private final JpaServiceRepository repo;

    public ServiceRepositoryImpl(JpaServiceRepository repo) {
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

    @Override
    public List<Service> findAll() {
        return repo.findAll().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Service> search(
            String tenant,
            ServiceStatus status,
            int limit,
            String cursor
    ) {
        var statusStr = status != null ? status.name() : null;

        Instant cursorTime = null;
        if (cursor != null) {
            cursorTime = Instant.parse(cursor); // ISO-8601
        }

        var pageable = PageRequest.of(0, limit);

        return repo.search(tenant, statusStr, cursorTime, pageable)
                .stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }
}