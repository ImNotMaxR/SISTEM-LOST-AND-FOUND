package com.managers;

import com.database.DBConnection;
import com.enumeration.ItemStatus;
import com.interfaces.Managerable;
import com.model.Category;
import com.model.Item;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;

public class ItemManager implements Managerable{
    private ArrayList<Item> items;
    private HashMap<String, Item> itemMap;
    private DBConnection dbConnection;
    
    
    public ItemManager() {
        this.items = new ArrayList<>();
        this.itemMap = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllItemsFromDB();
    }
    
    private void loadAllItemsFromDB() {
        items.clear();
        itemMap.clear();
        String sql = "SELECT i.item_id, i.name, i.description, i.status, i.location, i.date, " + "c.category_id, c.name AS category_name, c.request_verification " + "FROM items i " + "LEFT JOIN categories c ON i.category_id = c.category_id";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Item item = buildItemFromResultSet(rs);
                if (item != null) {
                    items.add(item);
                    itemMap.put(item.getItemID(), item);
                }
            }
            System.out.println("Data item berhasil diload dari database dengan jumlah: " + items.size());
        } catch (SQLException e) {
            System.out.println("Gagal load items " + e.getMessage());
        }
    }
    
    private Item buildItemFromResultSet(ResultSet rs) throws SQLException {
        String itemID = rs.getString("item_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String statusStr = rs.getString("status");
        String location = rs.getString("location");
        LocalDateTime date = rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime(): LocalDateTime.now();
 
        Category category = null;
        String categoryId = rs.getString("category_id");
        if (categoryId != null) {
            category = new Category(categoryId, rs.getString("category_name"), rs.getBoolean("request_verification"));
        }
        
        Item item = new Item(itemID, name, description, category, location);
        item.setStatus(ItemStatus.valueOf(statusStr));
        return item;
    }
    
    @Override
    public void add(Object obj) {
        if (obj instanceof Item) {
            addItem((Item) obj);
        }
    }

    @Override
    public void delete(String id) {
        deleteItem(id);
    }

    @Override
    public Object findById(String id) {
        return findItem(id);
    }

    public void addItem(Item item) {
        String sql = "INSERT INTO items (item_id, name, description, category_id, status, location, date) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, item.getItemID());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getCategory() != null ? item.getCategory().getCategoryID() : null);
            ps.setString(5, item.getStatus().name());
            ps.setString(6, item.getLocation());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(item.getDate()));
            ps.executeUpdate();
 
            items.add(item);
            itemMap.put(item.getItemID(), item);
            System.out.println("Item Dengan ID: " + item.getName() + " berhasil ditambahkan.");
        } catch (SQLException e) {
            System.out.println("Gagal Menambahkan Item" + e.getMessage());
        }
    }

    public void editItem(String itemId, String newName, String newDescription, String newLocation) {
        String sql = "UPDATE items SET name = ?, description = ?, location = ? WHERE item_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setString(2, newDescription);
            ps.setString(3, newLocation);
            ps.setString(4, itemId);
            ps.executeUpdate();
 
            // Update juga object di memori supaya sinkron
            Item item = itemMap.get(itemId);
            if (item != null) {
                item.setName(newName);
                item.setDescription(newDescription);
                item.setLocation(newLocation);
            }
            System.out.println("Item Dengan ID: " + itemId + " berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println("Gagal edit item" + e.getMessage());
        }
    }

    public void deleteItem(String itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, itemId);
            ps.executeUpdate();
 
            Item item = itemMap.remove(itemId);
            if (item != null) {
                items.remove(item);
                System.out.println("Item Dengan ID: " + itemId + " berhasil dihapus.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal hapus item" + e.getMessage());
        }
    }

    public Item findItem(String itemId) {
        return itemMap.getOrDefault(itemId, null);
    }
    
    public ArrayList<Item> searchItems(String keyword) {
        ArrayList<Item> result = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Item item : items) {
            if (item.getName().toLowerCase().contains(lower) || item.getDescription().toLowerCase().contains(lower) || item.getLocation().toLowerCase().contains(lower)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }
    
    public ArrayList<Item> getItemByStatus(ItemStatus status) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getStatus() == status) {
                result.add(item);
            }
        }
        return result;
    }
    
    public void reload() {
        loadAllItemsFromDB();
    }
}