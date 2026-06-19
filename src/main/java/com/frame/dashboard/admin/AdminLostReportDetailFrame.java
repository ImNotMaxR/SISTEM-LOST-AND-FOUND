package com.frame.dashboard.admin;

import com.enumeration.ReportStatus;
import com.frame.AppDialog;
import com.frame.dashboard.user.ReportDetailFrame;
import com.frame.dashboard.user.UserDashboardComponents;
import com.managers.ReportManager;
import com.model.Admin;
import com.model.LostReport;
import com.model.User;
import com.service.AuthService;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class AdminLostReportDetailFrame extends JDialog {

    private static final Dimension FRAME_SIZE = new Dimension(520, 520);
    private static final Color DETAIL_COLOR = new Color(44, 94, 173);
    private static final Color ACCEPT_COLOR = new Color(22, 163, 74);
    private static final Color REJECT_COLOR = new Color(220, 38, 38);

    private final LostReport report;
    private final ReportManager reportManager;
    private final Runnable onUpdated;

    public AdminLostReportDetailFrame(LostReport report, ReportManager reportManager, Runnable onUpdated) {
        super((java.awt.Frame) null, "Detail Laporan Barang Hilang", true);
        this.report = report;
        this.reportManager = reportManager;
        this.onUpdated = onUpdated;
        setMinimumSize(FRAME_SIZE);
        setPreferredSize(FRAME_SIZE);
        setResizable(false);
        setContentPane(createContent());
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        root.add(UserDashboardComponents.section("Detail Laporan", "Kelola Status Laporan Barang Hilang."), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(24, 0, 0, 0);
        root.add(createDetailCard(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        root.add(createActions(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        root.add(new JPanel() {{ setOpaque(false); }}, gbc);
        return root;
    }

    private JPanel createDetailCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 20, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        ));

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        addRow(card, gbc, 0, "Report ID", safe(report.getReportId()));
        addRow(card, gbc, 1, "Pelapor", report.getUser() == null ? "-" : titleCase(report.getUser().getName()));
        addRow(card, gbc, 2, "Barang", report.getItem() == null ? "-" : titleCase(report.getItem().getName()));
        addRow(card, gbc, 3, "Kategori", report.getItem() == null || report.getItem().getCategory() == null ? "-" : titleCase(report.getItem().getCategory().getName()));
        addRow(card, gbc, 4, "Lokasi Hilang", titleCase(report.getLostLocation()));
        addRow(card, gbc, 5, "Tanggal", UserDashboardComponents.date(report));
        addRow(card, gbc, 6, "Status", titleCase(report.getStatus().name()));
        if (report.getStatus() == ReportStatus.DITOLAK) {
            addRow(card, gbc, 7, "Alasan Ditolak", safe(report.getRejectionReason()));
        }
        return card;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 10, 0, 0, 0);
        panel.add(UserDashboardComponents.label(label + ": " + value, 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);
    }

    private JPanel createActions() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        JButton detailButton = createButton("Lihat Detail User", DETAIL_COLOR);
        detailButton.addActionListener(event -> new ReportDetailFrame(report).setVisible(true));
        panel.add(detailButton, gbc);

        gbc.gridx = 1;
        JButton acceptButton = createButton("Terima", ACCEPT_COLOR);
        acceptButton.setEnabled(report.getStatus() != ReportStatus.VALID);
        acceptButton.addActionListener(event -> acceptReport());
        panel.add(acceptButton, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton rejectButton = createButton("Tolak", REJECT_COLOR);
        rejectButton.setEnabled(report.getStatus() != ReportStatus.DITOLAK);
        rejectButton.addActionListener(event -> rejectReport());
        panel.add(rejectButton, gbc);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 40));
        button.setFont(new Font("Poppins", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics graphics, JComponent component) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color background = button.isEnabled()
                        ? (button.getModel().isRollover() ? color.brighter() : color)
                        : new Color(190, 199, 210);
                graphics2D.setColor(background);
                graphics2D.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), 16, 16);
                graphics2D.dispose();
                super.paint(graphics, component);
            }
        });
        return button;
    }

    private void acceptReport() {
        boolean confirmed = AppDialog.confirm(this, "Konfirmasi Terima", "Terima Laporan Barang Hilang Ini?", "Terima", "Batal");
        if (!confirmed) {
            return;
        }
        reportManager.validateReport(report.getReportId(), ReportStatus.VALID, currentAdmin(), null);
        AppDialog.success(this, "Status Diperbarui", "Laporan Barang Hilang Berhasil Diterima.");
        notifyUpdated();
    }

    private void rejectReport() {
        String reason = AppDialog.promptText(this, "Alasan Penolakan", "Masukkan Alasan Penolakan Yang Akan Dilihat Oleh Admin Dan User.", "Lanjut", "Batal");
        if (reason == null) {
            return;
        }
        reason = reason.trim();
        if (reason.isBlank()) {
            AppDialog.warning(this, "Alasan Wajib Diisi", "Alasan Penolakan Tidak Boleh Kosong.");
            return;
        }
        boolean confirmed = AppDialog.confirm(this, "Konfirmasi Tolak", "Tolak Laporan Barang Hilang Ini?", "Tolak", "Batal");
        if (!confirmed) {
            return;
        }
        reportManager.validateReport(report.getReportId(), ReportStatus.DITOLAK, currentAdmin(), reason);
        AppDialog.success(this, "Status Diperbarui", "Laporan Barang Hilang Berhasil Ditolak.");
        notifyUpdated();
    }

    private Admin currentAdmin() {
        User user = AuthService.getCurrentUser();
        return user instanceof Admin ? (Admin) user : null;
    }

    private void notifyUpdated() {
        if (onUpdated != null) {
            onUpdated.run();
        }
        dispose();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private String titleCase(String value) {
        String safeValue = safe(value);
        if ("-".equals(safeValue)) {
            return safeValue;
        }
        String normalized = safeValue.replace('_', ' ').toLowerCase();
        StringBuilder result = new StringBuilder(normalized.length());
        boolean capitalizeNext = true;
        for (int i = 0; i < normalized.length(); i++) {
            char character = normalized.charAt(i);
            if (Character.isWhitespace(character) || character == '-') {
                result.append(character);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(character));
                capitalizeNext = false;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}
