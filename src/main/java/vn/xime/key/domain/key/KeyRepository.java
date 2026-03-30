package vn.xime.key.domain.key;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {

    Optional<Key> findCurrent(String serviceName);

    Optional<Key> findNext(String serviceName);

    List<Key> findPublicKeys(String serviceName);
    // CURRENT + OLD

    void save(Key key);
}