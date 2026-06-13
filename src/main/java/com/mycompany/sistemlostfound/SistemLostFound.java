package com.mycompany.sistemlostfound;

import com.database.DBConnection;
import com.database.DBInitializer;
import com.model.User;
import com.service.AuthService;
import com.util.MissionUtil;
import java.sql.*;

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
        System.out.println("\n=====================================");
        System.out.println("  SISTEM INFORMASI LOST & FOUND");
        System.out.println("  Telkom University");
        System.out.println("=====================================");
 
        boolean running = true;
        while (running) {
            System.out.println("\n1. Login");
            System.out.println("2. Keluar");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1":
                    System.out.print("Username: ");
                    String username = MissionUtil.getUserInput();
                    System.out.print("Password: ");
                    String password = MissionUtil.getUserInput();
 
                    User user = authService.login(username, password);
                    if (user != null) {
                        // Tampilkan menu sesuai role
                        AppController controller = new AppController(authService);
                        controller.showMenu();
                    }
                    break;
 
                case "2":
                    System.out.println("Terima kasih. Program selesai.");
                    running = false;
                    break;
 
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
}