package com.managers;

import com.database.DBConnection;
import com.enumeration.ItemStatus;
import com.exception.ValidationException;
import com.model.Category;
import com.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ItemManagerTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;
    
    private ItemManager itemManager;

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
        
        // Mock DB load
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("item_id")).thenReturn("ITM001");
        when(mockResultSet.getString("name")).thenReturn("Dompet Hitam");
        when(mockResultSet.getString("description")).thenReturn("Dompet kulit");
        when(mockResultSet.getString("status")).thenReturn(ItemStatus.DITEMUKAN.name());
        when(mockResultSet.getString("location")).thenReturn("Gudang A");
        when(mockResultSet.getString("category_id")).thenReturn("CAT01");
        when(mockResultSet.getString("category_name")).thenReturn("Aksesoris");
        when(mockResultSet.getTimestamp("date")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        itemManager = new ItemManager();
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testGetItemByIdSuccess() {
        Item item = itemManager.findItem("ITM001");
        
        assertNotNull(item);
        assertEquals("Dompet Hitam", item.getName());
        assertEquals("Dompet kulit", item.getDescription());
        assertEquals("Gudang A", item.getLocation());
        assertEquals(ItemStatus.DITEMUKAN, item.getStatus());
        assertNotNull(item.getCategory());
        assertEquals("Aksesoris", item.getCategory().getName());
    }
    
    @Test
    public void testGetItemByIdNotFound() {
        Item item = itemManager.findItem("UNKNOWN_ID");
        
        assertNull(item);
    }
    
    @Test
    public void testAddItemSuccess() {
        Category cat = new Category("CAT02", "Elektronik");
        Item newItem = new Item("ITM002", "Laptop ASUS", "Laptop warna silver", cat, "Ruang Dosen");
        newItem.setStatus(ItemStatus.DICARI);
        
        assertDoesNotThrow(() -> itemManager.addItem(newItem));
        
        Item found = itemManager.findItem("ITM002");
        assertNotNull(found);
        assertEquals("Laptop ASUS", found.getName());
    }
    
    @Test
    public void testAddItemValidationFailed() {
        Item newItem = new Item("ITM002", "", "Laptop warna silver", null, "Ruang Dosen"); // Empty name
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            itemManager.addItem(newItem);
        });
        
        assertEquals("Data barang tidak lengkap.", exception.getMessage());
    }

    @Test
    public void testSearchItems() {
        var results = itemManager.searchItems("dompet");
        assertEquals(1, results.size());
        assertEquals("Dompet Hitam", results.get(0).getName());
        
        var noResults = itemManager.searchItems("laptop");
        assertEquals(0, noResults.size());
    }
}
