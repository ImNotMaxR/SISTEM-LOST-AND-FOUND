package com.mycompany.sistemlostfound;

import com.database.DBConnection;
import java.sql.*;

public class SistemLostFound {
    public static void main(String[] args) {
        DBConnection db = DBConnection.getInstance();
        Connection conn = db.getConnection();
        if (conn != null) {
            System.out.println("Koneksi sukses!");
        }
    }
}