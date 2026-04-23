package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class ServiceDto {
    private final String id;
    private final String name;
    private final String tenant;
    private final String status;
    private final Instant createdAt;
}