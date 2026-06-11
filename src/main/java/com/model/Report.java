package com.model;

import com.enumeration.ReportStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Report {
    protected String reportID;
    protected User user;
    protected Item item;
    protected LocalDateTime date;
    protected String description;
    protected ReportStatus status;
    protected LocalDateTime editableUntil;
    //Gatau Coba Aja Kalau ada file Poto untuk Khusus semua report
    protected String photoPath;
    private static final int EDITABLE_DURATION_MINUTES = 30;

    public Report(String reportID, User user, Item item, String description) {
        this.reportID      = reportID;
        this.user          = user;
        this.item          = item;
        this.description   = description;
        this.status        = ReportStatus.PENDING;
        this.date          = LocalDateTime.now();
        this.editableUntil = LocalDateTime.now().plusMinutes(EDITABLE_DURATION_MINUTES);
        this.photoPath     = null;
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
    
    //coba dulu aja
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public void submitReport() {
        this.status = ReportStatus.PENDING;
        System.out.println("Laporan Dengan ID: " + reportID + " berhasil disubmit. Status: PENDING, menunggu validasi dari Admin.");
    }
    
    public void editReport(String newDescription) {
        if (isEditable()) {
            this.description = newDescription;
            System.out.println("Laporan Dengan ID: " + reportID + " berhasil diedit.");
        } else {
            System.out.println("Laporan Dengan ID: " + reportID + " sudah tidak bisa diedit. Waktu edit telah habis.");
        }
    }
    
    public boolean isEditable() {
        return LocalDateTime.now().isBefore(editableUntil);
    }
    
    public boolean isValid() {
        return this.status == ReportStatus.VALID;
    }
    
    public boolean isClaimable() {
        return isValid() && item.getStatus() == com.enumeration.ItemStatus.DITEMUKAN;
    }
    
    public abstract void displayReport();

    @Override
    public String toString() {
        return "Report{" + "reportID=" + reportID + ", user=" + user + ", item=" + item + ", date=" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + ", description=" + description + '}';
    }

    
}
