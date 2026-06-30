package com.mycompany.sistemlostfound;

import com.database.DBConnection;
import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.enumeration.Role;
import com.managers.ClaimManager;
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.managers.StorageManager;
import com.managers.UserManager;
import com.model.Admin;
import com.model.Claim;
import com.model.Category;
import com.model.FoundReport;
import com.model.Item;
import com.model.LostReport;
import com.model.Report;
import com.model.Security;
import com.model.StorageRecord;
import com.model.User;
import com.model.VerificationDocument;
import com.service.AuthService;
import com.exception.ValidationException;
import java.io.*;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import com.util.MissionUtil;

public class AppController {
    private AuthService authService;
    private UserManager userManager;
    private ItemManager itemManager;
    private ReportManager reportManager;
    private ClaimManager claimManager;
    private StorageManager storageManager;
    private DBConnection dbConnection;
 
    public AppController(AuthService authService) {
        this.authService   = authService;
        this.userManager   = new UserManager();
        this.itemManager   = new ItemManager();
        this.reportManager = new ReportManager();
        this.claimManager  = new ClaimManager();
        this.storageManager = new StorageManager();
        this.dbConnection  = DBConnection.getInstance();
    }
 
    public void showMenu() {
        User user = authService.getCurrentUser();
        if (user == null) return;
 
        if (user instanceof Admin) {
            showAdminMenu((Admin) user);
        } else if (user instanceof Security) {
            showSecurityMenu((Security) user);
        } else {
            showUserMenu(user);
        }
    }
 
    // ================================================================
    // MENU USER BIASA
    // ================================================================
    /**
     * PENJELASAN METHOD showUserMenu:
     * Method ini adalah perulangan menu utama untuk User biasa menggunakan antarmuka command-line (CLI).
     * Menggunakan while(running) supaya menu tidak langsung keluar setelah eksekusi.
     * Switch-case digunakan untuk memanggil method lain berdasarkan input nomor dari user.
     */
    private void showUserMenu(User user) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU USER =====");
            System.out.println("Halo, " + user.getName() + " (" + user.getRole() + ")");
            System.out.println("1. Buat laporan barang hilang");
            System.out.println("2. Lihat laporan saya");
            System.out.println("3. Edit laporan saya");
            System.out.println("4. Lihat barang yang ditemukan");
            System.out.println("5. Ajukan klaim barang");
            System.out.println("6. Lihat status klaim saya");
            System.out.println("7. Edit profil saya");
            System.out.println("8. Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": buatLostReport(user);      break;
                case "2": lihatLaporanUser(user);    break;
                case "3": editLaporanUser(user);     break;
                case "4": lihatBarangDitemukan();    break;
                case "5": ajukanKlaim(user);         break;
                case "6": lihatStatusKlaim(user);    break;
                case "7": editProfil(user);          break;
                case "8": authService.logout(); running = false; break;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }
 
    // ----------------------------------------------------------------
    // 1. BUAT LAPORAN BARANG HILANG
    // Item dibuat sekaligus saat laporan dibuat
    // ----------------------------------------------------------------
    private void buatLostReport(User user) {
        System.out.println("\n--- Buat Laporan Barang Hilang ---");
 
        System.out.print("Nama barang       : ");
        String namaBarang = MissionUtil.getUserInput();
        if (namaBarang.isEmpty()) { System.out.println("Nama barang tidak boleh kosong."); return; }
 
        System.out.print("Deskripsi barang  : ");
        String deskripsiBarang = MissionUtil.getUserInput();
 
        System.out.print("Lokasi hilang     : ");
        String lokasiHilang = MissionUtil.getUserInput();
 
        System.out.print("Deskripsi laporan : ");
        String deskripsiLaporan = MissionUtil.getUserInput();
 
        // Pilih kategori dari DB
        ArrayList<Category> categories = getAllCategories();
        if (categories.isEmpty()) { System.out.println("Tidak ada kategori tersedia."); return; }
 
        System.out.println("Pilih kategori:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName() + " *butuh dokumen saat klaim");
        }
        System.out.print("Nomor kategori: ");
        int pilihKat;
        try {
            pilihKat = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilihKat < 0 || pilihKat >= categories.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        Category kategori = categories.get(pilihKat);
 
        // Tambah foto barang (opsional)
        System.out.print("Path foto barang (Enter untuk skip): ");
        String fotoPath = MissionUtil.getUserInput().trim().replace("\"", "");
        String photoPath = fotoPath.isEmpty() ? null : fotoPath;
 
        // Generate ID
        String itemId   = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
 
        // Buat Item sekaligus dengan laporan
        Item item = new Item(itemId, namaBarang, deskripsiBarang, kategori, lokasiHilang);
        
        // MENGGUNAKAN TRY-CATCH: Ini untuk menangkap error (ValidationException)
        // jika data barang tidak memenuhi syarat (misal kosong) saat ditambahkan ke sistem (ItemManager).
        try {
            itemManager.addItem(item);
        } catch (ValidationException e) {
            System.out.println("Gagal menambahkan item: " + e.getMessage());
            return; // Jika gagal, hentikan proses pembuatan laporan barang hilang.
        }
 
        LostReport report = new LostReport(reportId, user, item, deskripsiLaporan, lokasiHilang);
        if (!fotoPath.isEmpty()) {
            report.addEvidence(fotoPath);
        }
        reportManager.addReport(report);
        
        System.out.println("\nLaporan berhasil dibuat.");
        System.out.println("Report ID : " + reportId);
        System.out.println("Item ID   : " + itemId);
        System.out.println("Status    : PENDING, menunggu validasi Admin.");
        System.out.println("Laporan bisa diedit selama 30 menit ke depan.");
    }
 
    // ----------------------------------------------------------------
    // 2. LIHAT LAPORAN USER
    // ----------------------------------------------------------------
    /**
     * PENJELASAN METHOD lihatLaporanUser:
     * Menampilkan semua laporan kehilangan yang khusus dibuat oleh pengguna (user) ini saja.
     * Dilakukan pencarian (looping) ke semua laporan dan difilter dengan mencocokkan user_id 
     * milik laporan dengan user_id pengguna yang sedang menggunakan sistem.
     */
    private void lihatLaporanUser(User user) {
        System.out.println("\n--- Laporan Saya ---");
        ArrayList<LostReport> lostReports = reportManager.getLostReports();
        boolean ada = false;
 
        for (LostReport lr : lostReports) {
            if (lr.getUser().getUserId().equals(user.getUserId())) {
                lr.displayReport();
                ada = true;
            }
        }
 
        if (!ada) System.out.println("Kamu belum memiliki laporan.");
    }
 
    // ----------------------------------------------------------------
    // 3. EDIT LAPORAN USER (hanya dalam 30 menit)
    // ----------------------------------------------------------------
    /**
     * PENJELASAN METHOD editLaporanUser:
     * Berfungsi membiarkan user mengedit deskripsi laporan atau mengganti fotonya.
     * Logika utamanya memeriksa status `.isEditable()` yang memiliki rentang waktu 30 menit.
     * Jika sudah lewat waktu, aplikasi memblokir pengeditan.
     */
    private void editLaporanUser(User user) {
        System.out.println("\n--- Edit Laporan ---");
 
        ArrayList<LostReport> milikSaya = new ArrayList<>();
        for (LostReport lr : reportManager.getLostReports()) {
            if (lr.getUser().getUserId().equals(user.getUserId())) {
                milikSaya.add(lr);
            }
        }
 
        if (milikSaya.isEmpty()) { System.out.println("Kamu tidak punya laporan."); return; }
 
        System.out.println("Laporan kamu:");
        for (int i = 0; i < milikSaya.size(); i++) {
            LostReport lr = milikSaya.get(i);
            System.out.println((i + 1) + ". [" + lr.getStatus() + "] "
                    + lr.getItem().getName()
                    + " | ID: " + lr.getReportId()
                    + (lr.isEditable() ? " (bisa diedit)" : " (waktu edit habis)"));
        }
 
        System.out.print("Pilih nomor laporan: ");
        int pilih;
        try {
            pilih = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilih < 0 || pilih >= milikSaya.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        LostReport target = milikSaya.get(pilih);
 
        if (!target.isEditable()) {
            System.out.println("Waktu edit sudah habis. Laporan tidak bisa diubah.");
            return;
        }
 
        System.out.println("1. Edit deskripsi laporan");
        System.out.println("2. Tambah/ganti foto barang");
        System.out.print("Pilih: ");
        String pilihanEdit = MissionUtil.getUserInput();
 
        if (pilihanEdit.equals("1")) {
            System.out.print("Deskripsi baru: ");
            String descBaru = MissionUtil.getUserInput();
            target.editReport(descBaru);
            updateReportDescDB(target.getReportId(), descBaru);
        }else if (pilihanEdit.equals("2")) {
            System.out.print("Path foto baru: ");
            String fotoBaru = MissionUtil.getUserInput().trim().replace("\"", "");
            if (!fotoBaru.isEmpty()) {
                target.addEvidence(fotoBaru);
                updateReportPhotoDB(target.getReportId(), target.getPhotoPath());
                System.out.println("Foto berhasil diperbarui: " + target.getPhotoPath());
            }
        } else {
            System.out.println("Pilihan tidak valid.");
        }
    }
 
    // ----------------------------------------------------------------
    // 4. LIHAT BARANG DITEMUKAN (hanya yang VALID)
    // ----------------------------------------------------------------
    /**
     * PENJELASAN METHOD lihatBarangDitemukan:
     * Method ini berguna bagi user yang kehilangan barang agar bisa mencari
     * barang temuan yang laporan temuannya sudah di-"VALIDASI" oleh pihak Admin.
     * Ini menjamin bahwa laporan penemuan barang (FoundReport) adalah asli.
     */
    private void lihatBarangDitemukan() {
        System.out.println("\n--- Barang yang Ditemukan (Tervalidasi) ---");
        ArrayList<Report> validReports = reportManager.getValidReports();
        boolean ada = false;
 
        for (Report r : validReports) {
            if (r instanceof FoundReport) {
                r.displayReport();
                ada = true;
            }
        }
 
        if (!ada) System.out.println("Belum ada barang ditemukan yang tervalidasi.");
    }
 
    // ----------------------------------------------------------------
    // 5. AJUKAN KLAIM
    // ----------------------------------------------------------------
    private void ajukanKlaim(User user) {
        System.out.println("\n--- Ajukan Klaim Barang ---");
 
        // Kumpulkan semua item yang berstatus DITEMUKAN dari report yang VALID
        ArrayList<Item> bisaDiklaim   = new ArrayList<>();
        ArrayList<String> reportIds   = new ArrayList<>();
 
        for (Report r : reportManager.getValidReports()) {
            Item item = r.getItem();
            if (item.getStatus() == ItemStatus.DITEMUKAN
                    && !claimManager.hasActiveClaim(item.getItemID())
                    && !bisaDiklaim.contains(item)) {
                bisaDiklaim.add(item);
                reportIds.add(r.getReportId());
            }
        }
 
        // Tambah dari claimable lost reports
        for (LostReport lr : reportManager.getClaimableLostReports()) {
            if (!claimManager.hasActiveClaim(lr.getItem().getItemID())
                    && !bisaDiklaim.contains(lr.getItem())) {
                bisaDiklaim.add(lr.getItem());
                reportIds.add(lr.getReportId());
            }
        }
 
        if (bisaDiklaim.isEmpty()) {
            System.out.println("Tidak ada barang yang bisa diklaim saat ini.");
            return;
        }
 
        System.out.println("Barang yang bisa diklaim:");
        for (int i = 0; i < bisaDiklaim.size(); i++) {
            Item item = bisaDiklaim.get(i);
            System.out.println((i + 1) + ". " + item.getName());
            System.out.println("Kategori  : " + (item.getCategory() != null ? item.getCategory().getName() : "-") + " (Wajib Bawa Dokumen)");
        }
 
        System.out.print("Pilih nomor barang: ");
        int pilih;
        try {
            pilih = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilih < 0 || pilih >= bisaDiklaim.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        Item itemDiklaim = bisaDiklaim.get(pilih);
        String relatedReportId = reportIds.get(pilih);
        String claimId = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
 
        Claim claim = new Claim(claimId, user, itemDiklaim, relatedReportId);
 
        // Jika kategori butuh verifikasi, minta dokumen
        if (itemDiklaim.getCategory() != null) {
            System.out.println("Kategori ini membutuhkan dokumen verifikasi.");
            System.out.print("Tipe dokumen (contoh: STNK, KTP, KTM): ");
            String tipeDoc = MissionUtil.getUserInput();
            System.out.print("Path file dokumen: ");
            String pathDoc = MissionUtil.getUserInput();
            System.out.print("Keterangan dokumen: ");
            String ketDoc = MissionUtil.getUserInput();
 
            String docId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String fileDoc = pathDoc.trim().replace("\"", "");
            VerificationDocument doc = new VerificationDocument(docId, tipeDoc, fileDoc.isEmpty() ? null : fileDoc, ketDoc);
            claim.addDocument(doc);
        }
 
        // MENGGUNAKAN TRY-CATCH: Menangkap exception dari proses penambahan klaim di ClaimManager.
        // Jika klaim sudah ada, status barang salah, atau dokumen tidak lengkap, sistem akan 
        // melemparkan pesan error yang ditangkap di sini, sehingga program tidak crash (berhenti paksa).
        try {
            claimManager.addClaim(claim);
            System.out.println("Klaim berhasil diajukan!");
        } catch (ValidationException e) {
            System.out.println("Gagal mengajukan klaim: " + e.getMessage());
        }
    }
 
    // ----------------------------------------------------------------
    // 6. LIHAT STATUS KLAIM USER
    // ----------------------------------------------------------------
    private void lihatStatusKlaim(User user) {
        System.out.println("\n--- Status Klaim Saya ---");
        boolean ada = false;
 
        // ClaimManager tidak punya getClaimsByUser, filter manual
        for (Claim c : claimManager.getAllClaims()) {
            if (c.getUser().getUserId().equals(user.getUserId())) {
                c.displayClaim();
                ada = true;
            }
        }
 
        if (!ada) System.out.println("Kamu belum mengajukan klaim.");
    }
 
    // ----------------------------------------------------------------
    // 7. EDIT PROFIL
    // ----------------------------------------------------------------
    /**
     * PENJELASAN METHOD editProfil:
     * CLI untuk update profil (Ganti Username atau Password).
     * Memiliki dua mode (if/else), dan meneruskan input ke method editUser di UserManager.
     * MENGGUNAKAN TRY-CATCH: karena proses validasi di UserManager (seperti cek panjang password)
     * akan melemparkan exception, sehingga pesan gagal di print menggunakan System.out.println().
     */
    private void editProfil(User user) {
        System.out.println("\n--- Edit Profil ---");
        System.out.println("Username saat ini : " + user.getUsername());
        
        System.out.println("1. Edit Username");
        System.out.println("2. Ganti Password");
        System.out.println("0. Batal");
        System.out.print("Pilih: ");
        String pilihan = MissionUtil.getUserInput();
        
        if (pilihan.equals("1")) {
            System.out.print("Password lama untuk konfirmasi: ");
            String oldPass = MissionUtil.getUserInput();
            System.out.print("Username baru: ");
            String newUsername = MissionUtil.getUserInput();
            
            try {
                userManager.editUser(user, oldPass, newUsername);
                System.out.println("Username berhasil diubah!");
            } catch (Exception e) {
                System.out.println("Gagal merubah username: " + e.getMessage());
            }
        } else if (pilihan.equals("2")) {
            System.out.print("Password lama untuk konfirmasi: ");
            String oldPass = MissionUtil.getUserInput();
            System.out.print("Password baru: ");
            String newPassword = MissionUtil.getUserInput();
            System.out.print("Konfirmasi password baru: ");
            String confirmPassword = MissionUtil.getUserInput();
            
            try {
                userManager.editUser(user, oldPass, newPassword, confirmPassword);
                System.out.println("Password berhasil diubah!");
            } catch (Exception e) {
                System.out.println("Gagal merubah password: " + e.getMessage());
            }
        } else {
            System.out.println("Batal mengubah profil.");
        }
    }
 
    // ================================================================
    // MENU SECURITY
    // ================================================================
    private void showSecurityMenu(Security security) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU SECURITY =====");
            System.out.println("Halo, " + security.getName());
            System.out.println("1. Buat laporan barang ditemukan");
            System.out.println("2. Lihat laporan saya");
            System.out.println("3. Kelola storage barang");
            System.out.println("4. Lihat barang yang bisa diklaim");
            System.out.println("5. Edit profil saya");
            System.out.println("6. Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": buatFoundReport(security);       break;
                case "2": lihatLaporanSecurity(security);  break;
                case "3": kelolaStorage(security);         break;
                case "4": lihatBarangDitemukan();          break;
                case "5": editProfil(security);            break;
                case "6": authService.logout(); running = false; break;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }
 
    // ----------------------------------------------------------------
    // BUAT FOUND REPORT (Security)
    // ----------------------------------------------------------------
    private void buatFoundReport(Security security) {
        System.out.println("\n--- Buat Laporan Barang Ditemukan ---");
 
        System.out.print("Nama barang        : ");
        String namaBarang = MissionUtil.getUserInput();
        if (namaBarang.isEmpty()) { System.out.println("Nama barang tidak boleh kosong."); return; }
 
        System.out.print("Deskripsi barang   : ");
        String deskripsiBarang = MissionUtil.getUserInput();
 
        System.out.print("Lokasi ditemukan   : ");
        String lokasiDitemukan = MissionUtil.getUserInput();
 
        System.out.print("Deskripsi laporan  : ");
        String deskripsiLaporan = MissionUtil.getUserInput();
 
        // Pilih kategori
        ArrayList<Category> categories = getAllCategories();
        System.out.println("Pilih kategori:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        }
        System.out.print("Nomor kategori: ");
        int pilihKat;
        try {
            pilihKat = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilihKat < 0 || pilihKat >= categories.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
        Category kategori = categories.get(pilihKat);
 
        // Foto barang (opsional)
        System.out.print("Path foto barang (Enter untuk skip): ");
        String fotoPath = MissionUtil.getUserInput().trim().replace("\"", "");
 
        // Cek kecocokan dengan LostReport yang ada
        ArrayList<LostReport> semuaLost = reportManager.getLostReports();
        LostReport matched = null;
        Item item;
        String itemId;
 
        if (!semuaLost.isEmpty()) {
            System.out.println("\nApakah barang ini cocok dengan salah satu laporan hilang berikut?");
            for (int i = 0; i < semuaLost.size(); i++) {
                LostReport lr = semuaLost.get(i);
                System.out.println((i + 1) + ". " + lr.getItem().getName() + " | Pelapor: " + lr.getUser().getName() + " | Lokasi: " + lr.getLostLocation() + " | Status: " + lr.getStatus());
            }
            System.out.println("0. Tidak ada yang cocok (item baru)");
            System.out.print("Pilih: ");
 
            try {
                int pilihMatch = Integer.parseInt(MissionUtil.getUserInput());
                if (pilihMatch > 0 && pilihMatch <= semuaLost.size()) {
                    matched = semuaLost.get(pilihMatch - 1);
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, dianggap item baru.");
            }
        }
 
        if (matched != null) {
            item   = matched.getItem();
            itemId = item.getItemID();
            System.out.println("Cocok dengan laporan " + matched.getReportId() + " milik " + matched.getUser().getName());
        } else {
            itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            item   = new Item(itemId, namaBarang, deskripsiBarang, kategori, lokasiDitemukan);
            
            // MENGGUNAKAN TRY-CATCH: Saat security mencatat item baru, validasi dilakukan.
            // Jika ada parameter kosong, aplikasi menampilkan pesan gagal ke console alih-alih crash.
            try {
                itemManager.addItem(item);
            } catch (ValidationException e) {
                System.out.println("Gagal menambahkan item baru: " + e.getMessage());
                return;
            }
        }
 
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        FoundReport fr  = new FoundReport(reportId, security, item, deskripsiLaporan, lokasiDitemukan);
        if (matched != null) fr.setMatchedLostReport(matched);
        
        if (!fotoPath.isEmpty()) {
        java.io.File sourceFile = new java.io.File(fotoPath);
        if (sourceFile.exists()) {
            java.io.File uploadDir = new java.io.File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();
            String extension = fotoPath.substring(fotoPath.lastIndexOf("."));
            String targetName = "evidence_" + reportId + extension;
            java.nio.file.Path targetPath = java.nio.file.Paths.get("uploads", targetName);
            try {
                java.nio.file.Files.copy(sourceFile.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                fr.setPhotoPath(targetPath.toString());
                System.out.println("Foto disimpan ke: " + targetPath);
            } catch (java.io.IOException e) {
                System.out.println("Gagal salin foto: " + e.getMessage());
            }
        }else {
            System.out.println("File tidak ditemukan, foto dilewati.");
        }
        }
 
        reportManager.addReport(fr);
 
        // Buat StorageRecord otomatis setelah FoundReport dibuat
        System.out.print("Lokasi penyimpanan barang: ");
        String lokasiSimpan = MissionUtil.getUserInput();
        String recordId = "SRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        StorageRecord sr = new StorageRecord(recordId, item, security, lokasiSimpan);
        sr.storeItem();
        storageManager.add(sr);
 
        System.out.println("\nLaporan ditemukan berhasil dibuat.");
        System.out.println("Report ID  : " + reportId);
        System.out.println("Storage ID : " + recordId);
        System.out.println("Status     : PENDING, menunggu validasi Admin.");
    }
 
    private void lihatLaporanSecurity(Security security) {
        System.out.println("\n--- Laporan Saya ---");
        boolean ada = false;
 
        for (FoundReport fr : reportManager.getFoundReports()) {
            if (fr.getUser().getUserId().equals(security.getUserId())) {
                fr.displayReport();
                ada = true;
            }
        }
 
        if (!ada) System.out.println("Kamu belum membuat laporan barang ditemukan.");
    }
 
    private void kelolaStorage(Security security) {
        System.out.println("\n--- Kelola Storage ---");
        ArrayList<StorageRecord> records = storageManager.getStorageRecordsBySecurity(security.getUserId());
 
        if (records.isEmpty()) { System.out.println("Tidak ada barang di storage kamu."); return; }
 
        for (int i = 0; i < records.size(); i++) {
            StorageRecord sr = records.get(i);
            System.out.println((i + 1) + ". " + sr.getItem().getName()
                    + " | Lokasi: " + sr.getStorageLocation()
                    + " | " + (sr.isReleased() ? "Sudah diambil" : "Masih disimpan"));
        }
 
        System.out.print("Nomor barang untuk di-release (0 untuk kembali): ");
        String input = MissionUtil.getUserInput();
        if (input.equals("0")) return;
 
        int pilih;
        try {
            pilih = Integer.parseInt(input) - 1;
            if (pilih < 0 || pilih >= records.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        StorageRecord target = records.get(pilih);
        target.releaseItem();
        if (target.isReleased()) storageManager.updateStorageReleasedDB(target.getRecordId());
    }
 
    // ================================================================
    // MENU ADMIN
    // ================================================================
    private void showAdminMenu(Admin admin) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU ADMIN =====");
            System.out.println("Halo, " + admin.getName());
            System.out.println("1. Lihat semua laporan");
            System.out.println("2. Validasi laporan");
            System.out.println("3. Lihat semua klaim");
            System.out.println("4. Proses klaim");
            System.out.println("5. Lihat semua user");
            System.out.println("6. Lihat semua item");
            System.out.println("7. Lihat storage record");
            System.out.println("8. Edit profil saya");
            System.out.println("9. Logout");
            System.out.print("Pilih: ");
            String pilihan = MissionUtil.getUserInput();
 
            switch (pilihan) {
                case "1": lihatSemuaLaporan();     break;
                case "2": validasiLaporan(admin);  break;
                case "3": lihatSemuaKlaim();       break;
                case "4": prosesKlaim(admin);      break;
                case "5": lihatSemuaUser();        break;
                case "6": lihatSemuaItem();        break;
                case "7": lihatSemuaStorage();     break;
                case "8": editProfil(admin);       break;
                case "9": authService.logout(); running = false; break;
                default:  System.out.println("Pilihan tidak valid.");
            }
        }
    }
 
    private void lihatSemuaLaporan() {
        System.out.println("\n--- Semua Laporan ---");
        ArrayList<Report> semua = reportManager.getAllReports();
        if (semua.isEmpty()) { System.out.println("Belum ada laporan."); return; }
        for (Report r : semua) r.displayReport();
    }
 
    private void validasiLaporan(Admin admin) {
        System.out.println("\n--- Validasi Laporan ---");
 
        ArrayList<Report> pending = new ArrayList<>();
        for (Report r : reportManager.getAllReports()) {
            if (r.getStatus() == ReportStatus.PENDING) pending.add(r);
        }
 
        if (pending.isEmpty()) { System.out.println("Tidak ada laporan yang menunggu validasi."); return; }
 
        System.out.println("Laporan PENDING:");
        for (int i = 0; i < pending.size(); i++) {
            Report r = pending.get(i);
            String tipe = (r instanceof LostReport) ? "LOST" : "FOUND";
            System.out.println((i + 1) + ". [" + tipe + "] "
                    + r.getItem().getName()
                    + " | Pelapor: " + r.getUser().getName()
                    + " | ID: " + r.getReportId());
        }
 
        System.out.print("Pilih nomor laporan: ");
        int pilih;
        try {
            pilih = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilih < 0 || pilih >= pending.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        Report target = pending.get(pilih);
        target.displayReport();
 
        System.out.println("1. VALID");
        System.out.println("2. DITOLAK");
        System.out.print("Keputusan: ");
        String keputusan = MissionUtil.getUserInput();
 
        ReportStatus newStatus = keputusan.equals("1") ? ReportStatus.VALID : ReportStatus.DITOLAK;
        
        // MENGGUNAKAN TRY-CATCH: Saat admin memvalidasi laporan, ReportManager akan 
        // memeriksa aturan bisnis. Jika melanggar, error akan dilempar (throw) dan 
        // ditangkap di blok catch ini untuk dicetak sebagai pesan informasi.
        try {
            reportManager.validateReport(target.getReportId(), newStatus, admin);
            System.out.println("Laporan " + target.getReportId() + " diubah ke: " + newStatus);
        } catch (ValidationException e) {
            System.out.println("Gagal memvalidasi laporan: " + e.getMessage());
        }
    }
 
    private void lihatSemuaKlaim() {
        System.out.println("\n--- Semua Klaim ---");
        ArrayList<Claim> semua = claimManager.getAllClaims();
        if (semua.isEmpty()) { System.out.println("Belum ada klaim."); return; }
        for (Claim c : semua) c.displayClaim();
    }
 
    private void prosesKlaim(Admin admin) {
        System.out.println("\n--- Proses Klaim ---");
 
        ArrayList<Claim> pending = claimManager.getClaimsByStatus(ClaimStatus.PENDING);
        if (pending.isEmpty()) { System.out.println("Tidak ada klaim yang menunggu proses."); return; }
 
        System.out.println("Klaim PENDING:");
        for (int i = 0; i < pending.size(); i++) {
            Claim c = pending.get(i);
            System.out.println((i + 1) + ". " + c.getItem().getName()
                    + " | Pengklaim: " + c.getUser().getName()
                    + " | Dokumen: " + c.getDocuments().size()
                    + " | ID: " + c.getClaimId());
        }
 
        System.out.print("Pilih nomor klaim: ");
        int pilih;
        try {
            pilih = Integer.parseInt(MissionUtil.getUserInput()) - 1;
            if (pilih < 0 || pilih >= pending.size()) { System.out.println("Pilihan tidak valid."); return; }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid."); return; }
 
        Claim target = pending.get(pilih);
        target.displayClaim();
 
        System.out.println("1. Setujui (VALID)");
        System.out.println("2. Tolak (DITOLAK)");
        System.out.print("Keputusan: ");
        String keputusan = MissionUtil.getUserInput();
 
        ClaimStatus newStatus = keputusan.equals("1") ? ClaimStatus.VALID : ClaimStatus.DITOLAK;
        try {
            String reason = newStatus == ClaimStatus.DITOLAK ? "Ditolak dari console" : null;
            claimManager.processClaim(target.getClaimId(), newStatus, admin, reason);
            System.out.println("Klaim berhasil diproses.");
        } catch (ValidationException e) {
            System.out.println("Gagal proses klaim: " + e.getMessage());
        }
    }
 
    private void lihatSemuaUser() {
        System.out.println("\n--- Semua User ---");
        ArrayList<User> semua = userManager.getAllUsers();
        if (semua.isEmpty()) { System.out.println("Tidak ada user."); return; }
        for (User u : semua) u.displayInfo();
    }
 
    private void lihatSemuaItem() {
        System.out.println("\n--- Semua Item ---");
        ArrayList<Item> semua = itemManager.getAllItems();
        if (semua.isEmpty()) { System.out.println("Tidak ada item."); return; }
        for (Item item : semua) item.displayItem();
    }
 
    private void lihatSemuaStorage() {
        System.out.println("\n--- Storage Record ---");
        ArrayList<StorageRecord> semua = storageManager.getStorageRecords();
        if (semua.isEmpty()) { System.out.println("Tidak ada data storage."); return; }
        for (StorageRecord sr : semua) sr.displayStorage();
    }
 
    // ================================================================
    // HELPER DB
    // ================================================================
    private ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM categories");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(rs.getString("category_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("Gagal load kategori: " + e.getMessage());
        }
        return list;
    }
 
    private void updateReportDescDB(String reportId, String newDesc) {
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE reports SET description = ? WHERE report_id = ?");
            ps.setString(1, newDesc);
            ps.setString(2, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update deskripsi: " + e.getMessage());
        }
    }
 
    private void updateReportPhotoDB(String reportId, String photoPath) {
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE reports SET photo_path = ? WHERE report_id = ?");
            ps.setString(1, photoPath);
            ps.setString(2, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update foto: " + e.getMessage());
        }
    }
}
