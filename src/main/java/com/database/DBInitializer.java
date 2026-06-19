package com.database;

import java.sql.*;

public class DBInitializer {
    private DBConnection dbConnection;

    public DBInitializer() {
        dbConnection = DBConnection.getInstance();
    }
     
    public void initialize(){
        System.out.println("Initialisasi database...");
        createTables();
        insertDataDummy();
        System.out.println("Database siap digunakan.");
    }
    
    //Buat Table Table database nya tapi belom beres nanti dulu satu satu pusing gua
    public void createTables() {
        Connection conn = dbConnection.getConnection();
        try {
            Statement st = conn.createStatement();
 
            // Tabel users (induk semua role)
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id  VARCHAR(50)  PRIMARY KEY, " +
                "name     VARCHAR(100) NOT NULL, " +
                "username VARCHAR(50)  NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "role     ENUM('MAHASISWA','DOSEN','STAFF','SECURITY','ADMIN') NOT NULL)"
            );
 
            // Tabel mahasiswa
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS mahasiswa (" +
                "user_id  VARCHAR(50) PRIMARY KEY, " +
                "nim      VARCHAR(20), " +
                "fakultas VARCHAR(100), " +
                "jurusan  VARCHAR(100), " +
                "kelas    VARCHAR(20), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
 
            // Tabel dosen
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS dosen (" +
                "user_id VARCHAR(50) PRIMARY KEY, " +
                "nip     VARCHAR(20), " +
                "bidang  VARCHAR(100), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
 
            // Tabel staff
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS staff (" +
                "user_id  VARCHAR(50) PRIMARY KEY, " +
                "staff_id VARCHAR(50), " +
                "bagian   VARCHAR(100), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
 
            // Tabel admin
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS admin (" +
                "user_id  VARCHAR(50) PRIMARY KEY, " +
                "admin_id VARCHAR(50), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
 
            // Tabel security
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS security (" +
                "user_id     VARCHAR(50) PRIMARY KEY, " +
                "security_id VARCHAR(50), " +
                "bagian      VARCHAR(100), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
            );
 
            // Tabel categories
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "category_id          VARCHAR(50)  PRIMARY KEY, " +
                "name                 VARCHAR(100) NOT NULL, " +
                "request_verification BOOLEAN      NOT NULL DEFAULT FALSE)"
            );
 
            // Tabel items
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS items (" +
                "item_id     VARCHAR(50)  PRIMARY KEY, " +
                "name        VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "category_id VARCHAR(50), " +
                "status      ENUM('DICARI','DITEMUKAN','DIKLAIM') NOT NULL DEFAULT 'DICARI', " +
                "location    VARCHAR(200), " +
                "date        DATETIME NOT NULL, " +
                "FOREIGN KEY (category_id) REFERENCES categories(category_id))"
            );
 
            // Tabel reports (LOST dan FOUND digabung, dibedakan kolom type)
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reports (" +
                "report_id              VARCHAR(50) PRIMARY KEY, " +
                "user_id                VARCHAR(50) NOT NULL, " +
                "item_id                VARCHAR(50) NOT NULL, " +
                "type                   ENUM('LOST','FOUND') NOT NULL, " +
                "description            TEXT, " +
                "status                 ENUM('PENDING','VALID','DITOLAK') NOT NULL DEFAULT 'PENDING', " +
                "date                   DATETIME NOT NULL, " +
                "editable_until         DATETIME NOT NULL, " +
                "photo_path             VARCHAR(255) NULL, " +
                "rejection_reason       TEXT NULL, " +
                "lost_location          VARCHAR(200) NULL, " +
                "found_location         VARCHAR(200) NULL, " +
                "matched_lost_report_id VARCHAR(50)  NULL, " +
                "FOREIGN KEY (user_id)  REFERENCES users(user_id), " +
                "FOREIGN KEY (item_id)  REFERENCES items(item_id))"
            );
 
            // Tabel claims
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS claims (" +
                "claim_id          VARCHAR(50) PRIMARY KEY, " +
                "user_id           VARCHAR(50) NOT NULL, " +
                "item_id           VARCHAR(50) NOT NULL, " +
                "status            ENUM('PENDING','VALID','DITOLAK') NOT NULL DEFAULT 'PENDING', " +
                "date_claim        DATETIME NOT NULL, " +
                "related_report_id VARCHAR(50) NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                "FOREIGN KEY (item_id) REFERENCES items(item_id))"
            );
 
            // Tabel verification_documents
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS verification_documents (" +
                "document_id VARCHAR(50)  PRIMARY KEY, " +
                "claim_id    VARCHAR(50)  NOT NULL, " +
                "type        VARCHAR(100) NOT NULL, " +
                "file_path   VARCHAR(255) NULL, " +
                "description TEXT, " +
                "FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE)"
            );
 
            // Tabel storage_records
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS storage_records (" +
                "record_id        VARCHAR(50) PRIMARY KEY, " +
                "item_id          VARCHAR(50) NOT NULL, " +
                "security_user_id VARCHAR(50) NOT NULL, " +
                "storage_location VARCHAR(200), " +
                "date_stored      DATETIME NOT NULL, " +
                "is_released      BOOLEAN  NOT NULL DEFAULT FALSE, " +
                "date_released    DATETIME NULL, " +
                "FOREIGN KEY (item_id)          REFERENCES items(item_id), " +
                "FOREIGN KEY (security_user_id) REFERENCES users(user_id))"
            );
 
            System.out.println("Semua tabel berhasil dibuat.");
        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel: " + e.getMessage());
        }
    }
    
    public void insertDataDummy() {
        Connection conn = dbConnection.getConnection();
        try {
            Statement st = conn.createStatement();
 
            // Cek apakah data sudah ada, jika sudah skip
            var rs = st.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("[DBInitializer] Data dummy sudah ada, skip insert.");
                return;
            }
 
            // ---- USERS ----
            // Admin
            st.executeUpdate("INSERT INTO users VALUES ('USR001','Admin Sistem','admin','password123','ADMIN')");
            st.executeUpdate("INSERT INTO admin VALUES ('USR001','ADM001')");
 
            // Security
            st.executeUpdate("INSERT INTO users VALUES ('USR002','Budi Santoso','budi.security','password123','SECURITY')");
            st.executeUpdate("INSERT INTO security VALUES ('USR002','SEC001','Gedung A')");
 
            st.executeUpdate("INSERT INTO users VALUES ('USR003','Rudi Hartono','rudi.security','password123','SECURITY')");
            st.executeUpdate("INSERT INTO security VALUES ('USR003','SEC002','Gedung B')");
 
            // Mahasiswa
            st.executeUpdate("INSERT INTO users VALUES ('USR004','Rico Rezkya','rico.mhs','password123','MAHASISWA')");
            st.executeUpdate("INSERT INTO mahasiswa VALUES ('USR004','103022500008','FIF','Informatika','IF-46-01')");
 
            st.executeUpdate("INSERT INTO users VALUES ('USR005','Evan Oktavianus','evan.mhs','password123','MAHASISWA')");
            st.executeUpdate("INSERT INTO mahasiswa VALUES ('USR005','103022530018','FIF','Informatika','IF-46-02')");
 
            st.executeUpdate("INSERT INTO users VALUES ('USR006','Fadil Fauzi','fadil.mhs','password123','MAHASISWA')");
            st.executeUpdate("INSERT INTO mahasiswa VALUES ('USR006','103022530008','FIF','Informatika','IF-46-02')");
 
            // Dosen
            st.executeUpdate("INSERT INTO users VALUES ('USR007','Aaz M. Hafidz Azis','aaz.dosen','password123','DOSEN')");
            st.executeUpdate("INSERT INTO dosen VALUES ('USR007','NIP001','Pemrograman Berorientasi Objek')");
 
            // Staff
            st.executeUpdate("INSERT INTO users VALUES ('USR008','Siti Aminah','siti.staff','password123','STAFF')");
            st.executeUpdate("INSERT INTO staff VALUES ('USR008','STF001','Tata Usaha')");
 
            // ---- KATEGORI ----
            st.executeUpdate("INSERT INTO categories VALUES ('CAT001','Elektronik',false)");
            st.executeUpdate("INSERT INTO categories VALUES ('CAT002','Dompet / Tas',false)");
            st.executeUpdate("INSERT INTO categories VALUES ('CAT003','Kunci Kendaraan',true)");
            st.executeUpdate("INSERT INTO categories VALUES ('CAT004','Kartu Identitas',false)");
            st.executeUpdate("INSERT INTO categories VALUES ('CAT005','Pakaian',false)");
            st.executeUpdate("INSERT INTO categories VALUES ('CAT006','Lainnya',false)");
 
            // ---- ITEM & REPORT CONTOH ----
            // Item 1: laptop hilang milik Rico, LostReport status PENDING
            st.executeUpdate(
                "INSERT INTO items VALUES " +
                "('ITM001','Laptop ASUS VivoBook','Laptop abu-abu, ada stiker di cover','CAT001','DICARI','Lab FIF Lt.2',NOW())"
            );
            st.executeUpdate(
                "INSERT INTO reports VALUES " +
                "('RPT001','USR004','ITM001','LOST','Laptop hilang setelah kuliah DPBO'," +
                "'PENDING',NOW(),DATE_ADD(NOW(), INTERVAL 30 MINUTE),NULL,NULL,'Lab Komputer FIF Lt.2',NULL,NULL)"
            );
 
            // Item 2: kunci motor ditemukan Budi Security, FoundReport status VALID
            st.executeUpdate(
                "INSERT INTO items VALUES " +
                "('ITM002','Kunci Motor Honda Beat','Kunci dengan gantungan merah','CAT003','DITEMUKAN','Parkiran Gedung A',NOW())"
            );
            st.executeUpdate(
                "INSERT INTO reports VALUES " +
                "('RPT002','USR002','ITM002','FOUND','Ditemukan di parkiran Gedung A pagi hari'," +
                "'VALID',NOW(),DATE_ADD(NOW(), INTERVAL 30 MINUTE),NULL,NULL,NULL,'Parkiran Gedung A',NULL)"
            );
 
            // StorageRecord untuk kunci motor
            st.executeUpdate(
                "INSERT INTO storage_records VALUES " +
                "('SRD001','ITM002','USR002','Pos Keamanan Gedung A',NOW(),false,NULL)"
            );
 
            System.out.println("Data dummy berhasil dimasukkan.");
        } catch (SQLException e) {
            System.out.println("Gagal insert data dummy: " + e.getMessage());
        }
    }
}
