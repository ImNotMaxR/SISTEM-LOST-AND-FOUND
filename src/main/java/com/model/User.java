package com.model;

import com.enumeration.Role;
import com.interfaces.AuthServices;

public abstract class User implements AuthServices{
    protected String userId;
    protected String name;
    private String username;
    private String password;
    protected Role role;

    public User(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }
    
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean checkPassword(String inputPW) {
        return this.password.equals(inputPW);
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (checkPassword(oldPassword)) {
            this.password = newPassword;
            return true;
        } else {
            return false;
        }
    }

    public abstract Role getRole();
    
    @Override
    public void login(){
        System.out.println(name + " Berhasil Login Sebagai " + getRole());
    }

    @Override
    public void logout(){
        System.out.println(name + " Berhasil LogOut");
    }

    public abstract void displayInfo();
    
    @Override
    public String toString() {
        return "ID User: " + userId + ", Nama: "+ name + ", Role: " + getRole();
    }
}
