package vn.xime.trust.config.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.trust.api.grpc.internal.ShardAdminGrpcService;
import vn.xime.trust.api.grpc.internal.KeyAdminGrpcService;
import vn.xime.trust.api.grpc.internal.ServiceAdminGrpcService;
import vn.xime.trust.api.grpc.internal.KeyPolicyGrpcService;
import vn.xime.trust.api.grpc.internal.CertAdminGrpcService;

import vn.xime.trust.api.grpc.mapper.ShardGrpcMapper;
import vn.xime.trust.api.grpc.mapper.ServiceGrpcMapper;
import vn.xime.trust.api.grpc.mapper.KeyGrpcMapper;
import vn.xime.trust.api.grpc.mapper.KeyPolicyGrpcMapper;
import vn.xime.trust.api.grpc.mapper.CertGrpcMapper;

import vn.xime.trust.application.usecase.shard.RegisterShardUseCase;
import vn.xime.trust.application.usecase.shard.UpdateShardStatusUseCase;
import vn.xime.trust.application.usecase.shard.GetShardsUseCase;

import vn.xime.trust.application.usecase.service.CreateServiceUseCase;
import vn.xime.trust.application.usecase.service.GetServiceUseCase;
import vn.xime.trust.application.usecase.service.UpdateServiceStatusUseCase;

import vn.xime.trust.application.usecase.key.InitKeyUseCase;
import vn.xime.trust.application.usecase.key.GetKeysUseCase;
import vn.xime.trust.application.usecase.key.DeleteKeyUseCase;

import vn.xime.trust.application.usecase.policy.CreatePolicyUseCase;
import vn.xime.trust.application.usecase.policy.GetKeyPolicyUseCase;
import vn.xime.trust.application.usecase.policy.UpdatePolicyUseCase;
import vn.xime.trust.application.usecase.policy.DeletePolicyUseCase;

import vn.xime.trust.application.usecase.cert.BootstrapCertUseCase;
import vn.xime.trust.application.usecase.cert.GetCertificatesUseCase;
import vn.xime.trust.application.usecase.cert.GetCertRefreshTokensUseCase;
import vn.xime.trust.application.usecase.cert.RevokeCertificateUseCase;

import java.net.InetSocketAddress;

@Configuration
public class InternalGrpcServerConfig {

    // Services are instantiated with `new` so they are NOT Spring beans.
    // If they were @Component beans, Spring gRPC would auto-register them
    // on the external server (port 9090) as well — a security hole.
    // Các service được khởi tạo bằng `new` để không là Spring bean.
    // Nếu là @Component, Spring gRPC sẽ tự đăng ký chúng lên port 9090 (public).
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server internalGrpcServer(
            // Shard
            RegisterShardUseCase registerShardUseCase,
            UpdateShardStatusUseCase updateShardStatusUseCase,
            GetShardsUseCase getShardsUseCase,
            ShardGrpcMapper shardMapper,
            // Service
            CreateServiceUseCase createServiceUseCase,
            GetServiceUseCase getServiceUseCase,
            UpdateServiceStatusUseCase updateServiceStatusUseCase,
            ServiceGrpcMapper serviceMapper,
            // Key
            InitKeyUseCase initKeyUseCase,
            GetKeysUseCase getKeysUseCase,
            DeleteKeyUseCase deleteKeyUseCase,
            KeyGrpcMapper keyMapper,
            // Policy
            CreatePolicyUseCase createPolicyUseCase,
            GetKeyPolicyUseCase getKeyPolicyUseCase,
            UpdatePolicyUseCase updatePolicyUseCase,
            DeletePolicyUseCase deletePolicyUseCase,
            KeyPolicyGrpcMapper keyPolicyMapper,
            // Cert
            BootstrapCertUseCase bootstrapCertUseCase,
            GetCertificatesUseCase getCertificatesUseCase,
            GetCertRefreshTokensUseCase getCertRefreshTokensUseCase,
            RevokeCertificateUseCase revokeCertificateUseCase,
            CertGrpcMapper certMapper
    ) {
        return NettyServerBuilder
            .forAddress(new InetSocketAddress("127.0.0.2", 9091)) // internal only
            .addService(new ShardAdminGrpcService(registerShardUseCase, updateShardStatusUseCase, getShardsUseCase, shardMapper))
            .addService(new ServiceAdminGrpcService(createServiceUseCase, getServiceUseCase, updateServiceStatusUseCase, serviceMapper))
            .addService(new KeyAdminGrpcService(initKeyUseCase, getKeysUseCase, deleteKeyUseCase, keyMapper))
            .addService(new KeyPolicyGrpcService(createPolicyUseCase, getKeyPolicyUseCase, updatePolicyUseCase, deletePolicyUseCase, keyPolicyMapper))
            .addService(new CertAdminGrpcService(bootstrapCertUseCase, getCertificatesUseCase, getCertRefreshTokensUseCase, revokeCertificateUseCase, certMapper))
            .build();
    }
}
