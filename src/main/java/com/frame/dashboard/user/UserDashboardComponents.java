package com.frame.dashboard.user;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.AbstractBorder;

import com.model.Claim;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;

public final class UserDashboardComponents {

    private static final String FONT_FAMILY = "Poppins";
    private static final String EMPTY_TEXT = "-";
    private static final String TEXT_SEPARATOR = " - ";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Dimension REPORT_CARD_SIZE = new Dimension(320, 360);
    private static final Dimension CLAIM_CARD_SIZE = new Dimension(370, 190);
    private static final Dimension PHOTO_SIZE = new Dimension(320, 220);
    private static final Dimension CARD_PHOTO_SIZE = new Dimension(320, 160);
    private static final Dimension PHOTO_MINIMUM_SIZE = new Dimension(280, 160);
    private static final int CARD_ACTION_HEIGHT = 32;
    private static final int STATUS_BADGE_MIN_WIDTH = 78;

    public static final Color PRIMARY_DARK = new Color(44, 94, 173);
    public static final Color PRIMARY = new Color(21, 145, 220);
    public static final Color PRIMARY_LIGHT = new Color(75, 184, 250);
    public static final Color SURFACE = new Color(247, 251, 254);
    public static final Color BORDER = new Color(214, 226, 239);
    public static final Color TEXT_DARK = new Color(8, 20, 38);
    public static final Color TEXT_MUTED = new Color(83, 108, 135);
    public static final Color ORANGE = new Color(255, 128, 77);

    private UserDashboardComponents() {
    }

    // =========================
    // Basic Factories
    // =========================

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, style, size));
        label.setForeground(color);
        return label;
    }

    public static JTextArea paragraph(String text, int size) {
        JTextArea area = new JTextArea(text == null || text.isBlank() ? EMPTY_TEXT : text);
        area.setFont(new Font(FONT_FAMILY, Font.PLAIN, size));
        area.setForeground(TEXT_MUTED);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static JScrollPane scroll(Component content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        applyScrollBarStyle(scrollPane);
        return scrollPane;
    }

    private static void applyScrollBarStyle(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = PRIMARY;
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
            protected void paintThumb(java.awt.Graphics g, javax.swing.JComponent c, java.awt.Rectangle thumbBounds) {
                if (!c.isEnabled() || thumbBounds.isEmpty()) {
                    return;
                }
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintTrack(java.awt.Graphics g, javax.swing.JComponent c, java.awt.Rectangle trackBounds) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
    }

    public static JPanel section(String title, String subtitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label(title, 25, Font.BOLD, TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(label(subtitle, 14, Font.PLAIN, TEXT_MUTED), gbc);

        return panel;
    }

    public static JPanel cardGrid() {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 18, 18));
        panel.setOpaque(false);
        return panel;
    }

    public static GridBagConstraints contentConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    public static JPanel emptyState(String text) {
        RoundedPanel panel = new RoundedPanel(Color.WHITE, 20);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 20, 1),
                BorderFactory.createEmptyBorder(36, 28, 36, 28)
        ));
        panel.add(label(text, 15, Font.PLAIN, TEXT_MUTED));
        return panel;
    }

    // =========================
    // Text Helpers
    // =========================

    public static String date(Report report) {
        if (report == null || report.getDate() == null) {
            return EMPTY_TEXT;
        }
        return report.getDate().format(DISPLAY_DATE_FORMAT);
    }

    public static String date(Claim claim) {
        if (claim == null || claim.getDateClaim() == null) {
            return EMPTY_TEXT;
        }
        return claim.getDateClaim().format(DISPLAY_DATE_FORMAT);
    }

    public static String category(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) {
            return "Tanpa Kategori";
        }
        return report.getItem().getCategory().getName();
    }

    public static String location(Report report) {
        if (report instanceof LostReport) {
            return ((LostReport) report).getLostLocation();
        }
        if (report instanceof FoundReport) {
            return ((FoundReport) report).getFoundLocation();
        }
        return report != null && report.getItem() != null ? report.getItem().getLocation() : EMPTY_TEXT;
    }

    // =========================
    // Base Components
    // =========================

    public static JButton plainButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setForeground(TEXT_MUTED);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return button;
    }

    public static class RoundedPanel extends JPanel {

        private final Color color;
        private final int radius;

        public RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class GradientPanel extends JPanel {

        private final Color start;
        private final Color end;
        private final int radius;

        public GradientPanel(Color start, Color end, int radius) {
            this.start = start;
            this.end = end;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new java.awt.GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(255, 255, 255, 36));
            g2.fillOval(getWidth() - 64, -28, 96, 96);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedLineBorder extends AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }

    // =========================
    // Dashboard Cards
    // =========================

    public static class StatCard extends GradientPanel {

        public StatCard(String title, String value, String subtitle, Color start, Color end) {
            super(start, end, 18);
            setPreferredSize(new Dimension(230, 128));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(label(title, 15, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(12, 0, 8, 0);
            add(label(value, 34, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(label(subtitle, 13, Font.PLAIN, new Color(236, 248, 255)), gbc);
        }
    }

    public static class ReportCard extends RoundedPanel {

        public ReportCard(Report report, String statusText, Color statusColor) {
            this(report, statusText, statusColor, null);
        }

        public ReportCard(Report report, String statusText, Color statusColor, JButton actionButton) {
            super(Color.WHITE, 20);
            setPreferredSize(REPORT_CARD_SIZE);
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(new PhotoPanel(report, CARD_PHOTO_SIZE), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label(report.getItem().getName(), 20, Font.BOLD, TEXT_DARK), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(2, 0, 0, 0);
            add(label(report.getReportId() + TEXT_SEPARATOR + category(report) + TEXT_SEPARATOR + date(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(paragraph(report.getDescription(), 13), gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(12, 0, 0, 0);
            add(label("Lokasi: " + location(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(createCardActions(statusText, statusColor, actionButton), gbc);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        new ReportDetailFrame(report).setVisible(true);
                    } catch (Throwable ex) {
                        // fallback: ignore
                    }
                }
            });
        }

        private String location(Report report) {
            return UserDashboardComponents.location(report);
        }

        private JPanel createCardActions(String statusText, Color statusColor, JButton actionButton) {
            JPanel actions = new JPanel(new GridBagLayout());
            actions.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            actions.add(createStatusBadge(statusText, statusColor), gbc);

            if (actionButton != null) {
                gbc.gridx = 1;
                gbc.weightx = 0;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.insets = new Insets(0, 8, 0, 0);
                actions.add(actionButton);
            }
            return actions;
        }
    }

    public static class ClaimCard extends RoundedPanel {

        public ClaimCard(Claim claim) {
            super(Color.WHITE, 22);
            setPreferredSize(CLAIM_CARD_SIZE);
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(label(claim.getItem().getName(), 19, Font.BOLD, TEXT_DARK), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(5, 0, 0, 0);
            add(label(claim.getClaimId() + TEXT_SEPARATOR + date(claim), 13, Font.PLAIN, TEXT_MUTED), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(14, 0, 0, 0);
            add(label("Status klaim: " + claim.getStatus(), 14, Font.BOLD, PRIMARY_DARK), gbc);
            gbc.gridy = 3;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label("Report asal: " + (claim.getRelatedReportId() == null ? "-" : claim.getRelatedReportId()), 13, Font.PLAIN, TEXT_MUTED), gbc);
        }
    }

    private static GridBagConstraints createHorizontalConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private static RoundedPanel createStatusBadge(String statusText, Color statusColor) {
        RoundedPanel badge = new RoundedPanel(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 24), 18);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(calculateStatusBadgeWidth(statusText), CARD_ACTION_HEIGHT));
        badge.setMinimumSize(badge.getPreferredSize());
        badge.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        badge.add(label(statusText, 12, Font.BOLD, statusColor));
        return badge;
    }

    private static int calculateStatusBadgeWidth(String statusText) {
        String safeText = statusText == null ? EMPTY_TEXT : statusText;
        return Math.max(STATUS_BADGE_MIN_WIDTH, safeText.length() * 10 + 24);
    }

    // =========================
    // Image Components
    // =========================

    public static class PhotoPanel extends JPanel {

        private final Report report;
        private final boolean showTitleOverlay;

        PhotoPanel(Report report) {
            this(report, false, PHOTO_SIZE);
        }

        public PhotoPanel(Report report, Dimension size) {
            this(report, false, size);
        }

        public PhotoPanel(Report report, boolean showTitleOverlay, Dimension size) {
            this.report = report;
            this.showTitleOverlay = showTitleOverlay;
            setPreferredSize(size);
            setMinimumSize(new Dimension(Math.min(size.width, PHOTO_MINIMUM_SIZE.width), Math.min(size.height, PHOTO_MINIMUM_SIZE.height)));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            java.awt.geom.RoundRectangle2D round = new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setClip(round);

            String path = report != null ? report.getPhotoPath() : null;
            if (path != null && !path.isBlank() && new File(path).exists()) {
                Image image = new ImageIcon(path).getImage();
                int imgW = image.getWidth(this);
                int imgH = image.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    g2.setColor(new Color(239, 246, 252));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                    double scale = Math.min(1.0, Math.min((double) getWidth() / imgW, (double) getHeight() / imgH));
                    int drawW = (int) Math.round(imgW * scale);
                    int drawH = (int) Math.round(imgH * scale);
                    int x = (getWidth() - drawW) / 2;
                    int y = (getHeight() - drawH) / 2;
                    g2.drawImage(image, x, y, drawW, drawH, this);
                } else {
                    g2.setPaint(new java.awt.GradientPaint(0, 0, PRIMARY_LIGHT, getWidth(), getHeight(), PRIMARY_DARK));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }
            } else {
                g2.setPaint(new java.awt.GradientPaint(0, 0, PRIMARY_LIGHT, getWidth(), getHeight(), PRIMARY_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 44));
                g2.fillOval(getWidth() - 95, -35, 130, 130);
                if (showTitleOverlay && report != null && report.getItem() != null) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Poppins", Font.BOLD, 16));
                    String name = report.getItem().getName();
                    g2.drawString(name.length() > 34 ? name.substring(0, 34) + "..." : name, 18, getHeight() - 24);
                }
            }

            g2.setClip(null);
            g2.dispose();
        }
    }
}
