package com.model;

import com.enumeration.ItemStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
// Encapsulation
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
        this.status = ItemStatus.DICARI;
        this.date = LocalDateTime.now();
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
    
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public void updateStatus(ItemStatus status){
        this.status = status;
        this.date   = LocalDateTime.now();
        System.out.println("Status Item " + name + "' diubah menjadi: " + status);
    }
    
    public void updateStatus(ItemStatus status, LocalDateTime date){
        this.status = status;
        this.date   = date;
        System.out.println("StatusItem'" + name + "' diubah menjadi: " + status
                + " pada " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
    
    public void displayItem() {
        System.out.println("===== Info Item =====");
        System.out.println("Item ID     : " + itemID);
        System.out.println("Nama        : " + name);
        System.out.println("Deskripsi   : " + description);
        System.out.println("Kategori    : " + (category != null ? category.getName() : "-"));
        System.out.println("Status      : " + status);
        System.out.println("Lokasi      : " + location);
        System.out.println("Tanggal     : " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        System.out.println("=====================");
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Item{itemID='" + itemID + "', name='" + name + "', status=" + status + "', location='" + location + "'}";
    }
    // Polymorphism (Method Overriding)
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemID, item.itemID);
    }
}
