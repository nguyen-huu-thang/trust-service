package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;

@Component
public class GetServiceUseCase {

    private final ServiceRepository repository;

    public GetServiceUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    public ServiceDto execute(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        Service service = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Service not found: " + id)
                );

        return toDto(service);
    }

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