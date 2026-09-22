
# 2. PHIÊN BẢN MÔI TRƯỜNG THỰC HIỆN
- **Phần mềm ảo hóa:** VMware Workstation Pro / Player
- **Hệ điều hành máy ảo (Guest OS):** Windows 11 x64 (Build 26100 / 26200)
- **Môi trường dòng lệnh:** Windows PowerShell 5.1 & Command Prompt (CMD)
- **Ngôn ngữ lập trình:** Python 3.14.7
- **Công cụ phân tích mạng:** Wireshark 4.6.8 & Npcap Packet Capture Driver
- **Bộ công cụ Sysinternals:**
  - Sysmon v15.22
  - Autoruns v14.3
  - Process Explorer v17.14
---
## 3. CÁCH DỰNG MÔI TRƯỜNG THỰC HÀNH
1. **Khởi tạo máy ảo:** Tạo máy ảo Windows 11 x64 trên VMware, gắn tệp ISO `Win11_25H2_English_x64_v2.iso`, cấu hình chuẩn UEFI và TPM.
2. **Cài đặt Windows 11 & Vượt rào OOBE:** Bỏ qua yêu cầu tài khoản/mạng ban đầu bằng lệnh `oobe\bypassnro` để tạo tài khoản cục bộ (Local Account).
3. **Cài đặt VMware Tools:** Kích hoạt driver mạng (VMXNET3), hiển thị độ phân giải màn hình tối ưu và kích hoạt tính năng chia sẻ tệp/clipboard hai chiều giữa máy thật và máy ảo.
4. **Khởi tạo cấu trúc thư mục:** Mở PowerShell Administrator, tạo cấu trúc thư mục `C:\LAB3` gồm: `Evidence`, `Tools`, `Downloads`, `Assets`.
5. **Nạp dữ liệu bài thực hành:** Chép gói tệp `LAB3_Threats_Assets.zip` vào `C:\LAB3\Downloads`, xác minh mã băm SHA-256 (`EA93627111FD...`) và giải nén vào `C:\LAB3`.
6. **Cài đặt công cụ:**
   - Cài đặt Python 3.14 và Wireshark (Npcap Loopback) qua trình quản lý gói `winget` / bộ cài trực tiếp.
   - Tải bộ công cụ Sysinternals (Sysmon, Autoruns, ProcessExplorer) từ máy chủ Microsoft và giải nén vào `C:\LAB3\Tools`.
---
## 4. CÁC TÌNH HUỐNG ĐÃ THỰC HIỆN & KẾT QUẢ
| Tình huống | Nội dung thực hiện | Bằng chứng / Minh chứng | Kết quả |
| :--- | :--- | :--- | :---: |
| **0. Baseline** | Thu thập trạng thái tham chiếu của OS, Defender, Firewall, Network và Process. | `H3_Baseline_Defender_Firewall.png`, `baseline_*.txt` | **PASS** |
| **TH1. Risk Register** | Lập Risk Register 5 tài sản và phân loại 5 tình huống nguồn đe dọa. | Bảng phân tích trong báo cáo, `risk_register.txt` | **PASS** |
| **TH2. Mã độc EICAR** | Kiểm tra cơ chế Real-Time Protection và chu trình phát hiện/cách ly của Defender. | `H4_ProtectionHistory_EICAR.png`, `defender_eicar.txt` | **PASS** |
| **TH3. Password & Audit** | Bật Audit Logon, tạo user `lab3user`, sinh sự kiện Event 4624/4625/4648, thực hiện xoay vòng mật khẩu (Rotation). | `H5_Event4625.png`, `auth_events_before_rotation.txt` | **PASS** |
| **TH4. Backdoor & Persistence** | Cài đặt Sysmon (Event 1), tạo Registry Run & Scheduled Task, chạy HTTP Server localhost:8080 và ánh xạ PID qua Process Explorer. | `H6_Sysmon_Event1.png`, `H7_Autoruns_LAB3_Run_Demo.png`, `H8_ProcessExplorer_Python.png`, `sysmon_persistence.txt` | **PASS** |
| **TH5. Sniffing & HTTP/HTTPS** | Bắt gói tin qua Wireshark: phân tích Request URI bản rõ (HTTP loopback) so sánh với dữ liệu mã hóa TLS/443 (HTTPS). | `H9_HTTP_Plaintext.png`, `H10_TLS_443.png` | **PASS** |
| **TH7. Social Engineering** | Đánh dấu 5 chỉ dấu trong mẫu `phishing_email.txt` và phân loại 6 trường hợp tấn công Social Engineering. | `H10_Phishing_Offline.png`, Bảng phân loại trong báo cáo | **PASS** |
| **8. Cleanup & Phục hồi** | Gỡ bỏ Persistence, xóa user thử nghiệm, dừng server, thu baseline Autoruns sau cleanup và tính mã băm toàn vẹn SHA-256. | `H11_Recovery_Verification.png`, `evidence_sha256.csv`, `autoruns_diff.txt` | **PASS** |
---
## 5. CÁC LỖI GẶP PHẢI TRONG QUÁ TRÌNH THỰC HÀNH VÀ CÁCH KHẮC PHỤC
1. **Lỗi `EFI Network... Time out` khi boot máy ảo:**
   - *Nguyên nhân:* Không kịp bấm phím để boot từ đĩa CD/DVD trong vài giây đầu khởi động.
   - *Khắc phục:* Chọn *Restart Guest*, click chuột vào màn hình đen và nhấn liên tục phím `Space`/`Enter` ngay khi máy vừa bật.
2. **Lỗi Windows 11 yêu cầu kết nối mạng tại màn hình OOBE:**
   - *Nguyên nhân:* Bản cài đặt Windows 11 bắt buộc có mạng và tài khoản Microsoft khi mới cài.
   - *Khắc phục:* Nhấn `Shift + F10` mở CMD, chạy lệnh `oobe\bypassnro`, sau khi máy khởi động lại chọn *"I don't have internet"* $\rightarrow$ *"Continue with limited setup"*.
3. **Lỗi `winget` báo `Failed when opening source(s)` / `0x80072ee7`:**
   - *Nguyên nhân:* Máy ảo chưa nhận driver mạng (chưa cài VMware Tools) và nguồn repository của winget chưa được khởi tạo.
   - *Khắc phục:* Cài đặt VMware Tools, chuyển mạng sang chế độ NAT, chạy PowerShell với quyền Administrator và thực hiện lệnh `winget source reset --force`.
4. **Lỗi `runas` báo `RUNAS ERROR: Unable to acquire user password`:**
   - *Nguyên nhân:* PowerShell Host / PSReadLine chặn luồng nhập mật khẩu ẩn của tiện ích `runas.exe`.
   - *Khắc phục:* Bật dịch vụ `Start-Service seclogon`, chuyển sang môi trường `cmd` để chạy `runas /user:lab3user cmd.exe` hoặc sử dụng đối tượng `PSCredential` trong PowerShell.
5. **Lỗi `Failed to open xml configuration: sysmon-lab.xml`:**
   - *Nguyên nhân:* Đường dẫn tệp cấu hình sau khi giải nén nằm trong thư mục con khác so với lệnh mẫu.
   - *Khắc phục:* Sử dụng lệnh `Get-ChildItem -Filter "sysmon-lab.xml" -Recurse` để tự động dò tìm đường dẫn thực tế của tệp và nạp vào Sysmon.
6. **Lỗi không thấy `LAB3_Run_Demo` trong Autoruns:**
   - *Nguyên nhân:* Autoruns mặc định bật tính năng ẩn các tệp nhị phân do Microsoft phát hành (`Hide Microsoft Entries`), trong khi entry trỏ đến `notepad.exe`.
   - *Khắc phục:* Bỏ tích chọn `Hide Microsoft Entries` trong menu *Options* và gõ từ khóa `LAB3` vào ô *Quick Filter*.
7. **Lỗi Wireshark không bắt được gói tin HTTPS/443:**
   - *Nguyên nhân:* Wireshark vẫn đang ở chế độ bắt trên card mạng `Loopback` thay vì card mạng chính `Ethernet0`.
   - *Khắc phục:* Dừng capture (`Stop`), nhấn `Ctrl + K` chọn card mạng `Ethernet0` (IP: `192.168.43.130`) và bắt đầu phiên capture mới.
8. **Lỗi xung đột khóa tệp khi tạo `evidence_sha256.csv`:**
   - *Nguyên nhân:* Lệnh `Export-Csv` đang giữ handle ghi đè vào tệp `evidence_sha256.csv` trong khi pipeline `Get-FileHash` lại đang cố đọc danh sách tệp cùng thư mục `C:\LAB3\Evidence`.
   - *Khắc phục:* Xuất tệp mã băm ra thư mục tạm bên ngoài trước (`C:\LAB3\evidence_temp.csv`), sau đó dùng `Move-Item -Force` để chuyển nguyên vẹn vào `C:\LAB3\Evidence\evidence_sha256.csv`.
"@
