package vn.xime.trust.domain.repository;

import java.util.List;

import vn.xime.trust.domain.model.KeyEvent;

public interface KeyEventRepository {

    void save(KeyEvent event);

    List<KeyEvent> findByServiceId(String serviceId);

    List<KeyEvent> findByKid(String kid);
}