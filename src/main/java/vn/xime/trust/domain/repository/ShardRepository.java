package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Shard;

import java.util.List;
import java.util.Optional;

public interface ShardRepository {

    Shard save(Shard shard);

    Optional<Shard> findById(String shardId);

    List<Shard> findByServiceId(String serviceId);

    List<Shard> findActiveShards(String serviceId);
}