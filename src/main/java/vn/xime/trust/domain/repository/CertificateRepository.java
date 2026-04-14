package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Certificate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository {

    Certificate save(Certificate certificate);

    Optional<Certificate> findByKid(String kid);

    List<Certificate> findByServiceId(String serviceId);

    List<Certificate> findValidCertificates(String serviceId, Instant now);
}