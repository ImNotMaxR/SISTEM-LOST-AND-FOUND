package com.managers;

import com.database.DBConnection;
import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.enumeration.Role;
import com.exception.ValidationException;
import com.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReportManagerTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;
    
    private ReportManager reportManager;
    private ItemManager mockItemManager;
    private StorageManager mockStorageManager;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockDBConnection = mock(DBConnection.class);
        
        DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
        when(mockConnection.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumns(any(), any(), anyString(), anyString())).thenReturn(mockResultSet);

        DBConnection.setInstanceForTest(mockDBConnection);
        when(mockDBConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mock(java.sql.Statement.class));
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        when(mockResultSet.next()).thenReturn(false); // No existing reports in DB for easy testing

        reportManager = new ReportManager();
        mockItemManager = mock(ItemManager.class);
        mockStorageManager = mock(StorageManager.class);
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testCreateLostReportSuccess() throws ValidationException {
        User user = new Mahasiswa("U001", "Mahasiswa 1", "mhs1", "pass", "123", "Fasilkom", "TI", "A");
        Category category = new Category("CAT01", "Elektronik");
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("test.jpg");
        when(mockPhoto.length()).thenReturn(1000L);
        when(mockPhoto.getAbsolutePath()).thenReturn("/path/to/photo.jpg");

        assertDoesNotThrow(() -> {
            reportManager.createLostReport(user, "HP", "HP Samsung Hitam", "Kantin", "Hilang saat makan", category, mockPhoto, mockItemManager);
        });

        assertEquals(1, reportManager.getAllReports().size());
        assertTrue(reportManager.getAllReports().get(0) instanceof LostReport);
    }
    
    @Test
    public void testCreateLostReportValidationFailed() {
        User user = new Mahasiswa("U001", "Mahasiswa 1", "mhs1", "pass", "123", "Fasilkom", "TI", "A");
        Category category = new Category("CAT01", "Elektronik");

        // Missing item name
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reportManager.createLostReport(user, "", "HP Samsung Hitam", "Kantin", "Hilang saat makan", category, null, mockItemManager);
        });
        
        assertEquals("Semua Input Wajib Diisi Sebelum Menyimpan Laporan.", exception.getMessage());
    }
    
    @Test
    public void testCreateFoundReportSuccess() throws ValidationException {
        Security security = new Security("S001", "Sec 1", "sec1", "pass", "SEC-01", "Pusat");
        Category category = new Category("CAT01", "Elektronik");
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("test.jpg");
        when(mockPhoto.length()).thenReturn(1000L);
        when(mockPhoto.getAbsolutePath()).thenReturn("/path/to/photo.jpg");

        assertDoesNotThrow(() -> {
            reportManager.createFoundReport(security, "HP", "HP Samsung Hitam", "Kantin", "Locker A", "Ditemukan di meja", category, null, mockPhoto, mockItemManager, mockStorageManager);
        });

        assertEquals(1, reportManager.getAllReports().size());
        assertTrue(reportManager.getAllReports().get(0) instanceof FoundReport);
    }

    @Test
    public void testCreateFoundReportNotSecurity() {
        User mhs = new Mahasiswa("U001", "Mahasiswa 1", "mhs1", "pass", "123", "Fasilkom", "TI", "A");
        Category category = new Category("CAT01", "Elektronik");
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("test.jpg");
        when(mockPhoto.length()).thenReturn(1000L);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reportManager.createFoundReport(mhs, "HP", "HP Samsung", "Kantin", "Locker", "Desk", category, null, mockPhoto, mockItemManager, mockStorageManager);
        });
        
        assertEquals("Hanya Security yang dapat membuat Laporan Barang Ditemukan.", exception.getMessage());
        assertEquals(0, reportManager.getAllReports().size());
    }

    @Test
    public void testValidateReport() throws ValidationException {
        Admin admin = new Admin("A001", "Admin", "admin", "pass", "ADM-01");
        
        // Add a dummy report directly
        User user = new Mahasiswa("U001", "Mhs", "mhs", "pass", "123", "F", "T", "A");
        Item item = new Item("I001", "Dompet", "Hitam", new Category("C01", "Acc"), "Kantin");
        LostReport report = new LostReport("R001", user, item, "Hilang", "Kantin");
        reportManager.addReport(report);
        
        reportManager.validateReport("R001", ReportStatus.VALID, admin);
        
        assertEquals(ReportStatus.VALID, reportManager.findById("R001") != null ? ((Report)reportManager.findById("R001")).getStatus() : null);
    }
}
