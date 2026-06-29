package com.managers;

import com.database.DBConnection;
import com.enumeration.Role;
import com.exception.ValidationException;
import com.model.Mahasiswa;
import com.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserManagerTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;
    
    private UserManager userManager;

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
        
        // Mock data when loading users
        when(mockResultSet.next()).thenReturn(true, false); // One user, then end
        when(mockResultSet.getString("user_id")).thenReturn("U001");
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("role")).thenReturn(Role.MAHASISWA.name());
        when(mockResultSet.getString("nim")).thenReturn("12345");

        userManager = new UserManager();
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testLoadAllUsers() {
        ArrayList<User> users = userManager.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }
    
    @Test
    public void testEditUsernameSuccess() throws ValidationException, SQLException {
        User user = userManager.getUserById("U001");
        
        userManager.editUser(user, "password123", "newuser");
        
        assertEquals("newuser", user.getUsername());
        verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
    }

    @Test
    public void testEditUsernameWrongOldPassword() {
        User user = userManager.getUserById("U001");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userManager.editUser(user, "wrongpass", "newuser");
        });
        assertEquals("Password lama yang Anda masukkan salah.", exception.getMessage());
    }

    @Test
    public void testEditUsernameEmptyData() {
        User user = userManager.getUserById("U001");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userManager.editUser(user, "", "newuser");
        });
        assertEquals("Password Lama Dan Username Baru Wajib Diisi!", exception.getMessage());
    }
    
    @Test
    public void testEditPasswordSuccess() throws ValidationException, SQLException {
        User user = userManager.getUserById("U001");
        
        userManager.editUser(user, "password123", "NewPass@123", "NewPass@123");
        
        assertEquals("NewPass@123", user.getPassword());
        verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
    }
    
    @Test
    public void testEditPasswordNoMatch() {
        User user = userManager.getUserById("U001");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userManager.editUser(user, "password123", "NewPass@123", "WrongPass@123");
        });
        assertEquals("Password baru dan konfirmasi tidak cocok.", exception.getMessage());
    }
    
    @Test
    public void testEditPasswordTooShort() {
        User user = userManager.getUserById("U001");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userManager.editUser(user, "password123", "short", "short");
        });
        assertEquals("Password baru minimal harus 8 karakter.", exception.getMessage());
    }

    @Test
    public void testEditPasswordNoSpecialChar() {
        User user = userManager.getUserById("U001");
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userManager.editUser(user, "password123", "newpassword", "newpassword");
        });
        assertEquals("Password baru harus mengandung minimal 1 karakter spesial.", exception.getMessage());
    }
}
