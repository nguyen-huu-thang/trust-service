package vn.xime.trust.domain.event;

import java.time.Instant;

import vn.xime.trust.domain.model.KeyEvent;
import vn.xime.trust.domain.model.KeyEventType;

public class KeyCreatedEvent extends KeyEvent {

    public KeyCreatedEvent(String kid, String serviceName, Instant createdAt) {
        super(
            kid,
            serviceName,
            KeyEventType.KEY_CREATED,
            createdAt,
            null

        );
    }
}