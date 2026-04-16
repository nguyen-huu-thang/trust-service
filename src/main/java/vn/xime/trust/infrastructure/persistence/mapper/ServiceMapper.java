package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.infrastructure.persistence.entity.ServiceEntity;

public class ServiceMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Service toDomain(ServiceEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("ServiceEntity must not be null");
        }

        if (e.getId() == null) {
            throw new IllegalStateException("Service id must not be null");
        }

        if (e.getName() == null) {
            throw new IllegalStateException("Service name must not be null");
        }

        if (e.getStatus() == null) {
            throw new IllegalStateException("Service status must not be null");
        }

        if (e.getCreatedAt() == null) {
            throw new IllegalStateException("Service createdAt must not be null");
        }

        return new Service(
                e.getId(),
                e.getName(),
                e.getTenant(),
                mapStatus(e.getStatus()),
                e.getCreatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static ServiceEntity toEntity(Service d) {

        if (d == null) {
            throw new IllegalArgumentException("Service must not be null");
        }

        ServiceEntity e = new ServiceEntity();

        e.setId(d.getId());
        e.setName(d.getName());
        e.setTenant(d.getTenant());
        e.setStatus(d.getStatus().name());
        e.setCreatedAt(d.getCreatedAt());

        return e;
    }

    // =========================
    // Helpers
    // =========================

    private static ServiceStatus mapStatus(String status) {

        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        try {
            return ServiceStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid service status: " + status);
        }
    }
}