package com.model;

import com.enumeration.ReportStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// Abstraction & Encapsulation
public abstract class Report {
    protected String reportId;
    protected User user;
    protected Item item;
    protected LocalDateTime date;
    protected String description;
    protected ReportStatus status;
    protected LocalDateTime editableUntil;
    protected String photoPath;
    protected String rejectionReason;
    private static final int EDITABLE_DURATION_MINUTES = 30;

    public Report(String reportID, User user, Item item, String description) {
        this.reportId      = reportID;
        this.user          = user;
        this.item          = item;
        this.description   = description;
        this.status        = ReportStatus.PENDING;
        this.date          = LocalDateTime.now();
        this.editableUntil = LocalDateTime.now().plusMinutes(EDITABLE_DURATION_MINUTES);
        this.photoPath     = null;
        this.rejectionReason = null;
    }

    public String getReportId() {
        return reportId;
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
    
    public void setEditableUntil(LocalDateTime editableUntil) {
        this.editableUntil = editableUntil;
    }
    
    //coba dulu aja
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public void submitReport() {
        this.status = ReportStatus.PENDING;
        System.out.println("Laporan Dengan ID: " + reportId + " berhasil disubmit. Status: PENDING, menunggu validasi dari Admin.");
    }
    
    public void editReport(String newDescription) {
        if (isEditable()) {
            this.description = newDescription;
            System.out.println("Laporan Dengan ID: " + reportId + " berhasil diedit.");
        } else {
            System.out.println("Laporan Dengan ID: " + reportId + " sudah tidak bisa diedit. Waktu edit telah habis.");
        }
    }
    
    public boolean isEditable() {
        return LocalDateTime.now().isBefore(editableUntil);
    }
    
    public void validateCanEdit() throws Exception {
        if (this.status != ReportStatus.PENDING) {
            throw new Exception("Laporan hanya dapat diedit saat status masih pending.");
        }
        if (!isEditable()) {
            throw new Exception("Batas waktu edit laporan telah habis.");
        }
    }
    
    public boolean isValid() {
        return this.status == ReportStatus.VALID;
    }
    
    public boolean isClaimable() {
        return isValid() && item.getStatus() == com.enumeration.ItemStatus.DITEMUKAN;
    }
    
    public abstract void displayReport();
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Report{" + "ID Report =" + reportId + ", user=" + user + ", item=" + item + ", date=" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + ", description=" + description + '}';
    }

    
}
