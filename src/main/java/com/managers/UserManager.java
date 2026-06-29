package com.managers;

import com.database.DBConnection;
import com.enumeration.Role;
import com.interfaces.Managerable;
import com.model.User;
import com.model.Mahasiswa;
import com.model.Dosen;
import com.model.Security;
import com.model.Admin;
import com.model.Staff;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.exception.ValidationException;

// Abstraction & Encapsulation
public class UserManager implements Managerable{
    private ArrayList<User> users;
    private HashMap<String, User> userMap;
    private DBConnection dbConnection;

    public UserManager() {
        this.users = new ArrayList<>();
        this.userMap = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllUsersFromDB();
    }
    
    //Load Data Dari DB
    private void loadAllUsersFromDB() {
        //clear data lama
        users.clear();
        userMap.clear();
        //sql gabungan tabel user
        String sql = "SELECT u.user_id, u.name, u.username, u.password, u.role, " + "m.nim, m.fakultas, m.jurusan, m.kelas, " + "d.nip, d.bidang, " + "st.staff_id, st.bagian AS staff_bagian, " + "a.admin_id, " + "sc.security_id, sc.bagian AS security_bagian " + "FROM users u " + "LEFT JOIN mahasiswa m ON u.user_id = m.user_id " + "LEFT JOIN dosen d ON u.user_id = d.user_id " + "LEFT JOIN staff st ON u.user_id = st.user_id " + "LEFT JOIN admin a ON u.user_id = a.user_id " + "LEFT JOIN security sc ON u.user_id = sc.user_id";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            //looping data
            while (rs.next()) {
                User user = buildUserFromResultSet(rs);
                //masukan user ke list dan map
                if (user != null) {
                    users.add(user);
                    userMap.put(user.getUserId(), user);
                }
            }
            System.out.println("Data user berhasil diload dari database dengan jumlah: " + users.size());
        } catch (SQLException e) {
            System.out.println("Gagal load users " + e.getMessage());
        }
    }
    
    //Mapping data ke object
    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String name = rs.getString("name");
        String username = rs.getString("username");
        String password = rs.getString("password");
        Role role = Role.valueOf(rs.getString("role"));
        
        //cek role lalu return object yg sesuai
        switch (role) {
            case MAHASISWA:
                return new Mahasiswa(userId, name, username, password, rs.getString("nim"), rs.getString("fakultas"), rs.getString("jurusan"), rs.getString("kelas"));
 
            case DOSEN:
                return new Dosen(userId, name, username, password, rs.getString("nip"), rs.getString("bidang"));
 
            case STAFF:
                return new Staff(userId, name, username, password, rs.getString("staff_id"), rs.getString("staff_bagian"));
 
            case ADMIN:
                return new Admin(userId, name, username, password, rs.getString("admin_id"));
 
            case SECURITY:
                return new Security(userId, name, username, password, rs.getString("security_id"), rs.getString("security_bagian"));
 
            default:
                return null;
        }
    }
    // Polymorphism (Method Overriding Dari Interface Managerable)
    @Override
    public void add(Object obj) {
         if (obj instanceof User) {
            addUser((User) obj);
        }
    }
    // Polymorphism (Method Overriding Dari Interface Managerable)
    @Override
    public void delete(String id) {
        deleteUser(id);
    }
    
    //Method Ini Untuk Mengambil Data User berdasarkan ID
    public User getUserById(String userId) {
        //cek apakah user ada
        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        }
        return null;
    }

    //Method Ini Untuk Mengambil Data User berdasarkan Role
    public ArrayList<User> getAllUsers(){
        return users;
    }
    
    //Method Ini Untuk Mengambil Data User berdasarkan Role
    public ArrayList<User> getUsersByRole(Role role) {
        //Buat ArrayList untuk menyimpan user
        ArrayList<User> result = new ArrayList<>();
        //Looping data user
        for (User user : users) {
            //cek apakah user sesuai dengan role
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        //return hasil user sesuai role
        return result;
    }

    // Polymorphism (Method Overriding Dari Interface Managerable)
    @Override
    public Object findById(String id) {
        //Mencari data user berdasarkan ID
        return getUserById(id);
    }
    
    public void addUser(User user){
         String sql = "INSERT INTO users (user_id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.executeUpdate();
 
            users.add(user);
            userMap.put(user.getUserId(), user);
            System.out.println("User " + user.getName() + " berhasil ditambahkan.");
        } catch (SQLException e) {
            System.out.println("User gagal ditambahkan" + e.getMessage());
        }
    }
    
    // Method Overloading: Untuk Edit Username
    public void editUser(User currentUser, String oldPassword, String newUsername) throws ValidationException {
        if (oldPassword == null || oldPassword.trim().isEmpty() || newUsername == null || newUsername.trim().isEmpty()) {
            throw new ValidationException("Password Lama Dan Username Baru Wajib Diisi!");
        }
        if (!currentUser.checkPassword(oldPassword)) {
            throw new ValidationException("Password lama yang Anda masukkan salah.");
        }
        if (newUsername.length() < 4) {
            throw new ValidationException("Username harus memiliki minimal 4 karakter.");
        }
        if (newUsername.equals(currentUser.getUsername())) {
            throw new ValidationException("Username yang baru anda tulis sama seperti username sebelumnya.");
        }
        if (newUsername.contains(" ")) {
            throw new ValidationException("Username tidak boleh mengandung spasi.");
        }
        for (User u : users) {
            if (!u.getUserId().equals(currentUser.getUserId()) && u.getUsername().equalsIgnoreCase(newUsername)) {
                throw new ValidationException("Username sudah digunakan oleh pengguna lain.");
            }
        }

        String sql = "UPDATE users SET username = ? WHERE user_id = ?";
        try {
           Connection conn = dbConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setString(1, newUsername);
           ps.setString(2, currentUser.getUserId());
           ps.executeUpdate();
           
           User user = userMap.get(currentUser.getUserId());
            if (user != null) {
                user.setUsername(newUsername);
            }
            System.out.println("Username berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println("Gagal Update Data User: " + e.getMessage());
            throw new ValidationException("Terjadi kesalahan sistem saat memperbarui data.");
        }
    }
    
    // Method Overloading: Untuk Ganti Password
    public void editUser(User currentUser, String oldPassword, String newPassword, String confirmPassword) throws ValidationException {
        if (oldPassword == null || oldPassword.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new ValidationException("Semua Kolom Password Wajib Diisi!");
        }
        if (!currentUser.checkPassword(oldPassword)) {
            throw new ValidationException("Password lama yang Anda masukkan salah.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Password baru dan konfirmasi tidak cocok.");
        }
        if (newPassword.length() < 8) {
            throw new ValidationException("Password baru minimal harus 8 karakter.");
        }
        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new ValidationException("Password baru harus mengandung minimal 1 karakter spesial.");
        }
        if (newPassword.equals(currentUser.getPassword())) {
            throw new ValidationException("Password yang baru anda tulis sama seperti password sebelumnya.");
        }
        if (newPassword.contains(" ")) {
            throw new ValidationException("Password tidak boleh mengandung spasi.");
        }

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try {
           Connection conn = dbConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setString(1, newPassword);
           ps.setString(2, currentUser.getUserId());
           ps.executeUpdate();
           
           User user = userMap.get(currentUser.getUserId());
            if (user != null) {
                user.setPassword(newPassword);
            }
            System.out.println("Password berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println("Gagal Update Data User: " + e.getMessage());
            throw new ValidationException("Terjadi kesalahan sistem saat memperbarui data.");
        }
    }
    
    public void deleteUser(String userID){
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userID);
            ps.executeUpdate();
 
            User user = userMap.remove(userID);
            if (user != null) {
                users.remove(user);
                System.out.println("User dengan id: " + userID + " berhasil dihapus.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal hapus user: " + e.getMessage());
        }
    }
    
    public void reload() {
        loadAllUsersFromDB();
    }
}