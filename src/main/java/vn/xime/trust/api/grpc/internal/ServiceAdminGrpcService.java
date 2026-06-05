package vn.xime.trust.api.grpc.internal;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import vn.xime.trust.api.grpc.mapper.ServiceGrpcMapper;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.application.usecase.service.*;
import vn.xime.trust.grpc.internal.service.*;

import java.util.List;

public class ServiceAdminGrpcService extends ServiceAdminGrpc.ServiceAdminImplBase {

    private final CreateServiceUseCase createUseCase;
    private final GetServiceUseCase getUseCase;
    private final UpdateServiceStatusUseCase updateStatusUseCase;
    private final ServiceGrpcMapper serviceMapper;

    public ServiceAdminGrpcService(
            CreateServiceUseCase createUseCase,
            GetServiceUseCase getUseCase,
            UpdateServiceStatusUseCase updateStatusUseCase,
            ServiceGrpcMapper serviceMapper
    ) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.updateStatusUseCase = updateStatusUseCase;
        this.serviceMapper = serviceMapper;
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

            responseObserver.onNext(
                    CreateServiceResponse.newBuilder()
                            .setService(serviceMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY ID
    // ==================================================

    @Override
    public void getServiceById(
            GetServiceByIdRequest request,
            StreamObserver<GetServiceByIdResponse> responseObserver
    ) {
        try {

            ServiceDto result = getUseCase.getById(request.getId());

            responseObserver.onNext(
                    GetServiceByIdResponse.newBuilder()
                            .setService(serviceMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: ALL
    // ==================================================

    @Override
    public void getAllServices(
            GetAllServicesRequest request,
            StreamObserver<GetAllServicesResponse> responseObserver
    ) {
        try {

            List<ServiceDto> services = getUseCase.getAll();

            GetAllServicesResponse.Builder builder =
                    GetAllServicesResponse.newBuilder();

            services.forEach(s -> builder.addServices(serviceMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: PAGED
    // ==================================================

    @Override
    public void getAllServicesPaged(
            GetAllServicesPagedRequest request,
            StreamObserver<GetAllServicesPagedResponse> responseObserver
    ) {
        try {

            List<ServiceDto> services = getUseCase.getAll(
                    request.getPage(),
                    request.getSize()
            );

            GetAllServicesPagedResponse.Builder builder =
                    GetAllServicesPagedResponse.newBuilder();

            services.forEach(s -> builder.addServices(serviceMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY TENANT
    // ==================================================

    @Override
    public void getServicesByTenant(
            GetServicesByTenantRequest request,
            StreamObserver<GetServicesByTenantResponse> responseObserver
    ) {
        try {

            List<ServiceDto> services = getUseCase.getByTenant(
                    request.getTenant(),
                    request.getPage(),
                    request.getSize()
            );

            GetServicesByTenantResponse.Builder builder =
                    GetServicesByTenantResponse.newBuilder();

            services.forEach(s -> builder.addServices(serviceMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: TENANT NULL
    // ==================================================

    @Override
    public void getServicesByTenantIsNull(
            GetServicesByTenantIsNullRequest request,
            StreamObserver<GetServicesByTenantIsNullResponse> responseObserver
    ) {
        try {

            List<ServiceDto> services = getUseCase.getByTenantIsNull(
                    request.getPage(),
                    request.getSize()
            );

            GetServicesByTenantIsNullResponse.Builder builder =
                    GetServicesByTenantIsNullResponse.newBuilder();

            services.forEach(s -> builder.addServices(serviceMapper.toProto(s)));

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

            responseObserver.onNext(
                    UpdateServiceStatusResponse.newBuilder()
                            .setStatus(status)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
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