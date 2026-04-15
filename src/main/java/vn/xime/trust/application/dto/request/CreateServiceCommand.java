package vn.xime.trust.application.dto.request;

public class CreateServiceCommand {

    private final String id;
    private final String name;
    private final String tenant;

    public CreateServiceCommand(String id, String name, String tenant) {
        this.id = id;
        this.name = name;
        this.tenant = tenant;
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
}