package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Id;

import java.time.Instant;
import java.util.Objects;

public class CertRefreshTokenDomainService {

    // =========================
    // VALIDATE
    // =========================

    /**
     * validate token trước khi rotate cert
     */
    public void validateToken(
            CertRefreshToken token,
            String serviceId,
            Id currentCertId,
            Instant now
    ) {
        if (token == null) {
            throw new IllegalStateException("Refresh token not found");
        }

        Objects.requireNonNull(serviceId, "serviceId is required");
        Objects.requireNonNull(currentCertId, "currentCertId is required");
        Objects.requireNonNull(now, "now is required");

        // =========================
        // DOMAIN RULES
        // =========================

        token.ensureValid(now);

        // 🔥 chống misuse cross-service
        if (!token.getServiceId().equals(serviceId)) {
            throw new IllegalStateException("Token does not belong to this service");
        }

        token.ensureBoundTo(currentCertId);
    }

    // =========================
    // CONSUME (STRICT)
    // =========================

    /**
     * validate + consume trong 1 bước (khuyến nghị dùng)
     */
    public CertRefreshToken validateAndConsume(
            CertRefreshToken token,
            String serviceId,
            Id currentCertId,
            Instant now
    ) {
        validateToken(token, serviceId, currentCertId, now);
        return token.markUsed(now);
    }

    // =========================
    // CONSUME (LEGACY / OPTIONAL)
    // =========================

    /**
     * chỉ consume (giữ lại nếu bạn muốn tách flow)
     */
    public CertRefreshToken consumeToken(
            CertRefreshToken token,
            Instant now
    ) {
        if (token == null) {
            throw new IllegalStateException("Refresh token not found");
        }

        token.ensureValid(now);

        return token.markUsed(now);
    }
}