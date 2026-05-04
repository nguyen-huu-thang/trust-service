package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.BootstrapDto;
import vn.xime.trust.application.dto.response.TokenDto;


@Component
public class BootstrapMapper {

    public BootstrapDto toDto(Certificate cert, String privateKeyCert, TokenDto token) {
        return new BootstrapDto(
                IdService.toString(cert.getId()),
                cert.getServiceId(),
                cert.getPublicCert(),
                privateKeyCert,
                token.getPayload().getTokenId(),
                token.getRawToken(),
                cert.getIssuedAt(),
                cert.getExpiresAt()
        );
    }
    
}
