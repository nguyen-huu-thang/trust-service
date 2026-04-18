package vn.xime.trust.application.usecase.service;

import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.domain.model.Service;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.domain.repository.ServiceRepository;

import java.util.List;

@Component
public class ListServicesUseCase {

    private final ServiceRepository repository;

    public ListServicesUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    public Result execute(
            String tenant,
            String status,
            int limit,
            String cursor
    ) {

        // =========================
        // DEFAULTS
        // =========================

        int safeLimit = limit > 0 ? Math.min(limit, 100) : 50;
        String safeCursor = (cursor == null || cursor.isBlank()) ? null : cursor;

        // =========================
        // STATUS PARSE
        // =========================

        ServiceStatus statusEnum = null;

        if (status != null && !status.isBlank()) {
            try {
                statusEnum = ServiceStatus.valueOf(status);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        // =========================
        // QUERY
        // =========================

        List<Service> services = repository.search(
                tenant,
                statusEnum,
                safeLimit,
                safeCursor
        );

        // =========================
        // MAP DTO
        // =========================

        List<ServiceDto> dtos = services.stream()
                .map(this::toDto)
                .toList();

        // =========================
        // CURSOR
        // =========================

        String nextCursor = services.isEmpty()
                ? null
                : services.get(services.size() - 1).getId();

        return new Result(dtos, nextCursor);
    }

    private ServiceDto toDto(Service s) {
        return new ServiceDto(
                s.getId(),
                s.getName(),
                s.getTenant(),
                s.getStatus().name(),
                s.getCreatedAt().toEpochMilli()
        );
    }

    public record Result(List<ServiceDto> services, String nextCursor) {}
}