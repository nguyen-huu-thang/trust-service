package vn.xime.trust.api.grpc.external;

import org.springframework.stereotype.Service;
import io.grpc.stub.StreamObserver;
import vn.xime.trust.proto.*;
import vn.xime.trust.application.dto.request.GetKeysRequestDto;
import vn.xime.trust.application.dto.response.GetKeysResponseDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.application.usecase.GetKeyUseCase;

import java.util.List;

@Service
public class TrustGrpcService extends TrustServiceGrpc.TrustServiceImplBase {

    private final GetKeyUseCase getKeyUseCase;

    public TrustGrpcService(GetKeyUseCase getKeyUseCase) {
        this.getKeyUseCase = getKeyUseCase;
    }

    // =====================================================
    // GetKeys (API chính)
    // =====================================================

    @Override
    public void getKeys(
            GetKeysRequest request,
            StreamObserver<GetKeysResponse> responseObserver
    ) {
        try {
            // =========================
            // 1. Map proto → DTO request
            // =========================

            GetKeysRequestDto dtoRequest = new GetKeysRequestDto(
                    request.getService(),
                    request.getIncludePrivate()
            );

            // =========================
            // 2. Call use case
            // =========================

            GetKeysResponseDto dtoResponse = getKeyUseCase.getKeys(dtoRequest);

            // =========================
            // 3. Map DTO → proto response
            // =========================

            GetKeysResponse.Builder responseBuilder = GetKeysResponse.newBuilder();

            // ---- Public keys ----
            if (dtoResponse.getPublicKeys() != null) {
                for (PublicKeyDto key : dtoResponse.getPublicKeys()) {
                    responseBuilder.addKeys(
                            toProtoPublic(key, request.getService())
                    );
                }
            }

            // ---- Private keys ----
            if (dtoResponse.getPrivateKeys() != null) {
                for (PrivateKeyDto key : dtoResponse.getPrivateKeys()) {
                    responseBuilder.addKeys(
                            toProtoPrivate(key, request.getService())
                    );
                }
            }

            responseBuilder.setTotal(responseBuilder.getKeysCount());

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // =====================================================
    // GetKeyById (tạm thời chưa implement)
    // =====================================================

    @Override
    public void getKeyById(
            GetKeyByIdRequest request,
            StreamObserver<GetKeyResponse> responseObserver
    ) {
        responseObserver.onError(
                new UnsupportedOperationException("Not implemented yet")
        );
    }

    // =====================================================
    // MAPPER: DTO → PROTO
    // =====================================================

    private GetKeyResponse toProtoPublic(PublicKeyDto key, String service) {
        return GetKeyResponse.newBuilder()
                .setKid(key.getKid())
                .setService(service)
                .setPublicKey(key.getPublicKey())
                .setActivateAt(toTimestamp(key.getActivateAt()))
                .setExpiresAt(toTimestamp(key.getExpiresAt()))
                .build();
    }

    private GetKeyResponse toProtoPrivate(PrivateKeyDto key, String service) {
        return GetKeyResponse.newBuilder()
                .setKid(key.getKid())
                .setService(service)
                .setPrivateKey(key.getPrivateKey())
                .setActivateAt(toTimestamp(key.getActivateAt()))
                .setExpiresAt(toTimestamp(key.getExpiresAt()))
                .build();
    }

    // =====================================================
    // Helper: Instant → protobuf Timestamp
    // =====================================================

    private com.google.protobuf.Timestamp toTimestamp(java.time.Instant instant) {
        if (instant == null) return null;

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}