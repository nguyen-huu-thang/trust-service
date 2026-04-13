package vn.xime.trust.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "key_access_logs",
        indexes = {
                @Index(
                        name = "idx_key_access_logs_service_time",
                        columnList = "service_id,requested_at DESC"
                )
        }
)
public class KeyAccessLogEntity {

    // =========================
    // ID
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Reference (NO RELATION)
    // =========================

    /**
     * Không dùng ManyToOne
     * → log không nên phụ thuộc entity khác
     */
    @Column(name = "kid", length = 100)
    private String kid;

    @Column(name = "service_id", length = 100)
    private String serviceId;

    // =========================
    // Action
    // =========================

    @Column(name = "action", length = 50)
    private String action;

    /**
     * true nếu có trả private key
     */
    @Column(name = "include_private")
    private Boolean includePrivate;

    // =========================
    // Time (VERY IMPORTANT)
    // =========================

    @Column(
            name = "requested_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private Instant requestedAt;

    // =========================
    // Metadata
    // =========================

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // =========================
    // Lifecycle hooks
    // =========================

    @PrePersist
    public void prePersist() {
        if (requestedAt == null) {
            requestedAt = Instant.now();
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getIncludePrivate() {
        return includePrivate;
    }

    public void setIncludePrivate(Boolean includePrivate) {
        this.includePrivate = includePrivate;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}