package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.KeyAccessLog;

import java.util.List;

public interface KeyAccessLogRepository {

    void save(KeyAccessLog log);

    List<KeyAccessLog> findByServiceId(String serviceId);

    List<KeyAccessLog> findByKid(String kid);
}