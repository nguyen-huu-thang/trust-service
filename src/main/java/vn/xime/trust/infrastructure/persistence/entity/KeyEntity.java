package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "keys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_keys_pair_activate",
                        columnNames = {"signer_service_id", "verifier_service_id", "activate_at"}
                )
        },
        indexes = {
                @Index(name = "idx_keys_id", columnList = "id"),

                @Index(
                        name = "idx_keys_signer_active",
                        columnList = "signer_service_id,is_deleted,expires_at"
                ),

                @Index(
                        name = "idx_keys_signer_activate",
                        columnList = "signer_service_id,activate_at DESC"
                ),

                @Index(
                        name = "idx_keys_pair_active",
                        columnList = "signer_service_id,verifier_service_id,expires_at"
                )
        }
)
public class KeyEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // RELATIONSHIP
    // =========================

    @Column(name = "signer_service_id", nullable = false, length = 20)
    private String signerServiceId;

    @Column(name = "verifier_service_id", nullable = false, length = 20)
    private String verifierServiceId;

    // =========================
    // KEY DATA
    // =========================

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String privateKeyEncrypted;

    // =========================
    // CRYPTO
    // =========================

    @Column(name = "algorithm", nullable = false, length = 20)
    private String algorithm;

    @Column(name = "key_size", nullable = false)
    private Integer keySize;

    // =========================
    // LIFECYCLE
    // =========================

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "activate_at", nullable = false)
    private Instant activateAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // =========================
    // CONTROL
    // =========================

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public void setSignerServiceId(String signerServiceId) {
        this.signerServiceId = signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public void setVerifierServiceId(String verifierServiceId) {
        this.verifierServiceId = verifierServiceId;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}