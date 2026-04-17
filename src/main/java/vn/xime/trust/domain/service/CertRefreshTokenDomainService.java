package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;

public class CertRefreshTokenDomainService {

    /**
     * validate token trước khi rotate cert
     */
    public void validateToken(
            CertRefreshToken token,
            Id currentCertId,
            Instant now
    ) {
        token.ensureValid(now);
        token.ensureBoundTo(currentCertId);
    }

    /**
     * consume token (one-time)
     */
    public CertRefreshToken consumeToken(
            CertRefreshToken token,
            Instant now
    ) {
        token.ensureValid(now);
        return token.markUsed(now);
    }
}