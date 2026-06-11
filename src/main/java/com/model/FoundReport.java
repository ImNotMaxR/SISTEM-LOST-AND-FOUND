package com.model;

import com.interfaces.Reportable;

public class FoundReport extends Report implements Reportable{
    private String foundLocation;
    
    public FoundReport(String reportID, User user, Item item, String description, String foundLocation) {
        super(reportID, user, item, description);
        this.foundLocation = foundLocation;
    }

    public String getFoundLocation() {
        return foundLocation;
    }

    public void setFoundLocation(String foundLocation) {
        this.foundLocation = foundLocation;
    }
    
    @Override
    public void submitReport(){
    }
    
    @Override
    public void displayReport(){
    }   

    @Override
    public String toString() {
        return null;
    }
}
