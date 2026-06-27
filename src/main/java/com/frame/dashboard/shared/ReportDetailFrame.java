package com.frame.dashboard.shared;

import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.exception.ValidationException;
import com.frame.AppDialog;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Admin;
import com.model.Claim;
import com.model.FoundReport;
import com.model.Item;
import com.model.LostReport;
import com.model.Report;
import com.model.Security;
import com.model.User;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ReportDetailFrame extends JDialog {

    private static final Dimension DEFAULT_FRAME_SIZE = new Dimension(960, 720);
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(760, 560);
    private static final int PHOTO_HEIGHT = 220;
    private static final Color PRIMARY_ACTION = DashboardUi.PRIMARY;
    private static final Color SECONDARY_ACTION = new Color(245, 245, 245);

    // -------------------------------------------------------------------------
    // Frame Setup
    // -------------------------------------------------------------------------

    public ReportDetailFrame(Report report) {
        super((java.awt.Frame) null, "Detail Laporan - " + (report != null ? report.getReportId() : ""), true);
        Dimension frameSize = responsiveFrameSize();
        setMinimumSize(MINIMUM_FRAME_SIZE);
        setPreferredSize(frameSize);
        setSize(frameSize);
        setResizable(true);
        setContentPane(createContent(report));
        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // Frame Sizing
    // -------------------------------------------------------------------------

    private Dimension responsiveFrameSize() {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = Math.min(DEFAULT_FRAME_SIZE.width, Math.max(MINIMUM_FRAME_SIZE.width, bounds.width - 120));
        int height = Math.min(DEFAULT_FRAME_SIZE.height, Math.max(MINIMUM_FRAME_SIZE.height, bounds.height - 90));
        return new Dimension(width, height);
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private JPanel createContent(Report report) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DashboardUi.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        root.add(DashboardUi.section("Detail Laporan", getHeaderSubtitle(report)), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        center.add(createScrollContent(report), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JScrollPane createScrollContent(Report report) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        wrapper.add(createDetailCard(report), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        wrapper.add(spacer, gbc);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        applyScrollStyle(scrollPane.getVerticalScrollBar());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    // -------------------------------------------------------------------------
    // Detail UI
    // -------------------------------------------------------------------------

    private JPanel createDetailCard(Report report) {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(Color.WHITE, 22);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 22, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        card.add(createPhotoPanel(report), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 0, 0, 0);
        card.add(createBody(report), gbc);
        return card;
    }

    private JPanel createPhotoPanel(Report report) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(new DetailPhotoPanel(report), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBody(Report report) {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 0, 0, 16);
        body.add(createInfoCard(report), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 16, 0, 0);
        body.add(createActionCard(report), gbc);
        return body;
    }

    private JPanel createInfoCard(Report report) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        panel.add(DashboardUi.label("Informasi Barang", 18, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(createInfoGrid(report), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(createDescriptionBox("Deskripsi", getReportDescription(report)), gbc);
        if (report != null && report.getStatus() == com.enumeration.ReportStatus.DITOLAK) {
            gbc.gridy = 3;
            gbc.insets = new Insets(12, 0, 0, 0);
            panel.add(createRejectedReasonBox(getRejectionReason(report)), gbc);
        }
        return panel;
    }

    private JPanel createInfoGrid(Report report) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        addInfoCell(grid, gbc, 0, 0, "Kategori", getReportCategoryName(report));
        addInfoCell(grid, gbc, 1, 0, "Lokasi", getReportLocation(report));
        addInfoCell(grid, gbc, 0, 1, "Tanggal", getReportDate(report));
        addInfoCell(grid, gbc, 1, 1, "Status Laporan", getReportStatus(report));
        return grid;
    }

    private void addInfoCell(JPanel grid, GridBagConstraints gbc, int x, int y, String label, String value) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(y == 0 ? 0 : 10, x == 0 ? 0 : 10, 0, x == 0 ? 10 : 0);
        grid.add(createInfoCell(label, value), gbc);
    }

    private JPanel createInfoCell(String label, String value) {
        DashboardUi.RoundedPanel cell = new DashboardUi.RoundedPanel(new Color(248, 250, 252), 14);
        cell.setLayout(new GridBagLayout());
        cell.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(226, 232, 240), 14, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        cell.add(DashboardUi.label(label, 11, Font.BOLD, DashboardUi.TEXT_MUTED), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        cell.add(createText(value, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        return cell;
    }

    private JPanel createDescriptionBox(String title, String value) {
        DashboardUi.RoundedPanel box = new DashboardUi.RoundedPanel(new Color(248, 250, 252), 16);
        box.setLayout(new GridBagLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(226, 232, 240), 16, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        box.add(DashboardUi.label(title, 12, Font.BOLD, DashboardUi.TEXT_MUTED), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        box.add(createText(value, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        return box;
    }

    private JPanel createRejectedReasonBox(String value) {
        DashboardUi.RoundedPanel box = new DashboardUi.RoundedPanel(new Color(255, 241, 242), 16);
        box.setLayout(new GridBagLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(254, 205, 211), 16, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        box.add(DashboardUi.label("Alasan Ditolak", 12, Font.BOLD, new Color(190, 18, 60)), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        box.add(createText(value, 13, Font.BOLD, new Color(90, 20, 35)), gbc);
        return box;
    }

    private JTextArea createText(String value, int size, int style, Color color) {
        JTextArea area = new JTextArea(value == null || value.isBlank() ? "-" : value);
        area.setFont(new Font("Poppins", style, size));
        area.setForeground(color);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    // -------------------------------------------------------------------------
    // Action UI
    // -------------------------------------------------------------------------

    private JPanel createActionCard(Report report) {
        DashboardUi.RoundedPanel panel = new DashboardUi.RoundedPanel(Color.WHITE, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 20, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = DashboardUi.contentConstraints();

        gbc.gridy = 0;
        panel.add(createStatusPill(getReportStatus(report)), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 20, 0);
        panel.add(createText("Semua user dapat melihat status dan klaim untuk transparansi pengelolaan barang kampus.", 12, Font.PLAIN, DashboardUi.TEXT_MUTED), gbc);
        gbc.gridy = 2;
        gbc.weighty = 1;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer, gbc);

        addActions(report, panel, gbc, 3);
        return panel;
    }

    private void addActions(Report report, JPanel panel, GridBagConstraints gbc, int row) {
        User currentUser = AuthService.getCurrentUser();
        ClaimManager claimManager = new ClaimManager();
        boolean hasSubmittedClaim = hasCurrentUserSubmittedActiveClaim(claimManager, currentUser, report);
        int actionRow = row;

        if (hasSubmittedClaim) {
            gbc.gridy = actionRow++;
            gbc.weighty = 0;
            gbc.insets = new Insets(0, 0, 10, 0);
            panel.add(createNoticeBox("Pengajuan sudah dilakukan. Mohon tunggu konfirmasi admin."), gbc);
        }

        boolean isOwner = currentUser != null && report != null && report.getUser() != null
                && report.getUser().getUserId().equals(currentUser.getUserId());
        boolean isPending = report != null && report.getStatus() == com.enumeration.ReportStatus.PENDING;
        boolean isEditableTime = report != null && report.isEditable();
        if (isOwner && isPending && isEditableTime) {
            JButton editButton = createActionButton("Edit Laporan Saya", true);
            editButton.addActionListener(event -> {
                dispose();
                new com.frame.panel.EditLostReportPanel(report, new ReportManager(), () -> {}).setVisible(true);
            });
            gbc.gridy = actionRow++;
            gbc.insets = new Insets(0, 0, 10, 0);
            panel.add(editButton, gbc);
        }

        boolean isLostReport = report instanceof LostReport;
        boolean isFoundReport = report instanceof FoundReport;
        boolean isValid = report != null && report.getStatus() == ReportStatus.VALID;
        boolean hasMatch = isFoundReport && ((FoundReport) report).hasMatch();
        boolean canClaim = currentUser != null && !(currentUser instanceof Security) && !(currentUser instanceof Admin);
        boolean isItemFound = report != null && report.getItem() != null && report.getItem().getStatus() == ItemStatus.DITEMUKAN;

        if ((isFoundReport && isValid && !hasMatch && canClaim) || (isLostReport && isOwner && isItemFound && canClaim)) {
            JButton claimButton = createActionButton(hasSubmittedClaim ? "Pengajuan Sudah Dilakukan" : "Klaim Barang", true);
            claimButton.setEnabled(!hasSubmittedClaim);
            claimButton.addActionListener(event -> handleClaim(report, currentUser, claimManager, claimButton));
            gbc.gridy = actionRow++;
            gbc.insets = new Insets(0, 0, 10, 0);
            panel.add(claimButton, gbc);
        }

        JButton backButton = createActionButton("Kembali", false);
        backButton.addActionListener(event -> dispose());
        gbc.gridy = actionRow;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(backButton, gbc);
    }

    private JPanel createNoticeBox(String text) {
        DashboardUi.RoundedPanel box = new DashboardUi.RoundedPanel(new Color(239, 246, 255), 14);
        box.setLayout(new BorderLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(191, 219, 254), 14, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        box.add(createText(text, 12, Font.BOLD, DashboardUi.PRIMARY_DARK), BorderLayout.CENTER);
        return box;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void handleClaim(Report report, User currentUser, ClaimManager claimManager, JButton claimButton) {
        if (requiresVerificationDocument(report)) {
            new ClaimVerificationFrame(currentUser, report, claimManager, () -> {
                claimButton.setText("Pengajuan Sudah Dilakukan");
                claimButton.setEnabled(false);
            }).setVisible(true);
            return;
        }
        boolean confirm = AppDialog.confirm(this, "Konfirmasi Klaim", "Apakah Anda yakin ingin mengklaim barang ini?", "Ya, Klaim", "Batal");
        if (!confirm) return;
        Claim claim = new Claim("CLM-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(), currentUser, report.getItem(), report.getReportId());
        try {
            claimManager.saveClaim(claim);
            claimButton.setText("Pengajuan Sudah Dilakukan");
            claimButton.setEnabled(false);
            AppDialog.success(this, "Klaim Berhasil", "Permintaan klaim Anda telah diajukan dan menunggu persetujuan.");
        } catch (ValidationException e) {
            AppDialog.error(this, "Klaim Gagal", e.getMessage());
        }
    }

    private JPanel createStatusPill(String status) {
        DashboardUi.RoundedPanel pill = new DashboardUi.RoundedPanel(new Color(255, 237, 213), 14);
        pill.setLayout(new BorderLayout());
        pill.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        pill.add(DashboardUi.label("Status: " + status, 12, Font.BOLD, new Color(194, 65, 12)), BorderLayout.CENTER);
        return pill;
    }

    private JButton createActionButton(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 42));
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(primary ? Color.WHITE : DashboardUi.TEXT_DARK);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics graphics, JComponent component) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill;
                if (!button.isEnabled()) {
                    fill = new Color(238, 242, 247);
                    button.setForeground(new Color(130, 140, 150));
                } else if (primary) {
                    fill = button.getModel().isRollover() ? DashboardUi.PRIMARY_DARK : PRIMARY_ACTION;
                    button.setForeground(Color.WHITE);
                } else {
                    fill = button.getModel().isRollover() ? new Color(235, 235, 235) : SECONDARY_ACTION;
                    button.setForeground(DashboardUi.TEXT_DARK);
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), 18, 18);
                g2.dispose();
                super.paint(graphics, component);
            }
        });
        return button;
    }

    // -------------------------------------------------------------------------
    // Scroll Styling
    // -------------------------------------------------------------------------

    private void applyScrollStyle(JScrollBar scrollBar) {
        scrollBar.setUnitIncrement(16);
        scrollBar.setPreferredSize(new Dimension(8, 0));
        scrollBar.setOpaque(false);
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(75, 145, 255);
                trackColor = new Color(245, 248, 252);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return zeroButton(); }
            private JButton zeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics graphics, JComponent component, java.awt.Rectangle thumbBounds) {
                if (!component.isEnabled() || thumbBounds.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 2, Math.max(4, thumbBounds.width - 2), Math.max(8, thumbBounds.height - 4), 10, 10);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics graphics, JComponent component, java.awt.Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
                g2.dispose();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Report Text Helpers
    // -------------------------------------------------------------------------

    private String getHeaderSubtitle(Report report) {
        Item item = report == null ? null : report.getItem();
        return item == null ? "Informasi detail laporan barang." : item.getName();
    }

    private String getReportCategoryName(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) return "-";
        return report.getItem().getCategory().getName();
    }

    private String getReportDescription(Report report) { return report != null ? report.getDescription() : "-"; }
    private String getReportLocation(Report report) { return report != null ? DashboardUi.location(report) : "-"; }
    private String getReportItemStatus(Report report) { return report != null && report.getItem() != null ? report.getItem().getStatus().name() : "-"; }
    private String getReportDate(Report report) { return report != null ? DashboardUi.date(report) : "-"; }

    private String getReportStatus(Report report) {
        if (report == null || report.getStatus() == null) return "-";
        if (report.getStatus() == com.enumeration.ReportStatus.VALID && report.getItem() != null && report.getItem().getStatus() != null) {
            return titleCase(report.getItem().getStatus().name());
        }
        return titleCase(report.getStatus().name());
    }

    private String getRejectionReason(Report report) {
        return report == null || report.getRejectionReason() == null || report.getRejectionReason().isBlank() ? "-" : report.getRejectionReason();
    }

    private boolean hasCurrentUserSubmittedActiveClaim(ClaimManager claimManager, User currentUser, Report report) {
        if (claimManager == null || currentUser == null || report == null || report.getItem() == null) return false;
        if (claimManager.hasActiveClaimByUserForReportOrItem(currentUser.getUserId(), report.getReportId(), report.getItem().getItemID())) return true;
        for (Claim claim : claimManager.getClaims()) {
            if (claim.getUser() == null || claim.getItem() == null) continue;
            boolean sameUser = currentUser.getUserId().equals(claim.getUser().getUserId());
            boolean sameItem = report.getItem().getItemID().equals(claim.getItem().getItemID());
            boolean sameReport = report.getReportId() != null && report.getReportId().equals(claim.getRelatedReportId());
            boolean activeClaim = claim.getStatus() == com.enumeration.ClaimStatus.PENDING || claim.getStatus() == com.enumeration.ClaimStatus.VALID;
            if (sameUser && activeClaim && (sameItem || sameReport)) return true;
        }
        return false;
    }

    private boolean requiresVerificationDocument(Report report) {
        return report != null && report.getItem() != null && report.getItem().getCategory() != null;
    }

    private String titleCase(String value) {
        if (value == null || value.isBlank()) return "-";
        String normalized = value.replace('_', ' ').toLowerCase();
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

    // -------------------------------------------------------------------------
    // Custom Components
    // -------------------------------------------------------------------------

    private static class DetailPhotoPanel extends JPanel {
        private final Report report;

        DetailPhotoPanel(Report report) {
            this.report = report;
            setPreferredSize(new Dimension(1, PHOTO_HEIGHT));
            setMinimumSize(new Dimension(1, 160));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int width = getWidth();
            int height = getHeight();
            java.awt.geom.RoundRectangle2D round = new java.awt.geom.RoundRectangle2D.Double(0, 0, width, height, 18, 18);
            g2.setClip(round);

            String path = report == null ? null : report.getPhotoPath();
            if (path != null && !path.isBlank() && new File(path).exists()) {
                Image image = new ImageIcon(path).getImage();
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                if (imageWidth > 0 && imageHeight > 0) {
                    g2.setColor(new Color(239, 246, 252));
                    g2.fillRoundRect(0, 0, width, height, 18, 18);
                    double scale = Math.min((double) width / imageWidth, (double) height / imageHeight);
                    int drawWidth = (int) Math.round(imageWidth * scale);
                    int drawHeight = (int) Math.round(imageHeight * scale);
                    int x = (width - drawWidth) / 2;
                    int y = (height - drawHeight) / 2;
                    g2.drawImage(image, x, y, drawWidth, drawHeight, this);
                } else {
                    paintFallback(g2, width, height);
                }
            } else {
                paintFallback(g2, width, height);
            }

            g2.setClip(null);
            g2.setColor(new Color(226, 232, 240));
            g2.drawRoundRect(0, 0, Math.max(0, width - 1), Math.max(0, height - 1), 18, 18);
            g2.dispose();
        }

        private void paintFallback(Graphics2D g2, int width, int height) {
            g2.setPaint(new java.awt.GradientPaint(0, 0, DashboardUi.PRIMARY_LIGHT, width, height, DashboardUi.PRIMARY_DARK));
            g2.fillRoundRect(0, 0, width, height, 18, 18);
            g2.setColor(new Color(255, 255, 255, 44));
            g2.fillOval(width - 120, -42, 160, 160);
            if (report != null && report.getItem() != null) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 18));
                String name = report.getItem().getName();
                String text = name.length() > 40 ? name.substring(0, 40) + "..." : name;
                g2.drawString(text, 22, height - 28);
            }
        }
    }
}
