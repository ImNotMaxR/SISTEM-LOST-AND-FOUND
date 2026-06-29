package com.model;

import com.enumeration.Role;
// Inheritance & Encapsulation
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
    // Polymorphism (Method Overriding)
    @Override
    public Role getRole() {
         return Role.STAFF;
    }
    // Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("===== Info Staff =====");
        System.out.println("User ID  : " + getUserId());
        System.out.println("Nama     : " + getName());
        System.out.println("Username : " + getUsername());
        System.out.println("Staff ID : " + staffID);
        System.out.println("Bagian   : " + bagian);
        System.out.println("Role     : " + getRole());
        System.out.println("======================");
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Staff{userId='" + getUserId() + "', name='" + getName() + "', staffID='" + staffID + "', bagian='" + bagian + "'}";
    }
}
