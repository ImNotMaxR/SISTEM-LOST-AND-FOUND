/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

import com.enumeration.Role;

/**
 *
 * @author MaxR
 */
public class Mahasiswa extends User {
    private String nim;
    private String fakultas;
    private String jurusan;
    private String kelas;

    public Mahasiswa(String userId, String name, String username, String password, String nim, String fakultas, String jurusan, String kelas) {
        super(userId, name, username, password);
        this.nim = nim;
        this.fakultas = fakultas;
        this.jurusan = jurusan;
        this.kelas = kelas;
    }
        
    @Override
    public Role getRole() {
        return Role.MAHASISWA;
    }

    @Override
    public void displayInfo() {

    }

    
}
