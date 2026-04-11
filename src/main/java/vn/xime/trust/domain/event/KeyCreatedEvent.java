package vn.xime.trust.domain.event;

import java.time.Instant;

public class KeyCreatedEvent extends KeyEvent {

    public KeyCreatedEvent(String kid, String serviceName, Instant createdAt) {
        super(kid, serviceName, createdAt);
    }
}