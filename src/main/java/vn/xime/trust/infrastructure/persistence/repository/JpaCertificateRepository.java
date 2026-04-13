package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertificateMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaCertificateRepository implements CertificateRepository {

    private final SpringDataCertificateRepository repo;

    public JpaCertificateRepository(SpringDataCertificateRepository repo) {
        this.repo = repo;
    }

    @Override
    public Certificate save(Certificate cert) {
        var entity = CertificateMapper.toEntity(cert);
        var saved = repo.save(entity);
        return CertificateMapper.toDomain(saved);
    }

    @Override
    public Optional<Certificate> findByKid(String kid) {
        return repo.findByKid(kid)
                .map(CertificateMapper::toDomain);
    }

    @Override
    public List<Certificate> findByServiceId(String serviceId) {
        return repo.findByServiceIdOrderByIssuedAtDesc(serviceId)
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }

    @Override
    public List<Certificate> findValidCertificates(String serviceId, Instant now) {
        return repo.findByServiceIdAndExpiresAtAfter(serviceId, now)
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }
}