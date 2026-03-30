package vn.xime.key.api.grpc;

import org.springframework.stereotype.Service;
import io.grpc.stub.StreamObserver;
import vn.xime.key.application.usecase.GetKeyUseCase;
import vn.xime.key.domain.key.Key;
import vn.xime.key.proto.*;

import java.util.List;

@Service
public class KeyGrpcService extends KeyServiceGrpc.KeyServiceImplBase {

    private final GetKeyUseCase getKeyUseCase;

    public KeyGrpcService(GetKeyUseCase getKeyUseCase) {
        this.getKeyUseCase = getKeyUseCase;
    }

    // =========================
    // Identity → lấy private key
    // =========================

    @Override
    public void getCurrentKey(
            GetKeyRequest request,
            StreamObserver<KeyResponse> responseObserver
    ) {
        try {
            Key key = getKeyUseCase.getCurrentKeyWithPrivate(
                    request.getServiceName()
            );

            KeyResponse response = KeyResponse.newBuilder()
                    .setKid(key.getKid())
                    .setPublicKey(key.getPublicKey())
                    .setPrivateKey(key.getPrivateKeyEncrypted()) // đã decrypt ở usecase
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // =========================
    // Service khác → lấy public key
    // =========================

    @Override
    public void getPublicKeys(
            GetPublicKeysRequest request,
            StreamObserver<PublicKeysResponse> responseObserver
    ) {
        try {
            List<Key> keys = getKeyUseCase.getPublicKeys(
                    request.getServiceName()
            );

            PublicKeysResponse.Builder builder = PublicKeysResponse.newBuilder();

            for (Key key : keys) {
                builder.addKeys(
                        PublicKey.newBuilder()
                                .setKid(key.getKid())
                                .setPublicKey(key.getPublicKey())
                                .build()
                );
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}