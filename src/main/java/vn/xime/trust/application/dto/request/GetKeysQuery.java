package vn.xime.trust.application.dto.request;

public class GetKeysQuery {

    private final String signerServiceId;
    private final String verifierServiceId;

    public GetKeysQuery(String signerServiceId, String verifierServiceId) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }
}