package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class CertRefreshToken {

    private final Id id;

    private final String serviceId;

    private final String tokenHash;

    private final Id boundCertId;

    private final Instant issuedAt;
    private final Instant expiresAt;

    private final Instant usedAt;

    private final String issuedBy;

    public CertRefreshToken(
        Id id,
        String serviceId,
        String tokenHash,
        Id boundCertId,
        Instant issuedAt,
        Instant expiresAt,
        Instant usedAt,
        String issuedBy
    ) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }

        this.id = Objects.requireNonNull(id);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.boundCertId = Objects.requireNonNull(boundCertId);
        this.issuedAt = Objects.requireNonNull(issuedAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.usedAt = usedAt;
        this.issuedBy = issuedBy;
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isValid(Instant now) {
        return !isUsed() && !isExpired(now);
    }

    public void ensureValid(Instant now) {
        if (isUsed()) {
            throw new IllegalStateException("Token already used");
        }
        if (isExpired(now)) {
            throw new IllegalStateException("Token expired");
        }
    }

    /**
     * validate token có đúng cert hiện tại không
     */
    public void ensureBoundTo(Id currentCertId) {
        if (!this.boundCertId.equals(currentCertId)) {
            throw new IllegalStateException("Token not bound to current certificate");
        }
    }

    /**
     * mark token đã dùng
     */
    public CertRefreshToken markUsed(Instant now) {
        if (this.usedAt != null) {
            throw new IllegalStateException("Token already used");
        }

        return new CertRefreshToken(
                this.id,
                serviceId,
                tokenHash,
                boundCertId,
                issuedAt,
                expiresAt,
                now,
                issuedBy
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Id getBoundCertId() {
        return boundCertId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public String getIssuedBy() {
        return issuedBy;
    }
}