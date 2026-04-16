package vn.xime.trust.api.grpc.internal;

import org.springframework.stereotype.Component;
import io.grpc.stub.StreamObserver;
import vn.xime.trust.grpc.internal.*;
import vn.xime.trust.proto.Trust;
import vn.xime.trust.application.dto.request.CreateServiceCommand;
import vn.xime.trust.application.dto.request.CreateTrustCommand;
import vn.xime.trust.application.dto.request.GenerateKeyCommand;
import vn.xime.trust.application.dto.request.GetKeysQuery;
import vn.xime.trust.application.dto.request.GetServicesQuery;
import vn.xime.trust.application.dto.request.GetTrustsQuery;
import vn.xime.trust.application.dto.request.RegisterShardCommand;
import vn.xime.trust.application.dto.request.UpdateShardStatusCommand;
import vn.xime.trust.application.dto.response.ServiceDto;
import vn.xime.trust.application.usecase.service.CreateServiceUseCase;
import vn.xime.trust.application.usecase.service.GetServicesUseCase;
import vn.xime.trust.domain.model.ShardStatus;

import java.time.Instant;
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


    @Override
    public void registerShard(RegisterShardRequest request,
                            StreamObserver<RegisterShardResponse> responseObserver) {

    RegisterShardCommand cmd = new RegisterShardCommand(
        request.getShardId(),
        request.getServiceId(),
        request.getHost(),
        request.getPort(),
        request.getVersion()
    );

    String shardId = registerShardUseCase.execute(cmd);

    RegisterShardResponse response = RegisterShardResponse.newBuilder()
            .setShardId(shardId)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
    }

    @Override
    public void updateShardStatus(UpdateShardStatusRequest request,
                            StreamObserver<UpdateShardStatusResponse> responseObserver) {

    UpdateShardStatusCommand cmd = new UpdateShardStatusCommand(
            request.getShardId(),
            ShardStatus.valueOf(request.getStatus())
    );

    updateShardStatusUseCase.execute(cmd);

    UpdateShardStatusResponse response = UpdateShardStatusResponse.newBuilder()
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
    }

    @Override
    public void createTrust(CreateTrustRequest request,
                            StreamObserver<CreateTrustResponse> responseObserver) {

        CreateTrustCommand cmd = new CreateTrustCommand(
                request.getId(),
                request.getSignerServiceId(),
                request.getVerifierServiceId(),
                request.getKeyLifetimeSec(),
                request.getJwtTtlSec(),
                request.getPreloadSec()
        );

        String trustId = createTrustUseCase.execute(cmd);

        CreateTrustResponse response = CreateTrustResponse.newBuilder()
                .setTrustId(trustId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTrusts(GetTrustsRequest request,
                        StreamObserver<GetTrustsResponse> responseObserver) {

        GetTrustsQuery query = new GetTrustsQuery(
                request.getSignerServiceId()
        );

        var trusts = getTrustsUseCase.execute(query);

        GetTrustsResponse.Builder builder = GetTrustsResponse.newBuilder();

        for (TrustDto t : trusts) {
            builder.addTrusts(
                    Trust.newBuilder()
                            .setId(t.getId())
                            .setSignerServiceId(t.getSignerServiceId())
                            .setVerifierServiceId(t.getVerifierServiceId())
                            .setKeyLifetimeSec(t.getKeyLifetimeSec())
                            .setJwtTtlSec(t.getJwtTtlSec())
                            .setPreloadSec(t.getPreloadSec())
                            .build()
            );
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void generateKey(GenerateKeyRequest request,
                            StreamObserver<GenerateKeyResponse> responseObserver) {

        GenerateKeyCommand cmd = new GenerateKeyCommand(
                request.getSignerServiceId(),
                request.getVerifierServiceId(),
                request.getAlgorithm(),
                request.getKeySize(),
                request.hasActivateAt()
                        ? Instant.ofEpochSecond(request.getActivateAt())
                        : null
        );

        String kid = generateKeyUseCase.execute(cmd);

        GenerateKeyResponse response = GenerateKeyResponse.newBuilder()
                .setKid(kid)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getKeys(GetKeysRequest request,
                        StreamObserver<GetKeysResponse> responseObserver) {

        GetKeysQuery query = new GetKeysQuery(
                request.getSignerServiceId(),
                request.getVerifierServiceId()
        );

        var keys = getKeysUseCase.execute(query);

        GetKeysResponse.Builder builder = GetKeysResponse.newBuilder();

        for (KeyDto k : keys) {
            builder.addKeys(
                    Key.newBuilder()
                            .setKid(k.getKid())
                            .setPublicKey(k.getPublicKey())
                            .setAlgorithm(k.getAlgorithm())
                            .setKeySize(k.getKeySize())
                            .setActivateAt(k.getActivateAt().getEpochSecond())
                            .setExpiresAt(k.getExpiresAt().getEpochSecond())
                            .build()
            );
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    // =====================================================
    // NOT IMPLEMENTED (tạm thời)
    // =====================================================

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