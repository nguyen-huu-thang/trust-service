package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.event.CertEvent;

import java.util.List;

public interface CertEventRepository {

    void save(CertEvent event);

    List<CertEvent> findByServiceId(String serviceId);

    List<CertEvent> findByKid(String kid);
}