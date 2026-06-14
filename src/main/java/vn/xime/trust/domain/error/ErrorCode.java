package vn.xime.trust.domain.error;

/**
 * Centralized error catalog for trust-service.
 * Catalog mã lỗi tập trung của trust-service.
 *
 * Theo chuẩn platform (xem giới thiệu/.claude/docs/cross-cutting/quy-uoc-ma-loi-va-exception.md):
 * - common 000000-009999 (dùng chung + làm mã generic khi che lỗi)
 * - trust-service block 010000-019999
 *   (Private 010000-013999 / System 014000-016999 / Public 017000-019999)
 *
 * Domain pure: dùng int httpStatus + GrpcCode (không phụ thuộc Spring/io.grpc).
 */
public enum ErrorCode {

    // ===== common - Private (lỗi hạ tầng, không ra ngoài) =====
    UNKNOWN("E000000", 0, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi không xác định"),
    INTERNAL_ERROR("E000001", 1, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi nội bộ hệ thống"),
    DATABASE_ERROR("E000002", 2, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi cơ sở dữ liệu"),
    CONFIG_ERROR("E000003", 3, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi cấu hình"),

    // ===== common - System (lỗi liên service, chỉ service nội bộ đọc) =====
    UPSTREAM_ERROR("E004000", 4000, 502, GrpcCode.INTERNAL, Visibility.SYSTEM, "Lỗi gọi service nội bộ"),
    DEPENDENCY_UNAVAILABLE("E004001", 4001, 503, GrpcCode.UNAVAILABLE, Visibility.SYSTEM, "Service phụ thuộc không khả dụng"),
    UPSTREAM_TIMEOUT("E004002", 4002, 504, GrpcCode.DEADLINE_EXCEEDED, Visibility.SYSTEM, "Hết thời gian chờ service nội bộ"),
    INTER_SERVICE_AUTH_FAILED("E004003", 4003, 401, GrpcCode.UNAUTHENTICATED, Visibility.SYSTEM, "Xác thực liên service thất bại"),

    // ===== common - Public (an toàn cho client, dùng làm generic khi che) =====
    BAD_REQUEST("E007000", 7000, 400, GrpcCode.INVALID_ARGUMENT, Visibility.PUBLIC, "Yêu cầu không hợp lệ"),
    VALIDATION_FAILED("E007001", 7001, 400, GrpcCode.INVALID_ARGUMENT, Visibility.PUBLIC, "Dữ liệu đầu vào không hợp lệ"),
    UNAUTHENTICATED("E007002", 7002, 401, GrpcCode.UNAUTHENTICATED, Visibility.PUBLIC, "Chưa xác thực"),
    SESSION_EXPIRED("E007003", 7003, 401, GrpcCode.UNAUTHENTICATED, Visibility.PUBLIC, "Phiên làm việc đã hết hạn"),
    FORBIDDEN("E007004", 7004, 403, GrpcCode.PERMISSION_DENIED, Visibility.PUBLIC, "Không có quyền truy cập"),
    NOT_FOUND("E007005", 7005, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy tài nguyên"),
    ALREADY_EXISTS("E007006", 7006, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Tài nguyên đã tồn tại"),
    RULE_VIOLATION("E007007", 7007, 422, GrpcCode.FAILED_PRECONDITION, Visibility.PUBLIC, "Vi phạm ràng buộc nghiệp vụ"),
    TOO_MANY_REQUESTS("E007008", 7008, 429, GrpcCode.RESOURCE_EXHAUSTED, Visibility.PUBLIC, "Quá nhiều yêu cầu"),

    // ===== trust-service - Private (010000-013999): crypto/hạ tầng, không ra ngoài =====
    KEY_GENERATION_FAILED("E010000", 10000, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi sinh khóa"),
    KEY_ENCRYPTION_FAILED("E010001", 10001, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi mã hóa khóa riêng"),
    SIGNING_FAILED("E010002", 10002, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi ký số"),
    CERT_ISSUANCE_FAILED("E010010", 10010, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi cấp chứng chỉ"),

    // ===== trust-service - Public (017000-019999): an toàn cho client =====
    KEY_NOT_FOUND("E017000", 17000, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy khóa"),
    KEY_DUPLICATE_ACTIVATE_AT("E017001", 17001, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Đã tồn tại khóa cùng thời điểm kích hoạt"),
    KEY_CHAIN_VIOLATION("E017002", 17002, 422, GrpcCode.FAILED_PRECONDITION, Visibility.PUBLIC, "Vi phạm ràng buộc chuỗi khóa"),
    CERT_NOT_FOUND("E017010", 17010, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy chứng chỉ"),
    CERT_INVALID_STATE("E017011", 17011, 422, GrpcCode.FAILED_PRECONDITION, Visibility.PUBLIC, "Trạng thái chứng chỉ không hợp lệ cho thao tác này"),
    POLICY_NOT_FOUND("E017020", 17020, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy policy khóa"),
    POLICY_ALREADY_EXISTS("E017021", 17021, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Policy khóa đã tồn tại"),
    SERVICE_NOT_FOUND("E017030", 17030, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy service"),
    SERVICE_ALREADY_EXISTS("E017031", 17031, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Service đã tồn tại"),
    SHARD_NOT_FOUND("E017040", 17040, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy shard"),
    SHARD_ALREADY_EXISTS("E017041", 17041, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Shard đã tồn tại");

    private final String errorKey;
    private final int code;
    private final int httpStatus;
    private final GrpcCode grpcCode;
    private final Visibility visibility;
    private final String message;

    ErrorCode(String errorKey, int code, int httpStatus,
              GrpcCode grpcCode, Visibility visibility, String message) {
        this.errorKey = errorKey;
        this.code = code;
        this.httpStatus = httpStatus;
        this.grpcCode = grpcCode;
        this.visibility = visibility;
        this.message = message;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public GrpcCode getGrpcCode() {
        return grpcCode;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Generic common code in the same status family, used when redacting a hidden error.
     * Mã common cùng họ trạng thái, dùng khi che một lỗi không được phép phơi bày.
     */
    public static ErrorCode genericFor(ErrorCode ec) {
        return switch (ec.grpcCode) {
            case INVALID_ARGUMENT -> BAD_REQUEST;
            case NOT_FOUND -> NOT_FOUND;
            case ALREADY_EXISTS -> ALREADY_EXISTS;
            case PERMISSION_DENIED -> FORBIDDEN;
            case UNAUTHENTICATED -> UNAUTHENTICATED;
            case FAILED_PRECONDITION -> RULE_VIOLATION;
            case RESOURCE_EXHAUSTED -> TOO_MANY_REQUESTS;
            default -> UNKNOWN;
        };
    }
}
