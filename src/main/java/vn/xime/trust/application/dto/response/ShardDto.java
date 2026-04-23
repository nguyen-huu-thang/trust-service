package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class ShardDto {
    private final String id;
    private final String serviceId;
    private final String host;
    private final int port;
    private final String status;
    private final Instant createdAt;
}