package vn.xime.trust.domain.key.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import vn.xime.trust.domain.key.Key;
import vn.xime.trust.domain.key.KeyRepository;

/**

* Domain Service: KeyLifecycleManager
*
* =========================
* Vai trò:
* =========================
* * Chứa toàn bộ business logic về lifecycle của key
* * Quyết định:
* * key nào dùng để SIGN
* * key nào dùng để VERIFY
* * key nào là NEXT (preload)
*
* =========================
* Triết lý thiết kế:
* =========================
* ❌ Không dùng CURRENT / NEXT / OLD
* ✅ Dùng time-based:
*
* activateAt → bắt đầu SIGN
* expiresAt  → NGỪNG VERIFY
*
* =========================
* Nguyên tắc:
* =========================
* SIGN:
* → chọn key có activateAt <= now gần nhất
*
* VERIFY:
* → chấp nhận tất cả key chưa expiresAt
*
* NEXT:
* → key có activateAt > now gần nhất
*

*/
public class KeyLifecycleManager {

    private final KeyRepository keyRepository;

    public KeyLifecycleManager(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    // =====================================================
    // LOAD DATA
    // =====================================================

    /**
     * Load toàn bộ key của service
     *
     * ⚠️ Chỉ gọi DB 1 lần để tránh:
     * - multiple queries
     * - inconsistent snapshot
     *
     * Domain sẽ filter lifecycle sau
     */
    public List<Key> loadKeys(String serviceName) {
        return keyRepository.findAllByService(serviceName)
                .stream()
                .filter(k -> !k.isDeleted())
                .toList();
    }

    // =====================================================
    // VERIFY
    // =====================================================

    /**
     * Lấy tất cả key có thể dùng để VERIFY
     *
     * Điều kiện:
     * - chưa bị delete
     * - expiresAt > now
     *
     * ⚠️ VERIFY KHÔNG phụ thuộc CURRENT/NEXT
     *
     * → chỉ cần:
     *    token.kid → tìm key → verify
     */
    public List<Key> getKeysForVerify(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.isUsableForVerify(now))
                .toList();
    }

    // =====================================================
    // SIGN
    // =====================================================

    /**
     * Lấy key dùng để SIGN JWT
     *
     * Logic:
     * - activateAt <= now
     * - chưa expired
     * - chọn key có activateAt gần nhất
     *
     * → đảm bảo không bị:
     *   - dùng key cũ
     *   - dùng key chưa active
     */
    public Key getKeyForSign(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.isUsableForSign(now))
                .max(Comparator.comparing(Key::getActivateAt))
                .orElseThrow(() -> new IllegalStateException(
                        "No active key found for signing"
                ));
    }

    // =====================================================
    // NEXT (PRELOAD)
    // =====================================================

    /**
     * Lấy key tiếp theo (NEXT)
     *
     * Dùng để:
     * - preload tại Identity Service
     * - chuẩn bị trước khi rotation
     *
     * Logic:
     * - activateAt > now
     * - chọn key gần nhất
     *
     * ⚠️ Không bắt buộc phải có
     */
    public Key getNextKey(List<Key> keys, Instant now) {
        return keys.stream()
                .filter(k -> k.getActivateAt() != null && k.getActivateAt().isAfter(now))
                .min(Comparator.comparing(Key::getActivateAt))
                .orElse(null);
    }

    // =====================================================
    // CLEANUP
    // =====================================================

    /**
     * Xóa (soft delete) các key đã hết hạn VERIFY
     *
     * Điều kiện:
     * - now >= expiresAt
     *
     * ⚠️ Không xóa ngay khi rotate
     * → phải giữ để verify JWT cũ
     *
     * Flow:
     * - markDeleted
     * - save lại DB
     */
    public void cleanupExpiredKeys(String serviceName, Instant now) {
        List<Key> keys = loadKeys(serviceName);

        keys.stream()
                .filter(k -> k.isExpiredForVerify(now))
                .forEach(k -> {
                    k.markDeleted();
                    keyRepository.save(k);
                });
    }
}
