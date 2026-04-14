package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_key_policies_service",
                        columnNames = {"service_id"}
                )
        }
)
public class KeyPolicyEntity {

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
     * 1 service = 1 policy
     * → giữ String, không dùng relation
     */
    @Column(name = "service_id", nullable = false, length = 100)
    private String serviceId;

    // =========================
    // Policy (CRITICAL LOGIC)
    // =========================

    /**
     * Tổng lifetime của key (từ activate → expires)
     */
    @Column(name = "key_lifetime_seconds", nullable = false)
    private Long keyLifetimeSeconds;

    /**
     * TTL của JWT
     */
    @Column(name = "jwt_ttl_seconds", nullable = false)
    private Long jwtTtlSeconds;

    /**
     * preload trước khi activate key mới
     */
    @Column(name = "preload_seconds", nullable = false)
    private Long preloadSeconds;

    // =========================
    // Audit
    // =========================

    @Column(
            name = "created_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant updatedAt;

    // =========================
    // Lifecycle hooks
    // =========================


    // ❌ Không đặt logic ở JPA Entity. chỉ để test tạm thời.

    // @PrePersist
    // public void prePersist() {
    //     if (createdAt == null) {
    //         throw new IllegalStateException("createdAt must not be null");
    //     }

    //     if (updatedAt == null) {
    //         throw new IllegalStateException("updatedAt must not be null");
    //     }

    //     validate();
    // }

    // @PreUpdate
    // public void preUpdate() {
    //     throw new UnsupportedOperationException("KeyPolicyEntity is immutable and cannot be updated");
    //     validate();
    // }

    // =========================
    // Validation
    // =========================

    // private void validate() {

    //     if (keyLifetimeSeconds == null || keyLifetimeSeconds <= 0) {
    //         throw new IllegalArgumentException("key_lifetime_seconds must be > 0");
    //     }

    //     if (jwtTtlSeconds == null || jwtTtlSeconds <= 0) {
    //         throw new IllegalArgumentException("jwt_ttl_seconds must be > 0");
    //     }

    //     if (preloadSeconds == null || preloadSeconds < 0) {
    //         throw new IllegalArgumentException("preload_seconds must be >= 0");
    //     }

    //     /**
    //      * QUAN TRỌNG:
    //      * expires_at phải cover JWT TTL
    //      *
    //      * => key_lifetime >= jwt_ttl
    //      */
    //     if (keyLifetimeSeconds < jwtTtlSeconds) {
    //         throw new IllegalArgumentException(
    //                 "key_lifetime_seconds must be >= jwt_ttl_seconds"
    //         );
    //     }

    //     /**
    //      * preload không được lớn hơn lifetime
    //      */
    //     if (preloadSeconds >= keyLifetimeSeconds) {
    //         throw new IllegalArgumentException(
    //                 "preload_seconds must be < key_lifetime_seconds"
    //         );
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