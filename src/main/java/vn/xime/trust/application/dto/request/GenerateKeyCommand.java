package vn.xime.trust.application.dto.request;

import java.time.Instant;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class GenerateKeyCommand {
    private final String signerServiceId;
    private final String verifierServiceId;
    private final String algorithm;
    private final int keySize;
    private final Instant activateAt;
}