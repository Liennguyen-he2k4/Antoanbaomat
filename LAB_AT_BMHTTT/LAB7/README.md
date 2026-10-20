Nguyễn Thị Kim Liên
11CNPM1
1150080024
## 1. Mục tiêu
* Xây dựng hệ thống Web Protection kiểm soát truy cập và lọc nội dung web theo Layer 7.
* Phân tách kiến trúc: **pfSense** làm Gateway/Firewall (L3/L4) kiểm soát luồng và chống bypass; máy chủ riêng **WEB-PROXY** (Ubuntu) đảm nhiệm lọc nội dung (E2Guardian), chuyển tiếp (Squid) và quét mã độc (ClamAV) thay vì cài trực tiếp các gói deprecated lên tường lửa.
## 2. Mô hình & Môi trường thực nghiệm
* **pfSense-LAB6 (Firewall/Gateway):** LAN `10.6.0.1/24`, DMZ `172.16.6.1/24`, MGMT `192.168.56.254/24`.
* **WEB-PROXY (Ubuntu 24.04):** `10.6.0.30/24` (GW: `10.6.0.1`, DNS: `10.6.0.10`) chạy Squid (`:3128`), E2Guardian (`:8080`) và ClamAV daemon.
* **DMZ-WEB (Ubuntu 24.04):** `172.16.6.10/24` chạy Nginx chứa 13 website mô phỏng (`*.lab7.test`) và file test EICAR/kích thước.
* **DC01 (Windows Server 2022):** `10.6.0.10/24` chạy DNS Server phân giải các domain `*.lab7.test` về `172.16.6.10`.
* **Client-A (Windows 11):** `10.6.0.21/24` máy trạm người dùng, cấu hình Manual Proxy trỏ về `10.6.0.30:8080`.
## 3. Các bước triển khai chính
1. **Khởi tạo dịch vụ DMZ-WEB & DNS DC01:**
   * Cấu hình 13 Virtual Host trên Nginx tại DMZ-WEB đại diện cho các nhóm nội dung (Allow, Hard Block, Warning, Schedule, Download).
   * Tạo Primary Zone `lab7.test` trên DC01 với 13 bản ghi A trỏ về IP `172.16.6.10`.

2. **Dựng máy chủ WEB-PROXY (Squid + E2Guardian + ClamAV):**
   * **Squid:** Lắng nghe duy nhất tại `127.0.0.1:3128`, chỉ cho phép localhost truy cập.
   * **E2Guardian:** Lắng nghe tại `10.6.0.30:8080`, đẩy tiếp lưu lượng sang Squid, bật scanner `clamdscan.conf`.
   * **Chính sách lọc:** Thiết lập `bannedsitelist` (Hard block: china, jobs, blockedgame...; Warning: weapons, politics...) với `bypass = 300` (mở tạm 5 phút), và `exceptionsitelist` (Whitelist: normal, education).

3. **Cấu hình pfSense ép lưu lượng qua Proxy (Anti-Bypass):**
   * Tạo Aliases: `LAB7_CLIENTS`, `LAB7_PROXY`, `WEB_PORTS` (80, 443), `MAIL_PORTS`.
   * Tạo Firewall Rules tab LAN: Cho phép DNS (`53`), cho phép Client kết nối Proxy (`8080`), **chặn tuyệt đối Client đi trực tiếp Web (`80, 443`)**, chỉ cho phép `LAB7_PROXY` được kết nối ra Web và vào DMZ (`172.16.6.10:80`).
   * Tạo Rule mở tab DMZ cho phép lưu lượng phản hồi.

4. **Kiểm thử trên Client-A:**
   * Cấu hình proxy `10.6.0.30:8080` trên trình duyệt: kiểm thử thành công truy cập `normal.lab7.test`.
   * Tắt proxy: truy cập bị pfSense chặn (chống bypass thành công).
   * Kiểm thử các chính sách: Hard block, Warning & Bypass 5 phút, Whitelist, và phát hiện virus EICAR qua ClamAV.
## 4. Các lỗi gặp phải & Cách khắc phục thực tế
| Lỗi gặp phải | Nguyên nhân | Cách khắc phục |
| :--- | :--- | :--- |
| **Sai mật khẩu admin pfSense** | Nhập nhầm vào trang Captive Portal hoặc quên mật khẩu WebGUI sau Lab 6. | Mở Console máy ảo pfSense, chọn mục **`3`** (*Reset webConfigurator password*) để đưa mật khẩu về mặc định **`pfsense`**. |
| **Rule pfSense chặn sạch mạng LAN** | Tạo rule `Default Deny` nhưng đặt ở dòng trên cùng, vi phạm nguyên tắc xử lý rule từ trên xuống dưới của pfSense. | Xóa bỏ hoặc kéo rule `Default Deny` xuống dưới cùng của danh sách; bấm `Apply Changes` và `Reset States`. |
| **Lỗi kết nối DMZ (`Connection timed out` sau 135s)** | pfSense chưa mở rule cho phép trên tab `DMZ`, khiến gói tin giữa LAN và DMZ bị chặn. | Vào `Firewall > Rules > DMZ`, thêm rule `Pass Any` cho cổng DMZ. |
| **Lỗi `Connection Refused` (thất bại sau 0-1ms)** | Script `setup_lab7_dmz_web.sh` bị ngắt (`Ctrl+C`) khi chạy `apt update`, khiến file cấu hình Nginx bị xóa mà chưa tạo lại vhost, Nginx không lắng nghe cổng 80. | Viết lại lệnh tạo vhost cho 13 site vào Nginx, chỉ định `default_server` và restart dịch vụ. |
| **Card mạng ảo không thông (`ping 100% loss`)** | Cấu hình lệch LAN Segment giữa máy DMZ-WEB và pfSense trong VMware. | Kiểm tra lệnh `ip neigh` đối chiếu đúng địa chỉ MAC giữa card DMZ của pfSense và card `ens33` của DMZ-WEB. |
```