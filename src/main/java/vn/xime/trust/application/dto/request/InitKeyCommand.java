package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class InitKeyCommand {
    private final String signerServiceId;
    private final String verifierServiceId;
}