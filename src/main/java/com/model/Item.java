package com.model;

import com.enumeration.ItemStatus;
import java.time.LocalDateTime;

public class Item {
    private String itemID;
    private String name;
    private String description;
    private Category category;
    private ItemStatus status;
    private String location;
    private LocalDateTime date;

    public Item(String itemID, String name, String description, Category category, String location) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
    }

    public String getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public void updateStatus(){
    }
    
    public void updateStatus(LocalDateTime date){
    }
    
    public void displayItem(){
    }
    
    @Override
    public boolean equals(Object o){
        return false;
    }
}
