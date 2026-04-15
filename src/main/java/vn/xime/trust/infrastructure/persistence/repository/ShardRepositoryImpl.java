package vn.xime.trust.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ShardRepository;
import vn.xime.trust.infrastructure.persistence.mapper.ShardMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class ShardRepositoryImpl implements ShardRepository {

    private final JpaShardRepository repo;

    public ShardRepositoryImpl(JpaShardRepository repo) {
        this.repo = repo;
    }

    @Override
    public Shard save(Shard shard) {
        var entity = ShardMapper.toEntity(shard);
        var saved = repo.save(entity);
        return ShardMapper.toDomain(saved);
    }

    @Override
    public Optional<Shard> findById(String shardId) {
        return repo.findById(shardId)
                .map(ShardMapper::toDomain);
    }

    @Override
    public List<Shard> findByServiceId(String serviceId) {
        return repo.findByServiceId(serviceId)
                .stream()
                .map(ShardMapper::toDomain)
                .toList();
    }

    @Override
    public List<Shard> findActiveShards(String serviceId) {
        return repo.findByServiceIdAndStatus(serviceId, "ACTIVE")
                .stream()
                .map(ShardMapper::toDomain)
                .toList();
    }
}