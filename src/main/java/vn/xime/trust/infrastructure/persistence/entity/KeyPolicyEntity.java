package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_key_policies_pair",
                        columnNames = {"signer_service_id", "verifier_service_id"}
                )
        },
        indexes = {
                @Index(name = "idx_policies_signer", columnList = "signer_service_id"),
                @Index(name = "idx_policies_verifier", columnList = "verifier_service_id")
        }
)
public class KeyPolicyEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // RELATIONSHIP
    // =========================

    /**
     * Service dùng để SIGN (identity service)
     */
    @Column(name = "signer_service_id", nullable = false, length = 20)
    private String signerServiceId;

    /**
     * Service dùng để VERIFY
     */
    @Column(name = "verifier_service_id", nullable = false, length = 20)
    private String verifierServiceId;

    // =========================
    // POLICY
    // =========================

    /**
     * Thời gian key còn hiệu lực để VERIFY
     */
    @Column(name = "key_lifetime_seconds", nullable = false)
    private Long keyLifetimeSeconds;

    /**
     * TTL của JWT
     */
    @Column(name = "jwt_ttl_seconds", nullable = false)
    private Long jwtTtlSeconds;

    /**
     * preload key trước khi activate
     */
    @Column(name = "preload_seconds", nullable = false)
    private Long preloadSeconds;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

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

    public Long getKeyLifetimeSeconds() {
        return keyLifetimeSeconds;
    }

    public void setKeyLifetimeSeconds(Long keyLifetimeSeconds) {
        this.keyLifetimeSeconds = keyLifetimeSeconds;
    }

    public Long getJwtTtlSeconds() {
        return jwtTtlSeconds;
    }

    public void setJwtTtlSeconds(Long jwtTtlSeconds) {
        this.jwtTtlSeconds = jwtTtlSeconds;
    }

    public Long getPreloadSeconds() {
        return preloadSeconds;
    }

    public void setPreloadSeconds(Long preloadSeconds) {
        this.preloadSeconds = preloadSeconds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}