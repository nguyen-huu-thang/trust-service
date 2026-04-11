package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "key_policies",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_key_policies_service", columnNames = "service_name")
    }
)
public class KeyPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Identify
    // =========================

    @Column(name = "service_name", nullable = false, unique = true)
    private String serviceName;

    // =========================
    // Rotation Config
    // =========================

    /**
     * Lifetime của key (vd: 30 ngày)
     */
    @Column(name = "key_lifetime_seconds", nullable = false)
    private Long keyLifetimeSeconds;

    /**
     * TTL của JWT (vd: 3600s)
     */
    @Column(name = "jwt_ttl_seconds", nullable = false)
    private Long jwtTtlSeconds;

    /**
     * Preload NEXT key trước bao lâu
     */
    @Column(name = "preload_seconds", nullable = false)
    private Long preloadSeconds;

    // =========================
    // Audit
    // =========================

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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