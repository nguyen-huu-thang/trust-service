package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class KeyRepositoryImpl implements KeyRepository {

    private final JpaKeyRepository repo;

    public KeyRepositoryImpl(JpaKeyRepository repo) {
        this.repo = repo;
    }

    @Override
    public Key save(Key key) {
        var entity = KeyMapper.toEntity(key);
        var saved = repo.save(entity);
        return KeyMapper.toDomain(saved);
    }

    @Override
    public Optional<Key> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(KeyMapper::toDomain);
    }

    // =========================
    // SIGNING
    // =========================

    @Override
    public List<Key> findBySignerServiceId(String signerServiceId) {
        return repo.findBySignerServiceId(signerServiceId)
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    @Override
    public List<Key> findActiveKeysBySigner(String signerServiceId) {
        return repo.findBySignerServiceIdAndIsDeletedFalse(signerServiceId)
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    @Override
    public List<Key> findActiveKeysByVerifier(String verifierServiceId) {
        return repo.findByVerifierServiceIdAndIsDeletedFalse(verifierServiceId)
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    // =========================
    // TRUST PAIR
    // =========================

    @Override
    public List<Key> findBySignerAndVerifier(String signerServiceId, String verifierServiceId) {
        return repo.findBySignerServiceIdAndVerifierServiceId(
                        signerServiceId,
                        verifierServiceId
                )
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    // =========================
    // CLEANUP
    // =========================

    @Override
    public List<Key> findAllNotDeleted() {
        return repo.findByIsDeletedFalse()
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    @Override
    public List<Key> findAllDeleted() {
        return repo.findByIsDeletedTrue()
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    // =========================
    // DELETE
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

        repo.deleteByIdIn(rawIds); // 🔥 batch delete
    }
}