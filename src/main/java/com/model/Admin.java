package com.model;

import com.enumeration.Role;
// Inheritance & Encapsulation
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
    // Polymorphism (Method Overriding)
    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
    // Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("===== Info Admin =====");
        System.out.println("User ID  : " + getUserId());
        System.out.println("Nama     : " + getName());
        System.out.println("Username : " + getUsername());
        System.out.println("Admin ID : " + adminID);
        System.out.println("Role     : " + getRole());
        System.out.println("======================");
    }
    
    public void validateReport(String reportId) {
        System.out.println("Laporan dengan ID " + reportId + " divalidasi oleh " + getName());
    }
 
    public void verifyClaim(String claimId) {
        System.out.println("Klaim dengan ID " + claimId + " diverifikasi oleh " + getName());
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Admin{userId='" + getUserId() + "', name='" + getName() + "', adminID='" + adminID + "'}";
    }
}
