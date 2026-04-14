package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.ServiceEntity;

import java.util.Optional;

public interface JpaServiceRepository extends JpaRepository<ServiceEntity, String> {

    Optional<ServiceEntity> findById(String id);

    boolean existsById(String id);
}