package vn.xime.trust.domain.model;

public enum CertEventType {

    CERT_ISSUED,
    CERT_ROTATED,
    CERT_EXPIRED,
    CERT_REVOKED,

    CERT_REFRESH_TOKEN_USED,
    CERT_REFRESH_TOKEN_FAILED,

    UNKNOWN
}