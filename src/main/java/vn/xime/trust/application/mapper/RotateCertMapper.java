package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.service.IdService;

import vn.xime.trust.application.dto.response.RotateCertDto;


@Component
public class RotateCertMapper {
    
    public RotateCertDto toDto(Certificate targetCert, String privateKey, String idPlayload, String token) {
        return new RotateCertDto(
            IdService.toString(targetCert.getId()),
            targetCert.getPublicCert(),
            privateKey,
            idPlayload,
            token,
            targetCert.getIssuedAt(),
            targetCert.getExpiresAt()
        );
    }
}
