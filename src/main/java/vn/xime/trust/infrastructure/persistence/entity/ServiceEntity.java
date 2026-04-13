package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "services"
)
public class ServiceEntity {

    // =========================
    // ID (NO AUTO-GENERATE)
    // =========================

    /**
     * ID do hệ thống cấp (vd: user_service, payment_service)
     */
    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    // =========================
    // Basic info
    // =========================

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * multi-tenant support
     */
    @Column(name = "tenant", length = 100)
    private String tenant;

    /**
     * ACTIVE / DISABLED / DELETED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // =========================
    // Audit
    // =========================

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    // =========================
    // Lifecycle hooks
    // =========================

    @PrePersist
    public void prePersist() {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("service id must not be empty");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("service name must not be empty");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status must not be empty");
        }

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // =========================
    // Getter / Setter
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTenant() {
        return tenant;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}