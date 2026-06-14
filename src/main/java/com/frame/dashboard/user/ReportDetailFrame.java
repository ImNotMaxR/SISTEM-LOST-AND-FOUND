package com.frame.dashboard.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.model.Report;

public class ReportDetailFrame extends JFrame {

    public ReportDetailFrame(Report report) {
        setTitle("Detail Laporan - " + (report != null ? report.getReportId() : "Detail"));
        setMinimumSize(new Dimension(920, 560));
        setPreferredSize(new Dimension(920, 560));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        setContentPane(root);

        // Top: back button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JButton back = new JButton("\u2190  Kembali");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        back.addActionListener(e -> dispose());
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        top.add(back, BorderLayout.WEST);
        root.add(top, BorderLayout.NORTH);

        // Center: image and details
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        UserDashboardComponents.PhotoPanel imagePanel = new UserDashboardComponents.PhotoPanel(report);
        imagePanel.setPreferredSize(new Dimension(880, 280));
        center.add(imagePanel, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        details.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        details.add(UserDashboardComponents.label(report != null && report.getItem() != null ? report.getItem().getName() : "-", 20, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        details.add(UserDashboardComponents.label("Kategori: " + (report != null && report.getItem() != null && report.getItem().getCategory() != null ? report.getItem().getCategory().getName() : "-"), 14, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        details.add(UserDashboardComponents.paragraph(report != null ? report.getDescription() : "-", 14), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(12, 0, 0, 0);
        details.add(UserDashboardComponents.label("Lokasi: " + (report != null ? UserDashboardComponents.location(report) : "-"), 14, Font.PLAIN, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        details.add(UserDashboardComponents.label("Status: " + (report != null && report.getItem() != null ? report.getItem().getStatus() : "-"), 14, Font.BOLD, UserDashboardComponents.PRIMARY_DARK), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 0, 0);
        details.add(UserDashboardComponents.label("Tanggal: " + (report != null ? UserDashboardComponents.date(report) : "-"), 13, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        center.add(details, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        pack();
    }
}
