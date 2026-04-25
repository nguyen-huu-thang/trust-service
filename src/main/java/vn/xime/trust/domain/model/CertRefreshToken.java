package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class CertRefreshToken {

    private final Id id;

    private final String tokenHash;

    private final boolean isBootstrap;

    private final Instant issuedAt;
    private final Instant expiresAt;
    private final Instant usedAt;

    private final boolean isDeleted;

    public CertRefreshToken(
        Id id,
        String tokenHash,
        boolean isBootstrap,
        Instant issuedAt,
        Instant expiresAt,
        Instant usedAt,
        boolean isDeleted
    ) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("expiresAt must be after issuedAt");
        }
        this.id = Objects.requireNonNull(id);
        this.tokenHash = tokenHash;
        this.isBootstrap = isBootstrap;
        this.issuedAt = Objects.requireNonNull(issuedAt);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.usedAt = usedAt;
        this.isDeleted = isDeleted;
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isValid(Instant now) {
        return !isDeleted() && !isExpired(now);
    }

    /**
     * mark token tokenHash
     */

    public CertRefreshToken markTokenHash(String tokenHash) {
        if (this.isDeleted) {
            throw new IllegalStateException("Token already deleted");
        }

        return new CertRefreshToken(
                this.id,
                tokenHash,
                isBootstrap,
                issuedAt,
                expiresAt,
                usedAt,
                isDeleted
        );
    }

    /**
     * mark token đã dùng
     */
    public CertRefreshToken markUsed(Instant now) {
        if (this.isDeleted) {
            throw new IllegalStateException("Token already deleted");
        }

        return new CertRefreshToken(
                this.id,
                tokenHash,
                isBootstrap,
                issuedAt,
                expiresAt,
                now,
                true
        );
    }

    public CertRefreshToken markDeleted() {
    if (this.isDeleted) {
        return this;
    }

    return new CertRefreshToken(
            this.id,
            tokenHash,
            isBootstrap,
            issuedAt,
            expiresAt,
            usedAt,
            true
    );
}

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public boolean isBootstrap() {
        return isBootstrap;
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
}