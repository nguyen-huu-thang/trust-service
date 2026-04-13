package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.CertEventEntity;

import java.util.List;

public interface SpringDataCertEventRepository extends JpaRepository<CertEventEntity, Long> {

    List<CertEventEntity> findByServiceIdOrderByCreatedAtDesc(String serviceId);

    List<CertEventEntity> findByKidOrderByCreatedAtDesc(String kid);
}