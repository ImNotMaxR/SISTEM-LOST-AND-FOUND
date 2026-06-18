package com.frame.dashboard.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.model.Report;

public class ReportDetailFrame extends JDialog {

    private static final String TITLE = "Detail Laporan";
    private static final Dimension FRAME_SIZE = new Dimension(920, 650);
    private static final Dimension PHOTO_SIZE = new Dimension(852, 240);

    // =========================
    // Setup
    // =========================

    public ReportDetailFrame(Report report) {
        super((java.awt.Frame) null, "Detail Laporan - " + (report != null ? report.getReportId() : ""), true);
        configureFrame(report);
        setContentPane(createMainPanel(report));
        pack();
        setLocationRelativeTo(null);
    }

    private void configureFrame(Report report) {
        setMinimumSize(FRAME_SIZE);
        setPreferredSize(FRAME_SIZE);
        setResizable(false);
    }

    // =========================
    // Layout
    // =========================

    private JPanel createMainPanel(Report report) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        
        // Top Photo Header
        root.add(createPhotoHeader(report), BorderLayout.NORTH);
        
        // Bottom Content
        root.add(createDetailsContentPanel(report), BorderLayout.CENTER);
        
        return root;
    }

    private JPanel createPhotoHeader(Report report) {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(PHOTO_SIZE.width, PHOTO_SIZE.height + 25));
        header.setOpaque(false);

        JPanel imagePanel = new UserDashboardComponents.PhotoPanel(report, PHOTO_SIZE);
        header.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 25));
        header.add(imagePanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createDetailsContentPanel(Report report) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Left Column (Details)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 20);
        content.add(createReportInfoColumn(report), gbc);
        
        // Right Column (Actions)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 20, 0, 0);
        content.add(createStatusActionColumn(report), gbc);
        
        return content;
    }

    private JPanel createReportInfoColumn(Report report) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 12, 0);
        
        int row = 0;
        panel.add(createDetailRow("Kategori:", getReportCategoryName(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Deskripsi:", getReportDescription(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Lokasi:", getReportLocation(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Tanggal:", getReportDate(report)), gbc); gbc.gridy = ++row;
        
        gbc.weighty = 1.0;
        panel.add(new JPanel(){{setOpaque(false);}}, gbc);
        
        return panel;
    }

    // =========================
    // Components
    // =========================

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel lbl = UserDashboardComponents.label(label, 13, Font.PLAIN, UserDashboardComponents.TEXT_MUTED);
        
        if (label.equals("Deskripsi:")) {
            javax.swing.JTextArea area = new javax.swing.JTextArea(value);
            area.setFont(new Font("Poppins", Font.BOLD, 13));
            area.setForeground(UserDashboardComponents.TEXT_DARK);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(false);
            area.setEditable(false);
            area.setFocusable(false);
            area.setBorder(null);
            
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(false);
            p.add(lbl, BorderLayout.WEST);
            p.add(area, BorderLayout.CENTER);
            return p;
        } else {
            JLabel val = UserDashboardComponents.label(value, 13, Font.BOLD, UserDashboardComponents.TEXT_DARK);
            row.add(lbl, BorderLayout.WEST);
            row.add(val, BorderLayout.CENTER);
            return row;
        }
    }

    private JPanel createStatusActionColumn(Report report) {
        UserDashboardComponents.RoundedPanel panel = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 20, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Status Pill
        String status = getReportItemStatus(report);
        JPanel statusPill = createStatusPill(status);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(statusPill, gbc);
        
        // Info Text
        javax.swing.JTextArea infoText = new javax.swing.JTextArea("Semua user dapat melihat status dan klaim untuk transparansi pengelolaan barang kampus.");
        infoText.setFont(new Font("Poppins", Font.PLAIN, 11));
        infoText.setForeground(UserDashboardComponents.TEXT_MUTED);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setOpaque(false);
        infoText.setEditable(false);
        infoText.setFocusable(false);
        infoText.setBorder(null);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(infoText, gbc);
        
        // Spacer
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        panel.add(new JPanel(){{setOpaque(false);}}, gbc);
        
        // Buttons
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        int actionRow = 3;
        
        com.model.User currentUser = com.service.AuthService.getCurrentUser();
        boolean isOwner = currentUser != null && report != null && report.getUser().getUserId().equals(currentUser.getUserId());
        boolean isPending = report != null && report.getStatus() == com.enumeration.ReportStatus.PENDING;
        boolean isEditableTime = report != null && report.isEditable();
        com.managers.ClaimManager claimManager = new com.managers.ClaimManager();
        boolean hasSubmittedClaim = hasCurrentUserSubmittedActiveClaim(claimManager, currentUser, report);

        if (hasSubmittedClaim) {
            infoText.setText("Pengajuan sudah dilakukan. Mohon tunggu konfirmasi admin.");
        }

        if (isOwner && isPending && isEditableTime) {
            JButton btnEdit = createDetailActionButton("Edit Laporan Saya", true);
            btnEdit.addActionListener(e -> {
                dispose();
                com.managers.ReportManager reportManager = new com.managers.ReportManager();
                new com.frame.panel.EditLostReportPanel(report, reportManager, () -> {}).setVisible(true);
            });
            gbc.gridy = actionRow++;
            panel.add(btnEdit, gbc);
        }
        
        boolean isFoundReport = report instanceof com.model.FoundReport;
        boolean isValid = report != null && report.getStatus() == com.enumeration.ReportStatus.VALID;
        boolean hasMatch = isFoundReport && ((com.model.FoundReport) report).hasMatch();
        boolean canClaim = currentUser != null && !(currentUser instanceof com.model.Security) && !(currentUser instanceof com.model.Admin);
        
        if (isFoundReport && isValid && !hasMatch && canClaim) {
            gbc.gridy = actionRow++;
            gbc.insets = new Insets(0, 0, 10, 0);
            JButton btnClaim = createDetailActionButton(hasSubmittedClaim ? "Pengajuan Sudah Dilakukan" : "Klaim Barang", true);
            btnClaim.setEnabled(!hasSubmittedClaim);
            btnClaim.addActionListener(e -> {
                boolean confirm = com.frame.AppDialog.confirm(this, "Konfirmasi Klaim", "Apakah Anda yakin ingin mengklaim barang ini?", "Ya, Klaim", "Batal");
                if (confirm) {
                    com.model.Claim claim = new com.model.Claim("CLM-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(), currentUser, report.getItem(), report.getReportId());
                    boolean saved = claimManager.saveClaim(claim);
                    if (!saved) {
                        com.frame.AppDialog.error(this, "Klaim Gagal", claimManager.getLastErrorMessage());
                        return;
                    }
                    btnClaim.setText("Pengajuan Sudah Dilakukan");
                    btnClaim.setEnabled(false);
                    infoText.setText("Pengajuan sudah dilakukan. Mohon tunggu konfirmasi admin.");
                    com.frame.AppDialog.success(this, "Klaim Berhasil", "Permintaan klaim Anda telah diajukan dan menunggu persetujuan.");
                }
            });
            panel.add(btnClaim, gbc);
        }
        
        gbc.gridy = actionRow;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnBack = createDetailActionButton("Kembali", false);
        btnBack.addActionListener(e -> dispose());
        panel.add(btnBack, gbc);
        
        return panel;
    }

    // =========================
    // Actions
    // =========================

    private JPanel createStatusPill(String status) {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 237, 213));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lbl = UserDashboardComponents.label("Status: " + status, 12, Font.BOLD, new Color(194, 65, 12));
        pill.add(lbl);
        return pill;
    }

    private JButton createDetailActionButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(0, 36));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.getModel().setRollover(true);
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.getModel().setRollover(false);
                btn.repaint();
            }
        });

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean hovered = btn.getModel().isRollover();
                
                if (!btn.isEnabled()) {
                    g2.setColor(new Color(245, 245, 245));
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 18, 18);
                    g2.setColor(new Color(220, 220, 220));
                    g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 18, 18);
                    btn.setForeground(new Color(130, 140, 150));
                } else if (isPrimary) {
                    g2.setColor(hovered ? new Color(245, 248, 255) : Color.WHITE);
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 18, 18);
                    g2.setColor(UserDashboardComponents.PRIMARY);
                    g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 18, 18);
                    btn.setForeground(UserDashboardComponents.PRIMARY);
                } else {
                    g2.setColor(hovered ? new Color(235, 235, 235) : new Color(245, 245, 245));
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 18, 18);
                    btn.setForeground(UserDashboardComponents.TEXT_DARK);
                }
                
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        return btn;
    }

    // =========================
    // Data Helpers
    // =========================

    private String getReportCategoryName(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) {
            return "-";
        }
        return report.getItem().getCategory().getName();
    }

    private String getReportDescription(Report report) {
        return report != null ? report.getDescription() : "-";
    }

    private String getReportLocation(Report report) {
        return report != null ? UserDashboardComponents.location(report) : "-";
    }

    private String getReportItemStatus(Report report) {
        return report != null && report.getItem() != null ? report.getItem().getStatus().name() : "-";
    }

    private String getReportDate(Report report) {
        return report != null ? UserDashboardComponents.date(report) : "-";
    }

    private boolean hasCurrentUserSubmittedActiveClaim(com.managers.ClaimManager claimManager, com.model.User currentUser, Report report) {
        if (claimManager == null || currentUser == null || report == null || report.getItem() == null) {
            return false;
        }

        if (claimManager.hasActiveClaimByUserForReportOrItem(currentUser.getUserId(), report.getReportId(), report.getItem().getItemID())) {
            return true;
        }

        for (com.model.Claim claim : claimManager.getClaims()) {
            if (claim.getUser() == null || claim.getItem() == null) {
                continue;
            }

            boolean sameUser = currentUser.getUserId().equals(claim.getUser().getUserId());
            boolean sameItem = report.getItem().getItemID().equals(claim.getItem().getItemID());
            boolean sameReport = report.getReportId() != null && report.getReportId().equals(claim.getRelatedReportId());
            boolean activeClaim = claim.getStatus() == com.enumeration.ClaimStatus.PENDING
                    || claim.getStatus() == com.enumeration.ClaimStatus.VALID;

            if (sameUser && activeClaim && (sameItem || sameReport)) {
                return true;
            }
        }

        return false;
    }
}
