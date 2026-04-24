package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.BootstrapDto;


@Component
public class BootstrapMapper {

    public BootstrapDto toDto(Certificate cert, String privateKeyCert, String token) {
        return new BootstrapDto(
                IdService.toString(cert.getId()),
                cert.getServiceId(),
                cert.getPublicCert(),
                privateKeyCert,
                token,
                cert.getIssuedAt(),
                cert.getExpiresAt()
        );
    }
    
}
