// infra only


package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.key.infrastructure.persistence.entity.KeyAccessLogEntity;

@Repository
public class KeyAccessLogRepository {

    private final JpaKeyAccessLogRepository jpaRepository;

    public KeyAccessLogRepository(JpaKeyAccessLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public void save(KeyAccessLogEntity entity) {
        jpaRepository.save(entity);
    }
}