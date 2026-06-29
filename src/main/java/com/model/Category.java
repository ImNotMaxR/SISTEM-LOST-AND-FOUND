package com.model;
// Encapsulation
public class Category {
    private String categoryID;
    private String name;

    public Category(String categoryID, String name) {
        this.categoryID = categoryID;
        this.name = name;
    }

    public String getCategoryID() { 
        return categoryID; 
    }
    
    public String getName() { 
        return name; 
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID; 
    }
    
    public void setName(String name) {
        this.name = name; 
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Category{categoryID='" + categoryID + "', name='" + name + "'}";
    }
}