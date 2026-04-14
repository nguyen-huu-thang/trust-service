package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.KeyEvent;
import vn.xime.trust.domain.model.KeyEventType;

import java.time.Instant;
import java.util.Map;

public class KeyEventFactory {

    public KeyEvent keyCreated(String kid, String serviceId, Instant now) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_CREATED,
                now,
                null
        );
    }

    public KeyEvent keyRotated(String kid, String serviceId, Instant now, String previousKid) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_ROTATED,
                now,
                Map.of("previous_kid", previousKid)
        );
    }

    public KeyEvent keyExpired(String kid, String serviceId, Instant now) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_EXPIRED,
                now,
                null
        );
    }

    public KeyEvent keyDeleted(String kid, String serviceId, Instant now) {
        return new KeyEvent(
                kid,
                serviceId,
                KeyEventType.KEY_DELETED,
                now,
                null
        );
    }
}