package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Service;

public class ServiceDomainService {

    public void validateCanIssueKey(Service service) {
        service.ensureActive();
    }

    public void validateCanIssueCertificate(Service service) {
        service.ensureActive();
    }
}