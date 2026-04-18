package vn.xime.trust.application.dto.response;

public class ShardDto {

    private final String id;
    private final String serviceId;
    private final String host;
    private final int port;
    private final String status;
    private final long createdAt;

    public ShardDto(String id,
                    String serviceId,
                    String host,
                    int port,
                    String status,
                    long createdAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}