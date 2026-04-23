package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class PublicKeyDto {
    private final String kid;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final String publicKey;
    private final Instant activateAt;
    private final Instant expiresAt;
}