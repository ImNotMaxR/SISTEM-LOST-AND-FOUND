package com.model;

import com.enumeration.Role;

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
    
    @Override
    public Role getRole() {
        return Role.DOSEN;
    }

    @Override
    public void displayInfo() {
        
    }
}
