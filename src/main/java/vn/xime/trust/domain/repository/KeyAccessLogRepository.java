package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAccessLog;

import java.util.List;

public interface KeyAccessLogRepository {

    void save(KeyAccessLog log);

    List<KeyAccessLog> findBySignerServiceId(String signerServiceId);

    List<KeyAccessLog> findByKeyId(Id keyId);

    // List<KeyAccessLog> findBySignerAndKey(Id keyId, String signerServiceId); thêm sau
}