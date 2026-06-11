package com.model;

import com.enumeration.Role;

public class Admin extends User{
    private String adminID;

    public Admin(String userId, String name, String username, String password, String adminID) {
        super(userId, name, username, password);
        this.adminID = adminID;
        this.role = Role.ADMIN;
    }

    public String getAdminID() {
        return adminID;
    }
    
    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public void displayInfo() {
        
    }
    
    public void validateReport() {
        
    }

    public void verifyClaim() {
        
    }
}
