package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class ShardDto {

    private final String id;
    private final String serviceId;
    private final String host;
    private final String status;
    private final Instant createdAt;

    public ShardDto(String id, String serviceId, String host, String status, Instant createdAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.host = host;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getUrl() {
        return host;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}