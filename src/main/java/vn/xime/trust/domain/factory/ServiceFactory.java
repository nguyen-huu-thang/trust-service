package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.PlatformService;
import vn.xime.trust.domain.model.ServiceStatus;

import java.time.Instant;
import java.util.Objects;

public class ServiceFactory {

    public PlatformService create(
            String id,
            String name,
            String tenant,
            ServiceStatus status,
            Instant createdAt
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

        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(createdAt, "createdAt is required");

        // =========================
        // BUILD DOMAIN
        // =========================

        return new PlatformService(
                id,
                name,
                tenant,
                status,
                createdAt
        );
    }
}