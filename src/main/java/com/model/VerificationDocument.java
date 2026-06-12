package com.model;

import com.interfaces.Verifiable;
import java.io.File;

public class VerificationDocument implements Verifiable{
    private String documentId;
    private String type;
    private File file;
    private String description;

    public VerificationDocument(String documentId, String type, File file, String description) {
        this.documentId = documentId;
        this.type = type;
        this.file = file;
        this.description = description;
    }

    public String getDocumentId() {
        return documentId; 
    }
    
    public String getType() {
        return type; 
    }
    
    public File getFile() {
        return file; 
    }
    
    public String getDescription() {
        return description; 
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean validate() {
        if (type == null || type.isEmpty()) {
            System.out.println("Tipe Dokumen Tidak Boleh Kosong");
            return false;
        }
        if (file == null) {
            System.out.println("File Tidak Ada");
            return false;
        }
        if (!file.exists()) {
            System.out.println("File tidak ditemukan di path " + file.getAbsolutePath());
            return false;
        }
        System.out.println("Dokumen Claim " + documentId + " valid. File: " + file.getName());
        return true;
    }
    
    //Masih Berpikir..
    public void updateStatus() {
    }
    
    public void displayDocument() {
        System.out.println("======= Info Dokumen =======");
        System.out.println("Dokumen ID  : " + documentId);
        System.out.println("Tipe        : " + type);
        System.out.println("File        : " + (file != null ? file.getName() : "-"));
        System.out.println("Path        : " + (file != null ? file.getAbsolutePath() : "-"));
        System.out.println("Deskripsi   : " + description);
        System.out.println("Valid       : " + (validate() ? "Ya" : "Tidak"));
        System.out.println("==========================");
    }
    
    @Override
    public String toString() {
        return "VerificationDocument{documentId='" + documentId + "', type='" + type + "', file='" + (file != null ? file.getName() : "null") + "'}";
    }
}