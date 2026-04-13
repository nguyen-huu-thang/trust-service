package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.key.grpc.internal.*;

@Component // để Spring inject vào config
public class InternalCommandGrpcService
        extends InternalCommandServiceGrpc.InternalCommandServiceImplBase {

    @Override
    public void rotateKey(RotateKeyRequest request,
                          StreamObserver<RotateKeyResponse> responseObserver) {

        // TODO: gọi usecase RotateKeyUseCase

        RotateKeyResponse response = RotateKeyResponse.newBuilder()
                .setNewKid("dummy-kid")   // TODO real value
                .setStatus("ROTATED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void generateKey(GenerateKeyRequest request,
                            StreamObserver<GenerateKeyResponse> responseObserver) {

        GenerateKeyResponse response = GenerateKeyResponse.newBuilder()
                .setKid("dummy-kid")
                .setStatus("CREATED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getKeys(GetKeysRequest request,
                        StreamObserver<GetKeysResponse> responseObserver) {

        GetKeysResponse response = GetKeysResponse.newBuilder()
                .build();       // TODO: add real keys

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void health(HealthRequest request,
                       StreamObserver<HealthResponse> responseObserver) {

        HealthResponse response = HealthResponse.newBuilder()
                .setStatus("OK")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}