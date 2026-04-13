package vn.xime.trust.domain.event;

public enum CertEventType {

    CERT_ISSUED,
    CERT_ROTATED,
    CERT_EXPIRED,
    CERT_REVOKED,

    CERT_REFRESH_TOKEN_USED,
    CERT_REFRESH_TOKEN_FAILED,

    UNKNOWN
}