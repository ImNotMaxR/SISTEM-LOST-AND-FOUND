package com.mycompany.sistemlostfound;

import com.database.DBConnection;
import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.enumeration.Role;
import com.managers.ClaimManager;
import com.managers.ItemManager;
import com.managers.ReportManager;
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
    private DBConnection dbConnection;
 
    public AppController(AuthService authService) {
        this.authService   = authService;
        this.userManager   = new UserManager();
        this.itemManager   = new ItemManager();
        this.reportManager = new ReportManager();
        this.claimManager  = new ClaimManager();
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
            System.out.println((i + 1) + ". " + categories.get(i).getName()
                    + (categories.get(i).isVerificationRequired() ? " *butuh dokumen saat klaim" : ""));
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
        itemManager.addItem(item);
 
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
            System.out.println((i + 1) + ". " + item.getName()
                    + " | Kategori: " + (item.getCategory() != null ? item.getCategory().getName() : "-")
                    + " | Status: " + item.getStatus()
                    + (item.getCategory() != null && item.getCategory().isVerificationRequired()
                        ? " *butuh dokumen" : ""));
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
        if (itemDiklaim.getCategory() != null && itemDiklaim.getCategory().isVerificationRequired()) {
            System.out.println("Kategori ini membutuhkan dokumen verifikasi.");
            System.out.print("Tipe dokumen (contoh: STNK, KTP, KTM): ");
            String tipeDoc = MissionUtil.getUserInput();
            System.out.print("Path file dokumen: ");
            String pathDoc = MissionUtil.getUserInput();
            System.out.print("Keterangan dokumen: ");
            String ketDoc = MissionUtil.getUserInput();
 
            String docId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            File fileDoc = pathDoc.isEmpty() ? null : new File(pathDoc);
            VerificationDocument doc = new VerificationDocument(docId, tipeDoc, fileDoc, ketDoc);
            claim.addDocument(doc);
        }
 
        claimManager.addClaim(claim);
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
    private void editProfil(User user) {
        System.out.println("\n--- Edit Profil ---");
        System.out.println("Username saat ini : " + user.getUsername());
 
        System.out.print("Password lama untuk konfirmasi: ");
        String oldPass = MissionUtil.getUserInput();
 
        if (!user.checkPassword(oldPass)) {
            System.out.println("Password salah. Profil tidak diubah.");
            return;
        }
 
        System.out.print("Username baru (Enter untuk skip): ");
        String newUsername = MissionUtil.getUserInput();
        if (newUsername.isEmpty()) newUsername = user.getUsername();
 
        System.out.print("Password baru (Enter untuk skip): ");
        String newPassword = MissionUtil.getUserInput();
        if (newPassword.isEmpty()) newPassword = user.getPassword();
 
        userManager.editUser(user, user.getUserId(), newUsername, newPassword);
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
            itemManager.addItem(item);
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
        saveStorageRecordToDB(sr);
 
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
        ArrayList<StorageRecord> records = getStorageRecordsBySecurity(security.getUserId());
 
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
        if (target.isReleased()) updateStorageReleasedDB(target.getRecordId());
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
        reportManager.validateReport(target.getReportId(), newStatus, admin);
        System.out.println("Laporan " + target.getReportId() + " diubah ke: " + newStatus);
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
        claimManager.processClaim(target.getClaimId(), newStatus, admin);
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
        ArrayList<StorageRecord> semua = getAllStorageRecords();
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
                list.add(new Category(rs.getString("category_id"), rs.getString("name"), rs.getBoolean("request_verification")));
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
 
    private void saveStorageRecordToDB(StorageRecord sr) {
        String sql = "INSERT INTO storage_records (record_id, item_id, security_user_id, storage_location, date_stored, is_released, date_released) VALUES (?,?,?,?,?,?,?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sr.getRecordId());
            ps.setString(2, sr.getItem().getItemID());
            ps.setString(3, sr.getStoredBy().getUserId());
            ps.setString(4, sr.getStorageLocation());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(sr.getDateStored()));
            ps.setBoolean(6, sr.isReleased());
            ps.setNull(7, java.sql.Types.TIMESTAMP);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal simpan storage record: " + e.getMessage());
        }
    }
 
    private void updateStorageReleasedDB(String recordId) {
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE storage_records SET is_released = true, date_released = NOW() WHERE record_id = ?");
            ps.setString(1, recordId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update storage released: " + e.getMessage());
        }
    }
 
    private ArrayList<StorageRecord> getStorageRecordsBySecurity(String securityUserId) {
        ArrayList<StorageRecord> list = new ArrayList<>();
        String sql = "SELECT sr.record_id, sr.storage_location, sr.date_stored, sr.is_released, sr.date_released, "
                   + "i.item_id, i.name AS item_name, i.description AS item_desc, i.status AS item_status, i.location AS item_location, "
                   + "c.category_id, c.name AS cat_name, c.request_verification "
                   + "FROM storage_records sr "
                   + "JOIN items i ON sr.item_id = i.item_id "
                   + "LEFT JOIN categories c ON i.category_id = c.category_id "
                   + "WHERE sr.security_user_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, securityUserId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = rs.getString("category_id") != null
                    ? new Category(rs.getString("category_id"), rs.getString("cat_name"), rs.getBoolean("request_verification"))
                    : null;
                Item item = new Item(rs.getString("item_id"), rs.getString("item_name"), rs.getString("item_desc"), cat, rs.getString("item_location"));
                item.setStatus(ItemStatus.valueOf(rs.getString("item_status")));
                Security sec = (Security) authService.getCurrentUser();
                StorageRecord sr2 = new StorageRecord(rs.getString("record_id"), item, sec, rs.getString("storage_location"));
                list.add(sr2);
            }
        } catch (SQLException e) {
            System.out.println("Gagal load storage: " + e.getMessage());
        }
        return list;
    }
 
    private ArrayList<StorageRecord> getAllStorageRecords() {
        ArrayList<StorageRecord> list = new ArrayList<>();
        String sql = "SELECT sr.record_id, sr.storage_location, sr.date_stored, sr.is_released, "
                   + "i.item_id, i.name AS item_name, i.description AS item_desc, i.status AS item_status, i.location AS item_location, "
                   + "c.category_id, c.name AS cat_name, c.request_verification, "
                   + "u.user_id, u.name AS sec_name, u.username, u.password "
                   + "FROM storage_records sr "
                   + "JOIN items i ON sr.item_id = i.item_id "
                   + "LEFT JOIN categories c ON i.category_id = c.category_id "
                   + "JOIN users u ON sr.security_user_id = u.user_id";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = rs.getString("category_id") != null
                    ? new Category(rs.getString("category_id"), rs.getString("cat_name"), rs.getBoolean("request_verification"))
                    : null;
                Item item = new Item(rs.getString("item_id"), rs.getString("item_name"), rs.getString("item_desc"), cat, rs.getString("item_location"));
                item.setStatus(ItemStatus.valueOf(rs.getString("item_status")));
                Security sec = new Security(rs.getString("user_id"), rs.getString("sec_name"), rs.getString("username"), rs.getString("password"), "", "");
                StorageRecord sr2 = new StorageRecord(rs.getString("record_id"), item, sec, rs.getString("storage_location"));
                list.add(sr2);
            }
        } catch (SQLException e) {
            System.out.println("Gagal load semua storage: " + e.getMessage());
        }
        return list;
    }
}
