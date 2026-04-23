package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Service;

import java.util.Optional;
import java.util.List;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(String id);

    boolean existsById(String id);

    List<Service> findAll();

    List<Service> findAll(int page, int size);

    List<Service> findByTenant(String tenant, int page, int size);

    List<Service> findByTenantIsNull(int page, int size);

    List<Service> findAllActiveServices();
}