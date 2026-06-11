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
    }
    
    public void setFile(File file) {
    }
    
    public void setDescription(String description) {
    }

    public boolean validate() {
        return true;
    }
    
    public void updateStatus() {
    }
    

    @Override
    public String toString() {
        return null;
    }
}