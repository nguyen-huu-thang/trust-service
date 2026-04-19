package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.application.mapper.ShardMapper;

import java.util.List;

@Component
public class GetShardsUseCase {

    private final ShardRepository shardRepository;
    private final ShardMapper shardMapper;

    public GetShardsUseCase(ShardRepository shardRepository, ShardMapper shardMapper) {
        this.shardRepository = shardRepository;
        this.shardMapper = shardMapper;
    }

    // =========================
    // 1. Get by ID
    // =========================
    public ShardDto getById(String shardId) {
        Shard shard = shardRepository.findById(shardId)
                .orElseThrow(() -> new IllegalArgumentException("Shard not found: " + shardId));

        return shardMapper.toDto(shard);
    }

    // =========================
    // 2. Get by serviceId
    // =========================
    public List<ShardDto> getByServiceId(String serviceId) {
        return shardRepository.findByServiceId(serviceId)
                .stream()
                .map(shardMapper::toDto)
                .toList();
    }

    // =========================
    // 3. Get all
    // =========================
    public List<ShardDto> getAll() {
        return shardRepository.findAll()
                .stream()
                .map(shardMapper::toDto)
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
                .map(shardMapper::toDto)
                .toList();
    }
}