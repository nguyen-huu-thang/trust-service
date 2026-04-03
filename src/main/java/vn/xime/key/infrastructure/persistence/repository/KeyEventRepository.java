// KeyEventRepository (infra only)

// 👉 cái này KHÔNG cần domain interface (hiện tại), vì:

// chỉ dùng internal logging
// chưa phải business core


package vn.xime.key.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.key.infrastructure.persistence.entity.KeyEventEntity;

@Repository
public class KeyEventRepository {

    private final JpaKeyEventRepository jpaRepository;

    public KeyEventRepository(JpaKeyEventRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public void save(KeyEventEntity entity) {
        jpaRepository.save(entity);
    }
}