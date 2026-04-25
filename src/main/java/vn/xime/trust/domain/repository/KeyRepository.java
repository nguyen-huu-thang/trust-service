package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.Key;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {

    Key save(Key key);

    Optional<Key> findById(Id id);

    // 🔥 SIGNING
    List<Key> findBySignerServiceId(String signerServiceId);

    // 🔥 dùng cho SIGNING (lọc active)
    List<Key> findActiveKeysBySigner(String signerServiceId);

    // 🔥 dùng cho VERIFYING (lọc active)
    List<Key> findActiveKeysByVerifier(String verifierServiceId);

    // 🔥 trust pair
    List<Key> findBySignerAndVerifier(String signerServiceId, String verifierServiceId);

    // Cleanup Expired Keys
    List<Key> findAllNotDeleted();

    List<Key> findAllDeleted();

    boolean deleteById(Id id);

    void deleteAllByIds(List<Id> ids);
}