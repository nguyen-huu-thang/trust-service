package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class UpdateShardStatusCommand {
    private final String shardId;
    private final String status;
}