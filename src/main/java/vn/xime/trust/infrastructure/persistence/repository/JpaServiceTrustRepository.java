package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.trust.infrastructure.persistence.entity.ServiceTrustEntity;

import java.util.List;
import java.util.Optional;

public interface JpaServiceTrustRepository extends JpaRepository<ServiceTrustEntity, Long> {

    Optional<ServiceTrustEntity> findBySignerServiceIdAndVerifierServiceId(
            String signerServiceId,
            String verifierServiceId
    );

    List<ServiceTrustEntity> findBySignerServiceId(String signerServiceId);

    List<ServiceTrustEntity> findByVerifierServiceId(String verifierServiceId);
}