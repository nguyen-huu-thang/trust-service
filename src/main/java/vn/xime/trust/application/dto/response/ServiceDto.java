package vn.xime.trust.application.dto.response;

import java.time.Instant;

public class ServiceDto {

    private final String id;
    private final String name;
    private final String tenant;
    private final String status;
    private final Instant createdAt;

    public ServiceDto(String id, String name, String tenant, String status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.tenant = tenant;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTenant() {
        return tenant;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}