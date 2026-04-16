package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ShardRepository;

import java.util.List;

@Component
public class GetShardsUseCase {

    private final ShardRepository shardRepository;

    public GetShardsUseCase(ShardRepository shardRepository) {
        this.shardRepository = shardRepository;
    }

    public List<ShardDto> getByServiceId(String serviceId) {

        List<Shard> shards = shardRepository.findByServiceId(serviceId);

        return shards.stream()
                .map(this::toDto)
                .toList();
    }

    private ShardDto toDto(Shard s) {
        return new ShardDto(
                s.getId(),
                s.getServiceId(),
                s.getHost(),
                s.getStatus().name(),
                s.getCreatedAt()
        );
    }
}