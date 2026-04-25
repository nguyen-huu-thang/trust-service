package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "cert_refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_hash",
                        columnNames = {"token_hash"}
                )
        },
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_expires", columnList = "expires_at"),
                @Index(name = "idx_refresh_deleted", columnList = "is_deleted")
        }
)
public class CertRefreshTokenEntity {

    // =========================
    // ID (KSUID)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // Token
    // =========================

    @Column(name = "token_hash", nullable = false, columnDefinition = "TEXT")
    private String tokenHash;

    // =========================
    // Flags
    // =========================

    @Column(name = "is_bootstrap", nullable = false)
    private boolean isBootstrap;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // =========================
    // Lifecycle
    // =========================

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt; // nullable

    // =========================
    // Getter / Setter
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public boolean isBootstrap() {
        return isBootstrap;
    }

    public void setBootstrap(boolean bootstrap) {
        isBootstrap = bootstrap;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }
}