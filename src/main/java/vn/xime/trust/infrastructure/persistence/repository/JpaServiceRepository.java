package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.xime.trust.infrastructure.persistence.entity.ServiceEntity;

import java.time.Instant;
import java.util.List;

public interface JpaServiceRepository extends JpaRepository<ServiceEntity, String> {

    @Query("""
        SELECT s FROM ServiceEntity s
        WHERE (:tenant IS NULL OR s.tenantId = :tenant)
          AND (:status IS NULL OR s.status = :status)
          AND (:cursor IS NULL OR s.createdAt > :cursor)
        ORDER BY s.createdAt ASC
    """)
    List<ServiceEntity> search(
            String tenant,
            String status,
            Instant cursor,
            Pageable pageable
    );
}