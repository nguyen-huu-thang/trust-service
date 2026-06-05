package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;
import vn.xime.trust.grpc.internal.key.*;
import vn.xime.trust.domain.service.IdService;
import vn.xime.trust.application.usecase.key.*;
import vn.xime.trust.application.dto.request.*;
import vn.xime.trust.application.dto.response.AdminKeyDto;
import vn.xime.trust.api.grpc.mapper.KeyGrpcMapper;

import java.util.List;

public class KeyAdminGrpcService extends KeyAdminGrpc.KeyAdminImplBase {

    private final InitKeyUseCase initKeyUseCase;
    private final GetKeysUseCase getKeysUseCase;
    private final DeleteKeyUseCase deleteKeyUseCase;
    private final KeyGrpcMapper mapper;

    public KeyAdminGrpcService(
            InitKeyUseCase initKeyUseCase,
            GetKeysUseCase getKeysUseCase,
            DeleteKeyUseCase deleteKeyUseCase,
            KeyGrpcMapper mapper
    ) {
        this.initKeyUseCase = initKeyUseCase;
        this.getKeysUseCase = getKeysUseCase;
        this.deleteKeyUseCase = deleteKeyUseCase;
        this.mapper = mapper;
    }

    // ==================================================
    // GENERATE
    // ==================================================

    @Override
    public void initKey(
            InitKeyRequest request,
            StreamObserver<InitKeyResponse> responseObserver
    ) {
        try {
            InitKeyCommand cmd = new InitKeyCommand(
                request.getSignerServiceId(),
                request.getVerifierServiceId()
            );

            AdminKeyDto dto = initKeyUseCase.initKey(cmd);

            responseObserver.onNext(
                    InitKeyResponse.newBuilder()
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

            AdminKeyDto dto =
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

            List<AdminKeyDto> list =
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

            List<AdminKeyDto> list =
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