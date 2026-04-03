package vn.xime.key.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "key_access_logs",
    indexes = {
        @Index(name = "idx_key_access_logs_service_time", columnList = "service_name, requested_at")
    }
)
public class KeyAccessLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Key Info
    // =========================

    @Column(name = "kid")
    private String kid;

    @Column(name = "service_name")
    private String serviceName;

    // =========================
    // Caller Info
    // =========================

    @Column(name = "client_service")
    private String clientService;

    /**
     * GET_KEYS / GET_KEY_BY_ID
     */
    @Column(name = "action")
    private String action;

    /**
     * Có lấy private key không
     */
    @Column(name = "include_private")
    private boolean includePrivate;

    // =========================
    // Request Info
    // =========================

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    // =========================
    // Result
    // =========================

    @Column(name = "success")
    private boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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

    public String getClientService() {
        return clientService;
    }

    public void setClientService(String clientService) {
        this.clientService = clientService;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isIncludePrivate() {
        return includePrivate;
    }

    public void setIncludePrivate(boolean includePrivate) {
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}