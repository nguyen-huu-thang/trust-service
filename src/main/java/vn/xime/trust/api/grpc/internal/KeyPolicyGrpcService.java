package vn.xime.trust.api.grpc.internal;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.trust.api.grpc.mapper.KeyPolicyMapper;
import vn.xime.trust.application.dto.request.CreateKeyPolicyCommand;
import vn.xime.trust.application.dto.request.UpdateKeyPolicyCommand;
import vn.xime.trust.application.dto.response.KeyPolicyDto;
import vn.xime.trust.application.usecase.policy.*;
import vn.xime.trust.grpc.internal.keypolicy.*;

import java.util.List;

@Component
public class KeyPolicyGrpcService extends KeyPolicyAdminGrpc.KeyPolicyAdminImplBase {

    private final CreatePolicyUseCase createUseCase;
    private final GetKeyPolicyUseCase getUseCase;
    private final UpdatePolicyUseCase updateUseCase;
    private final DeletePolicyUseCase deleteUseCase;
    private final KeyPolicyMapper keyPolicyMapper;

    public KeyPolicyGrpcService(
            CreatePolicyUseCase createUseCase,
            GetKeyPolicyUseCase getUseCase,
            UpdatePolicyUseCase updateUseCase,
            DeletePolicyUseCase deleteUseCase,
            KeyPolicyMapper keyPolicyMapper
    ) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.keyPolicyMapper = keyPolicyMapper;
    }

    // ==================================================
    // CREATE
    // ==================================================

    @Override
    public void createKeyPolicy(
            CreateKeyPolicyRequest request,
            StreamObserver<CreateKeyPolicyResponse> responseObserver
    ) {
        try {

            CreateKeyPolicyCommand cmd = new CreateKeyPolicyCommand(
                    request.getSignerServiceId(),
                    request.getVerifierServiceId(),
                    request.getKeyLifetimeSeconds(),
                    request.getJwtTtlSeconds(),
                    request.getPreloadSeconds()
            );

            KeyPolicyDto result = createUseCase.execute(cmd);

            responseObserver.onNext(
                    CreateKeyPolicyResponse.newBuilder()
                            .setPolicy(keyPolicyMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY ID
    // ==================================================

    @Override
    public void getKeyPolicyById(
            GetKeyPolicyByIdRequest request,
            StreamObserver<GetKeyPolicyByIdResponse> responseObserver
    ) {
        try {

            KeyPolicyDto result = getUseCase.getById(request.getId());

            responseObserver.onNext(
                    GetKeyPolicyByIdResponse.newBuilder()
                            .setPolicy(keyPolicyMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY PAIR
    // ==================================================

    @Override
    public void getKeyPolicyByPair(
            GetKeyPolicyByPairRequest request,
            StreamObserver<GetKeyPolicyByPairResponse> responseObserver
    ) {
        try {

            KeyPolicyDto result = getUseCase.getByPair(
                    request.getSignerServiceId(),
                    request.getVerifierServiceId()
            );

            responseObserver.onNext(
                    GetKeyPolicyByPairResponse.newBuilder()
                            .setPolicy(keyPolicyMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY SIGNER
    // ==================================================

    @Override
    public void getKeyPoliciesBySigner(
            GetKeyPoliciesBySignerRequest request,
            StreamObserver<GetKeyPoliciesBySignerResponse> responseObserver
    ) {
        try {

            List<KeyPolicyDto> policies =
                    getUseCase.getBySigner(request.getSignerServiceId());

            GetKeyPoliciesBySignerResponse.Builder builder =
                    GetKeyPoliciesBySignerResponse.newBuilder();

            policies.forEach(p -> builder.addPolicies(keyPolicyMapper.toProto(p)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // GET: BY VERIFIER
    // ==================================================

    @Override
    public void getKeyPoliciesByVerifier(
            GetKeyPoliciesByVerifierRequest request,
            StreamObserver<GetKeyPoliciesByVerifierResponse> responseObserver
    ) {
        try {

            List<KeyPolicyDto> policies =
                    getUseCase.getByVerifier(request.getVerifierServiceId());

            GetKeyPoliciesByVerifierResponse.Builder builder =
                    GetKeyPoliciesByVerifierResponse.newBuilder();

            policies.forEach(p -> builder.addPolicies(keyPolicyMapper.toProto(p)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // UPDATE
    // ==================================================

    @Override
    public void updateKeyPolicy(
            UpdateKeyPolicyRequest request,
            StreamObserver<UpdateKeyPolicyResponse> responseObserver
    ) {
        try {

            UpdateKeyPolicyCommand cmd = new UpdateKeyPolicyCommand(
                    request.getId(),
                    request.getKeyLifetimeSeconds(),
                    request.getJwtTtlSeconds(),
                    request.getPreloadSeconds()
            );

            KeyPolicyDto result = updateUseCase.execute(cmd);

            responseObserver.onNext(
                    UpdateKeyPolicyResponse.newBuilder()
                            .setPolicy(keyPolicyMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // DELETE
    // ==================================================

    @Override
    public void deleteKeyPolicy(
            DeleteKeyPolicyRequest request,
            StreamObserver<DeleteKeyPolicyResponse> responseObserver
    ) {
        try {

            deleteUseCase.deleteById(request.getId());

            responseObserver.onNext(
                    DeleteKeyPolicyResponse.newBuilder()
                            .setSuccess(true)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(toStatus(e));
        }
    }

    // ==================================================
    // ERROR MAPPER
    // ==================================================

    private RuntimeException toStatus(Exception e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}