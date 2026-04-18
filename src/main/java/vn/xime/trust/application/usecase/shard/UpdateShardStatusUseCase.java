package vn.xime.trust.application.usecase.shard;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.xime.trust.application.dto.request.UpdateShardStatusCommand;
import vn.xime.trust.domain.model.Shard;
import vn.xime.trust.domain.model.ShardStatus;
import vn.xime.trust.domain.repository.ShardRepository;

@Component
public class UpdateShardStatusUseCase {

    private final ShardRepository shardRepository;

    public UpdateShardStatusUseCase(ShardRepository shardRepository) {
        this.shardRepository = shardRepository;
    }

    @Transactional
    public String execute(UpdateShardStatusCommand cmd) {

        Shard shard = shardRepository.findById(cmd.getShardId())
                .orElseThrow(() ->
                        new IllegalStateException("Shard not found: " + cmd.getShardId())
                );

        ShardStatus newStatus = ShardStatus.valueOf(cmd.getStatus());

        Shard updated = shard.changeStatus(newStatus);
        shardRepository.save(updated);

        return updated.getStatus().name();
    }

}