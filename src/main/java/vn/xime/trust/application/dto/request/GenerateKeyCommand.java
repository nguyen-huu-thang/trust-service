package vn.xime.trust.application.dto.request;

import java.time.Instant;

public class GenerateKeyCommand {

    private final String signerServiceId;
    private final String verifierServiceId;

    private final String algorithm;
    private final int keySize;

    private final Instant activateAt; // optional

    public GenerateKeyCommand(
            String signerServiceId,
            String verifierServiceId,
            String algorithm,
            int keySize,
            Instant activateAt
    ) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.activateAt = activateAt;
    }

    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public Instant getActivateAt() {
        return activateAt;
    }
}