package com.managers;

import com.interfaces.Managerable;
import com.model.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemManager implements Managerable{
    private ArrayList<Item> items;

    public ItemManager() {
        items = new ArrayList<>();
    }
    
    @Override
    public void add(Object obj) {
    }

    @Override
    public void delete(String id) {
    }

    @Override
    public Object findById(String id) {
        return null;
    }

    public void addItem() {
    }

    public void editItem(String itemId, Item updatedItem) {
        
    }

    public void deleteItem(String itemId) {
        items.remove(findItem(itemId));
    }

    public Item findItem(String itemId) {
        for (Item item : items) {
            if (item.getItemID().equals(item)) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }
    
    public ArrayList<Item> getItemByStatus() {
        return null;
    }
}