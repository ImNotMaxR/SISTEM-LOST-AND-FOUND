package com.model;

import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.interfaces.Verifiable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Claim implements Verifiable{
    private String claimId;
    private User user;
    private Item item;
    private ArrayList<VerificationDocument> documents;
    private ClaimStatus status;
    private LocalDateTime dateClaim;
    private String relatedReportId;
    

    public Claim(String claimId, User user, Item item, String relatedReportId) {
        this.claimId = claimId;
        this.user = user;
        this.item = item;
        this.relatedReportId = relatedReportId;
        this.documents = new ArrayList<>();
        this.status = ClaimStatus.PENDING;
        this.dateClaim = LocalDateTime.now();
    }

    public String getClaimId() {
        return claimId; 
    }
    
    public User getUser() {
        return user; 
    }
    
    public Item getItem() {
        return item; 
    }
    
    public String getRelatedReportId() {
        return relatedReportId;
    }
    
    public ArrayList<VerificationDocument> getDocuments() {
        return documents; 
    }
    
    public ClaimStatus getStatus() {
        return status; 
    }
    
    public LocalDateTime getDateClaim() {
        return dateClaim; 
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public void addDocument(VerificationDocument doc) {
        if (doc == null) {
            System.out.println("Dokumen tidak boleh null.");
            return;
        }
        documents.add(doc);
        System.out.println("Dokumen " + doc.getType() + " telah ditambahkan ke klaim " + claimId);
    }

    @Override
    public boolean validate() {
        if (item.getStatus() != ItemStatus.DITEMUKAN) {
            System.out.println("Gagal validasi: item barang " + item.getName() + " tidak berstatus DITEMUKAN.");
            return false;
        }
 
        if (status != ClaimStatus.PENDING) {
            System.out.println("Gagal validasi: klaim ini sudah diproses dengan status " + status);
            return false;
        }
 
        // Jika kategori butuh verifikasi dokumen, cek semua dokumen valid
        if (item.getCategory() != null) {
            if (documents.isEmpty()) {
                System.out.println("Gagal validasi: kategori " + item.getCategory().getName() + " membutuhkan dokumen verifikasi.");
                return false;
            }
            for (VerificationDocument doc : documents) {
                if (!doc.validate()) {
                    System.out.println("Gagal validasi: dokumen " + doc.getDocumentId() + " tidak valid.");
                    return false;
                }
            }
        }
        System.out.println("Klaim " + claimId + "  valid.");
        return true;
    }

    public void updateStatus(ClaimStatus newStatus) {
        this.status = newStatus;
        if (newStatus == ClaimStatus.VALID) {
            item.updateStatus(ItemStatus.DIKLAIM);
            System.out.println("Klaim dengan ID: " + claimId + " disetujui. Item Barang " + item.getName() + " sekarang berstatus DIKLAIM.");
        } else if (newStatus == ClaimStatus.DITOLAK) {
            System.out.println("Klaim dengan ID: " + claimId + " ditolak. Item Barang " + item.getName() + " tetap berstatus DITEMUKAN.");
        }
    }


    public void displayClaim() {
        System.out.println("========== Detail Klaim ==========");
        System.out.println("Claim ID    : " + claimId);
        System.out.println("Pengklaim   : " + user.getName() + " (" + user.getRole() + ")");
        System.out.println("Item        : " + item.getName());
        System.out.println("Kategori    : " + (item.getCategory() != null ? item.getCategory().getName() : "-"));
        System.out.println("Butuh Dok.  : Ya");
        System.out.println("Status Klaim: " + status);
        System.out.println("Status Item : " + item.getStatus());
        System.out.println("Tgl Klaim   : " + dateClaim.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        System.out.println("Report Asal : " + relatedReportId);
        System.out.println("Dokumen     : " + documents.size() + " file");
        for (VerificationDocument doc : documents) {
            doc.displayDocument();
        }
        System.out.println("==================================");
    }

    @Override
    public String toString() {
        return "Claim{claimId='" + claimId + "', user='" + user.getName() + "', item='" + item.getName() + "', status=" + status + "', dateClaim=" + dateClaim.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "}";
    }
}
