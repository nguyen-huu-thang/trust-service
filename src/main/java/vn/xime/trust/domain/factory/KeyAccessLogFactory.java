package vn.xime.trust.domain.factory;

import vn.xime.trust.domain.model.Id;
import vn.xime.trust.domain.model.KeyAccessAction;
import vn.xime.trust.domain.model.KeyAccessLog;

import java.time.Instant;

public class KeyAccessLogFactory {

    public KeyAccessLog create(
            Id keyId,
            String signerServiceId,
            String verifierServiceId,
            KeyAccessAction action,
            boolean includePrivate,
            String ipAddress,
            boolean success,
            String errorMessage
    ) {
        // =========================
        // VALIDATE (nhẹ thôi, đây là log)
        // =========================

        if (action == null) {
            throw new IllegalArgumentException("action is required");
        }

        // keyId có thể null (trường hợp lỗi trước khi resolve key)
        // signer/verifier có thể null (audit best-effort)

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();
        Instant now = Instant.now();

        return new KeyAccessLog(
                id,
                keyId,
                signerServiceId,
                verifierServiceId,
                action,
                includePrivate,
                now,
                ipAddress,
                success,
                errorMessage
        );
    }
}