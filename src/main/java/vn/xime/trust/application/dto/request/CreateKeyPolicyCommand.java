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
    private final Long keyLifetimeSec;
    private final Long rotationIntervalSeconds;
    private final Long preloadSec;
}