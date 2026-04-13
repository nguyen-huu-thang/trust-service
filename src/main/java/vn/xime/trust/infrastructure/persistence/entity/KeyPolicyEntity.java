package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "key_policies")
public class KeyPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false, unique = true)
    private String serviceId;

    @Column(name = "key_lifetime_seconds", nullable = false)
    private Long keyLifetimeSeconds;

    @Column(name = "jwt_ttl_seconds", nullable = false)
    private Long jwtTtlSeconds;

    @Column(name = "preload_seconds", nullable = false)
    private Long preloadSeconds;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // ===== getter/setter =====

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