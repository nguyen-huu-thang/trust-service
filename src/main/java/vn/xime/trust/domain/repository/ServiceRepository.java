package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;

import java.util.Optional;
import java.util.List;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(String id);

    boolean existsById(String id);

    List<Service> findAll();

    List<Service> search(
        String tenant,
        ServiceStatus status,
        int limit,
        String cursor
    );
}