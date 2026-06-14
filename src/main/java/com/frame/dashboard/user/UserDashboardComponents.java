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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.AbstractBorder;

import com.model.Claim;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;

public final class UserDashboardComponents {

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

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }

    public static JTextArea paragraph(String text, int size) {
        JTextArea area = new JTextArea(text == null || text.isBlank() ? "-" : text);
        area.setFont(new Font("Segoe UI", Font.PLAIN, size));
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
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
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

    public static String date(Report report) {
        if (report == null || report.getDate() == null) {
            return "-";
        }
        return report.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public static String date(Claim claim) {
        if (claim == null || claim.getDateClaim() == null) {
            return "-";
        }
        return claim.getDateClaim().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public static String category(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) {
            return "Tanpa kategori";
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
        return report != null && report.getItem() != null ? report.getItem().getLocation() : "-";
    }

    public static JButton plainButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    public static class StatCard extends GradientPanel {

        public StatCard(String title, String value, String subtitle, Color start, Color end) {
            super(start, end, 18);
            setPreferredSize(new Dimension(210, 120));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            add(label(title, 15, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(14, 0, 8, 0);
            add(label(value, 34, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(label(subtitle, 13, Font.PLAIN, new Color(236, 248, 255)), gbc);
        }
    }

    public static class ReportCard extends RoundedPanel {

        public ReportCard(Report report, String statusText, Color statusColor) {
            super(Color.WHITE, 20);
            setPreferredSize(new Dimension(320, 320));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            add(new PhotoPanel(report), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label(report.getItem().getName(), 20, Font.BOLD, TEXT_DARK), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(2, 0, 0, 0);
            add(label(report.getReportId() + " • " + category(report) + " • " + date(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(paragraph(report.getDescription(), 13), gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(12, 0, 0, 0);
            add(label("Lokasi: " + location(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            RoundedPanel badge = new RoundedPanel(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 24), 18);
            badge.setLayout(new GridBagLayout());
            badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            badge.add(label(statusText, 12, Font.BOLD, statusColor));

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(badge, gbc);
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
    }

    public static class ClaimCard extends RoundedPanel {

        public ClaimCard(Claim claim) {
            super(Color.WHITE, 22);
            setPreferredSize(new Dimension(370, 190));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            add(label(claim.getItem().getName(), 19, Font.BOLD, TEXT_DARK), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(5, 0, 0, 0);
            add(label(claim.getClaimId() + " • " + date(claim), 13, Font.PLAIN, TEXT_MUTED), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(14, 0, 0, 0);
            add(label("Status klaim: " + claim.getStatus(), 14, Font.BOLD, PRIMARY_DARK), gbc);
            gbc.gridy = 3;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label("Report asal: " + (claim.getRelatedReportId() == null ? "-" : claim.getRelatedReportId()), 13, Font.PLAIN, TEXT_MUTED), gbc);
        }
    }

    public static class PhotoPanel extends JPanel {

        private final Report report;

        PhotoPanel(Report report) {
            this.report = report;
            setPreferredSize(new Dimension(320, 180));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            java.awt.geom.RoundRectangle2D round = new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setClip(round);

            String path = report.getPhotoPath();
            if (path != null && !path.isBlank() && new File(path).exists()) {
                Image image = new ImageIcon(path).getImage();
                int imgW = image.getWidth(this);
                int imgH = image.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    double scale = Math.max((double) getWidth() / imgW, (double) getHeight() / imgH);
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
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String name = report.getItem().getName();
                g2.drawString(name.length() > 24 ? name.substring(0, 24) + "..." : name, 18, getHeight() - 24);
            }

            g2.setClip(null);
            g2.dispose();
        }
    }
}
