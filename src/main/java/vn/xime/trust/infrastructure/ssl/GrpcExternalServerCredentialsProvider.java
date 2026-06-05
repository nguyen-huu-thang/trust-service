package vn.xime.trust.infrastructure.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;

import lombok.RequiredArgsConstructor;

import vn.xime.trust.application.usecase.cert.GetRootCertificateUseCase;


/**
 * =========================================================
 * GRPC EXTERNAL SERVER CREDENTIALS PROVIDER
 * =========================================================
 *
 * Build TlsServerCredentials cho Spring gRPC external server
 * (port 9090). Yêu cầu client phải present cert hợp lệ (mTLS).
 *
 * Builds TlsServerCredentials for Spring gRPC external server
 * (port 9090). Requires client to present a valid cert (mTLS).
 *
 * Responsibilities:
 *
 * - đọc cert của Trust từ TrustSelfCertificateResolver
 * - lấy root CA cert qua GetRootCertificateUseCase
 * - build TlsServerCredentials với ClientAuth.REQUIRE
 *
 * Read Trust cert from TrustSelfCertificateResolver.
 * Get root CA cert via GetRootCertificateUseCase.
 * Build TlsServerCredentials with ClientAuth.REQUIRE.
 *
 * KHÔNG:
 *
 * - quản lý lifecycle cert
 * - rotation logic
 * - truy cập trực tiếp CA file hay repository
 *
 * =========================================================
 * TLS API
 * =========================================================
 *
 * Dùng io.grpc.TlsServerCredentials (gRPC core API).
 * Spring gRPC 1.0.x inject ServerCredentials bean vào
 * NettyServerBuilder constructor — không dùng .sslContext().
 *
 * Uses io.grpc.TlsServerCredentials (gRPC core API).
 * Spring gRPC 1.0.x injects ServerCredentials bean into
 * NettyServerBuilder constructor — not via .sslContext().
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcExternalServerCredentialsProvider {

    /**
     * =====================================================
     * SELF CERTIFICATE RESOLVER
     * =====================================================
     */
    private final TrustSelfCertificateResolver trustSelfCertificateResolver;

    /**
     * =====================================================
     * GET ROOT CERTIFICATE USE CASE
     * =====================================================
     */
    private final GetRootCertificateUseCase getRootCertificateUseCase;


    /**
     * =====================================================
     * BUILD SERVER CREDENTIALS
     * =====================================================
     *
     * Được gọi một lần khi Spring tạo ServerCredentials bean
     * (sau khi TrustSelfCertificateLoader đã load cert vào RAM).
     *
     * Called once when Spring creates the ServerCredentials bean
     * (after TrustSelfCertificateLoader has loaded cert to RAM).
     *
     * =====================================================
     */
    public ServerCredentials buildServerCredentials() throws IOException {

        // =================================================
        // LOAD SELF CERT
        // =================================================

        SelfCertificate selfCert =
                trustSelfCertificateResolver
                        .resolve()
                        .orElseThrow(() -> new IllegalStateException(
                                "Self certificate not found in resolver — " +
                                "TrustSelfCertificateLoader chưa load cert"
                        ));

        // =================================================
        // LOAD ROOT CA CERT
        // =================================================

        String rootCaPem = getRootCertificateUseCase.getRootCertificate();

        // =================================================
        // BUILD TLS CREDENTIALS
        // =================================================

        return TlsServerCredentials.newBuilder()
                .keyManager(
                        toStream(selfCert.publicCertificatePem()),
                        toStream(selfCert.privateKeyPem())
                )
                .trustManager(
                        toStream(rootCaPem)
                )
                .clientAuth(
                        TlsServerCredentials.ClientAuth.REQUIRE
                )
                .build();
    }


    /**
     * =====================================================
     * TO INPUT STREAM
     * =====================================================
     */
    private static InputStream toStream(String pem) {

        return new ByteArrayInputStream(
                pem.getBytes(StandardCharsets.UTF_8)
        );
    }
}
