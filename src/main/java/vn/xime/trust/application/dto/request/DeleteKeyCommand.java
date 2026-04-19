package vn.xime.trust.application.dto.request;

public class DeleteKeyCommand {

    private final String id;

    public DeleteKeyCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}