Nguyễn Thị Kim Liên
11CNPM1
1150080024

1. Mục tiêu

Xây mô hình pfSense bảo vệ LAN + DMZ, cấu hình WAN/LAN/DMZ/NAT/rule, kiểm thử các tình huống firewall.
2. Môi trường

VMware Workstation
pfSense CE 2.7.2
Win11 VM (quản trị)
DC, DMZ-Web, LAN-Test: chưa làm

IP

WAN: DHCP
LAN: 10.0.0.1/8
DMZ: 172.16.0.1/16
Win11: 10.0.0.100/8
DC: 10.0.0.2
DMZ-Web: 172.16.0.2
LAN-Test: 10.0.0.3

3. Các bước đã làm

Tải ISO + check SHA256
Tạo VM: FreeBSD 64-bit, 2GB RAM, 2 core, 3 card (Bridged / lan-net / dmz-net)
Cài Auto ZFS → tháo ISO
Gán em0=WAN, em1=LAN, em2=DMZ → LAN 10.0.0.1/8
Win11 gắn lan-net, IP 10.0.0.100 → ping được 10.0.0.1
Vào WebGUI (admin/pfsense) → Setup Wizard
Thêm OPT1 (DMZ) 172.16.0.1/16 (đang làm)

4. Lỗi đã gặp

Tải nhầm file SHA → tải lại .iso.gz
Chọn OS sai → FreeBSD 64-bit
Thiếu card mạng → tắt VM thêm đủ 3 card
Card sai kiểu → dùng LAN Segment
RAM thấp → tăng 2GB
Nhập sai số interface trên console
Win11 “No internet” → bình thường
Tài khoản: admin / pfsense