package vn.xime.trust.application.usecase.servicetrust;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.CreateTrustCommand;
import vn.xime.trust.domain.model.ServiceTrust;
import vn.xime.trust.domain.factory.ServiceTrustFactory;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.repository.ServiceTrustRepository;

@Component
public class CreateTrustUseCase {

    private final ServiceTrustRepository serviceTrustRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceTrustFactory serviceTrustFactory;


    public CreateTrustUseCase(
            ServiceTrustRepository serviceTrustRepository,
            ServiceRepository serviceRepository,
            ServiceTrustFactory serviceTrustFactory
    ) {
        this.serviceTrustRepository = serviceTrustRepository;
        this.serviceRepository = serviceRepository;
        this.serviceTrustFactory = serviceTrustFactory;
    }

    @Transactional
    public ServiceTrust execute(CreateTrustCommand cmd) {

        // =========================
        // VALIDATE
        // =========================

        if (cmd.getSignerServiceId() == null || cmd.getSignerServiceId().isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (cmd.getVerifierServiceId() == null || cmd.getVerifierServiceId().isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        if (!serviceRepository.existsById(cmd.getSignerServiceId())) {
            throw new IllegalStateException("Signer service not found");
        }

        if (!serviceRepository.existsById(cmd.getVerifierServiceId())) {
            throw new IllegalStateException("Verifier service not found");
        }

        if (cmd.getKeyLifetimeSec() <= 0) {
            throw new IllegalArgumentException("keyLifetime must be > 0");
        }

        if (cmd.getJwtTtlSec() <= 0) {
            throw new IllegalArgumentException("jwtTtl must be > 0");
        }

        if (cmd.getPreloadSec() < 0) {
            throw new IllegalArgumentException("preload must be >= 0");
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        ServiceTrust serviceTrust = serviceTrustFactory.create(
                cmd.getSignerServiceId(),
                cmd.getVerifierServiceId(),
                cmd.getKeyLifetimeSec(),
                cmd.getJwtTtlSec(),
                cmd.getPreloadSec()
        );

        // =========================
        // SAVE
        // =========================

        serviceTrustRepository.save(serviceTrust);

        return serviceTrust;
    }
}