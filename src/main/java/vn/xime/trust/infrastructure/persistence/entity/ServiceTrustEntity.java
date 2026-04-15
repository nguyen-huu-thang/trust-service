package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "service_trusts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_service_trusts_pair",
                        columnNames = {"signer_service_id", "verifier_service_id"}
                )
        },
        indexes = {
                @Index(name = "idx_trusts_signer", columnList = "signer_service_id"),
                @Index(name = "idx_trusts_verifier", columnList = "verifier_service_id")
        }
)
public class ServiceTrustEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // RELATIONSHIP (CORE)
    // =========================

    /**
     * Service dùng để SIGN (identity service)
     */
    @Column(name = "signer_service_id", nullable = false, length = 100)
    private String signerServiceId;

    /**
     * Service dùng để VERIFY
     */
    @Column(name = "verifier_service_id", nullable = false, length = 100)
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
    // ROTATION CONTROL
    // =========================

    @Column(name = "auto_rotate", nullable = false)
    private Boolean autoRotate = true;

    @Column(
            name = "last_rotated_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant lastRotatedAt;

    @Column(
            name = "next_rotation_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant nextRotationAt;

    // =========================
    // AUDIT
    // =========================

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant updatedAt;

    // =========================
    // GETTER / SETTER
    // =========================

    public Long getId() {
        return id;
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

    public Boolean getAutoRotate() {
        return autoRotate;
    }

    public void setAutoRotate(Boolean autoRotate) {
        this.autoRotate = autoRotate;
    }

    public Instant getLastRotatedAt() {
        return lastRotatedAt;
    }

    public void setLastRotatedAt(Instant lastRotatedAt) {
        this.lastRotatedAt = lastRotatedAt;
    }

    public Instant getNextRotationAt() {
        return nextRotationAt;
    }

    public void setNextRotationAt(Instant nextRotationAt) {
        this.nextRotationAt = nextRotationAt;
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