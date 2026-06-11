package com.model;

import com.enumeration.Role;

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
        this.role = Role.MAHASISWA;
    }

    public String getNim() {
        return nim;
    }

    public String getFakultas() {
        return fakultas;
    }

    public String getJurusan() {
        return jurusan;
    }

    public String getKelas() {
        return kelas;
    }

    @Override
    public Role getRole() {
        return Role.MAHASISWA;
    }

    @Override
    public void displayInfo() {
        System.out.println("===== Info Mahasiswa =====");
        System.out.println("User ID  : " + getUserId());
        System.out.println("Nama     : " + getName());
        System.out.println("Username : " + getUsername());
        System.out.println("NIM      : " + nim);
        System.out.println("Fakultas : " + fakultas);
        System.out.println("Jurusan  : " + jurusan);
        System.out.println("Kelas    : " + kelas);
        System.out.println("Role     : " + getRole());
        System.out.println("==========================");
    }
    
    @Override
    public String toString() {
        return "Mahasiswa{userId='" + getUserId() + "', name='" + getName() + "', nim='" + nim + "', jurusan='" + jurusan + "'}";
    }
    
}
