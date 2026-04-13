package vn.xime.trust.infrastructure.persistence.mapper;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.infrastructure.persistence.entity.ServiceEntity;

public class ServiceMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Service toDomain(ServiceEntity e) {
        return new Service(
                e.getId(),
                e.getName(),
                e.getTenant(),
                ServiceStatus.valueOf(e.getStatus()),
                e.getCreatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static ServiceEntity toEntity(Service d) {
        ServiceEntity e = new ServiceEntity();

        e.setId(d.getId());
        e.setName(d.getName());
        e.setTenant(d.getTenant());
        e.setStatus(d.getStatus().name());
        e.setCreatedAt(d.getCreatedAt());

        return e;
    }
}