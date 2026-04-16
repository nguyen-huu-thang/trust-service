package vn.xime.trust.application.dto.request;

public class RegisterShardCommand {

    private final String id;
    private final String serviceId;
    private final String host;
    private final Integer port;

    public RegisterShardCommand(String id, String serviceId, String host, Integer port) {
        this.id = id;
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
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

    public Integer getPort() {
        return port;
    }
}