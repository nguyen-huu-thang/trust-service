package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.xime.trust.infrastructure.persistence.entity.ShardEntity;

import java.util.List;

public interface JpaShardRepository extends JpaRepository<ShardEntity, String> {

    List<ShardEntity> findByServiceId(String serviceId);

    List<ShardEntity> findByServiceIdAndStatus(String serviceId, String status);

    // cursor-based (id > cursor)
    @Query("""
        SELECT s FROM ShardEntity s
        WHERE (:serviceId IS NULL OR s.serviceId = :serviceId)
          AND (:status IS NULL OR s.status = :status)
          AND (:cursor IS NULL OR s.id > :cursor)
        ORDER BY s.id ASC
        LIMIT :limit
    """)
    List<ShardEntity> search(
            String serviceId,
            String status,
            String cursor,
            int limit
    );
}