Nguyễn Thị Kim Liên_ 1150080024_ 11CNPM1_LAB4
2. PHIÊN BẢN MÔI TRƯỜNG THỰC HIỆN
• Phần mềm ảo hóa (Hypervisor): VMware Workstation Pro / Player.
• Hệ điều hành máy quét (Attacker): Kali Linux (Đã cài đặt sẵn gói nmap phiên bản 7.99).
• Hệ điều hành máy đích 1 (Target 1): Microsoft Windows 11 x64 (Đã cài đặt Nmap, Npcap và giao diện đồ họa Zenmap).
• Hệ điều hành máy đích 2 (Target 2): Metasploitable 2 Linux (Hệ điều hành thử nghiệm cấu hình sẵn các lỗ hổng bảo mật).
• Cấu hình mạng (Network Adapter): Chế độ Host-Only (VMnet1) để cô lập phòng Lab an toàn với mạng vật lý bên ngoài.
3. CÁCH DỰNG MÔI TRƯỜNG THỰC HÀNH
• Bước 1: Cài đặt công cụ trên các máy
	• Trên Windows 11: Tải file cài đặt chính thức từ nmap.org, chạy với quyền Administrator để cài đặt đầy đủ các thành phần gồm Nmap Core Files, Register Nmap Path, Npcap và Zenmap.
	• Trên Kali Linux: Kích hoạt card mạng sang chế độ NAT tạm thời để lấy Internet, mở Terminal và chạy lệnh nâng cấp danh sách gói sudo apt update kết hợp cài đặt công cụ thông qua lệnh sudo apt install nmap.
• Bước 2: Cấu hình mạng nội bộ an toàn (Host-Only)
	• Tắt toàn bộ các máy ảo để đảm bảo an toàn cấu hình.
	• Truy cập vào mục Settings -> Network Adapter của cả 3 máy ảo (Windows 11, Kali Linux, và Metasploitable 2), chuyển đổi tất cả đồng bộ sang chế độ mạng Host-only: A private network shared with the host (Switch ảo VMnet1).
• Bước 3: Sao lưu cấu hình phòng Lab (Snapshot)
	• Sử dụng tính năng quản lý của VMware Workstation, tiến hành chụp Snapshot cho cả 3 máy với tên gọi chung là Before-LAB4 trước khi thực hiện các bài quét chuyên sâu.
• Bước 4: Xác định thông số mạng (Địa chỉ IP)
	• Khởi động đồng thời cả 3 máy ảo lên để nhận IP động do VMware cấp.
	• Trên Windows 11: Mở Command Prompt, gõ lệnh ipconfig để lấy địa chỉ IPv4 và Subnet Mask của card mạng ảo.
	• Trên Kali Linux: Mở Terminal, gõ lệnh kiểm tra cấu hình IP.
	• Trên Metasploitable 2: Đăng nhập bằng tài khoản mặc định (msfadmin/msfadmin) và gõ lệnh ifconfig tại giao diện dòng lệnh để ghi nhận IP mục tiêu.
4. CÁC TÌNH HUỐNG ĐÃ THỰC HIỆN & KẾT QUẢ
• Tình huống 1: Kiểm tra và cập nhật ứng dụng Nmap trên Kali
	• Thao tác: Sử dụng trình quản lý gói apt để cài đặt Nmap.
	• Kết quả: Hệ thống thông báo gói phần mềm đã ở phiên bản mới nhất (nmap is already the newest version (7.99+dfsg-1kali1)), sẵn sàng sử dụng.
• Tình huống 2: Xác thực phiên bản phần mềm cài đặt
	• Thao tác: Thực thi lệnh nmap --version trong Terminal của hệ điều hành Kali Linux.
	• Kết quả: Trả về thông tin chi tiết đầy đủ của phiên bản Nmap version 7.99 kèm theo các thư viện hỗ trợ như liblua, openssl, libssh2, và libpcap.
• Tình huống 3: Thu thập thông tin mạng nội bộ trên máy ảo
	• Thao tác: Tiến hành chạy các lệnh kiểm tra cấu hình mạng trên từng dòng lệnh của hệ điều hành ảo.
	• Kết quả: Xác định thành công địa chỉ IP của các máy nằm chung trong dải mạng nội bộ nhằm chuẩn bị cho việc thực thi lệnh kiểm tra kết nối (ping) và quét cổng (nmap scan).
5. CÁC LỖI GẶP PHẢI TRONG QUÁ TRÌNH THỰC HÀNH VÀ CÁCH KHẮC PHỤC
• Lỗi 1: Nhập sai cú pháp lệnh kiểm tra IP trên Kali Linux
	• Mô tả lỗi: Khi muốn kiểm tra IP trên Terminal của Kali Linux, người thực hiện đã gõ nhầm cú pháp thành iip -br addr (thừa chữ cái "i" ở đầu). Hệ thống báo lỗi hệ thống: bash: iip: command not found (Không tìm thấy lệnh).
	• Cách khắc phục: Thực hiện gõ lại chính xác cú pháp chuẩn của hệ điều hành Linux là ip -br addr, hoặc sử dụng các lệnh thay thế tương đương như ip a hay ifconfig để hệ thống hiển thị đúng danh sách card mạng.
• Lỗi 2: Nhầm lẫn giữa các nền tảng ảo hóa khi thiết lập dải IP mạng Host-Only
	• Mô tả lỗi: Tài liệu hướng dẫn chi tiết theo các bước của phần mềm VirtualBox (sử dụng dải IP mặc định 192.168.56.0/24), trong khi môi trường thực tế của sinh viên đang triển khai 100% trên phần mềm VMware Workstation.
	• Cách khắc phục: Chuyển đổi linh hoạt sang cấu hình card mạng VMnet1 (Host-only) của VMware. Chấp nhận các dải IP cấp phát tự động theo định dạng riêng của VMware (ví dụ: 192.168.x.x) thay vì cố định theo số .56.x của VirtualBox, miễn là đảm bảo cả 3 máy ảo đều sử dụng chung một chế độ mạng này để thông kết nối với nhau.
