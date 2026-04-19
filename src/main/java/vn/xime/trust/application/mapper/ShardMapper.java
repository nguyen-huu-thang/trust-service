package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.domain.model.Shard;

@Component
public class ShardMapper {
    
    public ShardDto toDto(Shard s) {
        return new ShardDto(
            s.getId(),
            s.getServiceId(),
            s.getHost(),
            s.getPort(),
            s.getStatus().name(),
            s.getCreatedAt()
        );
    }
}
