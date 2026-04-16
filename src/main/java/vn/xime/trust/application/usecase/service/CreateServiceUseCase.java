package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.ServiceRepository;


@Component
public class CreateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final ServiceFactory serviceFactory;

    public CreateServiceUseCase(
            ServiceRepository serviceRepository,
            ServiceFactory serviceFactory
    ) {
        this.serviceRepository = serviceRepository;
        this.serviceFactory = serviceFactory;
    }

    public void execute(CreateServiceCommand cmd) {

        // =========================
        // VALIDATE (APPLICATION LEVEL)
        // =========================

        if (cmd.getId() == null || cmd.getId().isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        if (cmd.getName() == null || cmd.getName().isBlank()) {
            throw new IllegalArgumentException("service name is required");
        }

        if (serviceRepository.existsById(cmd.getId())) {
            throw new IllegalStateException(
                    "Service already exists: " + cmd.getId()
            );
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Service service = serviceFactory.create(
                cmd.getId(),
                cmd.getName(),
                cmd.getTenant()
        );

        // =========================
        // SAVE
        // =========================

        serviceRepository.save(service);
    }
}