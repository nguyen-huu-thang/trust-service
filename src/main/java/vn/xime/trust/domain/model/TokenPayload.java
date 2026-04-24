package vn.xime.trust.domain.model;

public class TokenPayload {

    private final String id;
    private final String serviceId;
    private final String shardId;
    private final String certId;
    private final long issuedAt;
    private final long expiresAt;

    public TokenPayload(String id, String serviceId, String shardId, String certId, long issuedAt, long expiresAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.shardId = shardId;
        this.certId = certId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    // Getters
    public String getTokenId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getShardId() {
        return shardId;
    }

    public String getCertId() {
        return certId;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
