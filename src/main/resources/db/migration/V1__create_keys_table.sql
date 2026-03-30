CREATE TABLE keys (
    id BIGSERIAL PRIMARY KEY,

    kid VARCHAR(255) UNIQUE NOT NULL,
    service_name VARCHAR(255) NOT NULL,

    public_key TEXT NOT NULL,
    private_key_encrypted TEXT NOT NULL,

    algorithm VARCHAR(50) NOT NULL,
    key_size INT NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    activate_at TIMESTAMP,
    expires_at TIMESTAMP,

    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_keys_service_status
ON keys(service_name, status);