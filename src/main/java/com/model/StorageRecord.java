package com.model;

import java.time.LocalDateTime;

public class StorageRecord {
    private String recordId;
    private Item item;
    private Security storedBy;
    private String storageLocation;
    public LocalDateTime date;

    public StorageRecord(String recordId, Item item, Security storedBy, String storageLocation) {
        this.recordId = recordId;
        this.item = item;
        this.storedBy = storedBy;
        this.storageLocation = storageLocation;
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

    public LocalDateTime getDate() {
        return date;
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
