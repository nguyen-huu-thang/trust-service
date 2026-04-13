package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Service;

import java.util.Optional;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(String id);

    boolean existsById(String id);
}