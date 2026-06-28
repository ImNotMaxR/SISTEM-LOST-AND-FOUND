package com.frame.dashboard.admin;

import com.frame.dashboard.shared.DashboardUi;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Claim;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;

public class AdminHomePanel extends JPanel {

    private static final int ROW_LIMIT = 5;
    private static final String EMPTY_VALUE = "-";

    private final ReportManager reportManager;
    private final ClaimManager claimManager;

    // -------------------------------------------------------------------------
    // Panel Setup
    // -------------------------------------------------------------------------

    public AdminHomePanel(ReportManager reportManager, ClaimManager claimManager) {
        this.reportManager = reportManager;
        this.claimManager = claimManager;
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(DashboardUi.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(44, 42, 48, 42));
        buildContent();
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private void buildContent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        add(createHeader(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(28, 0, 0, 0);
        add(createStatsPanel(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(24, 0, 0, 0);
        add(createLostReportsTableSection(), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(24, 0, 0, 0);
        add(createFoundReportsTableSection(), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(24, 0, 0, 0);
        add(createClaimsTableSection(), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1;
        add(createVerticalSpacer(), gbc);
    }

    // -------------------------------------------------------------------------
    // Header UI
    // -------------------------------------------------------------------------

    private JPanel createHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        panel.add(DashboardUi.label(
                "Admin Panel - Sistem Informasi Lost & Found Kampus",
                12,
                Font.PLAIN,
                DashboardUi.TEXT_MUTED
        ), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(DashboardUi.label("Dashboard Admin", 30, Font.BOLD, DashboardUi.TEXT_DARK), gbc);

        return panel;
    }

    // -------------------------------------------------------------------------
    // Stats UI
    // -------------------------------------------------------------------------

    private JPanel createStatsPanel() {
        JPanel panel = AdminDashboardComponents.statRow();
        panel.add(AdminDashboardComponents.statCard(
                "Total Barang Hilang",
                reportManager.getLostReports().size(),
                "Laporan Kehilangan Dari User",
                DashboardUi.PRIMARY_DARK,
                new Color(54, 104, 184)
        ));

        panel.add(AdminDashboardComponents.statCard(
                "Total Pengajuan Klaim",
                claimManager.getAllClaims().size(),
                "Menunggu Dan Sudah Diverifikasi",
                new Color(245, 169, 58),
                new Color(255, 204, 99)
        ));

        panel.add(AdminDashboardComponents.statCard(
                "Total Barang Ditemukan",
                reportManager.getFoundReports().size(),
                "Tersedia Atau Sudah Diklaim",
                new Color(21, 156, 219),
                new Color(79, 190, 206)
        ));

        return panel;
    }

    // -------------------------------------------------------------------------
    // Table UI
    // -------------------------------------------------------------------------

    private JPanel createLostReportsTableSection() {
        JTable lostTable = AdminDashboardComponents.table(
                new String[]{"USER", "ITEM", "TANGGAL", "STATUS"},
                createLostReportRows()
        );
        return AdminDashboardComponents.tableSection("Laporan Barang Hilang", lostTable);
    }

    private JPanel createFoundReportsTableSection() {
        JTable foundTable = AdminDashboardComponents.table(
                new String[]{"USER", "ITEM", "TANGGAL", "STATUS"},
                createFoundReportRows()
        );
        return AdminDashboardComponents.tableSection("Laporan Barang Ditemukan", foundTable);
    }

    private JPanel createClaimsTableSection() {
        JTable claimTable = AdminDashboardComponents.table(
                new String[]{"PENGAJU", "ITEM", "TANGGAL", "STATUS"},
                createClaimRows()
        );
        return AdminDashboardComponents.tableSection("Pengajuan Klaim", claimTable);
    }

    private JPanel createVerticalSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Table Data
    // -------------------------------------------------------------------------

    private Object[][] createLostReportRows() {
        ArrayList<LostReport> reports = reportManager.getLostReports();
        if (reports.isEmpty()) {
            return emptyRows("Belum ada laporan");
        }

        int count = Math.min(ROW_LIMIT, reports.size());
        Object[][] rows = new Object[count][4];
        for (int i = 0; i < count; i++) {
            LostReport report = reports.get(i);
            rows[i][0] = userName(report);
            rows[i][1] = itemName(report);
            rows[i][2] = DashboardUi.date(report);
            rows[i][3] = reportStatus(report);
        }
        return rows;
    }

    private Object[][] createFoundReportRows() {
        ArrayList<FoundReport> reports = reportManager.getFoundReports();
        if (reports.isEmpty()) {
            return emptyRows("Belum ada laporan");
        }

        int count = Math.min(ROW_LIMIT, reports.size());
        Object[][] rows = new Object[count][4];
        for (int i = 0; i < count; i++) {
            FoundReport report = reports.get(i);
            rows[i][0] = userName(report);
            rows[i][1] = itemName(report);
            rows[i][2] = DashboardUi.date(report);
            rows[i][3] = itemStatus(report);
        }
        return rows;
    }

    private Object[][] createClaimRows() {
        ArrayList<Claim> claims = claimManager.getAllClaims();
        if (claims.isEmpty()) {
            return emptyRows("Belum ada klaim");
        }

        int count = Math.min(ROW_LIMIT, claims.size());
        Object[][] rows = new Object[count][4];
        for (int i = 0; i < count; i++) {
            Claim claim = claims.get(i);
            rows[i][0] = claim.getUser() == null ? EMPTY_VALUE : claim.getUser().getName();
            rows[i][1] = claim.getItem() == null ? EMPTY_VALUE : claim.getItem().getName();
            rows[i][2] = DashboardUi.date(claim);
            rows[i][3] = claim.getStatus() == null ? EMPTY_VALUE : claim.getStatus().name();
        }
        return rows;
    }

    private Object[][] emptyRows(String text) {
        return new Object[][]{{text, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
    }

    // -------------------------------------------------------------------------
    // Text Helpers
    // -------------------------------------------------------------------------

    private String userName(Report report) {
        return report == null || report.getUser() == null ? EMPTY_VALUE : report.getUser().getName();
    }

    private String itemName(Report report) {
        return report == null || report.getItem() == null ? EMPTY_VALUE : report.getItem().getName();
    }

    private String reportStatus(Report report) {
        return report == null || report.getStatus() == null ? EMPTY_VALUE : report.getStatus().name();
    }

    private String itemStatus(Report report) {
        return report == null || report.getItem() == null || report.getItem().getStatus() == null
                ? EMPTY_VALUE
                : report.getItem().getStatus().name();
    }
}
