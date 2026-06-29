package com.service;

import com.exception.ValidationException;

import com.database.DBConnection;
import com.enumeration.Role;
import com.model.Admin;
import com.model.Dosen;
import com.model.Mahasiswa;
import com.model.Security;
import com.model.Staff;
import com.model.User;
import java.sql.*;

public class AuthService {
    private static User currentUser;
    private static DBConnection dbConnection;
    private static String loginError;
    
     public AuthService() {
        this.currentUser  = null;
        this.dbConnection = DBConnection.getInstance();
    }
 
    public static User getCurrentUser() {
        return currentUser;
    }
 
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static String getLoginError() {
        return loginError;
    }
 
    // Query DB, cocokkan username dan password
    // Buat object sesuai role menggunakan metode dynamic binding.
    public static User login(String username, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Username dan Password Wajib diisi.");
        }
        String sql =
            "SELECT u.user_id, u.name, u.username, u.password, u.role, " +
            "m.nim, m.fakultas, m.jurusan, m.kelas, " +
            "d.nip, d.bidang, " +
            "st.staff_id, st.bagian AS staff_bagian, " +
            "a.admin_id, " +
            "sc.security_id, sc.bagian AS security_bagian " +
            "FROM users u " +
            "LEFT JOIN mahasiswa m ON u.user_id = m.user_id " +
            "LEFT JOIN dosen d ON u.user_id = d.user_id " +
            "LEFT JOIN staff st ON u.user_id = st.user_id " +
            "LEFT JOIN admin a ON u.user_id = a.user_id " +
            "LEFT JOIN security sc ON u.user_id = sc.user_id " +
            "WHERE u.username = ?";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!storedPassword.equals(password)) {
                    throw new ValidationException("Password Anda Salah");
                }
 
                User user = buildUserFromResultSet(rs);
                if (user != null) {
                    AuthService.currentUser = user;
                    // Panggil login() dari interface AuthServices
                    user.login();
                }
                return user;
            } else {
                throw new ValidationException("Username Anda Tidak Ditemukan.");
            }
 
        } catch (SQLException e) {
            System.out.println("Gagal Login: " + e.getMessage());
            throw new ValidationException("Terjadi kesalahan sistem saat proses login: " + e.getMessage());
        }
    }
 
    // Dynamic binding return type User, object Mahasiswa/Dosen/dll
    private static User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String userId   = rs.getString("user_id");
        String name     = rs.getString("name");
        String username    = rs.getString("username");
        String password = rs.getString("password");
        Role role       = Role.valueOf(rs.getString("role"));
 
        switch (role) {
            case MAHASISWA:
                return new Mahasiswa(userId, name, username, password,
                        rs.getString("nim"), rs.getString("fakultas"),
                        rs.getString("jurusan"), rs.getString("kelas"));
            case DOSEN:
                return new Dosen(userId, name, username, password,
                        rs.getString("nip"), rs.getString("bidang"));
            case STAFF:
                return new Staff(userId, name, username, password,
                        rs.getString("staff_id"), rs.getString("staff_bagian"));
            case ADMIN:
                return new Admin(userId, name, username, password,
                        rs.getString("admin_id"));
            case SECURITY:
                return new Security(userId, name, username, password,
                        rs.getString("security_id"), rs.getString("security_bagian"));
            default:
                return null;
        }
    }
 
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        } else {
            System.out.println("Tidak ada user yang sedang login.");
        }
    }
 
    public boolean checkRole(Role role) {
        if (currentUser == null) return false;
        return currentUser.getRole() == role;
    }
 
    public boolean isAdmin() {
        return currentUser instanceof Admin;
    }
 
    public boolean isSecurity() {
        return currentUser instanceof Security;
    }
}
