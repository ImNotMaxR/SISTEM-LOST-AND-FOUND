package com.model;

import com.enumeration.Role;

/**
 *
 * @author MaxR
 */
// Inheritance & Encapsulation
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
    // Polymorphism (Method Overriding)
    @Override
    public Role getRole() {
        return Role.SECURITY;
    }
    // Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("===== Info Security =====");
        System.out.println("User ID     : " + getUserId());
        System.out.println("Nama        : " + getName());
        System.out.println("Username    : " + getUsername());
        System.out.println("Security ID : " + securityID);
        System.out.println("Bagian      : " + bagian);
        System.out.println("Role        : " + getRole());
        System.out.println("=========================");
    }
 
    public FoundReport createFoundReport(Item item, String foundLocation) {
        String reportId = "FR-" + System.currentTimeMillis();
        System.out.println("Petugas " + getName() + " membuat laporan barang ditemukan untuk: " + item.getName());
        return new FoundReport(reportId, this, item, "Barang ditemukan oleh petugas keamanan", foundLocation);
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Security{userId='" + getUserId() + "', name='" + getName() + "', securityID='" + securityID + "', bagian='" + bagian + "'}";
    }
}
