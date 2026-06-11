package com.model;

import com.enumeration.ReportStatus;
import java.time.LocalDateTime;

public abstract class Report {
    protected String reportID;
    protected User user;
    protected Item item;
    protected LocalDateTime date;
    protected String description;
    protected ReportStatus status;
    protected LocalDateTime editableUntil;

    public Report(String reportID, User user, Item item, String description) {
        this.reportID = reportID;
        this.user = user;
        this.item = item;
        this.description = description;
    }

    public String getReportID() {
        return reportID;
    }

    public User getUser() {
        return user;
    }

    public Item getItem() {
        return item;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public LocalDateTime getEditableUntil() {
        return editableUntil;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public void submitReport(){
    }
    
    public void editReport(){
    }
    
    public void isEditable(){
    }
    
    public abstract void displayReport();

    @Override
    public String toString() {
        return null;
    }
}
