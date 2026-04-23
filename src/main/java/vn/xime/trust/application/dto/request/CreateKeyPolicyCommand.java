package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class CreateKeyPolicyCommand {
    private final String signerServiceId;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final long keyLifetimeSec;
    private final long rotationIntervalSeconds;
    private final long preloadSec;
}