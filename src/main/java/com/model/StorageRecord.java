package com.model;

import com.enumeration.ItemStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// Encapsulation
public class StorageRecord {
    private String recordId;
    private Item item;
    private Security storedBy;
    private String storageLocation;
    private LocalDateTime dateStored;
    private boolean isReleased;
    private LocalDateTime dateReleased;

    public StorageRecord(String recordId, Item item, Security storedBy, String storageLocation) {
        this.recordId = recordId;
        this.item = item;
        this.storedBy = storedBy;
        this.storageLocation = storageLocation;
        this.dateStored = LocalDateTime.now();
        this.isReleased = false;
        this.dateReleased = null;
    }

    public String getRecordId() {
        return recordId;
    }

    public Item getItem() {
        return item;
    }

    public Security getStoredBy() {
        return storedBy;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public LocalDateTime getDateStored() {
        return dateStored;
    }
    
    public void setDateStored(LocalDateTime dateStored) {
        this.dateStored = dateStored;
    }
    
    public LocalDateTime getDateReleased() {
        return dateReleased;
    }

    public void setDateReleased(LocalDateTime dateReleased) {
        this.dateReleased = dateReleased;
    }

    public void setIsReleased(boolean isReleased) {
        this.isReleased = isReleased;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    public void storeItem() {
        item.updateStatus(ItemStatus.DITEMUKAN);
        System.out.println("Item " + item.getName() + " disimpan di " + storageLocation + " oleh " + storedBy.getName());
    }
    
    public boolean isReleased() {
        return isReleased;
    }
     
    public void releaseItem() {
        if (isReleased) {
            System.out.println("Item " + item.getName() + " sudah diambil pada " + dateReleased.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + ". Tidak bisa diambil lagi.");
            return;
        }
 
        if (item.getStatus() != ItemStatus.DIKLAIM) {
            System.out.println("Gagal. Item " + item.getName() + " belum berstatus DIKLAIM. Pastikan klaim sudah disetujui Admin.");
            return;
        }
 
        this.isReleased   = true;
        this.dateReleased = LocalDateTime.now();
        System.out.println("Item " + item.getName() + " berhasil diambil dari storage pada " + dateReleased.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
    
    public void displayStorage() {
        System.out.println("===== Storage Record =====");
        System.out.println("Record ID    : " + recordId);
        System.out.println("Item         : " + item.getName());
        System.out.println("Kategori     : " + (item.getCategory() != null ? item.getCategory().getName() : "-"));
        System.out.println("Status Item  : " + item.getStatus());
        System.out.println("Lokasi Simpan: " + storageLocation);
        System.out.println("Disimpan oleh: " + storedBy.getName());
        System.out.println("Tgl Masuk    : " + dateStored.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        System.out.println("Status       : " + (isReleased? "Sudah diambil pada " + dateReleased.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")): "Masih disimpan"));
        System.out.println("==========================");
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "StorageRecord{recordId='" + recordId + "', item='" + item.getName() + "', location='" + storageLocation + "', isReleased=" + isReleased + "}";
    }

}
