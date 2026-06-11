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

public class UserManager implements Managerable{
    private ArrayList<User> users;
    private HashMap<String, User> userMap;
    private DBConnection dbConnection;

    public UserManager() {
        this.users        = new ArrayList<>();
        this.userMap      = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllUsersFromDB();
    }
    
    private void loadAllUsersFromDB() {
        users.clear();
        userMap.clear();
        String sql = "SELECT u.user_id, u.name, u.username, u.password, u.role, "
                   + "m.nim, m.fakultas, m.jurusan, m.kelas, "
                   + "d.nip, d.bidang, "
                   + "st.staff_id, st.bagian AS staff_bagian, "
                   + "a.admin_id, "
                   + "sc.security_id, sc.bagian AS security_bagian "
                   + "FROM users u "
                   + "LEFT JOIN mahasiswa m ON u.user_id = m.user_id "
                   + "LEFT JOIN dosen d ON u.user_id = d.user_id "
                   + "LEFT JOIN staff st ON u.user_id = st.user_id "
                   + "LEFT JOIN admin a ON u.user_id = a.user_id "
                   + "LEFT JOIN security sc ON u.user_id = sc.user_id";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                User user = buildUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                    userMap.put(user.getUserId(), user);
                }
            }
            System.out.println("Data user berhasil dimuat dari database dengan jumlah: " + users.size());
        } catch (SQLException e) {
            System.out.println("Gagal load users: " + e.getMessage());
        }
    }
    
    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String userId   = rs.getString("user_id");
        String name     = rs.getString("name");
        String username = rs.getString("username");
        String password = rs.getString("password");
        Role role       = Role.valueOf(rs.getString("role"));
 
        switch (role) {
            case MAHASISWA:
                return new Mahasiswa(userId, name, username, password,
                        rs.getString("nim"),
                        rs.getString("fakultas"),
                        rs.getString("jurusan"),
                        rs.getString("kelas"));
 
            case DOSEN:
                return new Dosen(userId, name, username, password,
                        rs.getString("nip"),
                        rs.getString("bidang"));
 
            case STAFF:
                return new Staff(userId, name, username, password,
                        rs.getString("staff_id"),
                        rs.getString("staff_bagian"));
 
            case ADMIN:
                return new Admin(userId, name, username, password,
                        rs.getString("admin_id"));
 
            case SECURITY:
                return new Security(userId, name, username, password,
                        rs.getString("security_id"),
                        rs.getString("security_bagian"));
 
            default:
                return null;
        }
    }
    
    @Override
    public void add(Object obj) {
         if (obj instanceof User) {
            addUser((User) obj);
        }
    }

    @Override
    public void delete(String id) {
        deleteUser(id);
    }
    
    public User getUserById(String userId) {
        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        }
        return null;
    }
    
    public ArrayList<User> getAllUsers(){
        return users;
    }
    
    public ArrayList<User> getUsersByRole(Role role) {
        ArrayList<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }
    
    @Override
    public Object findById(String id) {
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
    
    public void editUser(User currentUser, String targetUserId, String newUsername, String newPassword){
        if (!currentUser.getUserId().equals(targetUserId)) {
            System.out.println("Kamu tidak bisa mengedit akun orang lain.");
            return;
        }
        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
        try {
           Connection conn = dbConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setString(1, newUsername);
           ps.setString(2, newPassword);
           ps.setString(3, targetUserId);
           ps.executeUpdate();
           
           User user = userMap.get(targetUserId);
            if (user != null) {
                user.setUsername(newUsername);
                user.setPassword(newPassword);
            }
            
            System.out.println("Akun berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println("Gagal Update Data User" + e.getMessage());
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