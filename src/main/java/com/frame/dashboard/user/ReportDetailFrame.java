package com.frame.dashboard.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComponent;
import java.awt.Image;
import java.awt.FlowLayout;

import com.model.Report;

public class ReportDetailFrame extends JDialog {

    private static final String TITLE = "Detail Laporan";
    private static final Color DELETE = new Color(220, 38, 38);
    private static final int CONTENT_PADDING_X = 34;
    private static final Dimension FRAME_SIZE = new Dimension(920, 650);
    private static final Dimension PHOTO_SIZE = new Dimension(852, 240);

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

    private JPanel createMainPanel(Report report) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        
        // Top Photo Header
        root.add(createPhotoHeader(report), BorderLayout.NORTH);
        
        // Bottom Content
        root.add(createDetailsContent(report), BorderLayout.CENTER);
        
        return root;
    }

    private JPanel createPhotoHeader(Report report) {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(720, 240));
        header.setOpaque(false);
        
        String imagePath = report != null ? report.getPhotoPath() : null;
        
        JPanel imagePanel = new JPanel() {
            private Image img = imagePath != null ? new javax.swing.ImageIcon(imagePath).getImage() : null;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Draw rounded image clip
                java.awt.geom.RoundRectangle2D.Double clip = new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 20, 20);
                g2.setClip(clip);
                
                if (img != null) {
                    int imgW = img.getWidth(this);
                    int imgH = img.getHeight(this);
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(img, x, y, drawW, drawH, this);
                } else {
                    g2.setColor(new Color(230, 240, 250));
                    g2.fillRect(0, 0, w, h);
                }
                
                // Draw bottom gradient overlay
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, h - 100, new Color(0, 0, 0, 0), 0, h, new Color(0, 50, 150, 200));
                g2.setPaint(gp);
                g2.fillRect(0, h - 100, w, 100);
                
                // Draw item name text
                g2.setClip(null);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 26));
                String name = report != null && report.getItem() != null ? report.getItem().getName() : "Unknown Item";
                g2.drawString(name, 30, h - 30);
                
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 25));
        header.add(imagePanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createDetailsContent(Report report) {
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
        content.add(createLeftColumn(report), gbc);
        
        // Right Column (Actions)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 20, 0, 0);
        content.add(createRightColumn(report), gbc);
        
        return content;
    }

    private JPanel createLeftColumn(Report report) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 12, 0);
        
        int row = 0;
        panel.add(createDetailRow("Kategori:", getCategoryName(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Deskripsi:", getDescription(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Lokasi:", getLocation(report)), gbc); gbc.gridy = ++row;
        panel.add(createDetailRow("Tanggal:", getReportDate(report)), gbc); gbc.gridy = ++row;
        
        gbc.weighty = 1.0;
        panel.add(new JPanel(){{setOpaque(false);}}, gbc);
        
        return panel;
    }

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

    private JPanel createRightColumn(Report report) {
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
        String status = getItemStatus(report);
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
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        
        com.model.User currentUser = com.service.AuthService.getCurrentUser();
        boolean isOwner = currentUser != null && report != null && report.getUser().getUserId().equals(currentUser.getUserId());
        boolean isPending = report != null && report.getStatus() == com.enumeration.ReportStatus.PENDING;
        boolean isEditableTime = report != null && report.isEditable();

        if (isOwner && isPending && isEditableTime) {
            JButton btnEdit = createButton("Edit laporan saya", true);
            btnEdit.addActionListener(e -> {
                dispose();
                com.managers.ReportManager reportManager = new com.managers.ReportManager();
                new com.frame.panel.EditLostReportPanel(report, reportManager, () -> {}).setVisible(true);
            });
            panel.add(btnEdit, gbc);
        }
        
        boolean isFoundReport = report instanceof com.model.FoundReport;
        boolean isValid = report != null && report.getStatus() == com.enumeration.ReportStatus.VALID;
        boolean hasMatch = isFoundReport && ((com.model.FoundReport) report).hasMatch();
        boolean canClaim = currentUser != null && !(currentUser instanceof com.model.Security) && !(currentUser instanceof com.model.Admin);
        
        if (isFoundReport && isValid && !hasMatch && canClaim) {
            gbc.gridy = 5;
            gbc.insets = new Insets(0, 0, 10, 0);
            JButton btnClaim = createButton("Klaim Barang", true);
            btnClaim.addActionListener(e -> {
                boolean confirm = com.frame.AppDialog.confirm(this, "Konfirmasi Klaim", "Apakah Anda yakin ingin mengklaim barang ini?", "Ya, Klaim", "Batal");
                if (confirm) {
                    com.model.Claim claim = new com.model.Claim("CLM-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(), currentUser, report.getItem(), "Tidak ada bukti");
                    com.managers.ClaimManager claimManager = new com.managers.ClaimManager();
                    claimManager.add(claim);
                    com.frame.AppDialog.success(this, "Klaim Berhasil", "Permintaan klaim Anda telah diajukan dan menunggu persetujuan.");
                }
            });
            panel.add(btnClaim, gbc);
        }
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnBack = createButton("Kembali", false);
        btnBack.addActionListener(e -> dispose());
        panel.add(btnBack, gbc);
        
        return panel;
    }

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

    private JButton createButton(String text, boolean isPrimary) {
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
                
                if (isPrimary) {
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

    private String getCategoryName(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) {
            return "-";
        }
        return report.getItem().getCategory().getName();
    }

    private String getDescription(Report report) {
        return report != null ? report.getDescription() : "-";
    }

    private String getLocation(Report report) {
        return report != null ? UserDashboardComponents.location(report) : "-";
    }

    private String getItemStatus(Report report) {
        return report != null && report.getItem() != null ? report.getItem().getStatus().name() : "-";
    }

    private String getReportDate(Report report) {
        return report != null ? UserDashboardComponents.date(report) : "-";
    }
}
