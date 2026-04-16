package vn.xime.trust.application.usecase.servicetrust;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.GetTrustsQuery;
import vn.xime.trust.application.dto.response.TrustDto;
import vn.xime.trust.domain.model.ServiceTrust;
import vn.xime.trust.domain.repository.ServiceTrustRepository;

import java.util.List;

@Component
public class GetTrustsUseCase {

    private final ServiceTrustRepository trustRepository;

    public GetTrustsUseCase(ServiceTrustRepository trustRepository) {
        this.trustRepository = trustRepository;
    }

    public List<TrustDto> execute(GetTrustsQuery query) {

        List<ServiceTrust> trusts =
                trustRepository.findBySignerServiceId(query.getSignerServiceId());

        return trusts.stream()
                .map(this::toDto)
                .toList();
    }

    private TrustDto toDto(ServiceTrust t) {
        return new TrustDto(
                t.getSignerServiceId(),
                t.getVerifierServiceId(),
                t.getKeyLifetimeSeconds(),
                t.getJwtTtlSeconds(),
                t.getPreloadSeconds(),
                t.getCreatedAt()
        );
    }
}