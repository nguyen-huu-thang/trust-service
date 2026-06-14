package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;

import vn.xime.trust.grpc.internal.cert.*;

import vn.xime.trust.application.usecase.cert.*;
import vn.xime.trust.application.dto.request.*;
import vn.xime.trust.application.dto.response.*;

import vn.xime.trust.api.grpc.error.GrpcErrorMapper;
import vn.xime.trust.api.grpc.mapper.CertGrpcMapper;

import java.util.List;

public class CertAdminGrpcService extends CertAdminGrpc.CertAdminImplBase {

    private final BootstrapCertUseCase bootstrapUseCase;
    private final GetCertificatesUseCase getCertificatesUseCase;
    private final GetCertRefreshTokensUseCase getTokensUseCase;
    private final RevokeCertificateUseCase revokeUseCase;

    private final CertGrpcMapper mapper;

    public CertAdminGrpcService(
            BootstrapCertUseCase bootstrapUseCase,
            GetCertificatesUseCase getCertificatesUseCase,
            GetCertRefreshTokensUseCase getTokensUseCase,
            RevokeCertificateUseCase revokeUseCase,
            CertGrpcMapper mapper
    ) {
        this.bootstrapUseCase = bootstrapUseCase;
        this.getCertificatesUseCase = getCertificatesUseCase;
        this.getTokensUseCase = getTokensUseCase;
        this.revokeUseCase = revokeUseCase;
        this.mapper = mapper;
    }

    // ==================================================
    // BOOTSTRAP
    // ==================================================

    @Override
    public void bootstrapCert(
            BootstrapCertRequest request,
            StreamObserver<BootstrapCertResponse> responseObserver
    ) {
        try {

            BootstrapCommand cmd = new BootstrapCommand(
                    request.getServiceId(),
                    request.getShardId()
            );

            BootstrapDto dto = bootstrapUseCase.execute(cmd);

            responseObserver.onNext(mapper.toProto(dto));
                    
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // CERT: GET BY ID
    // ==================================================

    @Override
    public void getCertificateById(
            GetCertificateByIdRequest request,
            StreamObserver<CertificateResponse> responseObserver
    ) {
        try {

            AdminCertDto dto =
                    getCertificatesUseCase.getById(request.getId());

            responseObserver.onNext(mapper.toProto(dto));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // CERT: LIST BY SERVICE
    // ==================================================

    @Override
    public void listCertificatesByService(
            ListCertificatesByServiceRequest request,
            StreamObserver<ListCertificatesResponse> responseObserver
    ) {
        try {
            List<AdminCertDto> list = getCertificatesUseCase.listByService(request.getServiceId());

            ListCertificatesResponse.Builder builder =
                    ListCertificatesResponse.newBuilder();

            list.forEach(c -> builder.addCertificates(mapper.toProto(c)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // CERT: REVOKE
    // ==================================================

    @Override
    public void revokeCertificate(
            RevokeCertificateRequest request,
            StreamObserver<RevokeCertificateResponse> responseObserver
    ) {
        try {

            RevokeCertificateCommand cmd = new RevokeCertificateCommand(
                    request.getCertificateId(),
                    request.getReason()
            );

            String id = revokeUseCase.execute(cmd);

            responseObserver.onNext(
                    RevokeCertificateResponse.newBuilder()
                            .setCertificateId(id)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // TOKEN: GET BY ID
    // ==================================================

    @Override
    public void getTokenById(
            GetTokenByIdRequest request,
            StreamObserver<TokenResponse> responseObserver
    ) {
        try {

            CertRefreshTokenDto dto =
                    getTokensUseCase.getById(request.getId());

            responseObserver.onNext(mapper.toProto(dto));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // TOKEN: GET BY HASH
    // ==================================================

    @Override
    public void getTokenByHash(
            GetTokenByHashRequest request,
            StreamObserver<TokenResponse> responseObserver
    ) {
        try {

            CertRefreshTokenDto dto =
                    getTokensUseCase.getByTokenHash(request.getTokenHash());

            responseObserver.onNext(mapper.toProto(dto));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // TOKEN: LIST
    // ==================================================

    @Override
    public void listTokens(
            ListTokensRequest request,
            StreamObserver<ListTokensResponse> responseObserver
    ) {
        try {

            List<CertRefreshTokenDto> list =
                    getTokensUseCase.listActiveTokens();

            ListTokensResponse.Builder builder =
                    ListTokensResponse.newBuilder();

            list.forEach(t -> builder.addTokens(mapper.toProto(t)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }
}