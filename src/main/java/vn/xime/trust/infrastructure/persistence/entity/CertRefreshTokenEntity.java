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
                @Index(name = "idx_refresh_service_used", columnList = "service_id,used_at")
        }
)
public class CertRefreshTokenEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Ownership
    // =========================

    @Column(name = "service_id", nullable = false, length = 100)
    private String serviceId;

    // =========================
    // Token (CRITICAL)
    // =========================

    /**
     * Lưu HASH, không lưu raw token
     */
    @Column(name = "token_hash", nullable = false, columnDefinition = "TEXT")
    private String tokenHash;

    /**
     * bind với cert hiện tại
     */
    @Column(name = "bound_kid", nullable = false, length = 100)
    private String boundKid;

    // =========================
    // Lifecycle
    // =========================

    @Column(
            name = "issued_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant issuedAt;

    @Column(
            name = "expires_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant expiresAt;

    /**
     * null = chưa dùng
     * != null = đã dùng (one-time enforcement)
     */
    @Column(
            name = "used_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant usedAt;

    // =========================
    // Metadata
    // =========================

    @Column(name = "issued_by", length = 100)
    private String issuedBy;

    // =========================
    // Lifecycle hooks
    // =========================

    @PrePersist
    public void prePersist() {

        if (issuedAt == null) {
            issuedAt = Instant.now();
        }

        validate();
    }

    @PreUpdate
    public void preUpdate() {
        validate();
    }

    // =========================
    // Validation (VERY CRITICAL)
    // =========================

    private void validate() {

        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("token_hash must not be empty");
        }

        if (boundKid == null || boundKid.isBlank()) {
            throw new IllegalArgumentException("bound_kid must not be empty");
        }

        if (issuedAt == null || expiresAt == null) {
            throw new IllegalArgumentException("issued_at and expires_at must not be null");
        }

        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("expires_at must be after issued_at");
        }
    }

    // =========================
    // Domain helper (IMPORTANT)
    // =========================

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    public boolean isValid(Instant now) {
        return !isUsed() && !isExpired(now);
    }

    /**
     * mark used (one-time)
     */
    public void markUsed(Instant now) {
        if (this.usedAt != null) {
            throw new IllegalStateException("Token already used");
        }
        this.usedAt = now;
    }

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getBoundKid() {
        return boundKid;
    }

    public void setBoundKid(String boundKid) {
        this.boundKid = boundKid;
    }

    public Instant getIssuedAt() {
        return issuedAt;
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

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }
}