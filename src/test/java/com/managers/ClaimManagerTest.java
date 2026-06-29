package com.managers;

import com.database.DBConnection;
import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.enumeration.Role;
import com.exception.ValidationException;
import com.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ClaimManagerTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;
    
    private ClaimManager claimManager;

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

        claimManager = new ClaimManager();
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testSubmitClaimSuccess() throws ValidationException {
        User user = new Mahasiswa("U001", "Mahasiswa", "mhs", "pass", "123", "F", "T", "A");
        Item item = new Item("I001", "Dompet", "Hitam", new Category("C1", "Acc"), "Gudang");
        item.setStatus(ItemStatus.DITEMUKAN);
        
        FoundReport report = new FoundReport("R001", new Security("S01", "Sec", "sec", "pass", "1", "1"), item, "Ditemukan", "Kantin");
        
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("bukti.jpg");
        when(mockPhoto.length()).thenReturn(1000L);
        when(mockPhoto.getAbsolutePath()).thenReturn("/path/to/bukti.jpg");
        when(mockPhoto.exists()).thenReturn(true);

        assertDoesNotThrow(() -> {
            claimManager.submitClaim(user, report, "Budi", "Jl. Mawar", "08123", mockPhoto);
        });

        assertEquals(1, claimManager.getClaims().size());
        assertEquals(ClaimStatus.PENDING, claimManager.getClaims().get(0).getStatus());
    }

    @Test
    public void testSubmitClaimItemNotDitemukan() {
        User user = new Mahasiswa("U001", "Mahasiswa", "mhs", "pass", "123", "F", "T", "A");
        Item item = new Item("I001", "Dompet", "Hitam", new Category("C1", "Acc"), "Kantin");
        item.setStatus(ItemStatus.DICARI); // Status dicari, not ditemukan
        
        LostReport report = new LostReport("R001", user, item, "Hilang", "Kantin");
        
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("bukti.jpg");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            claimManager.submitClaim(user, report, "Budi", "Jl. Mawar", "08123", mockPhoto);
        });
        
        assertTrue(exception.getMessage().contains("tidak berstatus DITEMUKAN"));
    }

    @Test
    public void testProcessClaimValid() throws ValidationException {
        User user = new Mahasiswa("U001", "Mahasiswa", "mhs", "pass", "123", "F", "T", "A");
        Item item = new Item("I001", "Dompet", "Hitam", new Category("C1", "Acc"), "Gudang");
        item.setStatus(ItemStatus.DITEMUKAN);
        Claim claim = new Claim("C001", user, item, "R001");
        File mockPhoto = mock(File.class);
        when(mockPhoto.getName()).thenReturn("bukti.jpg");
        when(mockPhoto.exists()).thenReturn(true);
        claim.addDocument(new com.model.VerificationDocument("D01", "KTP", mockPhoto, "desc"));
        
        // Add claim manually to map
        assertDoesNotThrow(() -> claimManager.saveClaim(claim)); // Will pass validation and add to list
        
        Admin admin = new Admin("A001", "Admin", "admin", "pass", "1");
        
        assertDoesNotThrow(() -> {
            claimManager.processClaim("C001", ClaimStatus.VALID, admin, null);
        });
        
        assertEquals(ClaimStatus.VALID, claimManager.findById("C001").getStatus());
    }
}
