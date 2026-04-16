package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.UpdateShardStatusCommand;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.repository.ShardRepository;

@Component
public class UpdateShardStatusUseCase {

    private final ShardRepository shardRepository;

    public UpdateShardStatusUseCase(ShardRepository shardRepository) {
        this.shardRepository = shardRepository;
    }

    @Transactional
    public void execute(UpdateShardStatusCommand cmd) {

        Shard shard = shardRepository.findById(cmd.getShardId())
                .orElseThrow(() ->
                        new IllegalStateException("Shard not found: " + cmd.getShardId())
                );

        Shard updated = shard.changeStatus(cmd.getStatus());

        shardRepository.save(updated);
    }
}