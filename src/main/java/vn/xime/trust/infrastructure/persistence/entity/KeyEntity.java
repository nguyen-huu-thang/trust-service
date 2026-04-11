package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
name = "keys",
indexes = {
@Index(name = "idx_keys_service", columnList = "service_name"),
@Index(name = "idx_keys_kid", columnList = "kid", unique = true),
@Index(name = "idx_keys_activate_at", columnList = "activate_at"),
@Index(name = "idx_keys_expires_at", columnList = "expires_at")
}
)
public class KeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Identify
    // =========================

    @Column(name = "kid", nullable = false, unique = true)
    private String kid;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    // =========================
    // Key Data
    // =========================

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String privateKeyEncrypted;

    // =========================
    // Crypto
    // =========================

    @Column(name = "algorithm", nullable = false)
    private String algorithm;

    @Column(name = "key_size", nullable = false)
    private Integer keySize;

    // =========================
    // Lifecycle (TIME-BASED)
    // =========================

    /**
     * Chỉ mang tính metadata (không dùng cho logic chính)
     * Có thể bỏ trong tương lai
     */
    @Column(name = "status")
    private String status;

    /**
     * Thời điểm tạo key
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Thời điểm bắt đầu SIGN
     */
    @Column(name = "activate_at", nullable = false)
    private Instant activateAt;

    /**
     * Thời điểm NGỪNG VERIFY (quan trọng nhất)
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // =========================
    // Control
    // =========================

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}