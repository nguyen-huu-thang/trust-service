package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Key;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {

    Key save(Key key);

    Optional<Key> findByKid(String kid);

    // 🔥 dùng cho SIGNING (identity service)
    List<Key> findBySignerServiceId(String signerServiceId);

    // 🔥 dùng cho SIGNING (lọc active)
    List<Key> findActiveKeysBySigner(String signerServiceId);

    // 🔥 dùng khi cần theo từng cặp trust
    List<Key> findBySignerAndVerifier(String signerServiceId, String verifierServiceId);
}