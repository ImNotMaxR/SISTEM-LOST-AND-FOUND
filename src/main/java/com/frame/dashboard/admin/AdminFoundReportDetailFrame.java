package com.frame.dashboard.admin;

import com.enumeration.ItemStatus;
import com.frame.dashboard.user.ReportDetailFrame;
import com.frame.dashboard.user.UserDashboardComponents;
import com.managers.ReportManager;
import com.model.FoundReport;
import com.model.Item;
import com.model.LostReport;
import java.awt.BorderLayout;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AdminFoundReportDetailFrame extends JDialog {

    private static final Dimension DEFAULT_FRAME_SIZE = new Dimension(700, 640);
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(600, 520);
    private static final Color DETAIL_COLOR = new Color(44, 94, 173);

    private final FoundReport report;
    private final Runnable onUpdated;

    public AdminFoundReportDetailFrame(FoundReport report, ReportManager reportManager, Runnable onUpdated) {
        super((java.awt.Frame) null, "Detail Barang Ditemukan", true);
        this.report = report;
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

    private Dimension responsiveFrameSize() {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int width = Math.min(DEFAULT_FRAME_SIZE.width, Math.max(MINIMUM_FRAME_SIZE.width, bounds.width - 140));
        int height = Math.min(DEFAULT_FRAME_SIZE.height, Math.max(MINIMUM_FRAME_SIZE.height, bounds.height - 90));
        return new Dimension(width, height);
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        root.add(UserDashboardComponents.section("Detail Barang Ditemukan", "Informasi Barang Temuan Dan Status Klaim."), BorderLayout.NORTH);

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
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
                g2.dispose();
            }
        });
    }

    private JPanel createDetailCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 20, 1),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        card.add(UserDashboardComponents.label("Informasi Barang", 18, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        card.add(createInfoGrid(), gbc);

        return card;
    }

    private JPanel createInfoGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        Item item = report.getItem();
        addInfoCell(grid, gbc, 0, 0, "Report ID", safe(report.getReportId()));
        addInfoCell(grid, gbc, 1, 0, "Pelapor", report.getUser() == null ? "-" : titleCase(report.getUser().getName()));
        addInfoCell(grid, gbc, 0, 1, "Barang", item == null ? "-" : titleCase(item.getName()));
        addInfoCell(grid, gbc, 1, 1, "Kategori", item == null || item.getCategory() == null ? "-" : titleCase(item.getCategory().getName()));
        addInfoCell(grid, gbc, 0, 2, "Lokasi Ditemukan", titleCase(report.getFoundLocation()));
        addInfoCell(grid, gbc, 1, 2, "Tanggal", UserDashboardComponents.date(report));
        addInfoCell(grid, gbc, 0, 3, "Status Klaim", claimStatus());
        addInfoCell(grid, gbc, 1, 3, "Match Laporan Hilang", matchedReport(report.getMatchedLostReport()));
        return grid;
    }

    private void addInfoCell(JPanel grid, GridBagConstraints gbc, int x, int y, String label, String value) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(y == 0 ? 0 : 10, x == 0 ? 0 : 10, 0, x == 0 ? 10 : 0);
        grid.add(createInfoCell(label, value), gbc);
    }

    private JPanel createInfoCell(String label, String value) {
        UserDashboardComponents.RoundedPanel cell = new UserDashboardComponents.RoundedPanel(new Color(248, 250, 252), 14);
        cell.setLayout(new GridBagLayout());
        cell.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(226, 232, 240), 14, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        cell.add(UserDashboardComponents.label(label, 11, Font.BOLD, UserDashboardComponents.TEXT_MUTED), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        cell.add(createValueText(value), gbc);
        return cell;
    }

    private JTextArea createValueText(String value) {
        JTextArea area = new JTextArea(safe(value));
        area.setFont(new Font("Poppins", Font.BOLD, 13));
        area.setForeground(UserDashboardComponents.TEXT_DARK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    private JPanel createActions() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        JButton detailButton = createButton("Lihat Detail Barang", DETAIL_COLOR);
        detailButton.addActionListener(event -> new ReportDetailFrame(report).setVisible(true));
        panel.add(detailButton, gbc);

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

    private void notifyUpdated() {
        if (onUpdated != null) {
            onUpdated.run();
        }
        dispose();
    }

    private String matchedReport(LostReport lostReport) {
        if (lostReport == null) {
            return "Belum Cocok";
        }
        String itemName = lostReport.getItem() == null ? "" : " - " + titleCase(lostReport.getItem().getName());
        return safe(lostReport.getReportId()) + itemName;
    }

    private String claimStatus() {
        return report.getItem() != null && report.getItem().getStatus() == ItemStatus.DIKLAIM
                ? "Sudah Diklaim"
                : "Belum Diklaim";
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
