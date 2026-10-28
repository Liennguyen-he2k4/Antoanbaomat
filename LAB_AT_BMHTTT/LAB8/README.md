Nguyễn Thị Kim Liên
11CNPM1
1150080024
## 1. Mục tiêu
-	Dựng DNS-SERVER BIND9 làm DNS đệ quy/cache cho riêng mạng DNS-LAB.
-	Cấu hình zone thuận/nghịch và kiểm thử bằng dig, nslookup/resolvectl.
-	Quan sát gói DNS bằng Wireshark/tcpdump và hiểu transaction ID, UDP source port, query name.
-	Thực hiện ba nhiệm vụ của tài liệu gốc: HOSTS-file redirection, direct DNS spoofing, DNS cache poisoning.
-	Khôi phục cache và cấu hình sau thử nghiệm; phân tích các biện pháp giảm thiểu như phân đoạn mạng, kiểm soát DNS egress, DNSSEC/DNS COOKIE và giới hạn promiscuous mode..
## 2. Mô hình & Môi trường thực nghiệm
pfSense-LAB6 (Firewall/Gateway):** LAN `10.6.0.1/24`, DMZ `172.16.6.1/24`, MGMT `192.168.56.254/24`,dns-lab `10.9.0.1/24`
VICTIM(WEB-PROXY ):** `10.9.0.100/24` 
DMZ-WEB (Ubuntu 24.04):** `172.16.6.10/24` và `172.16.6.20/24` 
Kali Linux:** `10.9.0.200/24`
DNS(Ubuntu) :`10.9.0.10/24`
## 3. Các bước triển khai chính
1. Trên máy APP-TEST (172.16.6.20) Chạy DNS Authoritative mô phỏng
2. Trên máy DNS-SERVER (10.9.0.10) Đảm bảo 4 file cấu hình trong thư mục 01_DNS_SERVER
3. Trên máy VICTIM (10.9.0.100) Kiểm tra Victim đã nhận DNS 10.9.0.10 và phân giải bình thường
4. Trên máy Kali Mở terminal và bật fake web server phục vụ trang fake_index.html
5. Trên máy VICTIM Mô phỏng trường hợp máy người dùng bị malware hoặc kẻ tấn công sửa file hosts cục bộ
## 4. Các lỗi gặp phải & Cách khắc phục thực tế
-Bị chặn 1 chiều gửi từ máy DMZ sang máy DNS ( khắc phục chuyển đổi về cùng mạng với pfsense )
-Tắt tường lửa bên pfSense nên ping qua máy DMZ không thông ( Bật tường lửa và ping lại)
```