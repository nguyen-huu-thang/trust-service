package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.ServiceTrust;

import java.util.List;
import java.util.Optional;

public interface ServiceTrustRepository {

    ServiceTrust save(ServiceTrust trust);

    Optional<ServiceTrust> findByPair(String signerServiceId, String verifierServiceId);

    List<ServiceTrust> findBySignerServiceId(String signerServiceId);

    List<ServiceTrust> findByVerifierServiceId(String verifierServiceId);
}