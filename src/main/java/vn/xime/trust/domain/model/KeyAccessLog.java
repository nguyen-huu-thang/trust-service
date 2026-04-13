package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class KeyAccessLog {

    private final String kid;
    private final String serviceId;

    private final KeyAccessAction action;

    private final boolean includePrivate;

    private final Instant requestedAt;

    private final String ipAddress;

    private final boolean success;
    private final String errorMessage;

    public KeyAccessLog(
            String kid,
            String serviceId,
            KeyAccessAction action,
            boolean includePrivate,
            Instant requestedAt,
            String ipAddress,
            boolean success,
            String errorMessage
    ) {
        this.kid = kid;
        this.serviceId = serviceId;
        this.action = Objects.requireNonNull(action);
        this.includePrivate = includePrivate;
        this.requestedAt = Objects.requireNonNull(requestedAt);
        this.ipAddress = ipAddress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    // =========================
    // GETTERS
    // =========================

    public String getKid() {
        return kid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public KeyAccessAction getAction() {
        return action;
    }

    public boolean isIncludePrivate() {
        return includePrivate;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}