package vn.xime.trust.application.dto.request;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class ScheduleKeyRotationCommand {
    private final String signerServiceId;
    private final String verifierServiceId;
    private final Instant activateAt;
}