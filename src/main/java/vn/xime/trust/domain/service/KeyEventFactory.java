package vn.xime.trust.domain.service;

import vn.xime.trust.domain.event.KeyEvent;
import vn.xime.trust.domain.event.KeyEventType;

import java.time.Instant;
import java.util.Map;

public class KeyEventFactory {

    public KeyEvent keyCreated(String kid, String serviceId) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_CREATED,
                Instant.now(),
                null
        );
    }

    public KeyEvent keyRotated(String kid, String serviceId, String previousKid) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_ROTATED,
                Instant.now(),
                Map.of("previous_kid", previousKid)
        );
    }

    public KeyEvent keyExpired(String kid, String serviceId) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_EXPIRED,
                Instant.now(),
                null
        );
    }

    public KeyEvent keyDeleted(String kid, String serviceId) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_DELETED,
                Instant.now(),
                null
        );
    }
}