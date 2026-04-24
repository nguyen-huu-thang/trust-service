package vn.xime.trust.application.port.out;

import vn.xime.trust.domain.model.TokenPayload;

public interface TokenCodec {

    String encode(TokenPayload payload);

    TokenPayload decode(String token);

    String hash(String token);
}