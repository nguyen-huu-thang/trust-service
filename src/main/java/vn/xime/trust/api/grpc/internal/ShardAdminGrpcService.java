package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;
import vn.xime.trust.api.grpc.error.GrpcErrorMapper;
import vn.xime.trust.api.grpc.mapper.ShardGrpcMapper;
import vn.xime.trust.application.dto.request.RegisterShardCommand;
import vn.xime.trust.application.dto.request.UpdateShardStatusCommand;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.application.usecase.shard.GetShardsUseCase;
import vn.xime.trust.application.usecase.shard.RegisterShardUseCase;
import vn.xime.trust.application.usecase.shard.UpdateShardStatusUseCase;
import vn.xime.trust.grpc.internal.shard.*;

import java.util.List;

public class ShardAdminGrpcService extends ShardAdminGrpc.ShardAdminImplBase {

    private final RegisterShardUseCase registerUseCase;
    private final UpdateShardStatusUseCase updateStatusUseCase;
    private final GetShardsUseCase getUseCase;
    private final ShardGrpcMapper shardMapper;

    public ShardAdminGrpcService(
            RegisterShardUseCase registerUseCase,
            UpdateShardStatusUseCase updateStatusUseCase,
            GetShardsUseCase getUseCase,
            ShardGrpcMapper shardMapper
    ) {
        this.registerUseCase = registerUseCase;
        this.updateStatusUseCase = updateStatusUseCase;
        this.getUseCase = getUseCase;
        this.shardMapper = shardMapper;
    }

    // ==================================================
    // REGISTER
    // ==================================================

    @Override
    public void registerShard(
            RegisterShardRequest request,
            StreamObserver<RegisterShardResponse> responseObserver
    ) {
        try {

            RegisterShardCommand cmd = new RegisterShardCommand(
                    request.getId(),
                    request.getServiceId(),
                    request.getHost(),
                    request.getPort()
            );

            ShardDto result = registerUseCase.execute(cmd);

            responseObserver.onNext(
                    RegisterShardResponse.newBuilder()
                            .setShard(shardMapper.toProto(result))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // UPDATE STATUS
    // ==================================================

    @Override
    public void updateShardStatus(
            UpdateShardStatusRequest request,
            StreamObserver<UpdateShardStatusResponse> responseObserver
    ) {
        try {

            UpdateShardStatusCommand cmd = new UpdateShardStatusCommand(
                    request.getShardId(),
                    request.getStatus()
            );

            String status = updateStatusUseCase.execute(cmd);

            responseObserver.onNext(
                    UpdateShardStatusResponse.newBuilder()
                            .setStatus(status)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: BY ID
    // ==================================================

    @Override
    public void getShardById(
            GetShardByIdRequest request,
            StreamObserver<GetShardByIdResponse> responseObserver
    ) {
        try {

            ShardDto shard = getUseCase.getById(request.getId());

            responseObserver.onNext(
                    GetShardByIdResponse.newBuilder()
                            .setShard(shardMapper.toProto(shard))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: BY SERVICE
    // ==================================================

    @Override
    public void getShardsByService(
            GetShardsByServiceRequest request,
            StreamObserver<GetShardsByServiceResponse> responseObserver
    ) {
        try {

            List<ShardDto> shards = getUseCase.getByServiceId(request.getServiceId());

            GetShardsByServiceResponse.Builder builder =
                    GetShardsByServiceResponse.newBuilder();

            shards.forEach(s -> builder.addShards(shardMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: ALL
    // ==================================================

    @Override
    public void getAllShards(
            GetAllShardsRequest request,
            StreamObserver<GetAllShardsResponse> responseObserver
    ) {
        try {

            List<ShardDto> shards = getUseCase.getAll();

            GetAllShardsResponse.Builder builder =
                    GetAllShardsResponse.newBuilder();

            shards.forEach(s -> builder.addShards(shardMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }

    // ==================================================
    // GET: PAGED
    // ==================================================

    @Override
    public void getAllShardsPaged(
            GetAllShardsPagedRequest request,
            StreamObserver<GetAllShardsPagedResponse> responseObserver
    ) {
        try {

            List<ShardDto> shards = getUseCase.getAll(
                    request.getPage(),
                    request.getSize()
            );

            GetAllShardsPagedResponse.Builder builder =
                    GetAllShardsPagedResponse.newBuilder();

            shards.forEach(s -> builder.addShards(shardMapper.toProto(s)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(GrpcErrorMapper.toStatus(e));
        }
    }
}