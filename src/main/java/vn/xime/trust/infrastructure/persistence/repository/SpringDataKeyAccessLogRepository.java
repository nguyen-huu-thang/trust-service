package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyAccessLogEntity;

import java.util.List;

public interface SpringDataKeyAccessLogRepository extends JpaRepository<KeyAccessLogEntity, Long> {

    List<KeyAccessLogEntity> findByServiceIdOrderByRequestedAtDesc(String serviceId);

    List<KeyAccessLogEntity> findByKidOrderByRequestedAtDesc(String kid);
}