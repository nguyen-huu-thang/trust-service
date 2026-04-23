package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository {

    Certificate save(Certificate certificate);

    Optional<Certificate> findById(Id id);

    List<Certificate> findByServiceId(String serviceId);

    List<Certificate> findValidCertificates(String serviceId, Instant now);

    List<Certificate> findAllNotDeleted();

    boolean deleteById(Id id);

    void deleteAllByIds(List<Id> ids);
}