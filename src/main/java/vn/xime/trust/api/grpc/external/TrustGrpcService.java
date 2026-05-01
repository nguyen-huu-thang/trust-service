package vn.xime.trust.api.grpc.external;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import vn.xime.trust.application.usecase.cert.GetRootCertificateUseCase;

import vn.xime.trust.grpc.external.trust.TrustServiceGrpc;
import vn.xime.trust.grpc.external.trust.GetRootCertificateRequest;
import vn.xime.trust.grpc.external.trust.GetRootCertificateResponse;

@Component
public class TrustGrpcService extends TrustServiceGrpc.TrustServiceImplBase {

    private final GetRootCertificateUseCase useCase;

    public TrustGrpcService(GetRootCertificateUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void getRootCertificate(
            GetRootCertificateRequest request,
            StreamObserver<GetRootCertificateResponse> responseObserver
    ) {
        try {
            // =========================
            // CALL USECASE
            // =========================
            String rootCert = useCase.getRootCertificate();

            // =========================
            // BUILD RESPONSE
            // =========================
            GetRootCertificateResponse response =
                    GetRootCertificateResponse.newBuilder()
                            .setRootCert(rootCert)
                            .build();

            // =========================
            // RETURN
            // =========================
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            // =========================
            // ERROR HANDLING
            // =========================
            responseObserver.onError(e);
        }
    }
}