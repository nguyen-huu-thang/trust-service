package vn.xime.trust.domain.service;

import vn.xime.trust.domain.event.CertEvent;
import vn.xime.trust.domain.event.CertEventType;

import java.time.Instant;
import java.util.Map;

public class CertEventFactory {

    public CertEvent certIssued(String serviceId, String kid) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_ISSUED,
                Instant.now(),
                null
        );
    }

    public CertEvent certRotated(String serviceId, String newKid, String oldKid) {
        return new CertEvent(
                serviceId,
                newKid,
                CertEventType.CERT_ROTATED,
                Instant.now(),
                Map.of("previous_kid", oldKid)
        );
    }

    public CertEvent certExpired(String serviceId, String kid) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_EXPIRED,
                Instant.now(),
                null
        );
    }

    public CertEvent certRevoked(String serviceId, String kid, String reason) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REVOKED,
                Instant.now(),
                Map.of("reason", reason)
        );
    }

    public CertEvent refreshTokenUsed(String serviceId, String kid) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REFRESH_TOKEN_USED,
                Instant.now(),
                null
        );
    }

    public CertEvent refreshTokenFailed(String serviceId, String kid, String error) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REFRESH_TOKEN_FAILED,
                Instant.now(),
                Map.of("error", error)
        );
    }
}