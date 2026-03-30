package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.key.infrastructure.persistence.entity.KeyEntity;

import java.util.List;
import java.util.Optional;

public interface JpaKeyRepository extends JpaRepository<KeyEntity, Long> {

    Optional<KeyEntity> findByServiceNameAndStatusAndIsDeletedFalse(
            String serviceName,
            String status
    );

    List<KeyEntity> findByServiceNameAndStatusInAndIsDeletedFalse(
            String serviceName,
            List<String> statuses
    );
}