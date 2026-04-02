package vn.xime.key.domain.key;

import java.util.List;
import java.util.Optional;

/**

* Domain Repository Interface
*
* =========================
* Vai trò:
* =========================
* * Là abstraction giữa Domain và Infrastructure
* * Domain KHÔNG biết đến JPA / DB / SQL
*
* =========================
* Nguyên tắc thiết kế:
* =========================
* * Không chứa business logic
* * Không filter theo lifecycle (activateAt / expiresAt)
* * Không dùng CURRENT / NEXT / OLD
*
* → Chỉ cung cấp dữ liệu "raw"
*
* =========================
* Lifecycle được xử lý ở đâu?
* =========================
* → KeyLifecycleManager (Domain Service)
*

*/
public interface KeyRepository {

    /**
     * Lấy tất cả key của một service
     *
     * ⚠️ KHÔNG filter theo:
     * - activateAt
     * - expiresAt
     *
     * Infrastructure có thể filter:
     * - isDeleted = false
     *
     * Domain sẽ xử lý tiếp lifecycle
     */
    List<Key> findAllByService(String serviceName);

    /**
     * Lấy key theo kid
     *
     * Dùng cho:
     * - verify JWT (lookup theo kid)
     * - debug / audit
     */
    Optional<Key> findByKid(String kid);

    /**
     * Lưu hoặc cập nhật key
     *
     * Dùng cho:
     * - generate key mới
     * - update lifecycle (mark deleted)
     */
    void save(Key key);

}
