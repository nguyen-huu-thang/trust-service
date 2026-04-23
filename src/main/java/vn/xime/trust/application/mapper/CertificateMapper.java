package vn.xime.trust.application.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.CertificateResponseDto;

public class CertificateMapper {
    
    public CertificateResponseDto toResponseDto(Certificate cert) {
        return new CertificateResponseDto(
                IdService.toString(cert.getId()),
                cert.getServiceId(),
                cert.getPublicCert(),
                cert.getIssuedAt(),
                cert.getExpiresAt(),
                cert.getStatus(),
                cert.isDeleted()
        );
    }
}