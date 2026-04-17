package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaCertificateRepository extends JpaRepository<CertificateEntity, byte[]> {

    // ❗ byte[] cần custom query
    @Query("SELECT c FROM CertificateEntity c WHERE c.id = :id")
    Optional<CertificateEntity> findByIdBytes(@Param("id") byte[] id);

    List<CertificateEntity> findByServiceIdOrderByIssuedAtDesc(String serviceId);

    List<CertificateEntity> findByServiceIdAndExpiresAtAfter(String serviceId, Instant now);

    List<CertificateEntity> findByServiceIdAndStatusAndExpiresAtAfter(
            String serviceId,
            String status,
            Instant now
    );
}