package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyPolicyMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class KeyPolicyRepositoryImpl implements KeyPolicyRepository {

    private final JpaKeyPolicyRepository repo;

    public KeyPolicyRepositoryImpl(JpaKeyPolicyRepository repo) {
        this.repo = repo;
    }

    @Override
    public KeyPolicy save(KeyPolicy trust) {
        var entity = KeyPolicyMapper.toEntity(trust);
        var saved = repo.save(entity);
        return KeyPolicyMapper.toDomain(saved);
    }

    @Override
    public Optional<KeyPolicy> findByPair(String signerServiceId, String verifierServiceId) {
        return repo.findBySignerServiceIdAndVerifierServiceId(
                        signerServiceId,
                        verifierServiceId
                )
                .map(KeyPolicyMapper::toDomain);
    }

    @Override
    public List<KeyPolicy> findBySignerServiceId(String signerServiceId) {
        return repo.findBySignerServiceId(signerServiceId)
                .stream()
                .map(KeyPolicyMapper::toDomain)
                .toList();
    }

    @Override
    public List<KeyPolicy> findByVerifierServiceId(String verifierServiceId) {
        return repo.findByVerifierServiceId(verifierServiceId)
                .stream()
                .map(KeyPolicyMapper::toDomain)
                .toList();
    }
}