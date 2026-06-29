package com.model;

import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.enumeration.Role;
import com.interfaces.Reportable;
import java.time.format.DateTimeFormatter;
// Inheritance & Encapsulation
public class FoundReport extends Report implements Reportable{
    private String foundLocation;
    //Buat Nanti Bisa cocokin FoundReport sama LostReport jadi nambah atribut
    private LostReport matchedLostReport;
    
    public FoundReport(String reportId, User user, Item item, String description, String foundLocation) {
        super(reportId, user, item, description);
        this.foundLocation = foundLocation;
        this.matchedLostReport = null;
        
        //Cek Role biar security yang bisa buat sama admin
        if (user.getRole() != Role.SECURITY && user.getRole() != Role.ADMIN) {
            System.out.println(user.getName() + " tidak memiliki hak untuk membuat laporan barang ditemukan.");
        }
    }

    public String getFoundLocation() {
        return foundLocation;
    }
    
    public LostReport getMatchedLostReport() {
        return matchedLostReport;
    }
 
    public boolean hasMatch() {
        return matchedLostReport != null;
    }

    public void setFoundLocation(String foundLocation) {
        this.foundLocation = foundLocation;
    }
    
    public void setMatchedLostReport(LostReport lostReport) {
        //Ngambil Objek Lost Report
        this.matchedLostReport = lostReport;
        //Kalau Item FoundReport sama LostReport nya ini Sama status berubah
        if (lostReport != null) {
            this.item.updateStatus(ItemStatus.DITEMUKAN);
            if (lostReport.getItem() != null) {
                lostReport.getItem().updateStatus(ItemStatus.DITEMUKAN);
            }
            System.out.println("Item " + item.getName() + " cocok dengan laporan hilang " + lostReport.getReportId() + ". Status item diubah ke DITEMUKAN.");
        } else {
            this.item.updateStatus(ItemStatus.DICARI);
            System.out.println("Tidak ada laporan hilang yang cocok untuk item " + item.getName() + ". Status item tetap DICARI.");
        }
    }
    // Polymorphism (Method Overriding)
    @Override
    public void submitReport() {
        this.status = ReportStatus.PENDING;
        System.out.println("Laporan barang ditemukan " + reportId + " disubmit oleh " + user.getName() + ". Menunggu validasi Admin.");
    }
    // Polymorphism (Method Overriding)
    @Override
    public void displayReport() {
        System.out.println("========== Laporan Barang Ditemukan ==========");
        System.out.println("Report ID        : " + reportId);
        System.out.println("Pelapor          : " + user.getName() + " (" + user.getRole() + ")");
        System.out.println("Barang           : " + item.getName());
        System.out.println("Kategori         : " + (item.getCategory() != null ? item.getCategory().getName() : "-"));
        System.out.println("Deskripsi        : " + description);
        System.out.println("Lokasi Ditemukan : " + foundLocation);
        System.out.println("Foto Bukti       : " + (photoPath != null && !photoPath.isEmpty() ? photoPath : "-"));
        System.out.println("Status Report    : " + status);
        System.out.println("Status Item      : " + item.getStatus());
        System.out.println("Tanggal          : " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        System.out.println("Cocok dgn Laporan: " + (matchedLostReport != null? matchedLostReport.getReportId() + " (" + matchedLostReport.getUser().getName() + ")": "Belum ada kecocokan"));
        System.out.println("Bisa Diklaim     : " + (isClaimable() ? "Ya" : "Tidak"));
        System.out.println("==============================================");
    } 
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "FoundReport{ID Report ='" + reportId + "', user='" + user.getName()+ "', item='" + item.getName()+ "', foundLocation='" + foundLocation + "', status=" + status + ", description=" + item.getDescription()+ '}';
    }
}
