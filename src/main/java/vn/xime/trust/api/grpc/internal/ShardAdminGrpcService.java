package vn.xime.trust.api.grpc.internal;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import vn.xime.trust.application.dto.request.RegisterShardCommand;
import vn.xime.trust.application.dto.request.UpdateShardStatusCommand;
import vn.xime.trust.application.usecase.shard.GetShardsUseCase;
import vn.xime.trust.application.usecase.shard.RegisterShardUseCase;
import vn.xime.trust.application.usecase.shard.UpdateShardStatusUseCase;
import vn.xime.trust.application.dto.response.ShardDto;
import vn.xime.trust.grpc.internal.shard.*;

@Component
public class ShardAdminGrpcService extends ShardAdminGrpc.ShardAdminImplBase {

    private final RegisterShardUseCase registerUseCase;
    private final UpdateShardStatusUseCase updateStatusUseCase;
    private final GetShardsUseCase listUseCase;

    public ShardAdminGrpcService(
            RegisterShardUseCase registerUseCase,
            UpdateShardStatusUseCase updateStatusUseCase,
            GetShardsUseCase listUseCase
    ) {
        this.registerUseCase = registerUseCase;
        this.updateStatusUseCase = updateStatusUseCase;
        this.listUseCase = listUseCase;
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

            RegisterShardResponse response = RegisterShardResponse.newBuilder()
                    .setShard(toProto(result))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
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

            UpdateShardStatusResponse response =
                    UpdateShardStatusResponse.newBuilder()
                            .setStatus(status)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // ==================================================
    // LIST
    // ==================================================

    @Override
    public void listShards(
            ListShardsRequest request,
            StreamObserver<ListShardsResponse> responseObserver
    ) {
        try {

            GetShardsUseCase.Result result = listUseCase.execute(
                    request.getServiceId(),
                    request.getStatus(),
                    request.getLimit(),
                    request.getCursor()
            );

            ListShardsResponse.Builder builder = ListShardsResponse.newBuilder();

            result.shards().forEach(s -> builder.addShards(toProto(s)));

            if (result.nextCursor() != null) {
                builder.setNextCursor(result.nextCursor());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // ==================================================
    // MAPPER
    // ==================================================

    private vn.xime.trust.grpc.internal.shard.ShardDto toProto(ShardDto dto) {
        return vn.xime.trust.grpc.internal.shard.ShardDto.newBuilder()
                .setId(dto.getId())
                .setServiceId(dto.getServiceId())
                .setHost(dto.getHost())
                .setPort(dto.getPort())
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt())
                .build();
    }
}