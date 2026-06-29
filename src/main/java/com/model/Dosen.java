package com.model;

import com.enumeration.Role;
// Inheritance & Encapsulation
public class Dosen extends User{
    private String nip;
    private String bidang;

    public Dosen(String userId, String name, String username, String password, String nip, String bidang) {
        super(userId, name, username, password);
        this.nip = nip;
        this.bidang = bidang;
        this.role = Role.DOSEN;
    }

    public String getNip() {
        return nip;
    }

    public String getBidang() {
        return bidang;
    }
    // Polymorphism (Method Overriding)
    @Override
    public Role getRole() {
        return Role.DOSEN;
    }
    // Polymorphism (Method Overriding)
    @Override
    public void displayInfo() {
        System.out.println("===== Info Dosen =====");
        System.out.println("User ID  : " + getUserId());
        System.out.println("Nama     : " + getName());
        System.out.println("Username : " + getUsername());
        System.out.println("NIP      : " + nip);
        System.out.println("Bidang   : " + bidang);
        System.out.println("Role     : " + getRole());
        System.out.println("======================");
    }
    // Polymorphism (Method Overriding)
    @Override
    public String toString() {
        return "Dosen{userId='" + getUserId() + "', name='" + getName() + "', nip='" + nip + "', bidang='" + bidang + "'}";
    }
}
