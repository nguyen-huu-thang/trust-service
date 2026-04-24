package vn.xime.trust.application.dto.response;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;
import vn.xime.trust.domain.model.CertificateStatus;


@Getter
@AllArgsConstructor
public class AdminCertDto {
    private final String id;
    private final String serviceId;
    private final String publicCert;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final CertificateStatus status;
    private final boolean deleted;
}
