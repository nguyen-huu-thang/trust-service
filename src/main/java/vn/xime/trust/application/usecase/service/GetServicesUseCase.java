package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetServicesQuery;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.model.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Component
public class GetServicesUseCase {

    private final ServiceRepository serviceRepository;

    public GetServicesUseCase(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // =========================
    // GET ALL
    // =========================

    public List<ServiceDto> execute(GetServicesQuery query) {

        List<Service> services =
                serviceRepository.findAll();

        if (services == null || services.isEmpty()) {
            return Collections.emptyList();
        }

        return services.stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // GET BY ID
    // =========================

    public Optional<ServiceDto> getById(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        return serviceRepository.findById(id)
                .map(this::toDto);
    }

    // =========================
    // EXISTS
    // =========================

    public boolean exists(String id) {

        if (id == null || id.isBlank()) {
            return false;
        }

        return serviceRepository.existsById(id);
    }

    // =========================
    // MAPPER
    // =========================

    private ServiceDto toDto(Service s) {
        return new ServiceDto(
                s.getId(),
                s.getName(),
                s.getTenant(),
                s.getStatus().name(),
                s.getCreatedAt()
        );
    }
}