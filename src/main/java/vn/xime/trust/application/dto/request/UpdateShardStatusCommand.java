package vn.xime.trust.application.dto.request;

public class UpdateShardStatusCommand {

    private final String shardId;
    private final String status;

    public UpdateShardStatusCommand(String shardId, String status) {
        this.shardId = shardId;
        this.status = status;
    }

    public String getShardId() {
        return shardId;
    }

    public String getStatus() {
        return status;
    }
}