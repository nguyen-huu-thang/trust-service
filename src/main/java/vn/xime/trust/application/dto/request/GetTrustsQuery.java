package vn.xime.trust.application.dto.request;

public class GetTrustsQuery {

    private final String signerServiceId;

    public GetTrustsQuery(String signerServiceId) {
        this.signerServiceId = signerServiceId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }
}