package vn.xime.trust.config.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.xime.trust.api.grpc.internal.InternalCommandGrpcService;

import java.net.InetSocketAddress;

@Configuration
public class InternalGrpcServerConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server internalGrpcServer(InternalCommandGrpcService internalService) {

        return NettyServerBuilder
                .forAddress(new InetSocketAddress("127.0.0.2", 9091)) // 🔥 local only
                .addService(internalService)
                .build();
    }
}