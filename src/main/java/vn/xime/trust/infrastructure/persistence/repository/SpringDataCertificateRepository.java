package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.CertificateEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SpringDataCertificateRepository extends JpaRepository<CertificateEntity, Long> {

    Optional<CertificateEntity> findByKid(String kid);

    List<CertificateEntity> findByServiceIdOrderByIssuedAtDesc(String serviceId);

    List<CertificateEntity> findByServiceIdAndExpiresAtAfter(String serviceId, Instant now);
}