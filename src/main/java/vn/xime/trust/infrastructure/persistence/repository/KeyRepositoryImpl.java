package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
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
    public Optional<Key> findByKid(String kid) {
        return repo.findByKid(kid)
                .map(KeyMapper::toDomain);
    }

    @Override
    public List<Key> findByServiceId(String serviceId) {
        return repo.findByServiceId(serviceId)
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    @Override
    public List<Key> findActiveKeys(String serviceId) {
        return repo.findByServiceIdAndIsDeletedFalse(serviceId)
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }
}