Nguyễn Thị Kim Liên
11CNPM1
1150080024

# LAB 6: XÁC THỰC ACTIVE DIRECTORY VÀ CHÍNH SÁCH TRUY CẬP TRÊN pfSense

## 1. Mục tiêu

Dựng pfSense riêng cho LAB 6 với 4 interface (WAN, MGMT, LAN, DMZ), tích hợp Active Directory qua LDAP, cấu hình Captive Portal xác thực user domain, tạo Alias/Schedule và áp dụng các chính sách truy cập theo kịch bản (chặn site theo máy, theo khung giờ, cô lập DMZ), chứng minh kết quả bằng Firewall Log và State Table.

## 2. Môi trường

- Phần mềm ảo hóa: VMware Workstation
- Firewall: pfSense CE (VM pfSense-LAB6, clone từ VM pfSense của LAB 5 rồi Reset to factory defaults)
- Client-A: Windows 11 VM
- DC01 (Windows Server, domain lab6.test), Client-B, DMZ-WEB (Ubuntu + Nginx): chưa làm

### Cấu hình card mạng của pfSense-LAB6 (2 GB RAM, 2 CPU, 20 GB disk)

| Adapter | Kiểu mạng VMware | Interface | Vai trò |
|---|---|---|---|
| Network Adapter 1 | NAT | em0 | WAN |
| Network Adapter 2 | Custom (VMnet1, Host-only) | em1 | MGMT (OPT1) |
| Network Adapter 3 | LAN Segment `lab6-lan` | em2 | LAN |
| Network Adapter 4 | LAN Segment `lab6-dmz` | em3 | DMZ (OPT2) |

### Địa chỉ IP

| Thiết bị / interface | Địa chỉ | Ghi chú |
|---|---|---|
| pfSense WAN | DHCP (hiện tại 192.168.43.134/24, GW 192.168.43.2) | Ra Internet qua VMware NAT |
| pfSense MGMT | 192.168.56.254/24 | WebGUI cho máy thật (192.168.56.1) |
| pfSense LAN | 10.6.0.1/24 | Gateway LAN-DOMAIN |
| pfSense DMZ | 172.16.6.1/24 | Gateway DMZ |
| DC01 | 10.6.0.10/24 | Dự kiến (AD DS + DNS, lab6.test) |
| Client-A | 10.6.0.21/24, GW 10.6.0.1, DNS 10.6.0.10 | Win11 |
| Client-B | 10.6.0.22/24 | Dự kiến |
| DMZ-WEB | 172.16.6.10 (+ .20/.30/.40) | Dự kiến |

## 3. Các bước đã làm

1. Clone VM pfSense thành pfSense-LAB6 (Full clone), chỉnh RAM 2 GB, 2 CPU.
2. Cấu hình đủ 4 Network Adapter như bảng trên.
3. Console pfSense: Reset to factory defaults để xóa cấu hình LAB 5.
4. Assign Interfaces: WAN = em0, LAN = em2, OPT1 = em1, OPT2 = em3.
5. Set interface IP: WAN DHCP, LAN 10.6.0.1/24, OPT1 192.168.56.254/24, OPT2 172.16.6.1/24, tắt DHCP server.
6. Vào WebGUI từ máy thật tại https://192.168.56.254 (admin / pfsense).
7. Kiểm tra WAN: trạng thái up, nhận IP DHCP; Diagnostics → Ping 8.8.8.8 thành công (3/3, 0% loss).
8. Cấu hình Client-A (Win11) gắn `lab6-lan`, IP 10.6.0.21/24 → `ping 10.6.0.1` thành công (4/4, 0% loss).

## 4. Lỗi đã gặp và cách xử lý

| Lỗi | Nguyên nhân | Cách xử lý |
|---|---|---|
| Assign Interfaces báo Invalid interface name `em3` | pfSense chỉ nhận 3 card (em0–em2) dù Settings đã có 4 adapter | Tắt hẳn VM, kiểm tra lại Adapter 4, bật lại → pfSense nhận đủ em0–em3 |
| Invalid interface name `emo0` | Gõ nhầm chữ `o` thay vì số `0` | Gõ lại `em0` |
| "Same interface name cannot be assigned twice" | Gán em2 cho cả LAN và Optional 2 | Làm lại Assign Interfaces, gán OPT2 = em3 |
| Nhập IP sai chỗ khi đặt IP cho LAN (IP 10.6.0.1 nhập vào ô IPv6) | Trả lời `y` cho câu "Configure IPv4 via DHCP" nên bị lệch thứ tự câu hỏi | Chọn lại Set interface IP address, trả lời `n` cho DHCP rồi nhập IPv4 đúng |
| Máy thật vào https://192.168.56.254 bị ERR_CONNECTION_TIMED_OUT | Firewall pfSense mặc định chặn truy cập WebGUI từ interface MGMT (chưa có rule) | Tạm tắt packet filter trên console (`pfctl -d`), vào WebGUI rồi tạo rule Pass cho MGMT |
| Win11 báo "No Internet" | Mạng LAN riêng chưa có DNS/DC | Bình thường ở giai đoạn này |

Tài khoản WebGUI: admin / pfsense
