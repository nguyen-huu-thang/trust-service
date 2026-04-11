package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.xime.trust.infrastructure.persistence.entity.KeyAccessLogEntity;

import java.util.List;

public interface JpaKeyAccessLogRepository extends JpaRepository<KeyAccessLogEntity, Long> {

    List<KeyAccessLogEntity> findByServiceNameOrderByRequestedAtDesc(String serviceName);

}