package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class BootstrapDto {
    private final String idCert;
    private final String serviceId;
    private final String publicCert;
    private final String privateKey;
    private final String idToken;
    private final String token;
    private final Instant issuedAt;
    private final Instant expiresAt;
}
