package com.managers;

import com.database.DBConnection;
import com.exception.ValidationException;
import com.model.Category;
import com.model.Item;
import com.model.Security;
import com.model.StorageRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StorageManagerTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;
    
    private StorageManager storageManager;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockDBConnection = mock(DBConnection.class);

        DBConnection.setInstanceForTest(mockDBConnection);
        when(mockDBConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        when(mockResultSet.next()).thenReturn(false);

        storageManager = new StorageManager();
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testSaveStorageRecordSuccess() throws ValidationException {
        Security security = new Security("S001", "Security", "sec", "pass", "1", "1");
        Item item = new Item("I001", "Tas", "Hitam", new Category("C1", "Lainnya"), "Kantin");
        
        StorageRecord record = new StorageRecord("STR001", item, security, "Rak A1");

        assertDoesNotThrow(() -> {
            storageManager.saveStorageRecordToDB(record);
        });

        assertEquals(1, storageManager.getStorageRecords().size());
        assertEquals("Rak A1", storageManager.getStorageRecords().get(0).getStorageLocation());
    }
    
    @Test
    public void testSaveStorageRecordEmptyLocation() {
        Security security = new Security("S001", "Security", "sec", "pass", "1", "1");
        Item item = new Item("I001", "Tas", "Hitam", new Category("C1", "Lainnya"), "Kantin");
        
        StorageRecord record = new StorageRecord("STR001", item, security, ""); // empty location

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            storageManager.saveStorageRecordToDB(record);
        });
        
        assertEquals("Lokasi penyimpanan tidak boleh kosong.", exception.getMessage());
    }

    @Test
    public void testUpdateStorageRecord() throws ValidationException {
        Security security = new Security("S001", "Security", "sec", "pass", "1", "1");
        Item item = new Item("I001", "Tas", "Hitam", new Category("C1", "Lainnya"), "Kantin");
        StorageRecord record = new StorageRecord("STR001", item, security, "Rak A1");
        
        storageManager.saveStorageRecordToDB(record);
        
        record.setStorageLocation("Rak B2");
        
        assertDoesNotThrow(() -> {
            storageManager.updateStorageRecordDB(record);
        });
        
        StorageRecord updated = (StorageRecord) storageManager.findById("STR001");
        assertEquals("Rak B2", updated.getStorageLocation());
    }
}
