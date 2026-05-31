/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

/**
 *
 * @author MaxR
 */
public class Category {
    private String userId;
    private String name;
    private boolean requestVerification;

    public Category(String userId, String name, boolean requestVerification) {
        this.userId = userId;
        this.name = name;
        this.requestVerification = requestVerification;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public boolean isVerificationRequired() {
        return requestVerification;
    }

    @Override
    public String toString() {
        return "Category: " + name +
               " | Verification: " + requestVerification;
    }
}
