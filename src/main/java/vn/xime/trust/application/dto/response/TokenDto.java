package vn.xime.trust.application.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.TokenPayload;


@Getter
@AllArgsConstructor
public class TokenDto {
    private final CertRefreshToken token;
    private final TokenPayload payload;
    private final String rawToken;
}
