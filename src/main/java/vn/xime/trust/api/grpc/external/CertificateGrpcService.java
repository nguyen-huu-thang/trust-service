package vn.xime.trust.api.grpc.external;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import vn.xime.trust.grpc.external.certificate.*;

import vn.xime.trust.application.usecase.cert.BootstrapCertUseCase;
import vn.xime.trust.application.usecase.cert.RotateCertificateUseCase;

import vn.xime.trust.application.dto.request.BootstrapCommand;
import vn.xime.trust.application.dto.response.BootstrapDto;
import vn.xime.trust.application.dto.response.RotateCertDto;

@Component
public class CertificateGrpcService extends CertificateServiceGrpc.CertificateServiceImplBase {

    private final BootstrapCertUseCase bootstrapUseCase;
    private final RotateCertificateUseCase rotateUseCase;

    public CertificateGrpcService(
        BootstrapCertUseCase bootstrapUseCase,
        RotateCertificateUseCase rotateUseCase
    ) {
        this.bootstrapUseCase = bootstrapUseCase;
        this.rotateUseCase = rotateUseCase;
    }

    // ==================================================
    // BOOTSTRAP
    // ==================================================

    @Override
    public void bootstrapCertificate(
        BootstrapCertificateRequest request,
        StreamObserver<BootstrapCertificateResponse> responseObserver
    ) {
        try {
            // =========================
            // MAP REQUEST → COMMAND
            // =========================
            BootstrapCommand cmd = new BootstrapCommand(
                request.getServiceId(),
                request.getShardId()
            );

            // =========================
            // CALL USECASE
            // =========================
            BootstrapDto dto = bootstrapUseCase.execute(cmd);

            // =========================
            // MAP → PROTO
            // =========================
            CertificateDto cert = CertificateDto.newBuilder()
                .setId(dto.getIdCert())
                .setPublicCert(dto.getPublicCert())
                .setPrivateKey(dto.getPrivateKey())
                .build();

            BootstrapCertificateResponse response =
                BootstrapCertificateResponse.newBuilder()
                    .setCertificate(cert)
                    .setRefreshToken(dto.getToken())
                    .setServiceId(dto.getServiceId())
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
                request.getServiceId(),
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