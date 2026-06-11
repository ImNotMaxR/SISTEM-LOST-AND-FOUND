package com.model;

import com.interfaces.Reportable;

public class LostReport extends Report implements Reportable{
    private String lostLocation;
    
    public LostReport(String reportID, User user, Item item, String description, String lostLocation) {
        super(reportID, user, item, description);
        this.lostLocation = lostLocation;
    }

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }
    
    public void addEvidence(String filePath){
    }
    
    public void addEvidence(){
    }
    
    @Override
    public void submitReport(){
    }
    
    @Override
    public void displayReport(){
    }    
}
