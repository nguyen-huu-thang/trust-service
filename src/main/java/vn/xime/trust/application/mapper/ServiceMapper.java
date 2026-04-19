package vn.xime.trust.application.mapper;

import org.springframework.stereotype.Component;

import vn.xime.trust.domain.model.Service;
import vn.xime.trust.application.dto.response.ServiceDto;


@Component
public class ServiceMapper {
    
    public ServiceDto toDto(Service s) {
        return new ServiceDto(
            s.getId(),
            s.getName(),
            s.getTenant(),
            s.getStatus().name(),
            s.getCreatedAt()
            );
        }
}
