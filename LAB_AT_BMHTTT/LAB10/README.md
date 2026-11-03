Nguyễn Thị Kim Liên
11CNPM1
1150080024
## 1. Mục tiêu
LAB 10 kế thừa DNS-LAB của LAB trước nhưng tách ATTACKER sang một subnet khác. Kali không được sniff lưu lượng DNS-SERVER. Pha tấn công dùng resolver mô phỏng có chủ đích để tái hiện điều kiện yếu của resolver cũ; sau đó sinh viên phục hồi BIND9 hiện đại và so sánh
## 2. Mô hình & Môi trường thực nghiệm
pfSense	10.9.0.1/24 (DNSLAB)
10.10.0.1/24 (ATTACKNET)	Adapter 5: LAN Segment DNS-LAB
Adapter 6: LAN Segment ATTACK-NET	Gateway định tuyến & firewall cô lập
DNS-SERVER	10.9.0.10/24	LAN Segment DNS-LAB	BIND9 & vuln_resolver_lab10.py
VICTIM (web-proxy)	10.9.0.100/24	LAN Segment DNS-LAB	Máy nạn nhân
KALI	10.10.0.200/24	LAN Segment ATTACK-NET	Rời khỏi DNSLAB
AUTH-DNS (APP-TEST)	172.16.6.20	DMZ 
DMZ-WEB	172.16.6.10`
## 3. Các bước triển khai chính
1. Chuẩn bị mạng và chuyển Kali sang ATTACKNET
pfSense: Thêm card mạng mới gán interface ATTACKNET với IP 10.10.0.1/24 (không gán gateway), gõ pfctl -d để mở thông mạng.
Kali: Chuyển card mạng sang LAN Segment ATTACK-NET, đặt IP tĩnh 10.10.0.200/24, Default Gateway 10.10.0.1 (xóa sạch IP cũ 10.9.0.200).
DMZ-WEB: Gán thêm IP phụ 172.16.6.20/24 trên card ens33.
2. Thiết lập và kiểm tra Baseline (Trước tấn công)
DMZ-WEB (172.16.6.20): Chạy sudo python3 auth_dns_relay_lab10.py.
DNS-SERVER (10.9.0.10): Chạy BIND9 với file cấu hình baseline (named.conf.options.baseline và named.conf.local.baseline).
VICTIM (10.9.0.100): Gõ dig @10.9.0.10 www.example.lab10.test 
→
→ Nhận đúng IP thật 172.16.6.10.
3. Chứng minh tính chất Remote (Kali không thể Sniff)
Trên Kali, chạy sudo tcpdump -i eth0 -n "udp port 53".
Trên Victim, gửi truy vấn DNS 
→
→ Terminal Kali trống rỗng, chứng minh Kali ở subnet khác và không thể nghe lén gói tin nội bộ của DNSLAB.
4. Thực hiện tấn công Kaminsky Remote Cache Poisoning
DNS-SERVER: Tắt BIND9 (sudo systemctl stop bind9), chạy resolver mô phỏng có lỗ hổng cố định cổng nguồn:
sudo python3 vuln_resolver_lab10.py (upstream cố định port 33333).
KALI: Mở 3 terminal chạy:
Terminal 1: sudo python3 evil_dns_lab10.py (máy chủ DNS độc hại cổng 53).
Terminal 2: sudo python3 -m http.server 80 (web server giả mạo).
Terminal 3: python3 kaminsky_remote_lab10.py (tấn công brute-force đoán batch TXID).
Kết quả: Terminal Kali báo SUCCESS; APP-TEST báo log [HIT]; DNS-SERVER báo log [CACHE] delegation poisoned.
5. Kiểm chứng hậu quả trên Victim
Trên VICTIM (10.9.0.100), gõ lại:
dig @10.9.0.10 www.example.lab10.test 
→
→ Bị trỏ sang IP của hacker 10.10.0.200!
curl http://www.example.lab10.test 
→
→ Tải về trang web giả mạo của Kali.
6. Khôi phục và Hardening (Phòng thủ)
Tắt toàn bộ các script mô phỏng trên Kali và DNS-SERVER.
Trên DNS-SERVER, bật lại dịch vụ BIND9: sudo systemctl start bind9.
Dùng tcpdump trên DNS-SERVER kiểm tra các truy vấn mới từ Victim 
→
→ Quan sát thấy BIND9 hiện đại tự động ngẫu nhiên hóa UDP Source Port (không còn cố định cổng 33333), triệt tiêu hoàn toàn khả năng đoán cổng của kẻ tấn công từ xa.

```