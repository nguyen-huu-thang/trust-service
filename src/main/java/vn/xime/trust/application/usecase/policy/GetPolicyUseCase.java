package vn.xime.trust.application.usecase.policy;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetTrustsQuery;
import vn.xime.trust.application.dto.response.TrustDto;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.domain.repository.KeyPolicyRepository;

import java.util.List;

@Component
public class GetPolicyUseCase {

    private final KeyPolicyRepository policyRepository;

    public GetPolicyUseCase(KeyPolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<TrustDto> execute(GetTrustsQuery query) {

        List<KeyPolicy> policies =
                policyRepository.findBySignerServiceId(query.getSignerServiceId());

        return policies.stream()
                .map(this::toDto)
                .toList();
    }

    private TrustDto toDto(KeyPolicy p) {
        return new TrustDto(
                p.getSignerServiceId(),
                p.getVerifierServiceId(),
                p.getKeyLifetimeSeconds(),
                p.getJwtTtlSeconds(),
                p.getPreloadSeconds(),
                p.getCreatedAt()
        );
    }
}