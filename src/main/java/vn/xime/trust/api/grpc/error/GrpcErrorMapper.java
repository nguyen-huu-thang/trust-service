package vn.xime.trust.api.grpc.error;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.xime.trust.common.exception.AppException;
import vn.xime.trust.domain.error.Channel;
import vn.xime.trust.domain.error.ErrorCode;
import vn.xime.trust.domain.error.ErrorRedactor;
import vn.xime.trust.domain.error.Visibility;

/**
 * Shared gRPC exception mapper - one place for all gRPC APIs.
 * Mapper exception gRPC dùng chung - một nơi cho mọi gRPC API.
 *
 * Map exception -> ErrorCode, che theo kênh GRPC_INTERNAL, gắn metadata
 * xime-error / xime-error-code, trả Status an toàn (không lộ stack trace).
 *
 * Static vì gRPC service của Trust được wire thủ công (không phải Spring bean).
 */
public final class GrpcErrorMapper {

    private static final Logger log = LoggerFactory.getLogger(GrpcErrorMapper.class);

    private static final Metadata.Key<String> ERROR_KEY =
        Metadata.Key.of("xime-error", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> ERROR_CODE_KEY =
        Metadata.Key.of("xime-error-code", Metadata.ASCII_STRING_MARSHALLER);

    private GrpcErrorMapper() {
    }

    public static StatusRuntimeException toStatus(Throwable ex) {
        ErrorCode original = resolve(ex);

        if (original.getVisibility() == Visibility.PRIVATE) {
            // Lỗi private bị che trên kênh gRPC nội bộ: log đầy đủ, ngoài chỉ thấy mã generic.
            // SYSTEM/PUBLIC lọt nguyên vẹn nên không cần log như lỗi.
            log.error("Private error in gRPC: {}", original.getErrorKey(), ex);
        }

        ErrorCode ec = ErrorRedactor.forChannel(original, Channel.GRPC_INTERNAL);
        String description = original.getVisibility() == Visibility.PUBLIC
            ? coalesce(ex.getMessage(), original.getMessage())
            : ec.getMessage();

        Metadata trailers = new Metadata();
        trailers.put(ERROR_KEY, ec.getErrorKey());
        trailers.put(ERROR_CODE_KEY, String.valueOf(ec.getCode()));

        return Status.fromCode(Status.Code.valueOf(ec.getGrpcCode().name()))
            .withDescription(description)
            .asRuntimeException(trailers);
    }

    private static ErrorCode resolve(Throwable ex) {
        if (ex instanceof AppException appException) {
            return appException.getErrorCode();
        }
        if (ex instanceof IllegalArgumentException) {
            return ErrorCode.VALIDATION_FAILED;
        }
        if (ex instanceof IllegalStateException) {
            return ErrorCode.RULE_VIOLATION;
        }
        return ErrorCode.UNKNOWN;
    }

    private static String coalesce(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
