package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "keys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_keys_service_activate",
                        columnNames = {"service_id", "activate_at"}
                )
        },
        indexes = {
                @Index(name = "idx_keys_kid", columnList = "kid"),
                @Index(name = "idx_keys_service_active", columnList = "service_id,is_deleted,expires_at"),
                @Index(name = "idx_keys_service_activate", columnList = "service_id,activate_at DESC")
        }
)
public class KeyEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Identify
    // =========================

    @Column(name = "kid", nullable = false, unique = true, length = 100)
    private String kid;

    /**
     * Giữ dạng String (KHÔNG map ManyToOne)
     * → đúng boundary microservice
     */
    @Column(name = "service_id", nullable = false, length = 100)
    private String serviceId;

    // =========================
    // Key data
    // =========================

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String privateKeyEncrypted;

    // =========================
    // Crypto
    // =========================

    @Column(name = "algorithm", nullable = false, length = 20)
    private String algorithm;

    @Column(name = "key_size", nullable = false)
    private Integer keySize;

    // =========================
    // Lifecycle (QUAN TRỌNG NHẤT)
    // =========================

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    @Column(
            name = "activate_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant activateAt;

    @Column(
            name = "expires_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant expiresAt;

    // =========================
    // Control
    // =========================

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // =========================
    // Lifecycle hooks (VERY IMPORTANT)
    // =========================

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (isDeleted == null) {
            isDeleted = false;
        }

        validateTime();
    }

    @PreUpdate
    public void preUpdate() {
        validateTime();
    }

    private void validateTime() {
        if (activateAt == null || expiresAt == null) {
            throw new IllegalArgumentException("activateAt and expiresAt must not be null");
        }

        if (!expiresAt.isAfter(activateAt)) {
            throw new IllegalArgumentException("expires_at must be after activate_at");
        }
    }

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public void setPrivateKeyEncrypted(String privateKeyEncrypted) {
        this.privateKeyEncrypted = privateKeyEncrypted;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getKeySize() {
        return keySize;
    }

    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getActivateAt() {
        return activateAt;
    }

    public void setActivateAt(Instant activateAt) {
        this.activateAt = activateAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}