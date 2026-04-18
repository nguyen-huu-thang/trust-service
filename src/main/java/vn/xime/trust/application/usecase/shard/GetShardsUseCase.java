package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.ShardStatus;
import vn.xime.trust.domain.repository.ShardRepository;

import java.util.List;

@Component
public class GetShardsUseCase {

    private final ShardRepository shardRepository;

    public GetShardsUseCase(ShardRepository shardRepository) {
        this.shardRepository = shardRepository;
    }

    public Result execute(
            String serviceId,
            String status,
            int limit,
            String cursor
    ) {

        ShardStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = ShardStatus.valueOf(status);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        List<Shard> shards = shardRepository.search(
                serviceId,
                statusEnum,
                limit,
                cursor
        );

        List<ShardDto> dtos = shards.stream()
                .map(this::toDto)
                .toList();

        String nextCursor = shards.isEmpty()
                ? null
                : shards.get(shards.size() - 1).getId();

        return new Result(dtos, nextCursor);
    }

    private ShardDto toDto(Shard s) {
        return new ShardDto(
                s.getId(),
                s.getServiceId(),
                s.getHost(),
                s.getPort(),
                s.getStatus().name(),
                s.getCreatedAt().toEpochMilli()
        );
    }

    public record Result(List<ShardDto> shards, String nextCursor) {}
}