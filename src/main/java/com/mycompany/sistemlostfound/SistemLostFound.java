package com.mycompany.sistemlostfound;

import com.database.DBConnection;
import com.database.DBInitializer;
import com.frame.LoginFrame;
import com.model.User;
import com.service.AuthService;
import com.util.MissionUtil;
import java.sql.*;
import javax.swing.SwingUtilities;

public class SistemLostFound {
    public static void main(String[] args) {
        DBConnection db = DBConnection.getInstance();
        Connection conn = db.getConnection();
        if (conn == null) {
            System.out.println("Gagal konek ke database. Pastikan Laragon sudah berjalan.");
            return;
        }
        System.out.println("Koneksi database sukses.");
        
        // 2. Inisialisasi database (buat tabel + data dummy jika belum ada)
        DBInitializer initializer = new DBInitializer();
        initializer.initialize();
 
        // 3. Jalankan aplikasi
        AuthService authService = new AuthService();
        runLoginMenu(authService);
 
        // 4. Tutup koneksi saat program selesai
        db.closeConnection();
    }
    
    private static void runLoginMenu(AuthService authService) {
        SwingUtilities.invokeLater(() -> {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        });
    }
}