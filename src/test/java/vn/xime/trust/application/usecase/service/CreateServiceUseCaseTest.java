package vn.xime.trust.application.usecase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.domain.repository.ServiceRepository;
import vn.xime.trust.domain.factory.ServiceFactory;
import vn.xime.trust.domain.model.PlatformService;
import vn.xime.trust.application.port.out.Clock;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateServiceUseCaseTest {

    private ServiceRepository serviceRepository;
    private CreateServiceUseCase useCase;
    private ServiceFactory serviceFactory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        serviceRepository = mock(ServiceRepository.class);
        serviceFactory = mock(ServiceFactory.class);
        clock = mock(Clock.class);
        useCase = new CreateServiceUseCase(serviceRepository, serviceFactory, clock);
    }

    // =========================
    // SUCCESS
    // =========================

    @Test
    void should_create_service_successfully() {
        CreateServiceCommand cmd =
                new CreateServiceCommand("user-service", "User Service", "tenant1");

        when(serviceRepository.existsById("user-service")).thenReturn(false);

        useCase.execute(cmd);

        ArgumentCaptor<PlatformService> captor =
                ArgumentCaptor.forClass(PlatformService.class);

        verify(serviceRepository).save(captor.capture());

        var saved = captor.getValue();

        assertEquals("user-service", saved.getId());
        assertEquals("User Service", saved.getName());
        assertEquals("tenant1", saved.getTenant());
        assertEquals("ACTIVE", saved.getStatus().name());
        assertNotNull(saved.getCreatedAt());
    }

    // =========================
    // VALIDATION
    // =========================

    @Test
    void should_throw_when_id_is_null() {
        CreateServiceCommand cmd =
                new CreateServiceCommand(null, "User Service", "tenant1");

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(cmd));
    }

    @Test
    void should_throw_when_name_is_blank() {
        CreateServiceCommand cmd =
                new CreateServiceCommand("user-service", "   ", "tenant1");

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(cmd));
    }

    @Test
    void should_throw_when_service_exists() {
        CreateServiceCommand cmd =
                new CreateServiceCommand("user-service", "User Service", "tenant1");

        when(serviceRepository.existsById("user-service")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> useCase.execute(cmd));
    }
}