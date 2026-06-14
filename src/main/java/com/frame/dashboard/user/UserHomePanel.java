package com.frame.dashboard.user;

import com.enumeration.ItemStatus;
import com.managers.ClaimManager;
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

    public UserHomePanel(User user, ReportManager reportManager, ClaimManager claimManager) {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        content.add(createHeader(user), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(26, 0, 8, 0);
        content.add(createStats(user, reportManager), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(UserDashboardComponents.section(
                "List barang hilang",
                reportManager.getLostReports().size() + " laporan barang hilang tercatat di sistem."
        ), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(12, 0, 0, 0);
        content.add(createLostGrid(reportManager), gbc);

        gbc.gridy = 4;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
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
        panel.add(UserDashboardComponents.label("Sistem Informasi Lost & Found Kampus", 15, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(UserDashboardComponents.label("Hello, " + displayName(user), 34, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        return panel;
    }

    private JPanel createStats(User user, ReportManager reportManager) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        int lostCount = reportManager.getLostReports().size();
        int myReports = 0;
        int foundCount = 0;

        for (LostReport report : reportManager.getLostReports()) {
            if (isMine(user, report)) {
                myReports++;
            }
        }

        for (FoundReport report : reportManager.getFoundReports()) {
            if (report.getItem().getStatus() == ItemStatus.DITEMUKAN || report.isValid()) {
                foundCount++;
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);

        gbc.gridx = 0;
        panel.add(new UserDashboardComponents.StatCard(
                "Total barang hilang",
                String.format("%02d", lostCount),
                "Seluruh laporan kampus",
                UserDashboardComponents.PRIMARY_DARK,
                UserDashboardComponents.PRIMARY
        ), gbc);

        gbc.gridx = 1;
        panel.add(new UserDashboardComponents.StatCard(
                "Laporan saya",
                String.format("%02d", myReports),
                "Pantau laporan akun ini",
                UserDashboardComponents.PRIMARY_LIGHT,
                new Color(126, 203, 236)
        ), gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(new UserDashboardComponents.StatCard(
                "Barang ditemukan",
                String.format("%02d", foundCount),
                "Menunggu proses klaim",
                UserDashboardComponents.ORANGE,
                new Color(255, 159, 93)
        ), gbc);

        return panel;
    }

    private JPanel createLostGrid(ReportManager reportManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        ArrayList<LostReport> reports = reportManager.getLostReports();

        if (reports.isEmpty()) {
            grid.add(UserDashboardComponents.emptyState("Belum ada laporan barang hilang."));
            return grid;
        }

        int limit = Math.min(6, reports.size());
        for (int i = 0; i < limit; i++) {
            LostReport report = reports.get(i);
            grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.ORANGE));
        }

        return grid;
    }

    private String displayName(User user) {
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
