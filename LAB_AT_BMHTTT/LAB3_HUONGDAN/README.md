Nguyễn Thị Kim Liên
11CNPM1
1150080024

# BÁO CÁO THỰC HÀNH LAB 3: MÃ HÓA KHÓA CÔNG KHAI (RSA, ECC, HỆ MÃ LAI & CHỮ KÝ SỐ)

## 1. Mục tiêu
- Nắm vững nguyên lý và cài đặt các hệ mật mã bất đối xứng hiện đại: **RSA** (OAEP SHA-256) và **ECC** (đường cong secp256r1 / P-256).
- Xây dựng **Hệ mã lai (Hybrid Encryption: RSA + AES-256-GCM)** kết hợp ưu điểm bảo mật khóa công khai và tốc độ mã hóa dữ liệu lớn của mật mã đối xứng.
- Triển khai **Chữ ký số ECDSA** (`SHA256withECDSA`) để đảm bảo tính toàn vẹn dữ liệu, xác thực nguồn gốc và chống chối bỏ.
- Tích hợp ghi nhận nhật ký thao tác an toàn vào hệ quản trị cơ sở dữ liệu **Microsoft SQL Server**.

## 2. Mô hình & Môi trường thực nghiệm
| Thành phần | Thông số / Công nghệ | Vai trò / Chức năng |
| :--- | :--- | :--- |
| **Hệ điều hành & IDE** | Windows 11, NetBeans 22, JDK 17, Apache Maven | Môi trường lập trình ứng dụng Java Desktop (Swing UI) |
| **Cơ sở dữ liệu** | Microsoft SQL Server (Port: `14333`) | Database `LAB3_MaHoaKhoaCongKhai`, user `lab3_user` |
| **Bảng lưu trữ** | `dbo.History` | Lưu lịch sử thao tác: `ENCRYPT`, `DECRYPT`, `SIGN`, `VERIFY` |
| **Thư viện JDBC** | `mssql-jdbc:12.8.1.jre11` | Kết nối CSDL bảo mật không lưu mật khẩu cứng trong code |
| **Gói mã nguồn** | Package `week_03` đặt tại `C:\BaoMat\LAB3_MaHoaKhoaCongKhai` | Cấu trúc chuẩn không dấu tiếng Việt tránh lỗi build Maven |

## 3. Các bước triển khai chính

### 1. Chuẩn bị CSDL & Các lớp nền tảng dùng chung
- **SQL Server:** Chạy script tạo CSDL `LAB3_MaHoaKhoaCongKhai`, tạo bảng `dbo.History` (ràng buộc CHECK operation) và cấp quyền cho login `lab3_user`.
- **CryptoSupport.java:** Cung cấp các tiện ích mã hóa chuẩn: Base64, UTF-8, AES-256-GCM (nonce 12-byte, tag 16-byte), HKDF-SHA256 (RFC 5869), định dạng tệp khóa `PUBLIC=` / `PRIVATE=`.
- **FileTextIO.java:** Xử lý hộp thoại mở/lưu tệp văn bản và tệp khóa an toàn.
- **DbConfig.java & HistoryDao.java:** Quản lý kết nối JDBC động và ghi nhật ký tự động (chỉ lưu độ dài dữ liệu và trạng thái thành công, tuyệt đối không lưu khóa).
- **frm_KetNoiSQL.java & frm_LichSu.java:** Giao diện kiểm thử kết nối SQL Server và xem 50 thao tác gần nhất trên JTable.

### 2. Bài 3.1 & 3.2: Mật mã RSA (OAEP SHA-256)
- Xây dựng lớp `RSACipher.java` với đệm an toàn chống tấn công chọn bản mã (`RSA/ECB/OAEPWithSHA-256AndMGF1Padding`).
- Chạy thử console trên `RSADemo.java` với 2 kích thước khóa:
  - Khóa **2048-bit**: Giới hạn bản rõ 190 byte, bản mã Base64 dài 344 ký tự.
  - Khóa **3072-bit**: Giới hạn bản rõ 318 byte, bản mã Base64 dài 512 ký tự (thời gian sinh khóa lâu hơn).
- Hoàn thiện giao diện `frm_RSA.java`: Sinh khóa ngẫu nhiên, mã hóa bằng khóa công khai, giải mã bằng khóa riêng.

### 3. Bài 3.3: Mật mã đường cong Elliptic ECC (ECIES)
- Cài đặt `ECCCipher.java` sử dụng đường cong chuẩn NIST `secp256r1` (P-256).
- Ứng dụng sơ đồ **ECIES**: Sinh cặp khóa tạm thời (ephemeral), thực hiện thỏa thuận khóa **ECDH**, dẫn xuất khóa AES qua **HKDF-SHA256** và mã hóa dữ liệu bằng **AES-256-GCM**.
- Hoàn thiện giao diện `frm_ECC.java`: Hỗ trợ lưu khóa ra file `ecc_keys.txt` và nạp lại khóa. Kiểm chứng giải mã bằng sai khóa riêng sẽ bị từ chối.

### 4. Bài 3.4: Hệ mã lai (Hybrid RSA + AES-256-GCM)
- Cài đặt `RSA_AES_Cipher.java`: Sinh khóa phiên AES 32-byte ngẫu nhiên để mã hóa dữ liệu lớn (> 1.000 ký tự) siêu nhanh; dùng khóa công khai RSA-2048 để bọc bảo vệ khóa AES (256 byte).
- Giao diện `frm_RSA_AES.java`:
  - Mã hóa và giải mã văn bản dài nguyên vẹn.
  - Kiểm thử nút **"Sửa 1 ký tự bản mã"**: Khi bản mã bị sửa đổi, thuật toán AES-GCM tự động phát hiện vi phạm tính toàn vẹn (bắt lỗi `AEADBadTagException`) $\rightarrow$ từ chối giải mã ngay lập tức.

### 5. Bài 3.5: Chữ ký số ECDSA (Bài tập nâng cao)
- Cài đặt `ECCSignature.java` theo thuật toán `SHA256withECDSA` trên đường cong `secp256r1`.
- Giao diện `frm_ECC_Signature.java`:
  - Người gửi: Dùng khóa riêng tạo chữ ký số (chuỗi Base64 ~ 96 ký tự).
  - Người nhận: Dùng khóa công khai xác minh $\rightarrow$ Hiện **VALID** (màu xanh).
  - Thử nghiệm sửa đổi nội dung thông điệp hoặc thay đổi khóa $\rightarrow$ Chữ ký lập tức báo **INVALID** (màu đỏ).

### 6. Phần 3: Menu chính & Kiểm thử tích hợp
- Tạo `frm_Menu.java` làm điểm khởi chạy trung tâm (`<exec.mainClass>` trong `pom.xml`), liên kết trực tiếp tới toàn bộ 6 form thành phần.
- Chạy toàn diện ứng dụng (F6): Kiểm tra tính đồng bộ dữ liệu giữa các thao tác mã hóa/ký số với bảng `dbo.History` trong SQL Server.

## 4. Kết quả đạt được
1. **Bảo mật và Hiệu năng:** Hiểu rõ giới hạn kích thước của RSA thuần và làm chủ giải pháp Hệ mã lai (RSA+AES) để bảo vệ dữ liệu dung lượng lớn trong thực tế.
2. **Toàn vẹn và Xác thực:** Kiểm chứng thành công tính năng tự bảo vệ chống giả mạo của AES-GCM và cơ chế chống chối bỏ của Chữ ký số ECDSA.
3. **Quản trị CSDL:** Ghi nhận và hiển thị lịch sử thao tác minh bạch, tuân thủ nguyên tắc an toàn thông tin (không để lộ khóa bí mật).