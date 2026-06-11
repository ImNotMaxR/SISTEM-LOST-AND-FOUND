package com.model;

import com.enumeration.Role;

public class Staff extends User{
    private String staffID;
    private String bagian;

    public Staff(String userId, String name, String username, String password, String staffID, String bagian) {
        super(userId, name, username, password);
        this.staffID = staffID;
        this.bagian = bagian;
        this.role = Role.STAFF;
    }

    public String getStaffID() {
        return staffID;
    }

    public String getBagian() {
        return bagian;
    }

    @Override
    public Role getRole() {
         return Role.STAFF;
    }

    @Override
    public void displayInfo() {
        
    }
}
