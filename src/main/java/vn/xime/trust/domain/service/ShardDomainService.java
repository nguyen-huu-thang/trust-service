package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.Shard;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ShardDomainService {

    /**
     * Lấy shard active (đơn giản)
     */
    public Optional<Shard> findAnyActiveShard(List<Shard> shards) {
        return shards.stream()
                .filter(Shard::isActive)
                .findFirst();
    }

    /**
     * Lấy shard theo strategy (ví dụ: newest)
     */
    public Optional<Shard> findLatestActiveShard(List<Shard> shards) {
        return shards.stream()
                .filter(Shard::isActive)
                .max(Comparator.comparing(Shard::getCreatedAt));
    }

    /**
     * Validate shard dùng để routing
     */
    public void validateShard(Shard shard) {
        shard.ensureActive();
    }
}