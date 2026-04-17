package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.CertEventEntity;

import java.util.List;

public interface JpaCertEventRepository extends JpaRepository<CertEventEntity, byte[]> {

    List<CertEventEntity> findByServiceIdOrderByCreatedAtDesc(String serviceId);

    // ❗ byte[] nên dùng custom query
    @Query("""
        SELECT e FROM CertEventEntity e
        WHERE e.certId = :certId
        ORDER BY e.createdAt DESC
    """)
    List<CertEventEntity> findByCertId(@Param("certId") byte[] certId);
}