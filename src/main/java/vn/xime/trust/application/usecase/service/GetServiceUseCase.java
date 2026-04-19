package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;

import java.util.List;

@Component
public class GetServiceUseCase {

    private final ServiceRepository repository;

    public GetServiceUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    // =========================
    // 1. Get by ID
    // =========================
    public ServiceDto getById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        Service service = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Service not found: " + id)
                );

        return toDto(service);
    }

    // =========================
    // 2. Get all
    // =========================
    public List<ServiceDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // 3. Get all with pagination
    // =========================
    public List<ServiceDto> getAll(int page, int size) {
        validatePage(page, size);

        return repository.findAll(page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // 4. Get by tenant
    // =========================
    public List<ServiceDto> getByTenant(String tenant, int page, int size) {
        if (tenant == null || tenant.isBlank()) {
            throw new IllegalArgumentException("tenant is required");
        }

        validatePage(page, size);

        return repository.findByTenant(tenant, page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // 5. Get tenant = NULL
    // =========================
    public List<ServiceDto> getByTenantIsNull(int page, int size) {
        validatePage(page, size);

        return repository.findByTenantIsNull(page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // Validation
    // =========================
    private void validatePage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination params");
        }
    }

    // =========================
    // Mapper
    // =========================
    private ServiceDto toDto(Service s) {
        return new ServiceDto(
                s.getId(),
                s.getName(),
                s.getTenant(),
                s.getStatus().name(),
                s.getCreatedAt().toEpochMilli()
        );
    }
}