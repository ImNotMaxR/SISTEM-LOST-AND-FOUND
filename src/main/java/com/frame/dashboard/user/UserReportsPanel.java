package com.frame.dashboard.user;

import com.frame.panel.LostReportPanel;
import com.managers.ReportManager;
import com.model.LostReport;
import com.model.User;
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
import javax.swing.JPanel;

public class UserReportsPanel extends JPanel {

    private static final String TITLE = "Laporan Saya";
    private static final String SUBTITLE = "Daftar Laporan Barang Hilang Yang Dibuat Oleh Akun Ini.";
    private static final String EMPTY_MESSAGE = "Kamu Belum Memiliki Laporan.";

    private final User user;
    private final ReportManager reportManager;

    public UserReportsPanel(User user, ReportManager reportManager) {
        this.user = user;
        this.reportManager = reportManager;
        configurePanel();
        refreshContent();
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private void refreshContent() {
        removeAll();
        add(UserDashboardComponents.scroll(createContent()), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(createHeader(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(createReportsGrid(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridx = 0;
        header.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0, 18, 0, 0);
        header.add(createAddReportButton(), gbc);

        return header;
    }

    private JButton createAddReportButton() {
        JButton button = new RoundedActionButton("+ Tambah Laporan");
        button.setPreferredSize(new Dimension(168, 40));
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(UserDashboardComponents.PRIMARY_DARK);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.addActionListener(event -> openLostReportPanel());
        return button;
    }

    private JPanel createReportsGrid() {
        JPanel grid = UserDashboardComponents.cardGrid();
        for (LostReport report : reportManager.getLostReports()) {
            if (isMine(user, report)) {
                grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.PRIMARY_DARK));
            }
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        return grid;
    }

    private void openLostReportPanel() {
        LostReportPanel panel = new LostReportPanel(user, reportManager, this::refreshContent);
        panel.setLocationRelativeTo(this);
        panel.setVisible(true);
    }

    private boolean isMine(User user, LostReport report) {
        return user != null
                && report.getUser() != null
                && user.getUserId().equals(report.getUser().getUserId());
    }

    private static class RoundedActionButton extends JButton {

        RoundedActionButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = getModel().isPressed() ? getBackground().darker() : getBackground();
            if (getModel().isRollover() && !getModel().isPressed()) {
                background = UserDashboardComponents.PRIMARY;
            }
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
