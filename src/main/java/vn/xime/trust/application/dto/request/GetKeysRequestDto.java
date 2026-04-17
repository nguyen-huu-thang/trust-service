package vn.xime.trust.application.dto.request;

public class GetKeysRequestDto {

    private final String signerServiceId;
    private final String verifierServiceId;

    public GetKeysRequestDto(String signerServiceId, String verifierServiceId) {
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