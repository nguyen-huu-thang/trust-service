package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class CertRefreshToken {

    private final String serviceId;

    private final String tokenHash;

    private final String boundKid;

    private final Instant issuedAt;
    private final Instant expiresAt;

    private final Instant usedAt;

    private final String issuedBy;

    public CertRefreshToken(
            String serviceId,
            String tokenHash,
            String boundKid,
            Instant issuedAt,
            Instant expiresAt,
            Instant usedAt,
            String issuedBy
    ) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }

        this.serviceId = Objects.requireNonNull(serviceId);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.boundKid = Objects.requireNonNull(boundKid);
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
    public void ensureBoundTo(String currentKid) {
        if (!this.boundKid.equals(currentKid)) {
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
                serviceId,
                tokenHash,
                boundKid,
                issuedAt,
                expiresAt,
                now,
                issuedBy
        );
    }

    // =========================
    // GETTERS
    // =========================

    public String getServiceId() {
        return serviceId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getBoundKid() {
        return boundKid;
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