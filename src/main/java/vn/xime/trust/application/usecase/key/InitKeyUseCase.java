package vn.xime.trust.application.usecase.key;

import java.util.List;
import java.time.Instant;
import org.springframework.stereotype.Component;

import vn.xime.trust.domain.model.Key;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.repository.KeyRepository;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.service.KeyLifecycleDomainService;
import vn.xime.trust.application.dto.request.InitKeyCommand;
import vn.xime.trust.application.dto.response.AdminKeyDto;
import vn.xime.trust.application.mapper.KeyMapper;


@Component
public class InitKeyUseCase {

    private final GenerateKeyUseCase generateKeyUseCase;
    private final KeyRepository keyRepository;
    private final ServiceRepository serviceRepository;
    private final KeyLifecycleDomainService keyLife;
    private final KeyMapper mapper;
    
    public InitKeyUseCase(
        GenerateKeyUseCase generateKeyUseCase,
        KeyRepository keyRepository,
        ServiceRepository serviceRepository,
        KeyLifecycleDomainService keyLife,
        KeyMapper mapper
    ) {
        this.generateKeyUseCase = generateKeyUseCase;
        this.keyRepository = keyRepository;
        this.serviceRepository = serviceRepository;
        this.keyLife = keyLife;
        this.mapper = mapper;
    }

    public AdminKeyDto initKey(InitKeyCommand cmd) {

        // =========================
        // VALIDATE SERVICE (application-level)
        // =========================

        if (cmd.getSignerServiceId() == null || cmd.getSignerServiceId().isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        if (cmd.getVerifierServiceId() == null || cmd.getVerifierServiceId().isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        Service signer = serviceRepository.findById(cmd.getSignerServiceId())
        .orElseThrow(() -> new IllegalStateException("Signer service not found"));

        Service verifier = serviceRepository.findById(cmd.getVerifierServiceId())
        .orElseThrow(() -> new IllegalStateException("Verifier service not found"));

        List <Key> keys = keyRepository.findBySignerAndVerifier(
                        cmd.getSignerServiceId(),
                        cmd.getVerifierServiceId()
                );
        
        System.out.println(keys.size());
        
        keys = keyLife.getAllActive(keys, Instant.now());

        System.out.println(keys.size());
        
        if (!keys.isEmpty()){
            throw new IllegalArgumentException("This key has been initialized.");
        }

        return mapper.toResponseDto(generateKeyUseCase.generate(signer, verifier));
    }
}
