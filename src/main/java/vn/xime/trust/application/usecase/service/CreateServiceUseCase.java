package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.application.mapper.ServiceMapper;

@Component
public class CreateServiceUseCase {

    private final ServiceRepository repository;
    private final ServiceFactory factory;
    private final ServiceMapper mapper;

    public CreateServiceUseCase(
            ServiceRepository repository,
            ServiceFactory factory,
            ServiceMapper mapper
    ) {
        this.repository = repository;
        this.factory = factory;
        this.mapper = mapper;
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

        return mapper.toDto(service);
    }
}