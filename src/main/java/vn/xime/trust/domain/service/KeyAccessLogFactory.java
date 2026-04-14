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
            Instant now,
            String ip
    ) {
        return new KeyAccessLog(
                kid,
                serviceId,
                action,
                includePrivate,
                now,
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
            Instant now,
            String ip,
            String error
    ) {
        return new KeyAccessLog(
                kid,
                serviceId,
                action,
                includePrivate,
                now,
                ip,
                false,
                error
        );
    }
}