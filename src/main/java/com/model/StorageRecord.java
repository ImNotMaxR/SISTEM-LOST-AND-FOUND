package com.model;

import com.enumeration.ItemStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StorageRecord {
    private String recordId;
    private Item item;
    private Security storedBy;
    private String storageLocation;
    public LocalDateTime dateStored;
    
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

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    void storeItem(){
    }
    
    void releaseItem(){
    }
    
    public void displayStorage(){
    }

    @Override
    public String toString() {
        return null;
    }
    
    
}
