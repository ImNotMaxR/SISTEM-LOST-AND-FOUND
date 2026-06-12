/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.database;

/**
 *
 * @author MaxR
 */
import java.sql.*;

public class DBInitializer {
    private DBConnection dbConnection;

    public DBInitializer() {
        dbConnection = dbConnection.getInstance();
    }
    
    //Buat Table Table database nya tapi belom beres nanti dulu satu satu pusing gua
    public void createTables(){
        String sql = "CREATE TABLE IF NOT EXISTS users(user_id VARCHAR(20) PRIMARY KEY, name VARCHAR(100), username VARCHAR(50), password VARCHAR(100), role VARCHAR(20))";
    }
    
    public void insertDataDummy(){
        
    }
    
    public void initialize(){
        
    }
}
