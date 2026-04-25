package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class BootstrapCommand {
    private final String serviceId;
    private final String shardId;
}
