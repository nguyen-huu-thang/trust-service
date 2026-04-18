package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.domain.repository.ServiceRepository;

@Component
public class UpdateServiceStatusUseCase {

    private final ServiceRepository repository;

    public UpdateServiceStatusUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String execute(String id, String status) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        Service service = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Service not found: " + id)
                );

        // =========================
        // PARSE STATUS
        // =========================

        ServiceStatus newStatus;

        try {
            newStatus = ServiceStatus.valueOf(status);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        // =========================
        // APPLY
        // =========================

        Service updated = service.changeStatus(newStatus);

        repository.save(updated);

        return updated.getStatus().name();
    }
}