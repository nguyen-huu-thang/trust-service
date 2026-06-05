package vn.xime.trust.config.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import vn.xime.trust.api.grpc.external.CertificateGrpcService;
import vn.xime.trust.api.grpc.external.KeyDistributionGrpcService;
import vn.xime.trust.api.grpc.external.TrustGrpcService;

import vn.xime.trust.infrastructure.ssl.GrpcExternalServerCredentialsProvider;


/**
 * =========================================================
 * EXTERNAL GRPC SERVER (mTLS)
 * =========================================================
 *
 * Public gRPC endpoint.
 *
 * Responsibilities:
 *
 * - mTLS
 * - certificate rotation API
 * - public key distribution
 * - root CA distribution
 *
 * =========================================================
 */
@Configuration
@ConditionalOnProperty(
    name = "trust.self.mtls.enabled",
    havingValue = "true"
)
public class ExternalGrpcServerConfig {

    /**
     * =====================================================
     * EXTERNAL SERVER
     * =====================================================
     */
    @Bean(
        initMethod = "start",
        destroyMethod = "shutdown"
    )
    @DependsOn("trustSelfCertificateLoader")
    public Server externalGrpcServer(

        GrpcExternalServerCredentialsProvider credentialsProvider,

        CertificateGrpcService certificateGrpcService,

        KeyDistributionGrpcService keyDistributionGrpcService,

        TrustGrpcService trustGrpcService

    ) throws Exception {

        System.out.println(
            ">>> STARTING EXTERNAL mTLS GRPC SERVER <<<"
        );

        return NettyServerBuilder

            .forPort(
                9090,
                credentialsProvider
                    .buildServerCredentials()
            )

            .addService(
                certificateGrpcService
            )

            .addService(
                keyDistributionGrpcService
            )

            .addService(
                trustGrpcService
            )

            .build();
    }
}