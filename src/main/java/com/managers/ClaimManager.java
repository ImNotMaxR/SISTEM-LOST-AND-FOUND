package com.managers;

import com.database.DBConnection;
import com.model.Admin;
import com.model.Item;
import com.model.StorageRecord;
import com.model.User;
import com.model.Claim;
import com.model.VerificationDocument;
import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.enumeration.Role;
import com.interfaces.Managerable;
import com.model.Category;
import com.model.Report;
import com.model.Mahasiswa;
import com.model.Dosen;
import com.model.Staff;
import com.model.Security;
import com.exception.ValidationException;
import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

public class ClaimManager implements Managerable{
    private ArrayList<Claim> claims;
    private HashMap<String, Claim> claimMap;
    private DBConnection dbConnection;

    public ClaimManager() {
        this.claims = new ArrayList<>();
        this.claimMap = new HashMap<>();
        this.dbConnection = DBConnection.getInstance();
        loadAllClaimsFromDB();
    }

    public void refreshClaimsFromDatabase() {
        loadAllClaimsFromDB();
    }
    
    private void loadAllClaimsFromDB() {
        claims.clear();
        claimMap.clear();
        String sql = "SELECT c.claim_id, c.status, c.date_claim, c.related_report_id, " + "u.user_id, u.name AS user_name, u.username, u.password, u.role, " + "i.item_id, i.name AS item_name, i.description AS item_desc, " + "i.status AS item_status, i.location AS item_location, " + "cat.category_id, cat.name AS category_name " + "FROM claims c " + "JOIN users u ON c.user_id = u.user_id " + "JOIN items i ON c.item_id = i.item_id " + "LEFT JOIN categories cat ON i.category_id = cat.category_id";
 
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Claim claim = buildClaimFromResultSet(rs);
                if (claim != null) {
                    loadDocumentsForClaim(claim);
                    claims.add(claim);
                    claimMap.put(claim.getClaimId(), claim);
                }
            }
            System.out.println("Data Claim berhasil diload dari database dengan jumlah: " + claims.size());
        } catch (SQLException e) {
            System.out.println("Gagal load claims " + e.getMessage());
        }
    }
    
    private void loadDocumentsForClaim(Claim claim) {
        String sql = "SELECT document_id, type, file_path, description FROM verification_documents WHERE claim_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, claim.getClaimId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String docId = rs.getString("document_id");
                String type = rs.getString("type");
                String desc = rs.getString("description");
                String file = rs.getString("file_path");
                VerificationDocument doc = new VerificationDocument(docId, type, file, desc);
                claim.addDocument(doc);
            }
        } catch (SQLException e) {
            System.out.println("Gagal load dokumen klaim " + claim.getClaimId() + ": " + e.getMessage());
        }
    }
    
    private Claim buildClaimFromResultSet(ResultSet rs) throws SQLException {
        String claimId = rs.getString("claim_id");
        String statusStr = rs.getString("status");
        String reportId = rs.getString("related_report_id");
 
        // Bangun User dari join
        Category category = null;
        String catId = rs.getString("category_id");
        if (catId != null) {
            category = new Category(catId, rs.getString("category_name"));
        }
 
        Item item = new Item(rs.getString("item_id"), rs.getString("item_name"), rs.getString("item_desc"), category, rs.getString("item_location"));
        item.setStatus(ItemStatus.valueOf(rs.getString("item_status")));
 
        // Bangun User minimal dari join
        User user = buildSimpleUser( rs.getString("user_id"), rs.getString("user_name"), rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));

        Claim claim = new Claim(claimId, user, item, reportId);
        claim.setStatus(ClaimStatus.valueOf(statusStr));
        java.sql.Timestamp claimDate = rs.getTimestamp("date_claim");
        if (claimDate != null) {
            claim.setDateClaim(claimDate.toLocalDateTime());
        }
        return claim;
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

    public void add(Object obj) {
        if (obj instanceof Claim) {
            try {
                addClaim((Claim) obj);
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void delete(String id) {
        deleteClaim(id);
    }

    public Claim findById(String id) {
        return claimMap.getOrDefault(id, null);
    }

    public void addClaim(Claim claim) throws ValidationException {
        saveClaim(claim);
    }

    public void submitClaim(User user, Report report, String name, String address, String phone, String photoPath) throws ValidationException {
        if (name == null || name.isEmpty() || address == null || address.isEmpty() || phone == null || phone.isEmpty() || photoPath == null || photoPath.isBlank()) {
            throw new ValidationException("Nama, alamat, no telepon, dan foto bukti wajib diisi.");
        }

        File evidenceFile = new File(photoPath);
        if (!evidenceFile.exists() || !evidenceFile.isFile()) {
            throw new ValidationException("File bukti tidak ditemukan.");
        }

        String fileName = evidenceFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
            throw new ValidationException("Foto harus berformat JPG atau PNG.");
        }

        if (evidenceFile.length() > 2 * 1024 * 1024) {
            throw new ValidationException("Ukuran foto maksimal 2 MB.");
        }

        Claim claim = new Claim("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), user, report.getItem(), report.getReportId());
        String description = "Nama: " + name + "\nAlamat: " + address + "\nNo Telepon: " + phone;
        claim.addDocument(new VerificationDocument(
                "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Bukti Kepemilikan",
                evidenceFile.getAbsolutePath(),
                description
        ));

        saveClaim(claim);
    }

    public void saveClaim(Claim claim) throws ValidationException {
        if (claim == null || claim.getItem() == null || claim.getUser() == null) {
            throw new ValidationException("Data klaim tidak lengkap. Silakan login ulang dan coba lagi.");
        }

        if (claim.getItem().getStatus() != ItemStatus.DITEMUKAN) {
            throw new ValidationException("Barang \"" + claim.getItem().getName() + "\" tidak berstatus DITEMUKAN, sehingga belum bisa diklaim.");
        }
 
        if (hasActiveClaim(claim.getItem().getItemID())) {
            throw new ValidationException("Barang \"" + claim.getItem().getName() + "\" sudah memiliki klaim yang sedang diproses.");
        }
 
        if (!claim.validate()) {
            if (claim.getItem().getCategory() != null 
                    && (claim.getDocuments() == null || claim.getDocuments().isEmpty())) {
                throw new ValidationException("Kategori \"" + claim.getItem().getCategory().getName() + "\" membutuhkan dokumen verifikasi sebelum klaim diajukan.");
            } else {
                throw new ValidationException("Data klaim tidak lolos validasi. Silakan periksa status barang dan dokumen pendukung.");
            }
        }
 
        String sql = "INSERT INTO claims (claim_id, user_id, item_id, status, date_claim, related_report_id) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, claim.getClaimId());
            ps.setString(2, claim.getUser().getUserId());
            ps.setString(3, claim.getItem().getItemID());
            ps.setString(4, claim.getStatus().name());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(claim.getDateClaim()));
            ps.setString(6, claim.getRelatedReportId());
            ps.executeUpdate();
 
            for (VerificationDocument doc : claim.getDocuments()) {
                saveDocumentToDB(claim.getClaimId(), doc);
            }
 
            claims.add(claim);
            claimMap.put(claim.getClaimId(), claim);
            System.out.println("Klaim dengan ID: " + claim.getClaimId() + " berhasil diajukan.");
        } catch (SQLException e) {
            throw new ValidationException("Gagal menyimpan klaim ke database: " + e.getMessage());
        }
    }
    
    private void saveDocumentToDB(String claimId, VerificationDocument doc) {
        String sql = "INSERT INTO verification_documents (document_id, claim_id, type, file_path, description) " + "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, doc.getDocumentId());
            ps.setString(2, claimId);
            ps.setString(3, doc.getType());
            ps.setString(4, doc.getFile());
            ps.setString(5, doc.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal simpan dokumen: " + e.getMessage());
        }
    }

    public void deleteClaim(String claimId) {
        String sql = "DELETE FROM claims WHERE claim_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, claimId);
            ps.executeUpdate();
 
            Claim claim = claimMap.remove(claimId);
            if (claim != null) {
                claims.remove(claim);
                System.out.println("Klaim dengan ID: " + claimId + " berhasil dihapus.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal hapus data klaim: " + e.getMessage());
        }
    }

    public void processClaim(String claimId, ClaimStatus newStatus, Admin admin, String reason) throws ValidationException {
        if (newStatus == ClaimStatus.DITOLAK && (reason == null || reason.trim().isEmpty())) {
            throw new ValidationException("Alasan penolakan tidak boleh kosong.");
        }

        Claim claim = claimMap.get(claimId);
        if (claim == null) {
            throw new ValidationException("Data Klaim tidak ditemukan.");
        }
 
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ValidationException("Klaim sudah diproses sebelumnya dan tidak dapat diubah lagi.");
        }
 
        claim.updateStatus(newStatus);
        updateClaimStatusInDB(claimId, newStatus);
        
        if (newStatus == ClaimStatus.VALID) {
            updateItemStatusInDB(claim.getItem().getItemID(), ItemStatus.DIKLAIM);
        }
        
        admin.verifyClaim(claimId);
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
    
    private void updateClaimStatusInDB(String claimId, ClaimStatus newStatus) {
        String sql = "UPDATE claims SET status = ? WHERE claim_id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus.name());
            ps.setString(2, claimId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Gagal update status klaim: " + e.getMessage());
        }
    }
    
    public boolean hasActiveClaim(String itemId) {
        for (Claim claim : claims) {
            if (claim.getItem().getItemID().equals(itemId)
                    && (claim.getStatus() == ClaimStatus.PENDING
                        || claim.getStatus() == ClaimStatus.VALID)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasActiveClaimByUserForItem(String userId, String itemId) {
        return hasActiveClaimByUserForReportOrItem(userId, null, itemId);
    }

    public boolean hasActiveClaimByUserForReportOrItem(String userId, String reportId, String itemId) {
        if (userId == null || itemId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) AS total FROM claims "
                + "WHERE user_id = ? "
                + "AND (item_id = ? OR related_report_id = ?) "
                + "AND UPPER(status) IN (?, ?)";

        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, itemId);
            ps.setString(3, reportId);
            ps.setString(4, ClaimStatus.PENDING.name());
            ps.setString(5, ClaimStatus.VALID.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal cek klaim aktif user: " + e.getMessage());
        }

        return false;
    }

    public ArrayList<Claim> getClaims() {
        return claims;
    }

    public ArrayList<Claim> getAllClaims() {
        return claims;
    }

    public ArrayList<?> getAll() {
        return claims;
    }

    public ArrayList<Claim> getClaimsByStatus(ClaimStatus status) {
        ArrayList<Claim> result = new ArrayList<>();
        for (Claim claim : claims) {
            if (claim.getStatus() == status) {
                result.add(claim);
            }
        }
        return result;
    }
    
    public void reload() {
        loadAllClaimsFromDB();
    }
}
