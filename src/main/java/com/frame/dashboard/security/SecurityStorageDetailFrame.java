package com.frame.dashboard.security;

import com.enumeration.ClaimStatus;
import com.enumeration.ItemStatus;
import com.frame.AppDialog;
import com.frame.dashboard.user.UserDashboardComponents;
import com.managers.ClaimManager;
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.managers.StorageManager;
import com.model.Claim;
import com.model.FoundReport;
import com.model.Item;
import com.model.Report;
import com.model.StorageRecord;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SecurityStorageDetailFrame extends JDialog {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final Color DANGER = new Color(220, 38, 38);
    private static final Color DANGER_HOVER = new Color(185, 28, 28);

    private final StorageRecord record;
    private final StorageManager storageManager;
    private final ClaimManager claimManager;
    private final ReportManager reportManager;
    private final Runnable onChanged;

    public SecurityStorageDetailFrame(StorageRecord record, StorageManager storageManager, Runnable onChanged) {
        super((java.awt.Frame) null, "Detail Storage Record", true);
        this.record = record;
        this.storageManager = storageManager;
        this.claimManager = new ClaimManager();
        this.reportManager = new ReportManager();
        this.onChanged = onChanged;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(680, 540));
        setPreferredSize(new Dimension(820, 680));
        setContentPane(createMainPanel());
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

        root.add(createScrollableBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JScrollPane createScrollableBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        body.add(createHeader(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(22, 0, 0, 0);
        body.add(createDetailCard(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        body.add(createSpacer(), gbc);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(UserDashboardComponents.SURFACE);
        scrollPane.getViewport().setBackground(UserDashboardComponents.SURFACE);
        styleScrollbar(scrollPane.getVerticalScrollBar());
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
        
        return scrollPane;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        header.add(UserDashboardComponents.section(
                "Detail Storage Record",
                "Informasi Penyimpanan Barang Dan Status Pengambilan."
        ), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(2, 22, 0, 0);
        header.add(createStatusBadge(), gbc);
        return header;
    }

    private JPanel createStatusBadge() {
        Color textColor = record.isReleased() ? new Color(20, 125, 78) : new Color(181, 112, 0);
        Color background = record.isReleased() ? new Color(226, 250, 238) : new Color(255, 246, 229);
        UserDashboardComponents.RoundedPanel badge = new UserDashboardComponents.RoundedPanel(background, 18);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(150, 42));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        badge.add(UserDashboardComponents.label(record.isReleased() ? "Sudah Diambil" : "Belum Diambil", 12, Font.BOLD, textColor));
        return badge;
    }

    private JPanel createDetailCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(22, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(createHeroPanel(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 0, 0, 0);
        card.add(createInfoGrid(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.insets = new Insets(18, 0, 0, 0);
        card.add(createDescriptionBlock(), gbc);

        return card;
    }

    private JPanel createHeroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 18);
        panel.add(createPhotoBox(), gbc);

        JPanel summary = new JPanel(new GridBagLayout());
        summary.setOpaque(false);
        GridBagConstraints summaryGbc = new GridBagConstraints();
        summaryGbc.gridx = 0;
        summaryGbc.weightx = 1;
        summaryGbc.fill = GridBagConstraints.HORIZONTAL;
        summaryGbc.anchor = GridBagConstraints.NORTHWEST;

        summaryGbc.gridy = 0;
        summary.add(createInfoBox("Nama Barang", itemName(), true), summaryGbc);
        summaryGbc.gridy = 1;
        summaryGbc.insets = new Insets(10, 0, 0, 0);
        summary.add(createInfoBox("Record ID", safe(record.getRecordId()), false), summaryGbc);
        summaryGbc.gridy = 2;
        summary.add(createInfoBox("Record Status", record.isReleased() ? "Sudah Diambil" : "Belum Diambil", false), summaryGbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(summary, gbc);

        return panel;
    }

    private JPanel createPhotoBox() {
        UserDashboardComponents.RoundedPanel box = new UserDashboardComponents.RoundedPanel(new Color(248, 252, 255), 18);
        box.setLayout(new BorderLayout());
        box.setPreferredSize(new Dimension(270, 222));
        box.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(226, 236, 247), 18, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        box.add(UserDashboardComponents.label("Foto Barang", 12, Font.BOLD, UserDashboardComponents.TEXT_MUTED), BorderLayout.NORTH);
        JPanel photoWrapper = new JPanel(new GridBagLayout());
        photoWrapper.setOpaque(false);
        photoWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        photoWrapper.add(new UserDashboardComponents.PhotoPanel(findRelatedReport(), new Dimension(246, 160)));
        box.add(photoWrapper, BorderLayout.CENTER);
        return box;
    }

    private JPanel createInfoGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        addInfoBox(grid, 0, 0, "Kategori", categoryName());
        addInfoBox(grid, 0, 1, "Status Item", itemStatus());
        addInfoBox(grid, 1, 0, "Lokasi Storage", safe(record.getStorageLocation()));
        addInfoBox(grid, 1, 1, "Disimpan Oleh", record.getStoredBy() == null ? "-" : safe(record.getStoredBy().getName()));
        addInfoBox(grid, 2, 0, "Tanggal Masuk", record.getDateStored() == null ? "-" : record.getDateStored().format(DATE_TIME_FORMAT));
        addInfoBox(grid, 2, 1, "Tanggal Diambil", record.getDateReleased() == null ? "-" : record.getDateReleased().format(DATE_TIME_FORMAT));
        addInfoBox(grid, 3, 0, "Pemilik Klaim", ownerName());
        return grid;
    }

    private void addInfoBox(JPanel grid, int row, int column, String label, String value) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(row == 0 ? 0 : 10, column == 0 ? 0 : 10, 0, column == 0 ? 10 : 0);
        grid.add(createInfoBox(label, value, false), gbc);
    }

    private JPanel createInfoBox(String label, String value, boolean prominent) {
        UserDashboardComponents.RoundedPanel box = new UserDashboardComponents.RoundedPanel(new Color(248, 252, 255), 16);
        box.setLayout(new GridBagLayout());
        box.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(226, 236, 247), 16, 1),
                BorderFactory.createEmptyBorder(prominent ? 16 : 12, 14, prominent ? 16 : 12, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        box.add(UserDashboardComponents.label(label, 11, Font.BOLD, UserDashboardComponents.TEXT_MUTED), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        box.add(UserDashboardComponents.label(value, prominent ? 22 : 15, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);
        return box;
    }

    private JPanel createDescriptionBlock() {
        UserDashboardComponents.RoundedPanel panel = new UserDashboardComponents.RoundedPanel(new Color(248, 252, 255), 16);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(226, 236, 247), 16, 1),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)
        ));

        panel.add(UserDashboardComponents.label("Deskripsi Barang", 12, Font.BOLD, UserDashboardComponents.TEXT_MUTED), BorderLayout.NORTH);

        JTextArea area = UserDashboardComponents.paragraph(itemDescription(), 13);
        area.setRows(6);
        area.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JButton closeButton = createSecondaryButton("Tutup");
        closeButton.addActionListener(event -> dispose());
        footer.add(closeButton);

        JButton deleteButton = createDangerButton("Hapus Record");
        deleteButton.addActionListener(event -> deleteRecord());
        footer.add(deleteButton);

        JButton releaseButton = createPrimaryButton(record.isReleased() ? "Sudah Diambil" : "Release Item");
        releaseButton.setEnabled(!record.isReleased() && record.getItem() != null && record.getItem().getStatus() == ItemStatus.DIKLAIM);
        if (!releaseButton.isEnabled() && !record.isReleased()) {
            releaseButton.setToolTipText("Item belum berstatus DIKLAIM. Menunggu validasi klaim admin.");
        }
        releaseButton.addActionListener(event -> releaseItem());
        footer.add(releaseButton);

        return footer;
    }

    private JPanel createSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private void styleScrollbar(JScrollBar scrollbar) {
        scrollbar.setUnitIncrement(16);
        scrollbar.setPreferredSize(new Dimension(8, 0));
        scrollbar.setOpaque(false);
        scrollbar.setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UserDashboardComponents.PRIMARY);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
                g2.dispose();
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
        });
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text, UserDashboardComponents.PRIMARY_DARK, UserDashboardComponents.PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createBaseButton(text, DANGER, DANGER_HOVER);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text, Color.WHITE, new Color(245, 248, 252));
        button.setForeground(UserDashboardComponents.TEXT_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));
        return button;
    }

    private JButton createBaseButton(String text, Color background, Color hoverBackground) {
        JButton button = new RoundedButton(text, background, hoverBackground);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        return button;
    }

    private void releaseItem() {
        boolean confirmed = AppDialog.confirm(
                this,
                "Release Item",
                "Tandai barang \"" + itemName() + "\" sebagai sudah diambil?",
                "Release",
                "Batal"
        );
        if (!confirmed) {
            return;
        }
        storageManager.updateStorageReleasedDB(record.getRecordId());
        if (onChanged != null) {
            onChanged.run();
        }
        AppDialog.success(this, "Item Direlease", "Storage record berhasil diperbarui.");
        dispose();
    }

    private void deleteRecord() {
        Report related = findRelatedReport();
        boolean isMatched = false;
        if (related instanceof FoundReport) {
             isMatched = ((FoundReport) related).getMatchedLostReport() != null;
        }

        String warningText = "Yakin ingin menghapus record ini?\n\n";
        warningText += 
        "PERINGATAN: Laporan barang temuan (Found Report) yang terkait dengan item ini juga akan ikut dihapus.";
        if (!isMatched) {
             warningText += "\nData fisik item juga akan ikut dihapus dari sistem.";
        } else {
             warningText += "\nData fisik item TIDAK akan dihapus karena terkait dengan laporan kehilangan milik pengguna.";
        }

        boolean confirmed = AppDialog.confirm(
                this,
                "Hapus Storage Record",
                warningText,
                "Hapus",
                "Batal"
        );
        if (!confirmed) {
            return;
        }

        storageManager.delete(record.getRecordId());
        //Untuk menghapus laporan yang terkait
        if (related != null) {
             reportManager.delete(related.getReportId());
             //Untuk menghapus item
             if (!isMatched && related.getItem() != null) {
                  ItemManager itemManager = new ItemManager();
                  itemManager.delete(related.getItem().getItemID());
             }
        }

        if (onChanged != null) {
            onChanged.run();
        }
        AppDialog.success(this, "Record Dihapus", "Storage record beserta laporan terkait berhasil dihapus.");
        dispose();
    }

    private String ownerName() {
        if (record.getItem() == null) {
            return "-";
        }
        for (Object object : claimManager.getClaims()) {
            Claim claim = (Claim) object;
            if (claim.getItem() != null
                    && claim.getItem().getItemID().equals(record.getItem().getItemID())
                    && claim.getStatus() == ClaimStatus.VALID
                    && claim.getUser() != null) {
                return claim.getUser().getName();
            }
        }
        return "-";
    }

    private String itemName() {
        return record.getItem() == null ? "-" : safe(record.getItem().getName());
    }

    private String itemDescription() {
        return record.getItem() == null ? "-" : safe(record.getItem().getDescription());
    }

    private String categoryName() {
        Item item = record.getItem();
        return item == null || item.getCategory() == null ? "-" : safe(item.getCategory().getName());
    }

    private String itemStatus() {
        Item item = record.getItem();
        return item == null || item.getStatus() == null ? "-" : item.getStatus().name();
    }

    private Report findRelatedReport() {
        if (record.getItem() == null) {
            return null;
        }
        String itemId = record.getItem().getItemID();
        for (FoundReport report : reportManager.getFoundReports()) {
            if (report.getItem() != null && itemId.equals(report.getItem().getItemID())) {
                return report;
            }
        }
        return null;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private static class RoundedButton extends JButton {

        private final Color background;
        private final Color hoverBackground;

        RoundedButton(String text, Color background, Color hoverBackground) {
            super(text);
            this.background = background;
            this.hoverBackground = hoverBackground;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isPressed() ? background.darker() : background;
            if (getModel().isRollover() && !getModel().isPressed()) {
                fill = hoverBackground;
            }
            if (!isEnabled()) {
                fill = new Color(203, 213, 225);
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
