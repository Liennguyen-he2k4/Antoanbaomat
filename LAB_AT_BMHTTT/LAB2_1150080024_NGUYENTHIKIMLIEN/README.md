# LAB 2 - MÃ HÓA ĐỐI XỨNG HIỆN ĐẠI

Ứng dụng thực hành các giải thuật mã hóa đối xứng khối và dòng, tích hợp xác thực tài khoản và lưu lịch sử mã hóa vào Microsoft SQL Server.

---

## 1. Môi trường yêu cầu
- **Java**: JDK 17 hoặc 21 (Apache NetBeans 20+).
- **Cơ sở dữ liệu**: SQL Server Express (TCP/IP Port `14333`, Mixed Mode Authentication).
  - Database: `LAB2_MaHoaHienDai`
  - User: `lab2_user` | Password: `LabUser@2026`
- **Python**: Python 3.10+ (thư viện `cryptography`).

---

## 2. Cấu trúc bài thực hành

| Bài | Thuật toán / Chế độ | Mô tả kỹ thuật |
| :--- | :--- | :--- |
| **2.1** | **DES (Data Encryption Standard)** | Khối 64-bit, Key 8 byte (56-bit), CBC + PKCS5Padding, IV ngẫu nhiên 8 byte. |
| **2.2** | **TripleDES (3DES / DESede)** | 3 lần DES (Mã - Giải - Mã), Key 24 byte, CBC + PKCS5Padding, IV ngẫu nhiên. |
| **2.3** | **AES-256 GCM + SQL Server** | Khối 128-bit, Key 256-bit (PBKDF2), chế độ GCM (xác thực tính toàn vẹn). Đăng ký/Đăng nhập và ghi lịch sử vào SQL Server. |
| **2.4** | **RC4 (Mã dòng)** | KSA + PRGA, Key 1 - 256 byte, mã hóa/giải mã là cùng phép XOR, xuất mã dạng HEX. |
| **2.5** | **Twofish (BouncyCastle)** | Khối 128-bit, Key 256-bit (SHA-256), CBC + PKCS7Padding, lưu tệp gồm IV và bản mã. |
| **2.6** | **AES-256 CTR (Java)** | Chuyển mã khối thành mã dòng (Nonce 12 byte + Counter 4 byte), không cần đệm. |
| **2.6** | **AES-256 CBC (Python)** | Giao diện Tkinter (`aes_cbc_gui.py`), đệm PKCS7, hỗ trợ tự kiểm thử `--self-test`. |
| **Menu**| **frm_Menu** | Giao diện chính điều hướng mở tất cả các bài thực hành. |

---

## 3. Hướng dẫn chạy chương trình

### Java (NetBeans):
1. Mở project `LAB2_MaHoaHienDai` trong NetBeans.
2. Nhấp phải project $\rightarrow$ **Clean and Build** (Shift + F11) để tải thư viện Maven (`mssql-jdbc`, `bcprov-jdk18on`).
3. Nhấn **F6** (Run Project) để mở **Menu chính** (`frm_Menu`), hoặc nhấn **Shift + F6** trên từng form để chạy riêng.
4. Mở form **Kết nối SQL Server** $\rightarrow$ Nhập password `LabUser@2026` $\rightarrow$ Bấm **Test Connection** trước khi dùng bài AES.

### Python:
```bash
# Cài đặt thư viện (1 lần duy nhất)
pip install cryptography

# Chạy tự kiểm thử
python C:\BaoMat\LAB2_Python\aes_cbc_gui.py --self-test

# Chạy giao diện Tkinter
python C:\BaoMat\LAB2_Python\aes_cbc_gui.py
```

---

## 4. Dữ liệu kiểm thử nhanh (Test Cases)

- **DES**: Plaintext `Xin chào Lab 2 - Mã hóa hiện đại!`, Key `12345678` $\rightarrow$ Encrypt/Decrypt đúng.
- **TripleDES**: Key `123456789012345678901234` (24 ký tự).
- **AES**: Register user `sinhvien01` $\rightarrow$ Login $\rightarrow$ Có Ticket mới được Encrypt/Decrypt.
- **RC4**: Plaintext `Plaintext`, Key `Key` $\rightarrow$ Ra đúng HEX: `BBF316E8D940AF0AD3`.
- **Twofish**: Bấm `Generate IV` $\rightarrow$ Encrypt $\rightarrow$ Save/Open File khôi phục đúng IV.
- **AES-CTR**: Bấm `Generate Key` & `Generate Nonce` $\rightarrow$ Mã hóa không làm tăng độ dài dữ liệu.
