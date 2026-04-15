package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.ShardEntity;

import java.util.List;

public interface JpaShardRepository extends JpaRepository<ShardEntity, String> {

    List<ShardEntity> findByServiceId(String serviceId);

    List<ShardEntity> findByServiceIdAndStatus(String serviceId, String status);
}