/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

import com.enumeration.Role;
import com.interfaces.AuthServices;

/**
 *
 * @author MaxR
 */
public abstract class User implements AuthServices{
    protected String userId;
    protected String name;
    private String username;
    private String password;

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
    
    public boolean checkPassword(String password) {
        return false;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        return false;
    }

    public abstract Role getRole();
    
    @Override
    public void login(){
        
    }

    @Override
    public void logout(){
        
    }

    public abstract void displayInfo();

}
