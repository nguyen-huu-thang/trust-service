package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.trust.grpc.internal.key.*;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.usecase.key.*;
import vn.xime.trust.application.dto.request.*;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.api.grpc.mapper.KeyGrpcMapper;

import java.time.Instant;
import java.util.List;

@Component
public class KeyAdminGrpcService extends KeyAdminGrpc.KeyAdminImplBase {

    private final GenerateKeyUseCase generateKeyUseCase;
    private final ScheduleKeyRotationUseCase rotationUseCase;
    private final GetKeysUseCase getKeysUseCase;
    private final DeleteKeyUseCase deleteKeyUseCase;
    private final KeyGrpcMapper mapper;

    public KeyAdminGrpcService(
            GenerateKeyUseCase generateKeyUseCase,
            ScheduleKeyRotationUseCase rotationUseCase,
            GetKeysUseCase getKeysUseCase,
            DeleteKeyUseCase deleteKeyUseCase,
            KeyGrpcMapper mapper
    ) {
        this.generateKeyUseCase = generateKeyUseCase;
        this.rotationUseCase = rotationUseCase;
        this.getKeysUseCase = getKeysUseCase;
        this.deleteKeyUseCase = deleteKeyUseCase;
        this.mapper = mapper;
    }

    // ==================================================
    // GENERATE
    // ==================================================

    @Override
    public void generateKey(
            GenerateKeyRequest request,
            StreamObserver<GenerateKeyResponse> responseObserver
    ) {
        try {
            GenerateKeyCommand cmd = new GenerateKeyCommand(
                request.getSignerServiceId(),
                request.getVerifierServiceId(),
                request.getAlgorithm(),
                request.getKeySize(),
                request.getActivateAt() > 0 ? Instant.ofEpochMilli(request.getActivateAt()) : null
            );

            String id = generateKeyUseCase.execute(cmd);

            KeyResponseDto dto = getKeysUseCase.getById(IdService.fromString(id));

            responseObserver.onNext(
                    GenerateKeyResponse.newBuilder()
                            .setKey(mapper.toProto(dto))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }

    // ==================================================
    // ROTATION
    // ==================================================

    @Override
    public void scheduleKeyRotation(
            ScheduleKeyRotationRequest request,
            StreamObserver<ScheduleKeyRotationResponse> responseObserver
    ) {
        try {
            ScheduleKeyRotationCommand cmd = new ScheduleKeyRotationCommand(
                request.getSignerServiceId(),
                request.getVerifierServiceId(),
                request.getActivateAt() > 0 ? Instant.ofEpochMilli(request.getActivateAt()) : null
            );

            String id = rotationUseCase.execute(cmd);

            KeyResponseDto dto = getKeysUseCase.getById(IdService.fromString(id));

            responseObserver.onNext(
                    ScheduleKeyRotationResponse.newBuilder()
                            .setKey(mapper.toProto(dto))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: BY ID
    // ==================================================

    @Override
    public void getKeyById(
            GetKeyByIdRequest request,
            StreamObserver<GetKeyByIdResponse> responseObserver
    ) {
        try {

            KeyResponseDto dto =
                    getKeysUseCase.getById(IdService.fromString(request.getId()));

            responseObserver.onNext(
                    GetKeyByIdResponse.newBuilder()
                            .setKey(mapper.toProto(dto))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: BY SIGNER
    // ==================================================

    @Override
    public void getKeysBySigner(
            GetKeysBySignerRequest request,
            StreamObserver<GetKeysBySignerResponse> responseObserver
    ) {
        try {

            List<KeyResponseDto> list =
                    getKeysUseCase.getBySigner(request.getSignerServiceId());

            GetKeysBySignerResponse.Builder builder =
                    GetKeysBySignerResponse.newBuilder();

            list.forEach(k -> builder.addKeys(mapper.toProto(k)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: BY SIGNER + VERIFIER
    // ==================================================

    @Override
    public void getKeysBySignerAndVerifier(
            GetKeysBySignerAndVerifierRequest request,
            StreamObserver<GetKeysBySignerAndVerifierResponse> responseObserver
    ) {
        try {

            List<KeyResponseDto> list =
                    getKeysUseCase.getBySignerAndVerifier(
                            request.getSignerServiceId(),
                            request.getVerifierServiceId()
                    );

            GetKeysBySignerAndVerifierResponse.Builder builder =
                    GetKeysBySignerAndVerifierResponse.newBuilder();

            list.forEach(k -> builder.addKeys(mapper.toProto(k)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }

    // ==================================================
    // DELETE
    // ==================================================

    @Override
    public void deleteKey(
            DeleteKeyRequest request,
            StreamObserver<DeleteKeyResponse> responseObserver
    ) {
        try {

            DeleteKeyCommand cmd = new DeleteKeyCommand(request.getId());

            deleteKeyUseCase.execute(cmd);

            responseObserver.onNext(
                    DeleteKeyResponse.newBuilder()
                            .setStatus("OK")
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(mapper.toStatus(e));
        }
    }
}