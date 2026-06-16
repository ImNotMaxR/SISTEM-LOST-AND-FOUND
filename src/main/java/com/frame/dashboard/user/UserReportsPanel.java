package com.frame.dashboard.user;

import com.enumeration.ReportStatus;
import com.frame.AppDialog;
import com.frame.panel.LostReportPanel;
import com.managers.ReportManager;
import com.model.LostReport;
import com.model.User;
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
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UserReportsPanel extends JPanel {

    private static final String TITLE = "Laporan saya";
    private static final String SUBTITLE = "Daftar Laporan Barang Hilang Yang Dibuat Oleh Akun Ini.";    private static final String EMPTY_MESSAGE = "Kamu Belum Memiliki Laporan.";
    private static final Color DELETE = new Color(220, 38, 38);
    private static final Color DELETE_HOVER = new Color(185, 28, 28);
    private static final Dimension ADD_BUTTON_SIZE = new Dimension(260, 40);
    private static final Dimension DELETE_BUTTON_SIZE = new Dimension(76, 32);

    private final User user;
    private final ReportManager reportManager;
    private JPanel grid;
    private JTextField searchField;
    private String currentFilter = "Semua";

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
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(createFilterPanel(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 0, 0);
        grid = UserDashboardComponents.cardGrid();
        content.add(grid, gbc);

        updateGrid();

        gbc.gridy = 3;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UserDashboardComponents.section(TITLE, SUBTITLE), BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(createAddReportButton());
        header.add(actionPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pillsPanel.setOpaque(false);
        
        String[] filters = {"Semua", "Pending", "Valid", "Ditolak"};
        UserDashboardComponents.FilterPill[] pillButtons = new UserDashboardComponents.FilterPill[filters.length];
        
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            UserDashboardComponents.FilterPill pill = new UserDashboardComponents.FilterPill(filter, filter.equals(currentFilter));
            pillButtons[i] = pill;
            pill.addActionListener(e -> {
                for (UserDashboardComponents.FilterPill p : pillButtons) {
                    p.setActive(false);
                }
                pill.setActive(true);
                currentFilter = filter;
                updateGrid();
            });
            pillsPanel.add(pill);
        }
        
        panel.add(pillsPanel, BorderLayout.WEST);
        
        searchField = new UserDashboardComponents.SearchField("Cari laporan...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateGrid(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateGrid(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateGrid(); }
        });
        
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField);
        panel.add(searchWrapper, BorderLayout.EAST);
        
        return panel;
    }

    private void updateGrid() {
        grid.removeAll();
        
        ArrayList<LostReport> myReports = new ArrayList<>();
        for (LostReport report : reportManager.getLostReports()) {
            if (isMine(user, report)) {
                myReports.add(report);
            }
        }
        
        myReports.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        
        String keyword = searchField != null ? searchField.getText().toLowerCase() : "";
        int count = 0;
        
        for (LostReport report : myReports) {
            boolean matchSearch = keyword.isEmpty() || report.getItem().getName().toLowerCase().contains(keyword);
            boolean matchFilter = false;
            
            if (currentFilter.equals("Semua")) {
                matchFilter = true;
            } else if (currentFilter.equals("Valid") && report.getStatus() == ReportStatus.VALID) {
                matchFilter = true;
            } else if (currentFilter.equals("Pending") && report.getStatus() == ReportStatus.PENDING) {
                matchFilter = true;
            } else if (currentFilter.equals("Ditolak") && report.getStatus() == ReportStatus.DITOLAK) {
                matchFilter = true;
            }
            
            if (matchSearch && matchFilter) {
                grid.add(createReportCard(report));
                count++;
            }
        }
        
        if (count == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        
        grid.revalidate();
        grid.repaint();
    }

    private JButton createAddReportButton() {
        JButton button;
        if (user.getRole() == com.enumeration.Role.SECURITY) {
            button = new RoundedActionButton("+ Buat laporan barang ditemukan", UserDashboardComponents.PRIMARY, 25);
        } else {
            button = new RoundedActionButton("+ Buat laporan barang hilang", UserDashboardComponents.PRIMARY, 20);
        }
        
        button.addActionListener(e -> {
            if (user.getRole() == com.enumeration.Role.SECURITY) {
                com.frame.dashboard.security.SecurityFoundReportPanel dialog = new com.frame.dashboard.security.SecurityFoundReportPanel(user, reportManager, () -> {
                    refreshContent();
                });
                dialog.setVisible(true);
            } else {
                com.frame.panel.LostReportPanel dialog = new com.frame.panel.LostReportPanel(user, reportManager, () -> {
                    refreshContent();
                });
                dialog.setVisible(true);
            }
        });
        // Removed fixed button width so text can dictate length
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(UserDashboardComponents.PRIMARY_DARK);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        return button;
    }

    private UserDashboardComponents.ReportCard createReportCard(LostReport report) {
        String statusText;
        Color statusColor;
        
        if (report.getStatus() == ReportStatus.PENDING) {
            statusText = "Pending";
            statusColor = new Color(245, 158, 11);
        } else if (report.getStatus() == ReportStatus.DITOLAK) {
            statusText = "Ditolak";
            statusColor = new Color(220, 38, 38);
        } else {
            statusText = report.getItem().getStatus().name();
            statusColor = UserDashboardComponents.ORANGE;
        }

        return new UserDashboardComponents.ReportCard(
                report,
                statusText,
                statusColor,
                createDeleteReportButton(report)
        );
    }

    private JButton createDeleteReportButton(LostReport report) {
        JButton button = new RoundedActionButton("Hapus", DELETE_HOVER, 16);
        button.setPreferredSize(DELETE_BUTTON_SIZE);
        button.setFont(new Font("Poppins", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(DELETE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        button.addActionListener(event -> deleteReport(report));
        return button;
    }

    private void deleteReport(LostReport report) {
        if (report == null) {
            return;
        }

        // Laporan yang barangnya sudah ditemukan atau dikembalikan TIDAK BOLEH dihapus oleh user.
        // Supaya log historis ditemukan sama klaim tetep ada karena penting plus tidak perlu nambah method lagi juga buat validasi jadi yang bisa hapus hanya admin nya saja.
        if (report.getItem().getStatus() == com.enumeration.ItemStatus.DITEMUKAN || 
            report.getItem().getStatus() == com.enumeration.ItemStatus.DIKLAIM) {
            AppDialog.error(this, "Tidak Dapat Dihapus", "Laporan tidak dapat dihapus karena barang sudah ditemukan atau dikembalikan.\nData ini disimpan sebagai bukti historis.");
            return;
        }

        boolean confirmed = AppDialog.confirm(
                this,
                "Hapus Laporan",
                "Apakah Anda yakin ingin menghapus laporan \"" + report.getItem().getName() + "\"?\nData barang juga akan ikut terhapus dari sistem.",
                "Hapus",
                "Batal"
        );
        
        if (!confirmed) {
            return;
        }

        String itemId = report.getItem().getItemID();
        
        // 1. Hapus Report terlebih dahulu (karena Report memiliki Foreign Key ke Item)
        reportManager.deleteReport(report.getReportId());
        
        // 2. Hapus Item yang terkait (Mencegah orphaned item di database)
        com.managers.ItemManager itemManager = new com.managers.ItemManager();
        itemManager.deleteItem(itemId);

        if (reportManager.findById(report.getReportId()) == null) {
            updateGrid();
            AppDialog.success(this, "Laporan Dihapus", "Laporan beserta data barang berhasil dihapus dari sistem.");
        } else {
            AppDialog.error(this, "Gagal Menghapus", "Laporan tidak dapat dihapus. Silakan coba lagi.");
        }
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

        private final Color rolloverColor;
        private final int radius;

        RoundedActionButton(String text, Color rolloverColor, int radius) {
            super(text);
            this.rolloverColor = rolloverColor;
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = getModel().isPressed() ? getBackground().darker() : getBackground();
            if (getModel().isRollover() && !getModel().isPressed()) {
                background = rolloverColor;
            }
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
