package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Key;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {

    Key save(Key key);

    Optional<Key> findByKid(String kid);

    List<Key> findByServiceId(String serviceId);

    List<Key> findActiveKeys(String serviceId); // Hơi nguy hiểm.
}