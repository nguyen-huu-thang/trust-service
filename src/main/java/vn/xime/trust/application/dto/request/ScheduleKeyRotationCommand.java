package vn.xime.trust.application.dto.request;

import java.time.Instant;

public class ScheduleKeyRotationCommand {

    private String signerServiceId;
    private String verifierServiceId;

    private String algorithm; // optional
    private int keySize;      // optional

    private Instant activateAt;


    
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