package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.ServiceTrust;
import vn.xime.trust.domain.repository.ServiceTrustRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ServiceTrustMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class ServiceTrustRepositoryImpl implements ServiceTrustRepository {

    private final JpaServiceTrustRepository repo;

    public ServiceTrustRepositoryImpl(JpaServiceTrustRepository repo) {
        this.repo = repo;
    }

    @Override
    public ServiceTrust save(ServiceTrust trust) {
        var entity = ServiceTrustMapper.toEntity(trust);
        var saved = repo.save(entity);
        return ServiceTrustMapper.toDomain(saved);
    }

    @Override
    public Optional<ServiceTrust> findByPair(String signerServiceId, String verifierServiceId) {
        return repo.findBySignerServiceIdAndVerifierServiceId(
                        signerServiceId,
                        verifierServiceId
                )
                .map(ServiceTrustMapper::toDomain);
    }

    @Override
    public List<ServiceTrust> findBySignerServiceId(String signerServiceId) {
        return repo.findBySignerServiceId(signerServiceId)
                .stream()
                .map(ServiceTrustMapper::toDomain)
                .toList();
    }

    @Override
    public List<ServiceTrust> findByVerifierServiceId(String verifierServiceId) {
        return repo.findByVerifierServiceId(verifierServiceId)
                .stream()
                .map(ServiceTrustMapper::toDomain)
                .toList();
    }
}