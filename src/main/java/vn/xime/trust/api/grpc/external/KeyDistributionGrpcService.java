package vn.xime.trust.api.grpc.external;

import java.util.List;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import vn.xime.trust.api.grpc.error.GrpcErrorMapper;
import vn.xime.trust.grpc.external.key.*;

import vn.xime.trust.application.usecase.key.GetKeysUseCase;
import vn.xime.trust.application.dto.response.PublicKeyDto;
import vn.xime.trust.application.dto.response.PrivateKeyDto;



@Component
public class KeyDistributionGrpcService extends KeyDistributionServiceGrpc.KeyDistributionServiceImplBase {

    private final GetKeysUseCase getKeysUseCase;

    public KeyDistributionGrpcService(GetKeysUseCase getKeysUseCase) {
        this.getKeysUseCase = getKeysUseCase;
    }

    // ==================================================
    // VERIFY (PUBLIC KEYS)
    // ==================================================

    @Override
    public void getPublicKeys(
        GetPublicKeysRequest request,
        StreamObserver<GetPublicKeysResponse> responseObserver
    ) {
        try {
            // =========================
            // CALL USECASE
            // =========================
            List<PublicKeyDto> keys =
                getKeysUseCase.getActiveForVerifier(request.getVerifierServiceId());

            // =========================
            // MAP → PROTO
            // =========================
            GetPublicKeysResponse.Builder responseBuilder =
                GetPublicKeysResponse.newBuilder();

            for (PublicKeyDto key : keys) {
                responseBuilder.addKeys(
                    vn.xime.trust.grpc.external.key.PublicKeyDto.newBuilder()
                        .setId(key.getKid())
                        .setVerifierServiceId(key.getVerifierServiceId())
                        .setAlgorithm(key.getAlgorithm())
                        .setKeySize(key.getKeySize())
                        .setPublicKey(key.getPublicKey())
                        .setActivateAt(key.getActivateAt().toEpochMilli())
                        .setExpiresAt(key.getExpiresAt().toEpochMilli())
                        .build()
                );
            }

            // =========================
            // RETURN
            // =========================
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // SIGNING (PRIVATE KEYS)
    // ==================================================

    @Override
    public void getSigningKey(
        GetSigningKeyRequest request,
        StreamObserver<GetSigningKeyResponse> responseObserver
    ) {
        try {
            // =========================
            // CALL USECASE
            // =========================
            List<PrivateKeyDto> keys =
                getKeysUseCase.getActiveForSigning(request.getSignerServiceId());

            // =========================
            // MAP → PROTO
            // =========================
            GetSigningKeyResponse.Builder responseBuilder =
                    GetSigningKeyResponse.newBuilder();

            for (PrivateKeyDto key : keys) {
                responseBuilder.addKeys(
                    SigningKeyDto.newBuilder()
                        .setId(key.getKid())
                        .setSignerServiceId(key.getSignerServiceId())
                        .setVerifierServiceId(key.getVerifierServiceId())
                        .setAlgorithm(key.getAlgorithm())
                        .setKeySize(key.getKeySize())
                        .setPrivateKey(key.getPrivateKey())
                        .setActivateAt(key.getActivateAt().toEpochMilli())
                        .setExpiresAt(key.getExpiresAt().toEpochMilli())
                        .build()
                );
            }

            // =========================
            // RETURN
            // =========================
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }
}