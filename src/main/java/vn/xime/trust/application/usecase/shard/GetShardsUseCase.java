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

    // =========================
    // 1. Get by ID
    // =========================
    public ShardDto getById(String shardId) {
        Shard shard = shardRepository.findById(shardId)
                .orElseThrow(() -> new IllegalArgumentException("Shard not found: " + shardId));

        return toDto(shard);
    }

    // =========================
    // 2. Get by serviceId
    // =========================
    public List<ShardDto> getByServiceId(String serviceId) {
        return shardRepository.findByServiceId(serviceId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // 3. Get all
    // =========================
    public List<ShardDto> getAll() {
        return shardRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // 4. Get all with pagination
    // =========================
    public List<ShardDto> getAll(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination params");
        }

        return shardRepository.findAll(page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // Mapper
    // =========================
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
}