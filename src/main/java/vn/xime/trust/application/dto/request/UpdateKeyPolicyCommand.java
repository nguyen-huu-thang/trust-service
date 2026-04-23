package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class UpdateKeyPolicyCommand {
    private final String id;
    private final String algorithm;
    private final Integer keySize;
    private final Long keyLifetimeSec;
    private final Long rotationIntervalSeconds;
    private final Long preloadSec;
}