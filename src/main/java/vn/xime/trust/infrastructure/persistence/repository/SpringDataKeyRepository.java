package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;

import java.util.List;
import java.util.Optional;

public interface SpringDataKeyRepository extends JpaRepository<KeyEntity, Long> {

    Optional<KeyEntity> findByKid(String kid);

    List<KeyEntity> findByServiceId(String serviceId);

    List<KeyEntity> findByServiceIdAndIsDeletedFalse(String serviceId);
}