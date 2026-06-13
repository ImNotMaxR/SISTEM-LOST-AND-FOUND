/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemlostfound;

/**
 *
 * @author MaxR
 */
import com.enumeration.Role;
import com.model.Admin;
import com.model.Security;
import com.model.User;
import com.service.AuthService;
import com.util.MissionUtil;

public class AppController {
    private AuthService authService;
 
    public AppController(AuthService authService) {
        this.authService = authService;
    }
 
    // Tampilkan menu sesuai role user yang login
    public void showMenu() {
        User user = authService.getCurrentUser();
        if (user == null) return;
 
        // instanceof dipakai di sini untuk routing menu
        if (user instanceof Admin) {
            showAdminMenu((Admin) user);
        } else if (user instanceof Security) {
            showSecurityMenu((Security) user);
        } else {
            showUserMenu(user);
        }
    }
 
    // ======== MENU ADMIN ========
    private void showAdminMenu(Admin admin) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU ADMIN =====");
            System.out.println("Halo, " + admin.getName());
            System.out.println("1.  Lihat semua laporan");
            System.out.println("2.  Validasi laporan (PENDING -> VALID/DITOLAK)");
            System.out.println("3.  Lihat semua klaim");
            System.out.println("4.  Proses klaim (PENDING -> VALID/DITOLAK)");
            System.out.println("5.  Lihat semua user");
            System.out.println("6.  Lihat semua item");
            System.out.println("7.  Lihat storage record");
            System.out.println("8.  Edit profil saya");
            System.out.println("9.  Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": System.out.println("[TODO] Tampilkan semua laporan"); break;
                case "2": System.out.println("[TODO] Validasi laporan"); break;
                case "3": System.out.println("[TODO] Tampilkan semua klaim"); break;
                case "4": System.out.println("[TODO] Proses klaim"); break;
                case "5": System.out.println("[TODO] Tampilkan semua user"); break;
                case "6": System.out.println("[TODO] Tampilkan semua item"); break;
                case "7": System.out.println("[TODO] Tampilkan storage record"); break;
                case "8": System.out.println("[TODO] Edit profil"); break;
                case "9":
                    authService.logout();
                    running = false;
                    break;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
 
    // ======== MENU SECURITY ========
    private void showSecurityMenu(Security security) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU SECURITY =====");
            System.out.println("Halo, " + security.getName());
            System.out.println("1. Buat laporan barang ditemukan");
            System.out.println("2. Lihat laporan saya");
            System.out.println("3. Kelola storage barang");
            System.out.println("4. Lihat barang yang valid dan bisa diklaim");
            System.out.println("5. Edit profil saya");
            System.out.println("6. Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": System.out.println("[TODO] Buat FoundReport"); break;
                case "2": System.out.println("[TODO] Lihat laporan security"); break;
                case "3": System.out.println("[TODO] Kelola storage"); break;
                case "4": System.out.println("[TODO] Lihat barang valid"); break;
                case "5": System.out.println("[TODO] Edit profil"); break;
                case "6":
                    authService.logout();
                    running = false;
                    break;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
 
    // ======== MENU USER BIASA (Mahasiswa, Dosen, Staff) ========
    private void showUserMenu(User user) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU USER =====");
            System.out.println("Halo, " + user.getName() + " (" + user.getRole() + ")");
            System.out.println("1. Buat laporan barang hilang");
            System.out.println("2. Lihat laporan saya");
            System.out.println("3. Edit laporan saya");
            System.out.println("4. Lihat barang yang ditemukan (valid)");
            System.out.println("5. Ajukan klaim barang");
            System.out.println("6. Lihat status klaim saya");
            System.out.println("7. Edit profil saya");
            System.out.println("8. Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": System.out.println("[TODO] Buat LostReport + Item baru"); break;
                case "2": System.out.println("[TODO] Lihat laporan user"); break;
                case "3": System.out.println("[TODO] Edit laporan (cek 30 menit)"); break;
                case "4": System.out.println("[TODO] Lihat barang valid"); break;
                case "5": System.out.println("[TODO] Ajukan klaim"); break;
                case "6": System.out.println("[TODO] Lihat status klaim"); break;
                case "7": System.out.println("[TODO] Edit profil"); break;
                case "8":
                    authService.logout();
                    running = false;
                    break;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
}
