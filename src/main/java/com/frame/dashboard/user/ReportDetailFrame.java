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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.model.Report;

public class ReportDetailFrame extends JFrame {

    private static final int CONTENT_PADDING_X = 34;
    private static final Dimension FRAME_SIZE = new Dimension(920, 650);
    private static final Dimension PHOTO_SIZE = new Dimension(852, 240);

    public ReportDetailFrame(Report report) {
        configureFrame(report);
        setContentPane(createMainPanel(report));
        pack();
        setLocationRelativeTo(null);
    }

    private void configureFrame(Report report) {
        setTitle("Detail Laporan - " + (report != null ? report.getReportId() : "Detail"));
        setMinimumSize(FRAME_SIZE);
        setPreferredSize(FRAME_SIZE);
        setResizable(false);
    }

    private JPanel createMainPanel(Report report) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);

        JPanel content = createContentWrapper();
        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createScrollContent(report), BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);

        return root;
    }

    private JPanel createContentWrapper() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(16, CONTENT_PADDING_X, 18, CONTENT_PADDING_X));
        return content;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JButton backButton = new BackButton("Kembali");
        backButton.addActionListener(event -> dispose());
        header.add(backButton, BorderLayout.WEST);

        return header;
    }

    private JScrollPane createScrollContent(Report report) {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(createPhotoSection(report), BorderLayout.NORTH);
        center.add(createDetailsPanel(report), BorderLayout.CENTER);

        JScrollPane scrollPane = UserDashboardComponents.scroll(center);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        return scrollPane;
    }

    private JPanel createPhotoSection(Report report) {
        UserDashboardComponents.PhotoPanel imagePanel = new UserDashboardComponents.PhotoPanel(report, true, PHOTO_SIZE);

        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setOpaque(false);
        imageWrapper.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        imageWrapper.add(imagePanel, BorderLayout.CENTER);
        return imageWrapper;
    }

    private JPanel createDetailsPanel(Report report) {
        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        details.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        GridBagConstraints gbc = createDetailsConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        details.add(UserDashboardComponents.label("Kategori: " + getCategoryName(report), 14, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        details.add(UserDashboardComponents.paragraph("Deskripsi: " + getDescription(report), 14), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        details.add(UserDashboardComponents.label("Lokasi: " + getLocation(report), 14, Font.PLAIN, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 0, 0);
        details.add(UserDashboardComponents.label("Status: " + getItemStatus(report), 14, Font.BOLD, UserDashboardComponents.PRIMARY_DARK), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        details.add(UserDashboardComponents.label("Tanggal: " + getReportDate(report), 13, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        details.add(spacer, gbc);

        return details;
    }

    private GridBagConstraints createDetailsConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
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

    private static class BackButton extends JButton {

        BackButton(String text) {
            super(text);
            setFont(new Font("Poppins", Font.BOLD, 14));
            setForeground(UserDashboardComponents.PRIMARY_DARK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = getModel().isPressed()
                    ? new Color(207, 226, 249)
                    : getModel().isRollover() ? new Color(226, 240, 253) : Color.WHITE;
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.setColor(UserDashboardComponents.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
