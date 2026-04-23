package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class KeyPolicyDto {
    private final String id;
    private final String signerServiceId;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final long keyLifetimeSec;
    private final long rotationIntervalSeconds;
    private final long preloadSec;
    private final Instant createdAt;
    private final Instant updatedAt;
}