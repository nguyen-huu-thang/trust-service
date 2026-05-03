package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class AdminKeyDto {
    private final String id;
    private final String signerServiceId;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final Instant activateAt;
    private final Instant expiresAt;
    private final boolean isDeleted;
}