package com.model;

public class Category {
    private String categoryID;
    private String name;
    private boolean requestVerification;

    public Category(String categoryID, String name, boolean requestVerification) {
        this.categoryID = categoryID;
        this.name = name;
        this.requestVerification = requestVerification;
    }

    public String getCategoryID() { 
        return categoryID; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public boolean isRequestVerification() {
        return requestVerification; 
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID; 
    }
    
    public void setName(String name) {
        this.name = name; 
    }
    
    public void setRequestVerification(boolean requestVerification) {
        this.requestVerification = requestVerification;
    }

    public boolean isVerificationRequired() {
            return requestVerification;
    }
    
    @Override
    public String toString() {
        return "Category{categoryID='" + categoryID + "', name='" + name + "', requestVerification=" + requestVerification + "}";
    }
}