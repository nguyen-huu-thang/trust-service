package vn.xime.trust.domain.service;

import vn.xime.trust.domain.model.CertEvent;
import vn.xime.trust.domain.model.CertEventType;

import java.time.Instant;
import java.util.Map;

public class CertEventFactory {

    public CertEvent certIssued(String serviceId, String kid, Instant now) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_ISSUED,
                now,
                null
        );
    }

    public CertEvent certRotated(String serviceId, String newKid, Instant now, String oldKid) {
        return new CertEvent(
                serviceId,
                newKid,
                CertEventType.CERT_ROTATED,
                now,
                Map.of("previous_kid", oldKid)
        );
    }

    public CertEvent certExpired(String serviceId, String kid, Instant now) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_EXPIRED,
                now,
                null
        );
    }

    public CertEvent certRevoked(String serviceId, String kid, Instant now, String reason) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REVOKED,
                now,
                Map.of("reason", reason)
        );
    }

    public CertEvent refreshTokenUsed(String serviceId, String kid, Instant now) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REFRESH_TOKEN_USED,
                now,
                null
        );
    }

    public CertEvent refreshTokenFailed(String serviceId, String kid, Instant now, String error) {
        return new CertEvent(
                serviceId,
                kid,
                CertEventType.CERT_REFRESH_TOKEN_FAILED,
                now,
                Map.of("error", error)
        );
    }
}