package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;
import vn.xime.trust.domain.model.KeyPolicy;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.domain.service.IdService;

@Component
public class KeyPolicyMapper {
    
    public KeyPolicyDto toDto(KeyPolicy p) {
        return new KeyPolicyDto(
                IdService.toString(p.getId()),
                p.getSignerServiceId(),
                p.getVerifierServiceId(),
                p.getAlgorithm().name(),
                p.getKeySize(),
                p.getKeyLifetimeSeconds(),
                p.getRotationIntervalSeconds(),
                p.getPreloadSeconds(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}