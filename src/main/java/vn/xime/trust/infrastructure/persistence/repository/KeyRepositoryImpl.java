package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.trust.domain.key.Key;
import vn.xime.trust.domain.key.KeyRepository;
import vn.xime.trust.infrastructure.persistence.entity.KeyEntity;
import vn.xime.trust.infrastructure.persistence.mapper.KeyMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class KeyRepositoryImpl implements KeyRepository {

private final JpaKeyRepository jpaRepository;

public KeyRepositoryImpl(JpaKeyRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
}

@Override
public List<Key> findAllByService(String serviceName) {
    return jpaRepository
            .findByServiceNameAndIsDeletedFalse(serviceName)
            .stream()
            .map(KeyMapper::toDomain)
            .toList();
}

@Override
public Optional<Key> findByKid(String kid) {
    return jpaRepository
            .findByKidAndIsDeletedFalse(kid)
            .map(KeyMapper::toDomain);
}

@Override
public void save(Key key) {
    KeyEntity entity = KeyMapper.toEntity(key);
    jpaRepository.save(entity);
}

}
