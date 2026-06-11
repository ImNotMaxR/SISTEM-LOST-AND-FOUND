package com.model;

import com.enumeration.Role;

/**
 *
 * @author MaxR
 */
public class Security extends User{
    private String securityID;
    private String bagian;

    public Security(String userId, String name, String username, String password, String securityID, String bagian) {
        super(userId, name, username, password);
        this.securityID = securityID;
        this.bagian = bagian;
        this.role = Role.SECURITY;
    }

    public String getSecurityID() {
        return securityID;
    }

    public String getBagian() {
        return bagian;
    }
    
    
    @Override
    public Role getRole() {
        return Role.SECURITY;
    }

    @Override
    public void displayInfo() {
        
    }
    
    public void createFoundReport() {
    }
}
