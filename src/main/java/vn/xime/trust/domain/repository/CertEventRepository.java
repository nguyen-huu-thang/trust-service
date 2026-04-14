package vn.xime.trust.domain.repository;

import java.util.List;

import vn.xime.trust.domain.model.CertEvent;

public interface CertEventRepository {

    void save(CertEvent event);

    List<CertEvent> findByServiceId(String serviceId);

    List<CertEvent> findByKid(String kid);
}