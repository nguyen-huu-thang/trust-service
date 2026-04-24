package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class RotateCertDto {
    private final String idCert;
    private final String publicCert;
    private final String privateKey;
    private final String idRefreshToken;
    private final String refreshToken;
    private final Instant issuedAt;
    private final Instant expiresAt;
}
