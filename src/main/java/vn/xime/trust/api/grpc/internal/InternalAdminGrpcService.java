package vn.xime.trust.api.grpc.internal;

import org.springframework.stereotype.Component;
import io.grpc.stub.StreamObserver;
import vn.xime.trust.grpc.internal.*;

import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.dto.request.GetServicesQuery;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.application.usecase.service.CreateServiceUseCase;
import vn.xime.trust.application.usecase.service.GetServicesUseCase;

import java.util.List;

@Component // để Spring inject vào config
public class InternalAdminGrpcService
        extends InternalAdminServiceGrpc.InternalAdminServiceImplBase {

    private final CreateServiceUseCase createServiceUseCase;
    private final GetServicesUseCase getServicesUseCase;

    public InternalAdminGrpcService(
            CreateServiceUseCase createServiceUseCase,
            GetServicesUseCase getServicesUseCase
    ) {
        this.createServiceUseCase = createServiceUseCase;
        this.getServicesUseCase = getServicesUseCase;
    }

    // =====================================================
    // CreateService
    // =====================================================

    @Override
    public void createService(
            CreateServiceRequest request,
            StreamObserver<CreateServiceResponse> responseObserver
    ) {
        try {
            // =========================
            // 1. Map proto → DTO
            // =========================

            CreateServiceCommand cmd = new CreateServiceCommand(
                    request.getId(),
                    request.getName(),
                    request.getTenant()
            );

            // =========================
            // 2. Call usecase
            // =========================

            createServiceUseCase.execute(cmd);

            // =========================
            // 3. Build response
            // =========================

            CreateServiceResponse response =
                    CreateServiceResponse.newBuilder()
                            .setStatus("CREATED")
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // =====================================================
    // GetServices
    // =====================================================

    @Override
    public void getServices(
            GetServicesRequest request,
            StreamObserver<GetServicesResponse> responseObserver
    ) {
        try {
            // =========================
            // 1. Map proto → DTO
            // =========================

            GetServicesQuery query = new GetServicesQuery();

            // =========================
            // 2. Call usecase
            // =========================

            List<ServiceDto> services =
                    getServicesUseCase.execute(query);

            // =========================
            // 3. Map DTO → proto
            // =========================

            GetServicesResponse.Builder builder =
                    GetServicesResponse.newBuilder();

            if (services != null) {
                for (ServiceDto s : services) {
                    builder.addServices(toProto(s));
                }
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // =====================================================
    // NOT IMPLEMENTED (tạm thời)
    // =====================================================

    @Override
    public void generateKey(
            GenerateKeyRequest request,
            StreamObserver<GenerateKeyResponse> responseObserver
    ) {
        responseObserver.onError(
                new UnsupportedOperationException("generateKey not implemented")
        );
    }

    @Override
    public void rotateKey(
            RotateKeyRequest request,
            StreamObserver<RotateKeyResponse> responseObserver
    ) {
        responseObserver.onError(
                new UnsupportedOperationException("rotateKey not implemented")
        );
    }

    @Override
    public void createTrust(
            CreateTrustRequest request,
            StreamObserver<CreateTrustResponse> responseObserver
    ) {
        responseObserver.onError(
                new UnsupportedOperationException("createTrust not implemented")
        );
    }

    @Override
    public void getTrusts(
            GetTrustsRequest request,
            StreamObserver<GetTrustsResponse> responseObserver
    ) {
        responseObserver.onError(
                new UnsupportedOperationException("getTrusts not implemented")
        );
    }

    @Override
    public void health(
            HealthRequest request,
            StreamObserver<HealthResponse> responseObserver
    ) {
        responseObserver.onNext(
                HealthResponse.newBuilder()
                        .setStatus("OK")
                        .build()
        );
        responseObserver.onCompleted();
    }

    // =====================================================
    // MAPPER: DTO → PROTO
    // =====================================================

    private vn.xime.trust.grpc.internal.ServiceDto toProto(ServiceDto s) {
        return vn.xime.trust.grpc.internal.ServiceDto.newBuilder()
                .setId(s.getId())
                .setName(s.getName())
                .setTenant(s.getTenant() == null ? "" : s.getTenant())
                .setStatus(s.getStatus())
                .setCreatedAt(s.getCreatedAt().toEpochMilli())
                .build();
    }
}