package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;

import java.time.Instant;

public class ServiceFactory {

    public Service create(
            String id,
            String name,
            String tenant
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("service name is required");
        }


        // =========================
        // BUILD DOMAIN
        // =========================

        return new Service(
                id,
                name,
                tenant,
                ServiceStatus.INACTIVE,
                Instant.now()
        );
    }
}