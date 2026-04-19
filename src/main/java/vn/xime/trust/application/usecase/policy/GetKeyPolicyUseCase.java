package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.mapper.KeyPolicyMapper;

import java.util.List;

@Component
public class GetKeyPolicyUseCase {

    private final KeyPolicyRepository repository;
    private final KeyPolicyMapper mapper;

    public GetKeyPolicyUseCase(KeyPolicyRepository repository, KeyPolicyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // =========================
    // 1. Get by ID
    // =========================
    public KeyPolicyDto getById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        KeyPolicy policy = repository.findById(IdService.fromString(id))
                .orElseThrow(() ->
                        new IllegalStateException("KeyPolicy not found: " + id)
                );

        return mapper.toDto(policy);
    }

    // =========================
    // 2. Get by pair
    // =========================
    public KeyPolicyDto getByPair(String signerServiceId, String verifierServiceId) {
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }
        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        KeyPolicy policy = repository
                .findByPair(signerServiceId, verifierServiceId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "KeyPolicy not found for pair: " +
                                        signerServiceId + " -> " + verifierServiceId
                        )
                );

        return mapper.toDto(policy);
    }

    // =========================
    // 3. Get by signer
    // =========================
    public List<KeyPolicyDto> getBySigner(String signerServiceId) {
        if (signerServiceId == null || signerServiceId.isBlank()) {
            throw new IllegalArgumentException("signerServiceId is required");
        }

        return repository.findBySignerServiceId(signerServiceId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // =========================
    // 4. Get by verifier
    // =========================
    public List<KeyPolicyDto> getByVerifier(String verifierServiceId) {
        if (verifierServiceId == null || verifierServiceId.isBlank()) {
            throw new IllegalArgumentException("verifierServiceId is required");
        }

        return repository.findByVerifierServiceId(verifierServiceId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}