package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.event.KeyEvent;
import vn.xime.trust.domain.repository.KeyEventRepository;
import vn.xime.trust.infrastructure.persistence.mapper.KeyEventMapper;

import java.util.List;

@Repository
public class JpaKeyEventRepository implements KeyEventRepository {

    private final SpringDataKeyEventRepository repo;

    public JpaKeyEventRepository(SpringDataKeyEventRepository repo) {
        this.repo = repo;
    }

    @Override
    public KeyEvent save(KeyEvent event) {
        var entity = KeyEventMapper.toEntity(event);
        var saved = repo.save(entity);
        return KeyEventMapper.toDomain(saved);
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