package com.frame.dashboard.admin;

import com.enumeration.ClaimStatus;
import com.exception.ValidationException;
import com.frame.AppDialog;
import com.frame.dashboard.shared.ReportDetailFrame;
import com.frame.dashboard.shared.DashboardUi;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Admin;
import com.model.Claim;
import com.model.Item;
import com.model.Report;
import com.model.User;
import com.model.VerificationDocument;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AdminClaimDetailFrame extends JDialog {

    private static final Dimension DEFAULT_FRAME_SIZE = new Dimension(720, 720);
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(620, 560);
    private static final Color DETAIL_COLOR = new Color(44, 94, 173);
    private static final Color ACCEPT_COLOR = new Color(22, 163, 74);
    private static final Color REJECT_COLOR = new Color(220, 38, 38);

    private final Claim claim;
    private final ClaimManager claimManager;
    private final Runnable onUpdated;

    // -------------------------------------------------------------------------
    // Frame Setup
    // -------------------------------------------------------------------------

    public AdminClaimDetailFrame(Claim claim, ClaimManager claimManager, Runnable onUpdated) {
        super((java.awt.Frame) null, "Detail Klaim", true);
        this.claim = claim;
        this.claimManager = claimManager;
        this.onUpdated = onUpdated;
        Dimension frameSize = responsiveFrameSize();
        setMinimumSize(MINIMUM_FRAME_SIZE);
        setPreferredSize(frameSize);
        setSize(frameSize);
        setResizable(true);
        setContentPane(createContent());
        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // Frame Sizing
    // -------------------------------------------------------------------------

    private Dimension responsiveFrameSize() {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int width = Math.min(DEFAULT_FRAME_SIZE.width, Math.max(MINIMUM_FRAME_SIZE.width, bounds.width - 140));
        int height = Math.min(DEFAULT_FRAME_SIZE.height, Math.max(MINIMUM_FRAME_SIZE.height, bounds.height - 90));
        return new Dimension(width, height);
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DashboardUi.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        root.add(DashboardUi.section("Detail Klaim", "Periksa Data Pengajuan Klaim Pengguna."), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        center.add(createDetailScroll(), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        footer.add(createActions(), BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    // -------------------------------------------------------------------------
    // Scroll UI
    // -------------------------------------------------------------------------

    private JScrollPane createDetailScroll() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        wrapper.add(createDetailCard(), gbc);

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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        applyScrollStyle(scrollPane.getVerticalScrollBar());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void applyScrollStyle(JScrollBar scrollBar) {
        scrollBar.setUnitIncrement(16);
        scrollBar.setPreferredSize(new Dimension(8, 0));
        scrollBar.setOpaque(false);
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(75, 145, 255);
                trackColor = new Color(245, 248, 252);
                thumbDarkShadowColor = thumbColor.darker();
                thumbHighlightColor = thumbColor.brighter();
                thumbLightShadowColor = thumbColor;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent component, java.awt.Rectangle thumbBounds) {
                if (!component.isEnabled() || thumbBounds.isEmpty()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(
                        thumbBounds.x + 1,
                        thumbBounds.y + 2,
                        Math.max(4, thumbBounds.width - 2),
                        Math.max(8, thumbBounds.height - 4),
                        10,
                        10
                );
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics graphics, JComponent component, java.awt.Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRoundRect(
                        trackBounds.x,
                        trackBounds.y,
                        trackBounds.width,
                        trackBounds.height,
                        10,
                        10
                );
                g2.dispose();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Detail Card UI
    // -------------------------------------------------------------------------

    private JPanel createDetailCard() {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(Color.WHITE, 20);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 20, 1),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints gbc = DashboardUi.contentConstraints();
        Item item = claim.getItem();
        gbc.gridy = 0;
        card.add(DashboardUi.label("Informasi Klaim", 18, Font.BOLD, DashboardUi.TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        card.add(createInfoGrid(item), gbc);

        if (hasDocuments()) {
            addDocumentSection(card, gbc, 2);
        }
        return card;
    }

    // -------------------------------------------------------------------------
    // Information Grid UI
    // -------------------------------------------------------------------------

    private JPanel createInfoGrid(Item item) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        addInfoCell(grid, gbc, 0, 0, "Claim ID", safe(claim.getClaimId()));
        addInfoCell(grid, gbc, 1, 0, "Pengaju", claim.getUser() == null ? "-" : titleCase(claim.getUser().getName()));
        addInfoCell(grid, gbc, 0, 1, "Barang", item == null ? "-" : titleCase(item.getName()));
        addInfoCell(grid, gbc, 1, 1, "Kategori", item == null || item.getCategory() == null ? "-" : titleCase(item.getCategory().getName()));
        addInfoCell(grid, gbc, 0, 2, "Lokasi Barang", item == null ? "-" : titleCase(item.getLocation()));
        addInfoCell(grid, gbc, 1, 2, "Status Item", item == null || item.getStatus() == null ? "-" : titleCase(item.getStatus().name()));
        addInfoCell(grid, gbc, 0, 3, "Report Asal", safe(claim.getRelatedReportId()));
        addInfoCell(grid, gbc, 1, 3, "Tanggal Klaim", DashboardUi.date(claim));
        addInfoCell(grid, gbc, 0, 4, "Status Klaim", statusText(claim.getStatus()));
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
        cell.add(createValueText(value), gbc);
        return cell;
    }

    private JTextArea createValueText(String value) {
        JTextArea area = new JTextArea(safe(value));
        area.setFont(new Font("Poppins", Font.BOLD, 13));
        area.setForeground(DashboardUi.TEXT_DARK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 10, 0, 0, 0);
        panel.add(DashboardUi.label(label + ": " + value, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
    }

    private void addDocumentSection(JPanel panel, GridBagConstraints gbc, int startRow) {
        gbc.gridy = startRow;
        gbc.insets = new Insets(22, 0, 0, 0);
        panel.add(DashboardUi.label("Dokumen Verifikasi", 16, Font.BOLD, DashboardUi.TEXT_DARK), gbc);

        int row = startRow + 1;
        for (VerificationDocument document : claim.getDocuments()) {
            gbc.gridy = row++;
            gbc.insets = new Insets(12, 0, 0, 0);
            panel.add(createDocumentCard(document), gbc);
        }
    }

    private JPanel createDocumentCard(VerificationDocument document) {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(new Color(248, 250, 252), 18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(226, 232, 240), 18, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 0, 0, 16);
        card.add(new EvidencePhotoPanel(document.getFile()), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(createDocumentInfo(document), gbc);

        return card;
    }

    private JPanel createDocumentInfo(VerificationDocument document) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = DashboardUi.contentConstraints();

        gbc.gridy = 0;
        panel.add(DashboardUi.label(safe(document.getType()), 15, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 0, 0);
        panel.add(DashboardUi.label("Dokumen ID: " + safe(document.getDocumentId()), 12, Font.PLAIN, DashboardUi.TEXT_MUTED), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        panel.add(createDescriptionArea(document.getDescription()), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(12, 0, 0, 0);
        String fileName = document.getFile() == null ? "-" : document.getFile().getName();
        panel.add(DashboardUi.label("File: " + fileName, 12, Font.BOLD, DashboardUi.TEXT_MUTED), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 0, 0);
        String path = document.getFile() == null ? "-" : document.getFile().getAbsolutePath();
        panel.add(createMutedText(path), gbc);
        return panel;
    }

    private JTextArea createDescriptionArea(String text) {
        JTextArea area = new JTextArea(safe(text));
        area.setFont(new Font("Poppins", Font.BOLD, 12));
        area.setForeground(DashboardUi.TEXT_DARK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    private JTextArea createMutedText(String text) {
        JTextArea area = new JTextArea(safe(text));
        area.setFont(new Font("Poppins", Font.PLAIN, 11));
        area.setForeground(DashboardUi.TEXT_MUTED);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    // -------------------------------------------------------------------------
    // Actions UI
    // -------------------------------------------------------------------------

    private JPanel createActions() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        JButton detailButton = createButton("Lihat Detail Barang", DETAIL_COLOR);
        detailButton.addActionListener(event -> openItemDetail());
        panel.add(detailButton, gbc);

        gbc.gridx = 1;
        JButton acceptButton = createButton("Terima", ACCEPT_COLOR);
        acceptButton.setEnabled(claim.getStatus() == ClaimStatus.PENDING);
        acceptButton.addActionListener(event -> acceptClaim());
        panel.add(acceptButton, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton rejectButton = createButton("Tolak", REJECT_COLOR);
        rejectButton.setEnabled(claim.getStatus() == ClaimStatus.PENDING);
        rejectButton.addActionListener(event -> rejectClaim());
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

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void acceptClaim() {
        boolean confirmed = AppDialog.confirm(this, "Konfirmasi Terima", "Terima Pengajuan Klaim Ini?", "Terima", "Batal");
        if (!confirmed) {
            return;
        }
        try {
            claimManager.processClaim(claim.getClaimId(), ClaimStatus.VALID, currentAdmin(), null);
            AppDialog.success(this, "Klaim Diterima", "Pengajuan klaim berhasil diterima.");
            notifyUpdated();
        } catch (ValidationException e) {
            AppDialog.warning(this, "Validasi Gagal", e.getMessage());
        }
    }

    private void rejectClaim() {
        String reason = AppDialog.promptText(this, "Alasan Penolakan", "Masukkan Alasan Penolakan Klaim.", "Lanjut", "Batal");
        if (reason == null) {
            return;
        }
        reason = reason.trim();
        boolean confirmed = AppDialog.confirm(this, "Konfirmasi Tolak", "Tolak Pengajuan Klaim Ini?", "Tolak", "Batal");
        if (!confirmed) {
            return;
        }
        try {
            claimManager.processClaim(claim.getClaimId(), ClaimStatus.DITOLAK, currentAdmin(), reason);
            AppDialog.success(this, "Klaim Ditolak", "Pengajuan klaim berhasil ditolak.");
            notifyUpdated();
        } catch (ValidationException e) {
            AppDialog.warning(this, "Validasi Gagal", e.getMessage());
        }
    }

    private void openItemDetail() {
        if (claim.getRelatedReportId() == null || claim.getRelatedReportId().isBlank()) {
            AppDialog.warning(this, "Detail Tidak Tersedia", "Klaim ini tidak memiliki report asal.");
            return;
        }

        Object report = new ReportManager().findById(claim.getRelatedReportId());
        if (!(report instanceof Report)) {
            AppDialog.warning(this, "Detail Tidak Tersedia", "Report asal klaim tidak ditemukan.");
            return;
        }
        new ReportDetailFrame((Report) report).setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Auth Helpers
    // -------------------------------------------------------------------------

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

    private boolean hasDocuments() {
        return claim.getDocuments() != null && !claim.getDocuments().isEmpty();
    }

    private String statusText(ClaimStatus status) {
        if (status == ClaimStatus.VALID) {
            return "Diterima";
        }
        if (status == ClaimStatus.DITOLAK) {
            return "Ditolak";
        }
        return "Pending";
    }

    // -------------------------------------------------------------------------
    // Text Helpers
    // -------------------------------------------------------------------------

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

    private static class EvidencePhotoPanel extends JPanel {
        private final File file;
        private Image image;

        EvidencePhotoPanel(File file) {
            this.file = file;
            if (file != null && file.exists()) {
                this.image = new ImageIcon(file.getAbsolutePath()).getImage();
            }
            setPreferredSize(new Dimension(240, 180));
            setMinimumSize(new Dimension(220, 160));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            if (image != null) {
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                if (imageWidth > 0 && imageHeight > 0) {
                    g2.clip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                    double scale = Math.min((double) getWidth() / imageWidth, (double) getHeight() / imageHeight);
                    int drawWidth = (int) Math.round(imageWidth * scale);
                    int drawHeight = (int) Math.round(imageHeight * scale);
                    int x = (getWidth() - drawWidth) / 2;
                    int y = (getHeight() - drawHeight) / 2;
                    g2.drawImage(image, x, y, drawWidth, drawHeight, this);
                }
            } else {
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(224, 242, 254), getWidth(), getHeight(), new Color(59, 130, 246)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 13));
                String text = "Foto Tidak Tersedia";
                int x = (getWidth() - g2.getFontMetrics().stringWidth(text)) / 2;
                int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2;
                g2.drawString(text, x, y);
            }

            g2.dispose();

            Graphics2D borderG2 = (Graphics2D) graphics.create();
            borderG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            borderG2.setColor(new Color(226, 232, 240));
            borderG2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            borderG2.dispose();
        }
    }
}
