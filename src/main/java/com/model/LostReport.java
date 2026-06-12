package com.model;

import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.interfaces.Reportable;
import java.time.format.DateTimeFormatter;

public class LostReport extends Report implements Reportable{
    private String lostLocation;
    private String evidencePath;
    
    public LostReport(String reportId, User user, Item item, String description, String lostLocation) {
        super(reportId, user, item, description);
        this.lostLocation = lostLocation;
        this.item.updateStatus(ItemStatus.DICARI);
    }

    public String getLostLocation() {
        return lostLocation;
    }
    
    public String getEvidencePath() {
        return evidencePath;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }
    
    public void addEvidence(String filePath) {
        this.evidencePath = filePath;
        System.out.println("Bukti foto ditambahkan: " + filePath);
    }
    
    public void addEvidence() {
        this.evidencePath = null;
        System.out.println("Bukti foto dihapus.");
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
        System.out.println("Foto Bukti    : " + (evidencePath != null ? evidencePath : "-"));
        System.out.println("Bisa Diklaim  : " + (isClaimable() ? "Ya" : "Tidak"));
        System.out.println("===========================================");
    }
    
    @Override
    public String toString() {
        return "LostReport{ID Report='" + reportId + "', user='" + user.getName()+ "', item='" + item.getName()+ "', lostLocation='" + lostLocation + "', status=" + status + ", description=" + item.getDescription()+ '}';
    }
}
