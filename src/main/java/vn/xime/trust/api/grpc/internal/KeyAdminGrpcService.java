package vn.xime.trust.api.grpc.internal;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.*;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.dto.response.KeyResponseDto;
import vn.xime.trust.application.usecase.key.*;
import vn.xime.trust.grpc.internal.key.*;

import java.time.Instant;
import java.util.List;

@Component
public class KeyAdminGrpcService extends KeyAdminGrpc.KeyAdminImplBase {

    private final GenerateKeyUseCase generateKeyUseCase;
    private final ScheduleKeyRotationUseCase rotationUseCase;
    private final GetKeysUseCase getKeysUseCase;
    private final DeleteKeyUseCase deleteKeyUseCase;

    public KeyAdminGrpcService(
            GenerateKeyUseCase generateKeyUseCase,
            ScheduleKeyRotationUseCase rotationUseCase,
            GetKeysUseCase getKeysUseCase,
            DeleteKeyUseCase deleteKeyUseCase
    ) {
        this.generateKeyUseCase = generateKeyUseCase;
        this.rotationUseCase = rotationUseCase;
        this.getKeysUseCase = getKeysUseCase;
        this.deleteKeyUseCase = deleteKeyUseCase;
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
            GenerateKeyCommand cmd = new GenerateKeyCommand();
            cmd.setSignerServiceId(request.getSignerServiceId());
            cmd.setVerifierServiceId(request.getVerifierServiceId());
            cmd.setAlgorithm(request.getAlgorithm());
            cmd.setKeySize(request.getKeySize());

            if (request.getActivateAt() > 0) {
                cmd.setActivateAt(Instant.ofEpochMilli(request.getActivateAt()));
            }

            String id = generateKeyUseCase.execute(cmd);

            KeyResponseDto dto = getById(id, true);

            GenerateKeyResponse response = GenerateKeyResponse.newBuilder()
                    .setKey(toProto(dto))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
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
            ScheduleKeyRotationCommand cmd = new ScheduleKeyRotationCommand();
            cmd.setSignerServiceId(request.getSignerServiceId());
            cmd.setVerifierServiceId(request.getVerifierServiceId());
            cmd.setActivateAt(Instant.ofEpochMilli(request.getActivateAt()));

            String id = rotationUseCase.execute(cmd);

            KeyResponseDto dto = getById(id, true);

            ScheduleKeyRotationResponse response =
                    ScheduleKeyRotationResponse.newBuilder()
                            .setKey(toProto(dto))
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // ==================================================
    // LIST KEYS
    // ==================================================

    @Override
    public void listKeys(
            ListKeysRequest request,
            StreamObserver<ListKeysResponse> responseObserver
    ) {
        try {
            GetKeysRequestDto query = new GetKeysRequestDto();
            query.setSignerServiceId(request.getSignerServiceId());
            query.setVerifierServiceId(request.getVerifierServiceId());
            query.setIncludeDeleted(request.getIncludeDeleted());
            query.setIncludePrivate(request.getIncludePrivate());
            query.setLimit(request.getLimit());
            query.setCursor(request.getCursor());

            KeyResponseDto result = getKeysUseCase.execute(query);

            List<KeyDto> keys = result.getKeys().stream()
                    .map(this::toProto)
                    .toList();

            ListKeysResponse response = ListKeysResponse.newBuilder()
                    .addAllKeys(keys)
                    .setNextCursor(result.getNextCursor() == null ? "" : result.getNextCursor())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // ==================================================
    // GET KEY
    // ==================================================

    @Override
    public void getKey(
            GetKeyRequest request,
            StreamObserver<GetKeyResponse> responseObserver
    ) {
        try {
            GetKeysRequestDto query = new GetKeysRequestDto();
            query.setId(request.getId().toByteArray());
            query.setIncludePrivate(request.getIncludePrivate());

            KeyResponseDto result = getKeysUseCase.execute(query);

            if (result.getKeys().isEmpty()) {
                throw new IllegalStateException("Key not found");
            }

            KeyDto proto = toProto(result.getKeys().get(0));

            GetKeyResponse response = GetKeyResponse.newBuilder()
                    .setKey(proto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
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
            DeleteKeyCommand cmd = new DeleteKeyCommand();
            cmd.setId(request.getId().toByteArray());

            deleteKeyUseCase.execute(cmd);

            DeleteKeyResponse response = DeleteKeyResponse.newBuilder()
                    .setStatus("OK")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // ==================================================
    // INTERNAL HELPER
    // ==================================================

    private KeyResponseDto getById(String base62Id, boolean includePrivate) {
        GetKeysRequestDto q = new GetKeysRequestDto();
        q.setId(vn.xime.trust.domain.service.IdService.fromBase62(base62Id));
        q.setIncludePrivate(includePrivate);

        return getKeysUseCase.execute(q).getKeys().get(0);
    }

    private KeyDto toProto(KeyResponseDto dto) {

        KeyDto.Builder b = KeyDto.newBuilder()
                .setId(ByteString.copyFrom(
                        vn.xime.trust.domain.service.IdService.fromBase62(dto.getId())
                ))
                .setSignerServiceId(dto.getSignerServiceId())
                .setVerifierServiceId(dto.getVerifierServiceId())
                .setAlgorithm(dto.getAlgorithm())
                .setKeySize(dto.getKeySize())
                .setPublicKey(dto.getPublicKey())
                .setActivateAt(dto.getActivateAt().toEpochMilli())
                .setExpiresAt(dto.getExpiresAt().toEpochMilli());

        if (dto.getPrivateKeyEncrypted() != null) {
            b.setPrivateKeyEncrypted(dto.getPrivateKeyEncrypted());
        }

        return b.build();
    }
}