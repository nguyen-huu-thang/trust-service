package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.CertEvent;
import vn.xime.trust.domain.model.Id;

import java.util.List;

public interface CertEventRepository {

    void save(CertEvent event);

    List<CertEvent> findByServiceId(String serviceId);

    List<CertEvent> findByCertId(Id certId);

    // Page<CertEvent> findByServiceId(..., Pageable pageable); làm sau nếu cần
}