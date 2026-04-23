package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class RegisterShardCommand {
    private final String id;
    private final String serviceId;
    private final String host;
    private final Integer port;
}