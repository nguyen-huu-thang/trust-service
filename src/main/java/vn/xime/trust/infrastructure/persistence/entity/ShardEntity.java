package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "shards",
        indexes = {
                @Index(
                        name = "idx_shards_service",
                        columnList = "service_id"
                )
        }
)
public class ShardEntity {

    // =========================
    // ID (Shard ID)
    // =========================

    /**
     * shard_id (ví dụ: A1B2C3)
     * Global unique
     */
    @Id
    @Column(name = "id", length = 20)
    private String id;

    // =========================
    // Ownership
    // =========================

    /**
     * FK → services(id)
     * Không map ManyToOne để tránh coupling
     */
    @Column(name = "service_id", nullable = false, length = 20)
    private String serviceId;

    // =========================
    // Network / Routing info
    // =========================

    @Column(name = "host", length = 100)
    private String host;

    @Column(name = "port")
    private Integer port;

    // =========================
    // Status
    // =========================

    /**
     * Ví dụ:
     * ACTIVE
     * INACTIVE
     * MAINTENANCE
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // =========================
    // Time
    // =========================

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant createdAt;

    // =========================
    // Getter / Setter
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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
}