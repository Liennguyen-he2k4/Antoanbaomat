package week_02;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserDao {
    private static final int ITERATIONS = 120_000;
    private UserDao() {}

    public static void register(String username, char[] password) throws SQLException {
        if (!username.matches("[A-Za-z0-9_.]{3,40}")) {
            throw new IllegalArgumentException("Username 3-40 ký tự, chỉ gồm chữ, số, dấu _ hoặc dấu chấm.");
        }
        if (password.length < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        }
        byte[] salt = CipherUtil.randomBytes(16);
        byte[] hash = CipherUtil.pbkdf2(password, salt, ITERATIONS, 256);
        String sql = "INSERT INTO dbo.app_users(username, salt, password_hash) VALUES (?, ?, ?)";
        try (Connection con = DbConfig.open(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setBytes(2, salt);
            ps.setBytes(3, hash);
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) {
                throw new IllegalArgumentException("Username đã tồn tại, hãy chọn tên khác.");
            }
            throw ex;
        }
    }

    public static boolean login(String username, char[] password) throws SQLException {
        String sql = "SELECT salt, password_hash FROM dbo.app_users WHERE username = ?";
        try (Connection con = DbConfig.open(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                byte[] salt = rs.getBytes(1);
                byte[] stored = rs.getBytes(2);
                byte[] actual = CipherUtil.pbkdf2(password, salt, ITERATIONS, 256);
                return MessageDigest.isEqual(stored, actual);
            }
        }
    }

    public static String newTicket() {
        return CipherUtil.hex(CipherUtil.randomBytes(16));
    }
}