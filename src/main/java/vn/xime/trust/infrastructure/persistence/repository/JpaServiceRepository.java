package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.ServiceEntity;

import java.util.List;
import java.util.Optional;

public interface JpaServiceRepository extends JpaRepository<ServiceEntity, String> {

    Optional<ServiceEntity> findById(String id);

    boolean existsById(String id);

    List<ServiceEntity> findAll();

    Page<ServiceEntity> findAll(Pageable pageable);

    Page<ServiceEntity> findByTenant(String tenant, Pageable pageable);

    Page<ServiceEntity> findByTenantIsNull(Pageable pageable);
}