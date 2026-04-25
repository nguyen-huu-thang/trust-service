package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaCertificateRepository extends JpaRepository<CertificateEntity, byte[]> {

    @Query("SELECT c FROM CertificateEntity c WHERE c.id = :id")
    Optional<CertificateEntity> findByIdBytes(@Param("id") byte[] id);

    // =========================
    // Service queries
    // =========================

    List<CertificateEntity> findByServiceIdAndDeletedFalseOrderByIssuedAtDesc(String serviceId);

    List<CertificateEntity> findByServiceIdAndDeletedFalseAndExpiresAtAfter(
            String serviceId,
            Instant now
    );

    List<CertificateEntity> findByServiceIdAndDeletedFalseAndStatusAndExpiresAtAfter(
            String serviceId,
            String status,
            Instant now
    );

    // =========================
    // Cleanup
    // =========================

    List<CertificateEntity> findByDeletedFalse();

    List<CertificateEntity> findByDeletedTrue();

    void deleteByIdIn(List<byte[]> ids); // 🔥 batch hard delete
}