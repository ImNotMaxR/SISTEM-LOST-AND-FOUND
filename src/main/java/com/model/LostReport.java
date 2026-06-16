package com.model;

import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.interfaces.Reportable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

public class LostReport extends Report implements Reportable{
    private String lostLocation;
    
    public LostReport(String reportId, User user, Item item, String description, String lostLocation) {
        super(reportId, user, item, description);
        this.lostLocation = lostLocation;
        this.item.updateStatus(ItemStatus.DICARI);
    }

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }
    
    public void addEvidence(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
 
        // Bersihkan tanda kutip jika ada
        filePath = filePath.trim().replace("\"", "");
 
        File sourceFile = new File(filePath);
        if (!sourceFile.exists()) {
            System.out.println("File tidak ditemukan: " + filePath);
            return;
        }
 
        // Buat folder uploads jika belum ada
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) uploadDir.mkdirs();
 
        // Nama file di folder uploads pakai reportId supaya unik
        String extension = filePath.substring(filePath.lastIndexOf("."));
        String targetName = "evidence_" + reportId + extension;
        Path targetPath = Paths.get("uploads", targetName);
 
        try {
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            this.photoPath = targetPath.toString();
            System.out.println("Foto berhasil disimpan ke: " + this.photoPath);
        } catch (IOException e) {
            System.out.println("Gagal menyalin foto: " + e.getMessage());
        }
    }
    
    public void addEvidence() {
        this.photoPath = null;
        System.out.println("Foto bukti dihapus.");
    }
    
    @Override
    public void submitReport() {
        this.status = ReportStatus.PENDING;
        System.out.println("Laporan barang hilang " + reportId + " disubmit oleh " + user.getName() + ". Menunggu validasi Admin...");
    }
    
    @Override
    public void displayReport() {
        System.out.println("========== Laporan Barang Hilang ==========");
        System.out.println("Report ID     : " + reportId);
        System.out.println("Pelapor       : " + user.getName() + " (" + user.getRole() + ")");
        System.out.println("Barang        : " + item.getName());
        System.out.println("Kategori      : " + (item.getCategory() != null ? item.getCategory().getName() : "-"));
        System.out.println("Deskripsi     : " + description);
        System.out.println("Lokasi Hilang : " + lostLocation);
        System.out.println("Status Report : " + status);
        System.out.println("Status Item   : " + item.getStatus());
        System.out.println("Tanggal       : " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        System.out.println("Bisa Diedit   : " + (isEditable() ? "Ya (sampai " + editableUntil.format(DateTimeFormatter.ofPattern("HH:mm")) + ")" : "Tidak"));
        System.out.println("Foto Bukti    : " + (photoPath != null && !photoPath.isEmpty() ? photoPath : "-"));
        System.out.println("Bisa Diklaim  : " + (isClaimable() ? "Ya" : "Tidak"));
        System.out.println("===========================================");
    }
    
    @Override
    public String toString() {
        return "LostReport{ID Report='" + reportId + "', user='" + user.getName()+ "', item='" + item.getName()+ "', lostLocation='" + lostLocation + "', status=" + status + ", description=" + item.getDescription()+ '}';
    }
}