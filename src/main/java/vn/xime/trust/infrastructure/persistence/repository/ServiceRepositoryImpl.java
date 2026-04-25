package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ServiceMapper;

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

    // =========================
    // Pagination
    // =========================

    @Override
    public List<Service> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .map(ServiceMapper::toDomain)
                .getContent();
    }

    // =========================
    // Tenant
    // =========================

    @Override
    public List<Service> findByTenant(String tenant, int page, int size) {
        return repo.findByTenant(tenant, PageRequest.of(page, size))
                .map(ServiceMapper::toDomain)
                .getContent();
    }

    @Override
    public List<Service> findByTenantIsNull(int page, int size) {
        return repo.findByTenantIsNull(PageRequest.of(page, size))
                .map(ServiceMapper::toDomain)
                .getContent();
    }

    // =========================
    // ACTIVE
    // =========================

    @Override
    public List<Service> findAllActiveServices() {
        return repo.findByStatus("ACTIVE")
                .stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }
}