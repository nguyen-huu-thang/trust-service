package vn.xime.trust.domain.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies channel-based redaction: which visibility may pass which channel,
 * and what a hidden error collapses to.
 * Kiểm tra che lỗi theo kênh: visibility nào được lọt kênh nào, và lỗi bị che
 * quy về mã gì.
 */
class ErrorRedactorTest {

    @Test
    void publicErrorPassesEveryChannel() {
        assertThat(ErrorRedactor.forChannel(ErrorCode.KEY_NOT_FOUND, Channel.REST_EXTERNAL))
            .isEqualTo(ErrorCode.KEY_NOT_FOUND);
        assertThat(ErrorRedactor.forChannel(ErrorCode.KEY_NOT_FOUND, Channel.GRPC_INTERNAL))
            .isEqualTo(ErrorCode.KEY_NOT_FOUND);
    }

    @Test
    void systemErrorPassesGrpcButIsRedactedOnRest() {
        // gRPC nội bộ: SYSTEM được lọt nguyên vẹn
        assertThat(ErrorRedactor.forChannel(ErrorCode.INTER_SERVICE_AUTH_FAILED, Channel.GRPC_INTERNAL))
            .isEqualTo(ErrorCode.INTER_SERVICE_AUTH_FAILED);
        // REST external: SYSTEM bị che về mã common cùng họ trạng thái (UNAUTHENTICATED)
        assertThat(ErrorRedactor.forChannel(ErrorCode.INTER_SERVICE_AUTH_FAILED, Channel.REST_EXTERNAL))
            .isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void privateErrorIsRedactedToUnknownOnEveryChannel() {
        assertThat(ErrorRedactor.forChannel(ErrorCode.KEY_GENERATION_FAILED, Channel.GRPC_INTERNAL))
            .isEqualTo(ErrorCode.UNKNOWN);
        assertThat(ErrorRedactor.forChannel(ErrorCode.KEY_GENERATION_FAILED, Channel.REST_EXTERNAL))
            .isEqualTo(ErrorCode.UNKNOWN);
    }

    @Test
    void genericForMapsByStatusFamily() {
        assertThat(ErrorCode.genericFor(ErrorCode.KEY_DUPLICATE_ACTIVATE_AT)).isEqualTo(ErrorCode.ALREADY_EXISTS);
        assertThat(ErrorCode.genericFor(ErrorCode.KEY_CHAIN_VIOLATION)).isEqualTo(ErrorCode.RULE_VIOLATION);
        assertThat(ErrorCode.genericFor(ErrorCode.SERVICE_NOT_FOUND)).isEqualTo(ErrorCode.NOT_FOUND);
    }
}
