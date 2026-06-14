package vn.xime.trust.api.grpc.error;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.junit.jupiter.api.Test;

import vn.xime.trust.common.exception.PrivateError;
import vn.xime.trust.common.exception.PublicError;
import vn.xime.trust.common.exception.SystemError;
import vn.xime.trust.domain.error.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the gRPC error mapper: exception -> redacted Status + xime-error metadata,
 * never leaking internal detail on the GRPC_INTERNAL channel.
 * Kiểm tra mapper lỗi gRPC: exception -> Status đã che + metadata xime-error,
 * không lộ chi tiết nội bộ trên kênh GRPC_INTERNAL.
 */
class GrpcErrorMapperTest {

    private static final Metadata.Key<String> ERROR_KEY =
        Metadata.Key.of("xime-error", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> ERROR_CODE_KEY =
        Metadata.Key.of("xime-error-code", Metadata.ASCII_STRING_MARSHALLER);

    private static String errorKeyOf(StatusRuntimeException ex) {
        return ex.getTrailers().get(ERROR_KEY);
    }

    private static String errorCodeOf(StatusRuntimeException ex) {
        return ex.getTrailers().get(ERROR_CODE_KEY);
    }

    @Test
    void privateErrorIsRedactedToUnknownAndHidesRealMessage() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(
            new PrivateError(ErrorCode.KEY_GENERATION_FAILED, "secret crypto internals"));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(errorKeyOf(ex)).isEqualTo("E000000");
        assertThat(errorCodeOf(ex)).isEqualTo("0");
        // không lộ message thật của lỗi private
        assertThat(ex.getStatus().getDescription()).isEqualTo(ErrorCode.UNKNOWN.getMessage());
        assertThat(ex.getStatus().getDescription()).doesNotContain("secret");
    }

    @Test
    void systemErrorPassesThroughOnGrpcWithRealKeyAndMessage() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(
            new SystemError(ErrorCode.INTER_SERVICE_AUTH_FAILED));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.UNAUTHENTICATED);
        assertThat(errorKeyOf(ex)).isEqualTo("E004003");
        assertThat(ex.getStatus().getDescription())
            .isEqualTo(ErrorCode.INTER_SERVICE_AUTH_FAILED.getMessage());
    }

    @Test
    void publicErrorPassesThroughUnchanged() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(new PublicError(ErrorCode.KEY_NOT_FOUND));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
        assertThat(errorKeyOf(ex)).isEqualTo("E017000");
        assertThat(errorCodeOf(ex)).isEqualTo("17000");
    }

    @Test
    void illegalArgumentMapsToValidationFailed() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(new IllegalArgumentException("bad input"));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(errorKeyOf(ex)).isEqualTo("E007001");
    }

    @Test
    void illegalStateMapsToRuleViolation() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(new IllegalStateException("invariant broken"));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.FAILED_PRECONDITION);
        assertThat(errorKeyOf(ex)).isEqualTo("E007007");
    }

    @Test
    void unknownExceptionIsRedactedAndHidesMessage() {
        StatusRuntimeException ex = GrpcErrorMapper.toStatus(
            new RuntimeException("stack trace / db url leak"));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(errorKeyOf(ex)).isEqualTo("E000000");
        assertThat(ex.getStatus().getDescription()).isEqualTo(ErrorCode.UNKNOWN.getMessage());
        assertThat(ex.getStatus().getDescription()).doesNotContain("leak");
    }
}
