package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "certificates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cert_service_issued",
                        columnNames = {"service_id", "issued_at"}
                )
        },
        indexes = {
                @Index(name = "idx_cert_kid", columnList = "kid"),
                @Index(name = "idx_cert_service_expire", columnList = "service_id,expires_at")
        }
)
public class CertificateEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Ownership
    // =========================

    /**
     * Không dùng ManyToOne
     * → giữ boundary microservice
     */
    @Column(name = "service_id", nullable = false, length = 100)
    private String serviceId;

    // =========================
    // Identity
    // =========================

    @Column(name = "kid", nullable = false, unique = true, length = 100)
    private String kid;

    // =========================
    // Certificate data
    // =========================

    @Column(name = "public_cert", nullable = false, columnDefinition = "TEXT")
    private String publicCert;

    @Column(name = "private_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String privateKeyEncrypted;

    // =========================
    // Lifecycle (CRITICAL)
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
     * ACTIVE / EXPIRED / REVOKED (future)
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // =========================
    // Lifecycle hooks
    // =========================

    // ❌ Không đặt logic ở JPA Entity. chỉ để test tạm thời.

    // @PrePersist
    // public void prePersist() {

    //     if (issuedAt == null) {
    //         throw new IllegalStateException("issuedAt must not be null");
    //     }

    //     validate();
    // }

    // @PreUpdate
    // public void preUpdate() {
    //     validate();
    // }

    // private void validate() {

    //     if (issuedAt == null || expiresAt == null) {
    //         throw new IllegalArgumentException("issued_at and expires_at must not be null");
    //     }

    //     if (!expiresAt.isAfter(issuedAt)) {
    //         throw new IllegalArgumentException("expires_at must be after issued_at");
    //     }

    //     if (status == null || status.isBlank()) {
    //         throw new IllegalArgumentException("status must not be empty");
    //     }
    // }

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

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getPublicCert() {
        return publicCert;
    }

    public void setPublicCert(String publicCert) {
        this.publicCert = publicCert;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public void setPrivateKeyEncrypted(String privateKeyEncrypted) {
        this.privateKeyEncrypted = privateKeyEncrypted;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}