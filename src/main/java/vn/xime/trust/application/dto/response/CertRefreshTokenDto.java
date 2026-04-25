package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class CertRefreshTokenDto {
    private final String id;
    private final boolean isBootstrap;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final Instant usedAt;
    private final boolean isDeleted;
}
