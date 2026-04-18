package vn.xime.trust.api.grpc.internal;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.usecase.service.*;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.grpc.internal.service.*;

@Component
public class ServiceAdminGrpcService extends ServiceAdminGrpc.ServiceAdminImplBase {

    private final CreateServiceUseCase createUseCase;
    private final GetServiceUseCase getUseCase;
    private final ListServicesUseCase listUseCase;
    private final UpdateServiceStatusUseCase updateStatusUseCase;

    public ServiceAdminGrpcService(
            CreateServiceUseCase createUseCase,
            GetServiceUseCase getUseCase,
            ListServicesUseCase listUseCase,
            UpdateServiceStatusUseCase updateStatusUseCase
    ) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateStatusUseCase = updateStatusUseCase;
    }

    // ==================================================
    // CREATE
    // ==================================================

    @Override
    public void createService(
            CreateServiceRequest request,
            StreamObserver<CreateServiceResponse> responseObserver
    ) {
        try {

            CreateServiceCommand cmd = new CreateServiceCommand(
                    request.getId(),
                    request.getName(),
                    request.getTenant()
            );

            ServiceDto result = createUseCase.execute(cmd);

            CreateServiceResponse response = CreateServiceResponse.newBuilder()
                    .setService(toProto(result))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET
    // ==================================================

    @Override
    public void getService(
            GetServiceRequest request,
            StreamObserver<GetServiceResponse> responseObserver
    ) {
        try {

            ServiceDto result = getUseCase.execute(request.getId());

            GetServiceResponse response = GetServiceResponse.newBuilder()
                    .setService(toProto(result))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // LIST
    // ==================================================

    @Override
    public void listServices(
            ListServicesRequest request,
            StreamObserver<ListServicesResponse> responseObserver
    ) {
        try {

            int limit = request.getLimit() > 0 ? request.getLimit() : 50;
            String cursor = request.getCursor().isBlank() ? null : request.getCursor();

            ListServicesUseCase.Result result = listUseCase.execute(
                    request.getTenant(),
                    request.getStatus(),
                    limit,
                    cursor
            );

            ListServicesResponse.Builder builder = ListServicesResponse.newBuilder();

            result.services().forEach(s -> builder.addServices(toProto(s)));

            if (result.nextCursor() != null) {
                builder.setNextCursor(result.nextCursor());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // UPDATE STATUS
    // ==================================================

    @Override
    public void updateServiceStatus(
            UpdateServiceStatusRequest request,
            StreamObserver<UpdateServiceStatusResponse> responseObserver
    ) {
        try {

            String status = updateStatusUseCase.execute(
                    request.getId(),
                    request.getStatus()
            );

            UpdateServiceStatusResponse response =
                    UpdateServiceStatusResponse.newBuilder()
                            .setStatus(status)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // MAPPER
    // ==================================================

    private vn.xime.trust.grpc.internal.service.ServiceDto toProto(ServiceDto dto) {
        return vn.xime.trust.grpc.internal.service.ServiceDto.newBuilder()
                .setId(dto.getId())
                .setName(dto.getName())
                .setTenant(dto.getTenant() == null ? "" : dto.getTenant())
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt())
                .build();
    }

    // ==================================================
    // ERROR MAPPER
    // ==================================================

    private RuntimeException toStatus(Exception e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}