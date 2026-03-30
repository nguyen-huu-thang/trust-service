package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.key.domain.key.Key;
import vn.xime.key.domain.key.KeyRepository;
import vn.xime.key.infrastructure.persistence.entity.KeyEntity;
import vn.xime.key.infrastructure.persistence.mapper.KeyMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class KeyRepositoryImpl implements KeyRepository {

    private final JpaKeyRepository jpaRepository;

    public KeyRepositoryImpl(JpaKeyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Key> findCurrent(String serviceName) {
        return jpaRepository
                .findByServiceNameAndStatusAndIsDeletedFalse(serviceName, "CURRENT")
                .map(KeyMapper::toDomain);
    }

    @Override
    public Optional<Key> findNext(String serviceName) {
        return jpaRepository
                .findByServiceNameAndStatusAndIsDeletedFalse(serviceName, "NEXT")
                .map(KeyMapper::toDomain);
    }

    @Override
    public List<Key> findPublicKeys(String serviceName) {
        return jpaRepository
                .findByServiceNameAndStatusInAndIsDeletedFalse(
                        serviceName,
                        List.of("CURRENT", "OLD")
                )
                .stream()
                .map(KeyMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Key key) {
        KeyEntity entity = KeyMapper.toEntity(key);
        jpaRepository.save(entity);
    }
}