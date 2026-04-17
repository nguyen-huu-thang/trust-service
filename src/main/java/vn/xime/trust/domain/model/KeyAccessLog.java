package vn.xime.trust.domain.model;

import java.time.Instant;
import java.util.Objects;

public class KeyAccessLog {

    private final Id id;

    private final Id keyId;
    private final String signerServiceId;
    private final String verifierServiceId;

    private final KeyAccessAction action;

    private final boolean includePrivate;

    private final Instant requestedAt;

    private final String ipAddress;

    private final boolean success;
    private final String errorMessage;

    public KeyAccessLog(
            Id id,
            Id keyId,
            String signerServiceId,
            String verifierServiceId,
            KeyAccessAction action,
            boolean includePrivate,
            Instant requestedAt,
            String ipAddress,
            boolean success,
            String errorMessage
    ) {
        this.id = Objects.requireNonNull(id);
        this.keyId = keyId;
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
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

    public Id getId() {
    return id;
    }

    public Id getKeyId() {
        return keyId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
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