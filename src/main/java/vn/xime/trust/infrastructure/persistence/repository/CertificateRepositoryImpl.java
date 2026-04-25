package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.repository.CertificateRepository;
import vn.xime.trust.infrastructure.persistence.mapper.CertificateMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {

    private final JpaCertificateRepository repo;

    public CertificateRepositoryImpl(JpaCertificateRepository repo) {
        this.repo = repo;
    }

    @Override
    public Certificate save(Certificate cert) {
        var entity = CertificateMapper.toEntity(cert);
        var saved = repo.save(entity);
        return CertificateMapper.toDomain(saved);
    }

    @Override
    public Optional<Certificate> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(CertificateMapper::toDomain);
    }

    // =========================
    // SERVICE
    // =========================

    @Override
    public List<Certificate> findByServiceId(String serviceId) {
        return repo.findByServiceIdAndDeletedFalseOrderByIssuedAtDesc(serviceId)
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }

    @Override
    public List<Certificate> findValidCertificates(String serviceId, Instant now) {
        return repo.findByServiceIdAndDeletedFalseAndStatusAndExpiresAtAfter(
                        serviceId,
                        "ACTIVE",
                        now
                )
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }

    // =========================
    // CLEANUP
    // =========================

    @Override
    public List<Certificate> findAllNotDeleted() {
        return repo.findByDeletedFalse()
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }

    @Override
    public List<Certificate> findAllDeleted() {
        return repo.findByDeletedTrue()
                .stream()
                .map(CertificateMapper::toDomain)
                .toList();
    }

    // =========================
    // HARD DELETE
    // =========================

    @Override
    public boolean deleteById(Id id) {
        byte[] rawId = id.toBytes();

        if (!repo.existsById(rawId)) {
            return false;
        }

        repo.deleteById(rawId);
        return true;
    }

    @Override
    public void deleteAllByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<byte[]> rawIds = ids.stream()
                .map(Id::toBytes)
                .toList();

        repo.deleteByIdIn(rawIds); // 🔥 batch hard delete
    }
}