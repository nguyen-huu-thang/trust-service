package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyEventEntity;

import java.util.List;

public interface JpaKeyEventRepository extends JpaRepository<KeyEventEntity, Long> {

    List<KeyEventEntity> findByServiceIdOrderByCreatedAtDesc(String serviceId);

    List<KeyEventEntity> findByKidOrderByCreatedAtDesc(String kid);
}