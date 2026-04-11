package vn.xime.trust.domain.event;

import java.time.Instant;

/**
 * Base Domain Event
 */
public abstract class KeyEvent {

    private final String kid;
    private final String serviceName;
    private final Instant createdAt;

    protected KeyEvent(String kid, String serviceName, Instant createdAt) {
        this.kid = kid;
        this.serviceName = serviceName;
        this.createdAt = createdAt;
    }

    public String getKid() {
        return kid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}