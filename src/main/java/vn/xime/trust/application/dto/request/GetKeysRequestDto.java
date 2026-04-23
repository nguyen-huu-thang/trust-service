package vn.xime.trust.application.dto.request;

import lombok.Getter;
import lombok.AllArgsConstructor;


@Getter
@AllArgsConstructor
public class GetKeysRequestDto {
    private final String signerServiceId;
    private final String verifierServiceId;
}