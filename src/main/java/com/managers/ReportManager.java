package com.managers;

import com.database.DBConnection;
import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.enumeration.Role;
import com.interfaces.Managerable;
import com.model.Category;
import com.model.Item;
import com.model.User;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportManager implements Managerable{
    private ArrayList<Report> reports;
    private HashMap<String, Report> reportMap;
    private DBConnection dbConnection;
    
    public ReportManager() {
        this.reports      = new ArrayList<>();
        this.reportMap    = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllReportsFromDB();
    }
    
    private void loadAllReportsFromDB() {
        reports.clear();
        reportMap.clear();
 
        String sql = "SELECT r.report_id, r.type, r.description, r.status, r.date, "
                   + "r.editable_until, r.photo_path, r.lost_location, r.found_location, "
                   + "r.matched_lost_report_id, "
                   + "u.user_id, u.name AS user_name, u.username, u.password, u.role, "
                   + "i.item_id, i.name AS item_name, i.description AS item_desc, "
                   + "i.status AS item_status, i.location AS item_location, i.date AS item_date, "
                   + "c.category_id, c.name AS category_name, c.request_verification "
                   + "FROM reports r "
                   + "JOIN users u ON r.user_id = u.user_id "
                   + "JOIN items i ON r.item_id = i.item_id "
                   + "LEFT JOIN categories c ON i.category_id = c.category_id";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Report report = buildReportFromResultSet(rs);
                if (report != null) {
                    reports.add(report);
                    reportMap.put(report.getReportID(), report);
                }
            }
 
            // Setelah semua report dimuat, proses kecocokan FoundReport dengan LostReport
            resolveMatchedReports();
 
            System.out.println("[ReportManager] " + reports.size() + " report berhasil dimuat dari database.");
        } catch (SQLException e) {
            System.out.println("[ReportManager] Gagal load reports: " + e.getMessage());
        }
    }
    
    private Report buildReportFromResultSet(ResultSet rs) throws SQLException {
        String reportID   = rs.getString("report_id");
        String type       = rs.getString("type");
        String desc       = rs.getString("description");
        String statusStr  = rs.getString("status");
        LocalDateTime date = rs.getTimestamp("date") != null
                           ? rs.getTimestamp("date").toLocalDateTime() : LocalDateTime.now();
        String photoPath  = rs.getString("photo_path");
 
        // Bangun User sederhana dari join, role saja sudah cukup untuk identifikasi
        // Full user object diambil dari UserManager jika dibutuhkan detail lengkap
        String userId   = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String username = rs.getString("username");
        String password = rs.getString("password");
        Role role       = Role.valueOf(rs.getString("role"));
 
        // Bangun Item dari join
        String itemId      = rs.getString("item_id");
        String itemName    = rs.getString("item_name");
        String itemDesc    = rs.getString("item_desc");
        String itemLoc     = rs.getString("item_location");
        String itemStatus  = rs.getString("item_status");
 
        Category category = null;
        String catId = rs.getString("category_id");
        if (catId != null) {
            category = new Category(catId, rs.getString("category_name"), rs.getBoolean("request_verification"));
        }
 
        Item item = new Item(itemId, itemName, itemDesc, category, itemLoc);
        item.setStatus(ItemStatus.valueOf(itemStatus));
 
        // Buat User sesuai role menggunakan instanceof pattern
        User user = buildSimpleUser(userId, userName, username, password, role);
 
        Report report = null;
        if ("LOST".equals(type)) {
            String lostLocation = rs.getString("lost_location");
            LostReport lr = new LostReport(reportID, user, item, desc, lostLocation);
            lr.setStatus(ReportStatus.valueOf(statusStr));
            if (photoPath != null) lr.setPhotoPath(photoPath);
            report = lr;
        } else if ("FOUND".equals(type)) {
            String foundLocation = rs.getString("found_location");
            FoundReport fr = new FoundReport(reportID, user, item, desc, foundLocation);
            fr.setStatus(ReportStatus.valueOf(statusStr));
            if (photoPath != null) fr.setPhotoPath(photoPath);
            report = fr;
        }
 
        return report;
    }
    
    private User buildSimpleUser(String userId, String name, String username, String password, Role role) {
        switch (role) {
            case MAHASISWA:
                return new com.model.Mahasiswa(userId, name, username, password, "", "", "", "");
            case DOSEN:
                return new com.model.Dosen(userId, name, username, password, "", "");
            case STAFF:
                return new com.model.Staff(userId, name, username, password, "", "");
            case ADMIN:
                return new com.model.Admin(userId, name, username, password, "");
            case SECURITY:
                return new com.model.Security(userId, name, username, password, "", "");
            default:
                return null;
        }
    }
    
    private void resolveMatchedReports() {
        for (Report report : reports) {
            if (report instanceof FoundReport) {
                FoundReport fr = (FoundReport) report;
                // Cari LostReport yang cocok di reportMap
                for (Report r : reports) {
                    if (r instanceof LostReport
                            && r.getItem().getItemID().equals(fr.getItem().getItemID())) {
                        fr.setMatchedLostReport((LostReport) r);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void add(Object obj){
        if (obj instanceof Report) {
            addReport((Report) obj);
        }
    }
    
    @Override
    public void delete(String id){
        deleteReport(id);
    }
    
    @Override
    public Object findById(String id){
        for (Report report : reports) {
            if (report.getReportID().equals(id)) {
                return report;
            }
        }
        return null;
    }
    
    public void addReport(Report report){
         if (report instanceof FoundReport) {
            Role role = report.getUser().getRole();
            if (role != Role.SECURITY && role != Role.ADMIN) {
                System.out.println("Pembuatan Report Gagal. Hanya Security dan Admin yang bisa membuat laporan barang ditemukan.");
                return;
            }
            String type = (report instanceof LostReport) ? "LOST" : "FOUND";
            String lostLoc  = (report instanceof LostReport) ? ((LostReport) report).getLostLocation() : null;
            String foundLoc = (report instanceof FoundReport) ? ((FoundReport) report).getFoundLocation() : null;
            String sql = "INSERT INTO reports (report_id, user_id, item_id, type, description, status, date, editable_until, photo_path, lost_location, found_location) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                Connection conn = dbConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, report.getReportID());
                ps.setString(2, report.getUser().getUserId());
                ps.setString(3, report.getItem().getItemID());
                ps.setString(4, type);
                ps.setString(5, report.getDescription());
                ps.setString(6, report.getStatus().name());
                ps.setTimestamp(7, java.sql.Timestamp.valueOf(report.getDate()));
                ps.setTimestamp(8, java.sql.Timestamp.valueOf(report.getEditableUntil()));
                ps.setString(9, report.getPhotoPath());
                ps.setString(10, lostLoc);
                ps.setString(11, foundLoc);
                ps.executeUpdate();
 
                report.submitReport();
                reports.add(report);
                reportMap.put(report.getReportID(), report);
                System.out.println("Laporan " + report.getReportID() + " berhasil disimpan.");
            } catch (SQLException e) {
                System.out.println("Gagal Menyimpan laporan: " + e.getMessage());
            }
        }
    }
    
    public void deleteReport(String reportId){
        String sql = "DELETE FROM reports WHERE report_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reportId);
            ps.executeUpdate();
 
            Report report = reportMap.remove(reportId);
            if (report != null) {
                reports.remove(report);
                System.out.println("[ReportManager] Laporan " + reportId + " berhasil dihapus.");
            }
        } catch (SQLException e) {
            System.out.println("[ReportManager] Gagal hapus laporan: " + e.getMessage());
        }
    }
    
    public void validateReport(String reportId, ReportStatus newStatus, com.model.Admin admin) {
        Report report = reportMap.get(reportId);
        if (report == null) {
            System.out.println("[ReportManager] Laporan tidak ditemukan.");
            return;
        }
 
        report.setStatus(newStatus);
 
        // Jika FoundReport VALID, langsung cocokkan item
        if (report instanceof FoundReport && newStatus == ReportStatus.VALID) {
            FoundReport fr = (FoundReport) report;
            LostReport matched = findMatchingLostReport(fr);
            fr.setMatchedLostReport(matched);
            updateMatchedReportInDB(fr.getReportID(), matched != null ? matched.getReportID() : null);
        }
 
        updateReportStatusInDB(reportId, newStatus);
        admin.validateReport(reportId);
    }
    
    private LostReport findMatchingLostReport(FoundReport foundReport) {
        for (Report r : reports) {
            if (r instanceof LostReport && r.getStatus() == ReportStatus.VALID) {
                LostReport lr = (LostReport) r;
                // Cocokin berdasarkan Nama Item dan kategori
                boolean sameName = lr.getItem().getName().equalsIgnoreCase(foundReport.getItem().getName());
                boolean sameCategory = lr.getItem().getCategory() != null
                        && foundReport.getItem().getCategory() != null
                        && lr.getItem().getCategory().getCategoryID()
                            .equals(foundReport.getItem().getCategory().getCategoryID());
                if (sameName && sameCategory) {
                    return lr;
                }
            }
        }
        return null;
    }
    
    private void updateReportStatusInDB(String reportId, ReportStatus status) {
        String sql = "UPDATE reports SET status = ? WHERE report_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setString(2, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ReportManager] Gagal update status laporan: " + e.getMessage());
        }
    }
    
    private void updateMatchedReportInDB(String foundReportId, String matchedLostReportId) {
        String sql = "UPDATE reports SET matched_lost_report_id = ? WHERE report_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, matchedLostReportId);
            ps.setString(2, foundReportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ReportManager] Gagal update matched report: " + e.getMessage());
        }
    }
    
    // Tampilin report yang statusnya VALID
    public ArrayList<Report> getValidReports(){
        ArrayList<Report> result = new ArrayList<>();
        for (Report report : reports) {
            if (report.isValid()) {
                result.add(report);
            }
        }
        return result;
    }
    
    public ArrayList<Report> getAllReports(){
        return reports;
    }
    
    public  ArrayList<LostReport> getLostReports(){
        ArrayList<LostReport> result = new ArrayList<>();
        for (Report report : reports) {
            if (report instanceof LostReport) {
                result.add((LostReport) report);
            }
        }
        return result;
    }
    
    public  ArrayList<FoundReport> getFoundReports(){
        ArrayList<FoundReport> result = new ArrayList<>();
        for (Report report : reports) {
            if (report instanceof FoundReport) {
                result.add((FoundReport) report);
            }
        }
        return result;
    }
    
    public ArrayList<LostReport> getClaimableLostReports() {
        ArrayList<LostReport> result = new ArrayList<>();
        for (Report report : reports) {
            if (report instanceof LostReport && report.isClaimable()) {
                result.add((LostReport) report);
            }
        }
        return result;
    }
 
    public void reload() {
        loadAllReportsFromDB();
    }
}
