Nguyễn Thị Kim Liên
11CNPM1
1150080024

# LAB 7: 

## 1. Mục tiêu

Xây dựng hệ thống lọc web an toàn. Thay vì cài trực tiếp các gói phần mềm cũ lên tường lửa pfSense, bài lab tách riêng một máy WEB-PROXY chạy Ubuntu (gồm E2Guardian, Squid, ClamAV) để lọc nội dung và quét virus

## 2. Môi trường

pfSense: Làm tường lửa chính (LAN: 10.6.0.1, DMZ: 172.16.6.1).

WEB-PROXY: Máy chủ Ubuntu (10.6.0.30) chạy E2Guardian, Squid và ClamAV.

DMZ-WEB: Máy chủ Ubuntu (172.16.6.10) chứa các trang web thử nghiệm (*.lab7.test).

DC01: Máy chủ Windows Server (10.6.0.10) phân giải tên miền (lab7.test).

Client-A: Máy trạm Windows 11 (10.6.0.21

## 3. Các bước đã làm

Cấu hình pfSense: Đổi IP mạng LAN và vùng DMZ.   Cài đặt Web Proxy: Cài Squid, E2Guardian và ClamAV trên máy WEB-PROXY.   Cấu hình Squid & E2Guardian:Chỉnh Squid chỉ nghe cổng nội bộ 127.0.0.1:3128, giới hạn file tải tối đa 100MB.   Cấu hình E2Guardian ở cổng 8080, bật quét virus ClamAV. Thiết lập máy chủ web (DMZ-WEB) & DNS (DC01): Trỏ tên miền lab7.test về địa chỉ của máy DMZ-WEB.   Cấu hình pfSense: Tạo các quy tắc (rules) bắt buộc người dùng phải đi qua proxy và thiết lập lịch trình chặn

## 4. Lỗi đã gặp và cách xử lý

Lỗi mất mạng do Netplan (systemd-networkd is not running): Do máy dùng Ubuntu Desktop có bật NetworkManager, tiến hành cấu hình lại mạng bằng công cụ nmcli.

Lỗi không kết nối được (Destination Host Unreachable / mất ping): Do cấu hình sai card mạng ảo (chưa khớp đoạn mạng LAN Segment với pfSense).

pfSense chặn ping: Do tường lửa mặc định chặn ICMP khi chưa có quy tắc cho phép, kiểm tra lại bằng lệnh curl thay vì chỉ dùng ping.

Lỗi cấu hình E2Guardian (phiên bản mới): Thư mục danh sách đổi thành example.group, cần đưa các tên miền bị chặn
