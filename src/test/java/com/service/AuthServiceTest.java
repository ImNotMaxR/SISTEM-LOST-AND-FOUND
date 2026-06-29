package com.service;

import com.database.DBConnection;
import com.enumeration.Role;
import com.exception.ValidationException;
import com.model.Admin;
import com.model.Mahasiswa;
import com.model.Security;
import com.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private DBConnection mockDBConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockDBConnection = mock(DBConnection.class);

        // Set mock DBConnection
        DBConnection.setInstanceForTest(mockDBConnection);
        when(mockDBConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Reset AuthService state
        new AuthService().logout(); // Ensure no one is logged in
    }

    @AfterEach
    public void tearDown() {
        DBConnection.setInstanceForTest(null);
    }

    @Test
    public void testLoginSuccessMahasiswa() throws SQLException, ValidationException {
        // Setup mock ResultSet for a valid Mahasiswa user
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("user_id")).thenReturn("U001");
        when(mockResultSet.getString("name")).thenReturn("Test Mahasiswa");
        when(mockResultSet.getString("username")).thenReturn("testmhs");
        when(mockResultSet.getString("role")).thenReturn(Role.MAHASISWA.name());
        when(mockResultSet.getString("nim")).thenReturn("123456");

        User user = AuthService.login("testmhs", "password123");

        assertNotNull(user);
        assertTrue(user instanceof Mahasiswa);
        assertEquals(Role.MAHASISWA, user.getRole());
        assertEquals("testmhs", user.getUsername());
        
        // Verify currentUser in AuthService
        assertTrue(AuthService.isLoggedIn());
        assertEquals(user, AuthService.getCurrentUser());
    }
    
    @Test
    public void testLoginSuccessAdmin() throws SQLException, ValidationException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("adminpass");
        when(mockResultSet.getString("user_id")).thenReturn("A001");
        when(mockResultSet.getString("name")).thenReturn("Test Admin");
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("role")).thenReturn(Role.ADMIN.name());
        when(mockResultSet.getString("admin_id")).thenReturn("ADM-001");

        AuthService authService = new AuthService();
        User user = AuthService.login("admin", "adminpass");

        assertNotNull(user);
        assertTrue(user instanceof Admin);
        assertEquals(Role.ADMIN, user.getRole());
        
        assertTrue(authService.isAdmin());
        assertFalse(authService.isSecurity());
    }
    
    @Test
    public void testLoginSuccessSecurity() throws SQLException, ValidationException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("secpass");
        when(mockResultSet.getString("user_id")).thenReturn("S001");
        when(mockResultSet.getString("name")).thenReturn("Test Security");
        when(mockResultSet.getString("username")).thenReturn("security");
        when(mockResultSet.getString("role")).thenReturn(Role.SECURITY.name());
        when(mockResultSet.getString("security_id")).thenReturn("SEC-001");

        AuthService authService = new AuthService();
        User user = AuthService.login("security", "secpass");

        assertNotNull(user);
        assertTrue(user instanceof Security);
        assertEquals(Role.SECURITY, user.getRole());
        
        assertTrue(authService.isSecurity());
        assertFalse(authService.isAdmin());
    }

    @Test
    public void testLoginEmptyCredentials() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AuthService.login("", "");
        });
        assertEquals("Username dan Password Wajib diisi.", exception.getMessage());
    }

    @Test
    public void testLoginUserNotFound() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AuthService.login("unknown", "password");
        });
        assertEquals("Username Anda Tidak Ditemukan.", exception.getMessage());
    }

    @Test
    public void testLoginWrongPassword() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("correctpass");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AuthService.login("user", "wrongpass");
        });
        assertEquals("Password Anda Salah", exception.getMessage());
    }

    @Test
    public void testLogout() throws SQLException, ValidationException {
        // First login
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("pass");
        when(mockResultSet.getString("user_id")).thenReturn("U001");
        when(mockResultSet.getString("role")).thenReturn(Role.MAHASISWA.name());
        AuthService.login("user", "pass");
        
        assertTrue(AuthService.isLoggedIn());
        
        // Then logout
        AuthService authService = new AuthService();
        authService.logout();
        
        assertFalse(AuthService.isLoggedIn());
        assertNull(AuthService.getCurrentUser());
    }
}
