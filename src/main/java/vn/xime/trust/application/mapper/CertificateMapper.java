package vn.xime.trust.application.mapper;

import vn.xime.trust.domain.model.Certificate;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.dto.response.AdminCertDto;
import vn.xime.trust.application.dto.response.ServiceCertDto;

public class CertificateMapper {
    
    public AdminCertDto toAdminDto(Certificate cert) {
        return new AdminCertDto(
                IdService.toString(cert.getId()),
                cert.getServiceId(),
                cert.getPublicCert(),
                cert.getIssuedAt(),
                cert.getExpiresAt(),
                cert.getStatus(),
                cert.isDeleted()
        );
    }

    public ServiceCertDto toServiceDto(Certificate cert, String privateKey) {
        return new ServiceCertDto(
                IdService.toString(cert.getId()),
                cert.getServiceId(),
                cert.getPublicCert(),
                privateKey,
                cert.getIssuedAt(),
                cert.getExpiresAt()
        );
    }
}