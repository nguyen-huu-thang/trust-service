package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.ShardEntity;

import java.util.List;
import java.util.Optional;

public interface JpaShardRepository extends JpaRepository<ShardEntity, String> {

    Optional<ShardEntity> findById(String shardId);

    boolean existsById(String shardId);

    List<ShardEntity> findByServiceId(String serviceId);

    List<ShardEntity> findByServiceIdAndStatus(String serviceId, String status);

    List<ShardEntity> findAll();
    
    Page<ShardEntity> findAll(Pageable pageable);

    Page<ShardEntity> findByServiceId(String serviceId, Pageable pageable);
}