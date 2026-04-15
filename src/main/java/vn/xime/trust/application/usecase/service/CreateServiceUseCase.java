package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Service;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.port.out.Clock;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.model.PlatformService;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.domain.repository.ServiceRepository;

@Service
public class CreateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final ServiceFactory serviceFactory;
    private final Clock clock;

    public CreateServiceUseCase(
            ServiceRepository serviceRepository,
            ServiceFactory serviceFactory,
            Clock clock
    ) {
        this.serviceRepository = serviceRepository;
        this.serviceFactory = serviceFactory;
        this.clock = clock;
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

        PlatformService service = serviceFactory.create(
                cmd.getId(),
                cmd.getName(),
                cmd.getTenant(),
                ServiceStatus.ACTIVE,
                clock.now()
        );

        // =========================
        // SAVE
        // =========================

        serviceRepository.save(service);
    }
}