package vn.xime.trust.application.usecase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.xime.trust.application.dto.request.GetServicesQuery;
import vn.xime.trust.domain.model.ServiceStatus;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.model.PlatformService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetServicesUseCaseTest {

    private ServiceRepository serviceRepository;
    private GetServicesUseCase useCase;

    @BeforeEach
    void setUp() {
        serviceRepository = mock(ServiceRepository.class);
        useCase = new GetServicesUseCase(serviceRepository);
    }

    // =========================
    // GET ALL
    // =========================

    @Test
    void should_return_empty_list_when_no_services() {
        when(serviceRepository.findAll()).thenReturn(List.of());

        var result = useCase.execute(new GetServicesQuery());

        assertTrue(result.isEmpty());
    }

    @Test
    void should_return_services() {
        var service = new PlatformService(
                "user-service",
                "User Service",
                "tenant1",
                ServiceStatus.ACTIVE,
                Instant.now()
        );

        when(serviceRepository.findAll()).thenReturn(List.of(service));

        var result = useCase.execute(new GetServicesQuery());

        assertEquals(1, result.size());
        assertEquals("user-service", result.get(0).getId());
    }

    // =========================
    // GET BY ID
    // =========================

    @Test
    void should_return_service_by_id() {
        var service = new PlatformService(
                "user-service",
                "User Service",
                "tenant1",
                ServiceStatus.ACTIVE,
                Instant.now()
        );

        when(serviceRepository.findById("user-service"))
                .thenReturn(Optional.of(service));

        var result = useCase.getById("user-service");

        assertTrue(result.isPresent());
        assertEquals("user-service", result.get().getId());
    }

    @Test
    void should_return_empty_when_service_not_found() {
        when(serviceRepository.findById("user-service"))
                .thenReturn(Optional.empty());

        var result = useCase.getById("user-service");

        assertTrue(result.isEmpty());
    }

    @Test
    void should_throw_when_id_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.getById(" "));
    }

    // =========================
    // EXISTS
    // =========================

    @Test
    void should_return_true_when_exists() {
        when(serviceRepository.existsById("user-service")).thenReturn(true);

        assertTrue(useCase.exists("user-service"));
    }

    @Test
    void should_return_false_when_not_exists() {
        when(serviceRepository.existsById("user-service")).thenReturn(false);

        assertFalse(useCase.exists("user-service"));
    }

    @Test
    void should_return_false_when_id_invalid() {
        assertFalse(useCase.exists(" "));
        assertFalse(useCase.exists(null));
    }
}