package vn.xime.trust.application.dto.request;

import vn.xime.trust.domain.model.ShardStatus;

public class UpdateShardStatusCommand {

    private final String shardId;
    private final ShardStatus status;

    public UpdateShardStatusCommand(String shardId, ShardStatus status) {
        this.shardId = shardId;
        this.status = status;
    }

    public String getShardId() {
        return shardId;
    }

    public ShardStatus getStatus() {
        return status;
    }
}