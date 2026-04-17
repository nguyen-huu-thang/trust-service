package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyAccessLogEntity;

import java.util.List;

public interface JpaKeyAccessLogRepository extends JpaRepository<KeyAccessLogEntity, byte[]> {

    List<KeyAccessLogEntity> findBySignerServiceIdOrderByRequestedAtDesc(String signerServiceId);

    List<KeyAccessLogEntity> findByKeyIdOrderByRequestedAtDesc(byte[] keyId);
}