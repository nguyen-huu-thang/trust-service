package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyEvent;

import java.util.List;

public interface KeyEventRepository {

    void save(KeyEvent event);

    List<KeyEvent> findBySignerServiceId(String signerServiceId);

    List<KeyEvent> findByKeyId(Id keyId);

    // List<KeyEvent> findBySignerAndKey(Id keyId, String signerServiceId);

    // List<KeyEvent> findTop100BySignerServiceIdOrderByCreatedAtDesc(...)

    // Page<KeyEvent> findBySignerServiceId(...)
}