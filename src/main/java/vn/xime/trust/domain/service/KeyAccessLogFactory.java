package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.KeyAccessAction;
import vn.xime.trust.domain.model.KeyAccessLog;

import java.time.Instant;

public class KeyAccessLogFactory {

    public KeyAccessLog success(
            String kid,
            String serviceId,
            KeyAccessAction action,
            boolean includePrivate,
            String ip
    ) {
        return new KeyAccessLog(
                kid,
                serviceId,
                action,
                includePrivate,
                Instant.now(),
                ip,
                true,
                null
        );
    }

    public KeyAccessLog failure(
            String kid,
            String serviceId,
            KeyAccessAction action,
            boolean includePrivate,
            String ip,
            String error
    ) {
        return new KeyAccessLog(
                kid,
                serviceId,
                action,
                includePrivate,
                Instant.now(),
                ip,
                false,
                error
        );
    }
}