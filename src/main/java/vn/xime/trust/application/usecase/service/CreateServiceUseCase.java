package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;

@Component
public class CreateServiceUseCase {

    private final ServiceRepository repository;
    private final ServiceFactory factory;

    public CreateServiceUseCase(
            ServiceRepository repository,
            ServiceFactory factory
    ) {
        this.repository = repository;
        this.factory = factory;
    }

    @Transactional
    public ServiceDto execute(CreateServiceCommand cmd) {

        // =========================
        // VALIDATE
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        if (cmd.getName() == null || cmd.getName().isBlank()) {
            throw new IllegalArgumentException("service name is required");
        }

        if (repository.existsById(cmd.getId())) {
            throw new IllegalStateException("Service already exists: " + cmd.getId());
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Service service = factory.create(
                cmd.getId(),
                cmd.getName(),
                cmd.getTenant()
        );

        // =========================
        // SAVE
        // =========================

        repository.save(service);

        // =========================
        // RETURN DTO
        // =========================

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