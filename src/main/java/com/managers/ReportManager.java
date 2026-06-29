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
import com.model.Mahasiswa;
import com.model.Dosen;
import com.model.Staff;
import com.model.Admin;
import com.model.Security;
import com.model.StorageRecord;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.io.File;
import com.exception.ValidationException;

public class ReportManager implements Managerable{
    private ArrayList<Report> reports;
    private HashMap<String, Report> reportMap;
    private DBConnection dbConnection;
    
    public ReportManager() {
        this.reports = new ArrayList<>();
        this.reportMap = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        ensureRejectionReasonColumn();
        loadAllReportsFromDB();
    }

    private void ensureRejectionReasonColumn() {
        try {
            Connection conn = dbConnection.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, "reports", "rejection_reason")) {
                if (columns.next()) {
                    return;
                }
            }
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("ALTER TABLE reports ADD COLUMN rejection_reason TEXT NULL");
            }
        } catch (SQLException exception) {
            System.out.println("Kolom rejection_reason tidak dapat dipastikan: " + exception.getMessage());
        }
    }
    
    private void loadAllReportsFromDB() {
        reports.clear();
        reportMap.clear();
        String sql = "SELECT r.report_id, r.type, r.description, r.status, r.date, " + "r.editable_until, r.photo_path, r.rejection_reason, r.lost_location, r.found_location, " + "r.matched_lost_report_id, " + "u.user_id, u.name AS user_name, u.username, u.password, u.role, " + "i.item_id, i.name AS item_name, i.description AS item_desc, " + "i.status AS item_status, i.location AS item_location, i.date AS item_date, " + "c.category_id, c.name AS category_name " + "FROM reports r " + "JOIN users u ON r.user_id = u.user_id " + "JOIN items i ON r.item_id = i.item_id " + "LEFT JOIN categories c ON i.category_id = c.category_id";
        
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Report report = buildReportFromResultSet(rs);
                if (report != null) {
                    reports.add(report);
                    reportMap.put(report.getReportId(), report);
                }
            }
 
            // Setelah semua report dimuat, proses kecocokan FoundReport dengan LostReport
            resolveMatchedReports();
 
            System.out.println("Data Report berhasil diload dari database dengan jumlah: " + reports.size());
        } catch (SQLException e) {
            System.out.println("Gagal Load Report " + e.getMessage());
        }
    }
    
    private Report buildReportFromResultSet(ResultSet rs) throws SQLException {
        String reportId = rs.getString("report_id");
        String type = rs.getString("type");
        String desc = rs.getString("description");
        String statusStr = rs.getString("status");
        LocalDateTime date = rs.getTimestamp("date") != null? rs.getTimestamp("date").toLocalDateTime() : LocalDateTime.now();
        String photoPath = rs.getString("photo_path");
        String rejectionReason = rs.getString("rejection_reason");
 
        // Full user object diambil dari UserManager jika dibutuhkan detail lengkap
        String userId = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String username = rs.getString("username");
        String password = rs.getString("password");
        Role role = Role.valueOf(rs.getString("role"));
 
        // Bangun Item dari join
        String itemId = rs.getString("item_id");
        String itemName = rs.getString("item_name");
        String itemDesc = rs.getString("item_desc");
        String itemLoc = rs.getString("item_location");
        String itemStatus = rs.getString("item_status");
 
        Category category = null;
        String catId = rs.getString("category_id");
        if (catId != null) {
            category = new Category(catId, rs.getString("category_name"));
        }
 
        Item item = new Item(itemId, itemName, itemDesc, category, itemLoc);
        item.setStatus(ItemStatus.valueOf(itemStatus));
 
        // Buat User sesuai role menggunakan instanceof pattern
        User user = buildSimpleUser(userId, userName, username, password, role);
 
        Report report = null;
        if ("LOST".equals(type)) {
            String lostLocation = rs.getString("lost_location");
            LostReport lr = new LostReport(reportId, user, item, desc, lostLocation);
            lr.setStatus(ReportStatus.valueOf(statusStr));
            if (photoPath != null) lr.setPhotoPath(photoPath);
            lr.setRejectionReason(rejectionReason);
            report = lr;
        } else if ("FOUND".equals(type)) {
            String foundLocation = rs.getString("found_location");
            FoundReport fr = new FoundReport(reportId, user, item, desc, foundLocation);
            fr.setStatus(ReportStatus.valueOf(statusStr));
            if (photoPath != null) fr.setPhotoPath(photoPath);
            fr.setRejectionReason(rejectionReason);
            report = fr;
        }
 
        if (report != null && rs.getTimestamp("editable_until") != null) {
            report.setEditableUntil(rs.getTimestamp("editable_until").toLocalDateTime());
        }

        if (report != null && report.getItem() != null) {
            report.getItem().setStatus(ItemStatus.valueOf(itemStatus));
        }

        return report;
    }
    
    private User buildSimpleUser(String userId, String name, String username, String password, Role role) {
        switch (role) {
            case MAHASISWA:
                return new Mahasiswa(userId, name, username, password, "", "", "", "");
            case DOSEN:
                return new Dosen(userId, name, username, password, "", "");
            case STAFF:
                return new Staff(userId, name, username, password, "", "");
            case ADMIN:
                return new Admin(userId, name, username, password, "");
            case SECURITY:
                return new Security(userId, name, username, password, "", "");
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
            if (report.getReportId().equals(id)) {
                return report;
            }
        }
        return null;
    }
    
    public void addReport(Report report) {
        // Validasi role khusus FoundReport
        if (report instanceof FoundReport) {
            Role role = report.getUser().getRole();
            if (role != Role.SECURITY && role != Role.ADMIN) {
                System.out.println("Pembuatan Report Gagal. Hanya Security dan Admin yang bisa membuat laporan barang ditemukan.");
                return;
            }
        }
        String type     = (report instanceof LostReport) ? "LOST" : "FOUND";
        String lostLoc  = (report instanceof LostReport) ? ((LostReport) report).getLostLocation() : null;
        String foundLoc = (report instanceof FoundReport) ? ((FoundReport) report).getFoundLocation() : null;
        
        String matchedLostReportId = report instanceof FoundReport && ((FoundReport) report).getMatchedLostReport() != null
                ? ((FoundReport) report).getMatchedLostReport().getReportId()
                : null;
        String sql = "INSERT INTO reports (report_id, user_id, item_id, type, description, status, date, editable_until, photo_path, rejection_reason, lost_location, found_location, matched_lost_report_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, report.getReportId());
            ps.setString(2, report.getUser().getUserId());
            ps.setString(3, report.getItem().getItemID());
            ps.setString(4, type);
            ps.setString(5, report.getDescription());
            ps.setString(6, report.getStatus().name());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(report.getDate()));
            ps.setTimestamp(8, java.sql.Timestamp.valueOf(report.getEditableUntil()));
            ps.setString(9, report.getPhotoPath());
            ps.setString(10, report.getRejectionReason());
            ps.setString(11, lostLoc);
            ps.setString(12, foundLoc);
            ps.setString(13, matchedLostReportId);
            ps.executeUpdate();
 
            report.submitReport();
            reports.add(report);
            reportMap.put(report.getReportId(), report);
            System.out.println("Laporan dengan ID: " + report.getReportId() + " berhasil disimpan.");
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan laporan: " + e.getMessage());
        }
    }

    public boolean updateReport(Report report) {
        String sql = "UPDATE reports SET description = ?, photo_path = ?, lost_location = ?, found_location = ? WHERE report_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, report.getDescription());
            ps.setString(2, report.getPhotoPath());
            if (report instanceof LostReport) {
                ps.setString(3, ((LostReport) report).getLostLocation());
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else if (report instanceof FoundReport) {
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setString(4, ((FoundReport) report).getFoundLocation());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, report.getReportId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                reportMap.put(report.getReportId(), report);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Gagal update laporan: " + e.getMessage());
            return false;
        }
    }

    private void validatePhotoFile(File photoFile) throws ValidationException {
        if (photoFile != null) {
            String name = photoFile.getName().toLowerCase();
            if (!name.endsWith(".jpg") && !name.endsWith(".png") && !name.endsWith(".jpeg")) {
                throw new ValidationException("Foto harus berformat JPG atau PNG.");
            }
            if (photoFile.length() > 2 * 1024 * 1024) { // 2MB
                throw new ValidationException("Ukuran foto maksimal 2 MB.");
            }
        }
    }

    public void createLostReport(User user, String itemName, String itemDescription, String lostLocation, String reportDescription, Category category, File photoFile, ItemManager itemManager) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Data user tidak ditemukan. Silakan login ulang.");
        }
        if (itemName == null || itemName.trim().isEmpty() || itemDescription == null || itemDescription.trim().isEmpty() || lostLocation == null || lostLocation.trim().isEmpty() || reportDescription == null || reportDescription.trim().isEmpty() || category == null) {
            throw new ValidationException("Semua Input Wajib Diisi Sebelum Menyimpan Laporan.");
        }
        validatePhotoFile(photoFile);

        String itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Item item = new Item(itemId, itemName, itemDescription, category, lostLocation);
        itemManager.addItem(item);

        LostReport report = new LostReport(reportId, user, item, reportDescription, lostLocation);
        if (photoFile != null) {
            report.addEvidence(photoFile.getAbsolutePath());
        }
        addReport(report);
    }

    public void createFoundReport(User user, String itemName, String itemDescription, String foundLocation, String storageLocation, String reportDescription, Category category, LostReport matched, File photoFile, ItemManager itemManager, StorageManager storageManager) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Data user tidak ditemukan. Silakan login ulang.");
        }
        if (itemName == null || itemName.trim().isEmpty() || itemDescription == null || itemDescription.trim().isEmpty() || foundLocation == null || foundLocation.trim().isEmpty() || storageLocation == null || storageLocation.trim().isEmpty() || reportDescription == null || reportDescription.trim().isEmpty() || category == null) {
            throw new ValidationException("Semua Input Wajib Diisi Sebelum Menyimpan Laporan.");
        }
        validatePhotoFile(photoFile);

        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String recordId = "STR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Item item;
        if (matched != null) {
            item = matched.getItem();
        } else {
            String itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            item = new Item(itemId, itemName, itemDescription, category, foundLocation);
            itemManager.addItem(item);
        }

        FoundReport report = new FoundReport(reportId, user, item, reportDescription, foundLocation);
        if (matched != null) {
            report.setMatchedLostReport(matched);
        }
        if (photoFile != null) {
            report.setPhotoPath(photoFile.getAbsolutePath());
        }
        addReport(report);

        StorageRecord storageRecord = new StorageRecord(recordId, item, (Security) user, storageLocation);
        storageManager.saveStorageRecordToDB(storageRecord);
    }

    public void createFoundReportByAdmin(User user, String itemName, String itemDescription, String foundLocation, String reportDescription, Category category, LostReport matched, File photoFile, ItemManager itemManager) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Data user tidak ditemukan. Silakan login ulang.");
        }
        if (itemName == null || itemName.trim().isEmpty() || itemDescription == null || itemDescription.trim().isEmpty() || foundLocation == null || foundLocation.trim().isEmpty() || reportDescription == null || reportDescription.trim().isEmpty() || category == null) {
            throw new ValidationException("Semua Input Wajib Diisi Sebelum Menyimpan Laporan.");
        }
        validatePhotoFile(photoFile);

        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Item item;
        if (matched != null) {
            item = matched.getItem();
        } else {
            String itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            item = new Item(itemId, itemName, itemDescription, category, foundLocation);
            item.setStatus(ItemStatus.DITEMUKAN);
            itemManager.addItem(item);
        }

        FoundReport report = new FoundReport(reportId, user, item, reportDescription, foundLocation);
        if (matched != null) {
            report.setMatchedLostReport(matched);
        }
        if (photoFile != null) {
            report.setPhotoPath(photoFile.getAbsolutePath());
        }
        addReport(report);
        validateReport(reportId, ReportStatus.VALID, (Admin) user, null);
    }

    public void editLostReport(LostReport report, String itemName, String itemDesc, String lostLocation, String reportDesc, Category category, File photoFile, ItemManager itemManager) throws ValidationException {
        if (itemName == null || itemName.trim().isEmpty() || itemDesc == null || itemDesc.trim().isEmpty() || lostLocation == null || lostLocation.trim().isEmpty() || reportDesc == null || reportDesc.trim().isEmpty() || category == null) {
            throw new ValidationException("Semua Input Wajib Diisi Sebelum Memperbarui Laporan.");
        }
        validatePhotoFile(photoFile);

        Item item = report.getItem();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setCategory(category);
        item.setLocation(lostLocation);
        itemManager.updateItem(item);

        report.setDescription(reportDesc);
        report.setLostLocation(lostLocation);
        if (photoFile != null) {
            report.setPhotoPath(photoFile.getAbsolutePath());
        }
        boolean success = updateReport(report);
        if (!success) {
            throw new ValidationException("Gagal mengupdate laporan ke database.");
        }
    }

    public void editFoundReport(FoundReport report, String itemName, String itemDesc, String foundLocation, String reportDesc, Category category, File photoFile, ItemManager itemManager) throws ValidationException {
        if (itemName == null || itemName.trim().isEmpty() || itemDesc == null || itemDesc.trim().isEmpty() || foundLocation == null || foundLocation.trim().isEmpty() || reportDesc == null || reportDesc.trim().isEmpty() || category == null) {
            throw new ValidationException("Semua Input Wajib Diisi Sebelum Memperbarui Laporan.");
        }
        validatePhotoFile(photoFile);

        Item item = report.getItem();
        item.setName(itemName);
        item.setDescription(itemDesc);
        item.setCategory(category);
        item.setLocation(foundLocation);
        itemManager.updateItem(item);

        report.setDescription(reportDesc);
        report.setFoundLocation(foundLocation);
        if (photoFile != null) {
            report.setPhotoPath(photoFile.getAbsolutePath());
        }
        boolean success = updateReport(report);
        if (!success) {
            throw new ValidationException("Gagal mengupdate laporan ke database.");
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
                System.out.println("Laporan Dengan ID: " + reportId + " berhasil dihapus.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal hapus laporan" + e.getMessage());
        }
    }
    
    public void validateReport(String reportId, ReportStatus newStatus, Admin admin) throws ValidationException {
        validateReport(reportId, newStatus, admin, null);
    }

    public void validateReport(String reportId, ReportStatus newStatus, Admin admin, String rejectionReason) throws ValidationException {
        if (newStatus == ReportStatus.DITOLAK && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            throw new ValidationException("Alasan Penolakan Tidak Boleh Kosong.");
        }
        Report report = reportMap.get(reportId);
        if (report == null) {
            throw new ValidationException("Laporan tidak ditemukan.");
        }
 
        report.setStatus(newStatus);
        report.setRejectionReason(newStatus == ReportStatus.DITOLAK ? rejectionReason : null);
 
        // Jika FoundReport VALID, cocokkan item jika belum dicocokkan
        if (report instanceof FoundReport && newStatus == ReportStatus.VALID) {
            FoundReport fr = (FoundReport) report;
            if (!fr.hasMatch()) {
                LostReport matched = findMatchingLostReport(fr);
                fr.setMatchedLostReport(matched);
                updateMatchedReportInDB(fr.getReportId(), matched != null ? matched.getReportId() : null);
            }
            // Langsung set ke DITEMUKAN sesuai logic found report
            updateItemStatusInDB(fr.getItem().getItemID(), ItemStatus.DITEMUKAN);
            fr.getItem().setStatus(ItemStatus.DITEMUKAN);
        }
 
        updateReportStatusInDB(reportId, newStatus, report.getRejectionReason());
        if (admin != null) {
            admin.validateReport(reportId);
        }
    }
    
    private LostReport findMatchingLostReport(FoundReport foundReport) {
        for (Report r : reports) {
            if (r instanceof LostReport && r.getStatus() == ReportStatus.VALID) {
                LostReport lr = (LostReport) r;
                // Cocokin berdasarkan Nama Item dan kategori
                boolean sameName = lr.getItem().getName().equalsIgnoreCase(foundReport.getItem().getName());
                boolean sameCategory = lr.getItem().getCategory() != null && foundReport.getItem().getCategory() != null && lr.getItem().getCategory().getCategoryID().equals(foundReport.getItem().getCategory().getCategoryID());
                if (sameName && sameCategory) {
                    return lr;
                }
            }
        }
        return null;
    }
    
    private void updateItemStatusInDB(String itemId, ItemStatus status) {
        String sql = "UPDATE items SET status = ?, date = ? WHERE item_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(3, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update status item di DB: " + e.getMessage());
        }
    }
    
    private void updateReportStatusInDB(String reportId, ReportStatus status, String rejectionReason) {
        String sql = "UPDATE reports SET status = ?, rejection_reason = ? WHERE report_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setString(2, rejectionReason);
            ps.setString(3, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update status laporan" + e.getMessage());
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
            System.out.println("Gagal update matched report" + e.getMessage());
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
