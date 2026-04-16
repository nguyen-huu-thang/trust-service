package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Service;

import java.util.Optional;
import java.util.List;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(String id);

    boolean existsById(String id);

    List<Service> findAll();
}