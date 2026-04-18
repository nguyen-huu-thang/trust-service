package vn.xime.trust.application.dto.response;

public class ServiceDto {

    private final String id;
    private final String name;
    private final String tenant;
    private final String status;
    private final long createdAt;

    public ServiceDto(String id, String name, String tenant, String status, long createdAt) {
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

    public long getCreatedAt() {
        return createdAt;
    }
}