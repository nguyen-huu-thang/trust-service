package vn.xime.trust.api.grpc.external;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import vn.xime.trust.grpc.external.certificate.*;

import vn.xime.trust.application.usecase.cert.RotateCertificateUseCase;

import vn.xime.trust.application.dto.response.RotateCertDto;

@Component
public class CertificateGrpcService extends CertificateServiceGrpc.CertificateServiceImplBase {

    private final RotateCertificateUseCase rotateUseCase;

    public CertificateGrpcService(
        RotateCertificateUseCase rotateUseCase
    ) {
        this.rotateUseCase = rotateUseCase;
    }

    // ==================================================
    // ROTATE
    // ==================================================

    @Override
    public void rotateCertificate(
        RotateCertificateRequest request,
        StreamObserver<RotateCertificateResponse> responseObserver
    ) {
        try {
            // =========================
            // CALL USECASE
            // =========================
            RotateCertDto dto = rotateUseCase.execute(
                request.getTokenId(),
                request.getRefreshToken(),
                request.getPrivateKey()
            );

            // =========================
            // MAP → PROTO
            // =========================
            CertificateDto cert = CertificateDto.newBuilder()
                .setId(dto.getIdCert())
                .setPublicCert(dto.getPublicCert())
                .setPrivateKey(dto.getPrivateKey())
                .build();

            RotateCertificateResponse response =
                RotateCertificateResponse.newBuilder()
                    .setCertificate(cert)
                    .setNextRefreshToken(dto.getRefreshToken())
                    .setRefreshTokenId(dto.getIdRefreshToken())
                    .setIssuedAt(dto.getIssuedAt().toEpochMilli())
                    .setExpiresAt(dto.getExpiresAt().toEpochMilli())
                    .build();

            // =========================
            // RETURN
            // =========================
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}