package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.trust.domain.model.KeyEvent;
import vn.xime.trust.domain.repository.KeyEventRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyEventMapper;

import java.util.List;

@Repository
public class KeyEventRepositoryImpl implements KeyEventRepository {

    private final JpaKeyEventRepository repo;

    public KeyEventRepositoryImpl(JpaKeyEventRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(KeyEvent event) {
        var entity = KeyEventMapper.toEntity(event);
        repo.save(entity);
    }
        
    @Override
    public List<KeyEvent> findByServiceId(String serviceId) {
        return repo.findByServiceIdOrderByCreatedAtDesc(serviceId)
                .stream()
                .map(KeyEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<KeyEvent> findByKid(String kid) {
        return repo.findByKidOrderByCreatedAtDesc(kid)
                .stream()
                .map(KeyEventMapper::toDomain)
                .toList();
    }
}