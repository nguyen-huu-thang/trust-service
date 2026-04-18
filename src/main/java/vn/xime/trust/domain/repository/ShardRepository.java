package vn.xime.trust.domain.repository;

import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.ShardStatus;

import java.util.List;
import java.util.Optional;

public interface ShardRepository {

    Shard save(Shard shard);

    Optional<Shard> findById(String shardId);

    boolean existsById(String shardId);

    List<Shard> findByServiceId(String serviceId);

    List<Shard> search(
            String serviceId,
            ShardStatus status,
            int limit,
            String cursor
    );
}