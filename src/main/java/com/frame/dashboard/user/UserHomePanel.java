package com.frame.dashboard.user;

import com.enumeration.ItemStatus;
import com.managers.ReportManager;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UserHomePanel extends JPanel {

    private static final String HEADER_SUBTITLE = "Sistem Informasi Lost & Found Kampus";
    private static final String LOST_SECTION_TITLE = "List Barang Hilang";
    private static final String EMPTY_LOST_REPORT_MESSAGE = "Belum Ada Laporan Barang Hilang.";
    private static final int RECENT_REPORT_LIMIT = 6;

    public UserHomePanel(User user, ReportManager reportManager) {
        configurePanel();
        add(UserDashboardComponents.scroll(createContent(user, reportManager)), BorderLayout.CENTER);
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent(User user, ReportManager reportManager) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(createHeader(user), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(26, 0, 8, 0);
        content.add(createStatsPanel(user, reportManager), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(UserDashboardComponents.section(
                LOST_SECTION_TITLE,
                reportManager.getLostReports().size() + " Laporan Barang Hilang Tercatat Di Sistem."
        ), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(12, 0, 0, 0);
        content.add(createRecentLostReportsGrid(reportManager), gbc);

        gbc.gridy = 4;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createHeader(User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UserDashboardComponents.label(HEADER_SUBTITLE, 15, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(UserDashboardComponents.label("Hello, " + getDisplayName(user), 34, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        return panel;
    }

    private JPanel createStatsPanel(User user, ReportManager reportManager) {
        JPanel panel = UserDashboardComponents.statGrid();
        panel.setOpaque(false);

        int totalLostReports = reportManager.getLostReports().size();
        int userReportCount = countUserReports(user, reportManager);
        int foundItemCount = countFoundItems(reportManager);

        panel.add(new UserDashboardComponents.StatCard(
                "Total Barang Hilang",
                formatCount(totalLostReports),
                "Seluruh Laporan Kampus",
                UserDashboardComponents.PRIMARY_DARK,
                UserDashboardComponents.PRIMARY
        ));

        panel.add(new UserDashboardComponents.StatCard(
                "Laporan Saya",
                formatCount(userReportCount),
                "Pantau Laporan Akun Ini",
                UserDashboardComponents.PRIMARY_LIGHT,
                new Color(126, 203, 236)
        ));

        panel.add(new UserDashboardComponents.StatCard(
                "Barang Ditemukan",
                formatCount(foundItemCount),
                "Menunggu Proses Klaim",
                UserDashboardComponents.ORANGE,
                new Color(255, 159, 93)
        ));

        return panel;
    }

    private JPanel createRecentLostReportsGrid(ReportManager reportManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        ArrayList<Report> reports = reportManager.getValidReports();

        if (reports.isEmpty()) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_LOST_REPORT_MESSAGE));
            return grid;
        }

        reports.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));

        int limit = Math.min(RECENT_REPORT_LIMIT, reports.size());
        for (int i = 0; i < limit; i++) {
            Report report = reports.get(i);
            grid.add(new UserDashboardComponents.ReportCard(report, report.getItem().getStatus().name(), UserDashboardComponents.ORANGE));
        }

        return grid;
    }

    private int countUserReports(User user, ReportManager reportManager) {
        int reportCount = 0;
        for (LostReport report : reportManager.getLostReports()) {
            if (isMine(user, report)) {
                reportCount++;
            }
        }
        return reportCount;
    }

    private int countFoundItems(ReportManager reportManager) {
        int foundCount = 0;
        for (FoundReport report : reportManager.getFoundReports()) {
            if (report.getItem().getStatus() == ItemStatus.DITEMUKAN || report.isValid()) {
                foundCount++;
            }
        }
        return foundCount;
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }

    private String getDisplayName(User user) {
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return "Pengguna";
        }
        return user.getName();
    }

    private boolean isMine(User user, Report report) {
        return user != null
                && report.getUser() != null
                && user.getUserId().equals(report.getUser().getUserId());
    }
}
