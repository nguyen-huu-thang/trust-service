package vn.xime.key.application.dto.request;

public class GetKeysRequestDto {

    /**
     * Tên service (identity-service, payment-service...)
     */
    private final String service;

    /**
     * Có cần private key không
     * - true: Identity Service
     * - false: các service verify
     */
    private final boolean includePrivate;

    public GetKeysRequestDto(String service, boolean includePrivate) {
        this.service = service;
        this.includePrivate = includePrivate;
    }

    public String getService() {
        return service;
    }

    public boolean isIncludePrivate() {
        return includePrivate;
    }
}