package vn.xime.trust.application.usecase.cert;

import lombok.RequiredArgsConstructor;
import vn.xime.trust.domain.model.TokenPayload;
import vn.xime.trust.domain.model.CertRefreshToken;
import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.factory.CertRefreshTokenFactory;
import vn.xime.trust.domain.repository.CertRefreshTokenRepository;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.port.out.TokenCodec;
import vn.xime.trust.application.dto.response.TokenDto;


@RequiredArgsConstructor
public class GenerateRefreshTokenUseCase {

    private final CertRefreshTokenRepository tokenRepository;
    private final CertRefreshTokenFactory tokenFactory;
    private final TokenCodec tokenCodec;

    public TokenDto execute(
            String serviceId,
            String shardId,
            Certificate cert
    ) {

        CertRefreshToken newToken = tokenFactory.create(
                true,
                cert.getExpiresAt()
        );

        TokenPayload newPayload = new TokenPayload(
            IdService.toString(newToken.getId()),
            serviceId,
            shardId,
            IdService.toString(cert.getId()),
            newToken.getIssuedAt().toEpochMilli(),
            cert.getExpiresAt().toEpochMilli()
        );

        String newRawToken = tokenCodec.encode(newPayload);

        String newTokenHash = tokenCodec.hash(newRawToken);

        newToken = newToken.markTokenHash(newTokenHash);

        tokenRepository.save(newToken);

        TokenDto tokenDto = new TokenDto(newToken, newPayload, newRawToken);

        return tokenDto;
    }
}