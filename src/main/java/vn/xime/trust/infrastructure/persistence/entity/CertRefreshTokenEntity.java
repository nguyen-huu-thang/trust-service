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
                @Index(name = "idx_refresh_service_used", columnList = "service_id,used_at"),
                @Index(name = "idx_refresh_bound_cert", columnList = "bound_cert_id")
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
    // Ownership
    // =========================

    @Column(name = "service_id", nullable = false, length = 20)
    private String serviceId;

    // =========================
    // Token (CRITICAL)
    // =========================

    @Column(name = "token_hash", nullable = false, columnDefinition = "TEXT")
    private String tokenHash;

    // 🔥 bind trực tiếp với certificate.id
    @Column(name = "bound_cert_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] boundCertId;

    // =========================
    // Lifecycle
    // =========================

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    // =========================
    // Metadata
    // =========================

    @Column(name = "issued_by", length = 100)
    private String issuedBy;

    // =========================
    // Getter / Setter
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
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

    public byte[] getBoundCertId() {
        return boundCertId;
    }

    public void setBoundCertId(byte[] boundCertId) {
        this.boundCertId = boundCertId;
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

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }
}