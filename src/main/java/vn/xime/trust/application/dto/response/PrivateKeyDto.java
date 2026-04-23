package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class PrivateKeyDto {
    private final String kid;
    private final String signerServiceId;
    private final String algorithm;
    private final int keySize;
    private final String privateKey;
    private final Instant activateAt;
    private final Instant expiresAt;
}