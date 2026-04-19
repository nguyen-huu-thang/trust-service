package vn.xime.trust.application.dto.request;

import java.time.Instant;

public class ScheduleKeyRotationCommand {

    private final String signerServiceId;
    private final String verifierServiceId;

    private final Instant activateAt;


    public ScheduleKeyRotationCommand(
            String signerServiceId,
            String verifierServiceId,
            Instant activateAt
    ) {
        this.signerServiceId = signerServiceId;
        this.verifierServiceId = verifierServiceId;
        this.activateAt = activateAt;
    }


    public String getSignerServiceId() {
        return signerServiceId;
    }

    public String getVerifierServiceId() {
        return verifierServiceId;
    }

    public Instant getActivateAt() {
        return activateAt;
    }
}