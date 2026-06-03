# Tổng Quan

[English](../en/overview.md) | **Tiếng Việt**

---

## Trust Service là gì?

Trust Service là **hạ tầng mã hóa và cơ quan tin cậy** của Xime Base Platform.

Vai trò của nó là trở thành nguồn tin cậy duy nhất cho toàn bộ vật liệu mã hóa được sử dụng trên platform — JWT signing key cho authentication và chứng chỉ X.509 cho mTLS giữa các service. Không có service nào khác được phép tự tạo hoặc sở hữu các vật liệu này.

```
Application Services
  identity-service → ký JWT bằng key từ Trust Service
  user-service     → giao tiếp qua mTLS cert từ Trust Service
  data-service     → giao tiếp qua mTLS cert từ Trust Service
         ↓  (mọi thông tin mã hóa đều xuất phát từ đây)
    Trust Service  ← Root of Trust
         ↓
  PostgreSQL
```

---

## Vị trí trong Base Platform

Xime Base Platform chia làm hai tầng:

### Base Platform (core services)

Các service hạ tầng chung, tái sử dụng được, xây dựng một lần và dùng cho mọi ứng dụng:

| Service | Vai trò |
|---|---|
| `trust-service` | **Trust infrastructure — CA, mTLS, JWT signing key** |
| `identity-service` | Authentication — cấp JWT, refresh token |
| `user-service` | Human Identity Domain — credential, trạng thái tài khoản |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Gửi thông báo |
| `payment-service` | Thanh toán |

### Application Layer (business services)

Logic nghiệp vụ cụ thể dựa trên nền tảng Base Platform:

- **Mạng xã hội**: post-service, comment-service, media-service
- **Thương mại điện tử**: product-service, order-service
- **SaaS / AI**: workspace-service, dataset-service, ai-agent-service

Trust Service phục vụ tất cả các service này một cách gián tiếp — thông qua nền bảo mật mà chúng phụ thuộc vào.

---

## Hai hệ thống độc lập

Trust Service quản lý hai hệ thống bảo mật hoàn toàn độc lập, không chia sẻ dữ liệu hay logic:

### A. JWT Key System

Dùng cho authentication user trên toàn platform:

```
Trust Service
   → tạo cặp khóa RSA/EC
   → Identity Service lấy signing key (private)
   → Identity Service ký JWT
   → Các service khác lấy public key để verify JWT local
```

Key rotation là **time-based**. Khi key hiện tại gần hết hạn, Trust Service tự tạo key tiếp theo. Các service cache tất cả public key chưa hết hạn để verify được cả token cũ.

### B. Service Trust System (mTLS)

Dùng cho giao tiếp xác thực giữa các service:

```
Trust Service
   → vận hành như CA nội bộ
   → cấp chứng chỉ X.509 cho từng service (SAN = service_id)
   → service trình chứng chỉ trong mỗi kết nối
   → service nhận xác thực chứng chỉ với Root CA
```

Cert rotation dùng **refresh token one-time** được gắn với cert hiện tại. Service phải trình cả token và kết nối mTLS hiện tại để chứng minh quyền sở hữu trước khi nhận cert mới.

---

## Triết lý thiết kế

| Câu hỏi | Câu trả lời |
|---|---|
| Trust Service là gì? | Hạ tầng mã hóa — Root of Trust |
| Nó quản lý gì? | JWT signing key pairs + X.509 service certificate |
| Nó có authenticate user không? | Không — đó là trách nhiệm của Identity Service |
| Nó có trong request path không? | Không — chỉ là control-plane |
| Nếu nó bị down thì sao? | JWT verify và mTLS vẫn hoạt động (dùng cached key/cert) |
| AI là CA? | Trust Service, thông qua `CertificateAuthoritySigner` |

---

## Mô hình Resilience

Trust Service là **control-plane service**, không phải data-plane. Điều này rất quan trọng về kiến trúc:

```
Trust Service DOWN
      ↓
JWT verification  → tiếp tục (public key đã cache ở mỗi service)
mTLS connections  → tiếp tục (cert hiện tại vẫn hợp lệ)
JWT signing       → tiếp tục (Identity Service có cached signing key)
      ↓
Chỉ bị ảnh hưởng khi:
  - cần rotate key (không generate được next key)
  - cần cấp cert mới (service mới không thể nhận cert)
  - cần rotate cert (bỏ lỡ rotation window)
```

Các service được thiết kế để chịu đựng Trust Service không khả dụng hàng giờ mà không ảnh hưởng đến traffic người dùng.

---

## Trust Service KHÔNG phải là gì

- Không phải identity provider — không cấp hay validate token user
- Không phải security gateway runtime — không chặn hay inspect request
- Không phải secrets manager — quản lý key lifecycle, không quản lý application secret
- Không phải certificate revocation service — revocation được xử lý qua expiry và rotation cadence
