package com.model;

import com.enumeration.ClaimStatus;
import com.interfaces.Verifiable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Claim implements Verifiable{
    private String claimId;
    private User user;
    private Item item;
    private ArrayList<VerificationDocument> documents;
    private ClaimStatus status;
    private LocalDateTime dateClaim;

    public Claim(String claimId, User user, Item item) {
        this.claimId = claimId;
        this.user = user;
        this.item = item;
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
    }

    public void addDocument(VerificationDocument doc) {
    }

    @Override
    public boolean validate() {
        return false;
    }

    public void updateStatus() {}

    public void displayClaim() {}

    @Override
    public String toString() {
        return null;
    }
}