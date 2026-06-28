package com.managers;

import com.database.DBConnection;
import com.model.Category;
import com.model.Item;
import com.model.Security;
import com.model.StorageRecord;
import com.model.User;
import com.enumeration.Role;
import com.enumeration.ItemStatus;
import com.interfaces.Managerable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class StorageManager implements Managerable {

    private ArrayList<StorageRecord> records;
    private HashMap<String, StorageRecord> recordMap;
    private DBConnection dbConnection;

    public StorageManager() {
        this.records = new ArrayList<>();
        this.recordMap = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllStorageRecordsFromDB();
    }

    private void loadAllStorageRecordsFromDB() {
        records.clear();
        recordMap.clear();
        String sql = "SELECT sr.record_id, sr.storage_location, sr.date_stored, sr.is_released, sr.date_released, "
                   + "i.item_id, i.name AS item_name, i.description AS item_desc, i.status AS item_status, "
                   + "i.location AS item_location, c.category_id, c.name AS category_name "
                   + "u.user_id, u.name AS user_name "
                   + "FROM storage_records sr "
                   + "JOIN items i ON sr.item_id = i.item_id "
                   + "LEFT JOIN categories c ON i.category_id = c.category_id "
                   + "JOIN users u ON sr.security_user_id = u.user_id ORDER BY sr.date_stored DESC";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = null;
                if (rs.getString("category_id") != null) {
                    cat = new Category(rs.getString("category_id"), rs.getString("category_name"));
                }
                Item item = new Item(rs.getString("item_id"), rs.getString("item_name"), rs.getString("item_desc"), cat, rs.getString("item_location"));
                item.setStatus(ItemStatus.valueOf(rs.getString("item_status")));

                Security sec = new Security(rs.getString("user_id"), rs.getString("user_name"), "dummy", "dummy", "", "");

                StorageRecord sr = new StorageRecord(rs.getString("record_id"), item, sec, rs.getString("storage_location"));
                sr.dateStored = rs.getTimestamp("date_stored").toLocalDateTime();
                
                boolean isRel = rs.getBoolean("is_released");
                if (isRel) {
                    sr.setIsReleased(true);
                    if (rs.getTimestamp("date_released") != null) {
                        sr.setDateReleased(rs.getTimestamp("date_released").toLocalDateTime());
                    }
                }
                records.add(sr);
                recordMap.put(sr.getRecordId(), sr);
            }
            System.out.println("Data Storage Record berhasil diload dari database dengan jumlah: " + records.size());
        } catch (SQLException e) {
            System.out.println("Gagal load all storage records: " + e.getMessage());
        }
    }

    @Override
    public void add(Object obj) {
        if (obj instanceof StorageRecord) {
            saveStorageRecordToDB((StorageRecord) obj);
        }
    }

    public void update(Object obj) {
        if (obj instanceof StorageRecord) {
            updateStorageRecordDB((StorageRecord) obj);
        }
    }

    @Override
    public void delete(String id) {
        deleteStorageRecord(id);
    }

    @Override
    public Object findById(String id) {
        return recordMap.get(id);
    }

    public ArrayList<StorageRecord> getStorageRecords() {
        return records;
    }

    public void saveStorageRecordToDB(StorageRecord record) {
        String sql = "INSERT INTO storage_records (record_id, item_id, security_user_id, storage_location, date_stored, is_released, date_released) VALUES (?,?,?,?,?,?,?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, record.getRecordId());
            ps.setString(2, record.getItem().getItemID());
            ps.setString(3, record.getStoredBy().getUserId());
            ps.setString(4, record.getStorageLocation());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(record.getDateStored()));
            ps.setBoolean(6, record.isReleased());
            if (record.getDateReleased() != null) {
                ps.setTimestamp(7, java.sql.Timestamp.valueOf(record.getDateReleased()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            ps.executeUpdate();
            records.add(0, record); // add at top since order is DESC
            recordMap.put(record.getRecordId(), record);
            System.out.println("Storage Record " + record.getRecordId() + " berhasil disimpan ke database.");
        } catch (SQLException e) {
            System.out.println("Gagal simpan storage record: " + e.getMessage());
        }
    }

    public void updateStorageReleasedDB(String recordId) {
        String sql = "UPDATE storage_records SET is_released = true, date_released = NOW() WHERE record_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, recordId);
            ps.executeUpdate();
            
            StorageRecord sr = recordMap.get(recordId);
            if (sr != null) {
                sr.setIsReleased(true);
                sr.setDateReleased(LocalDateTime.now());
            }
            System.out.println("Storage Record " + recordId + " berhasil di-release di database.");
        } catch (SQLException e) {
            System.out.println("Gagal update release storage record: " + e.getMessage());
        }
    }

    public void updateStorageRecordDB(StorageRecord record) {
        String sql = "UPDATE storage_records SET storage_location = ? WHERE record_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, record.getStorageLocation());
            ps.setString(2, record.getRecordId());
            ps.executeUpdate();
            
            StorageRecord sr = recordMap.get(record.getRecordId());
            if (sr != null) {
                sr.setStorageLocation(record.getStorageLocation());
            }
            System.out.println("Storage Record " + record.getRecordId() + " berhasil diupdate.");
        } catch (SQLException e) {
            System.out.println("Gagal update storage record: " + e.getMessage());
        }
    }

    public void deleteStorageRecord(String recordId) {
        String sql = "DELETE FROM storage_records WHERE record_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, recordId);
            ps.executeUpdate();
            
            StorageRecord sr = recordMap.remove(recordId);
            if (sr != null) {
                records.remove(sr);
            }
            System.out.println("Storage Record " + recordId + " berhasil dihapus dari database.");
        } catch (SQLException e) {
            System.out.println("Gagal hapus storage record: " + e.getMessage());
        }
    }

    public ArrayList<StorageRecord> getStorageRecordsBySecurity(String securityUserId) {
        ArrayList<StorageRecord> filtered = new ArrayList<>();
        for (StorageRecord sr : records) {
            if (sr.getStoredBy().getUserId().equals(securityUserId)) {
                filtered.add(sr);
            }
        }
        return filtered;
    }

    public void reload() {
        loadAllStorageRecordsFromDB();
    }
}
