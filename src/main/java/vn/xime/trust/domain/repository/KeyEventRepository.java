package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.event.KeyEvent;

import java.util.List;

public interface KeyEventRepository {

    void save(KeyEvent event);

    List<KeyEvent> findByServiceId(String serviceId);

    List<KeyEvent> findByKid(String kid);
}