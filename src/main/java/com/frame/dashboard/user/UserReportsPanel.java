package com.frame.dashboard.user;

import com.enumeration.ReportStatus;
import com.frame.AppDialog;
import com.managers.ReportManager;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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

    private static final String TITLE = "Laporan Saya";
    private static final String SUBTITLE = "Daftar Laporan Barang Hilang Yang Dibuat Oleh Akun Ini.";
    private static final String EMPTY_MESSAGE = "Kamu Belum Memiliki Laporan.";
    private static final Color DELETE = new Color(220, 38, 38);
    private static final Color DELETE_HOVER = new Color(185, 28, 28);
    private static final Color EDIT = UserDashboardComponents.PRIMARY_DARK;
    private static final Color EDIT_HOVER = UserDashboardComponents.PRIMARY;

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
        JPanel content = createContentPanel();
        UserDashboardComponents.clearTextFocusOnBackgroundClick(content);
        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(createHeader(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(createFilterSearchPanel(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 0, 0);
        grid = UserDashboardComponents.cardGrid();
        content.add(grid, gbc);

        refreshReportGrid();

        gbc.gridy = 3;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createHeader() {
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        GridBagConstraints actionGbc = new GridBagConstraints();
        actionGbc.gridx = 0;
        actionGbc.gridy = 0;
        actionGbc.weightx = 1;
        actionGbc.fill = GridBagConstraints.HORIZONTAL;
        actionPanel.add(createAddReportButton(), actionGbc);

        return UserDashboardComponents.responsiveActionRow(
                UserDashboardComponents.section(TITLE, SUBTITLE),
                actionPanel
        );
    }
    
    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
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
                refreshReportGrid();
            });
            pillsPanel.add(pill);
        }

        searchField = new UserDashboardComponents.SearchField("Cari Laporan...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshReportGrid(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshReportGrid(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshReportGrid(); }
        });
        
        JPanel searchWrapper = new JPanel(new GridBagLayout());
        searchWrapper.setOpaque(false);
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 1;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchWrapper.add(searchField, searchGbc);

        return UserDashboardComponents.responsiveActionRow(pillsPanel, searchWrapper);
    }

    private void refreshReportGrid() {
        grid.removeAll();

        if (user.getRole() == com.enumeration.Role.SECURITY) {
            refreshSecurityFoundReportGrid();
            return;
        }
        
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
                grid.add(createUserReportCard(report));
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
            button = new RoundedActionButton("+ Buat Laporan Barang Ditemukan", "+ Buat Laporan", UserDashboardComponents.PRIMARY, 25);
        } else {
            button = new RoundedActionButton("+ Buat Laporan Barang Hilang", "+ Buat Laporan", UserDashboardComponents.PRIMARY, 20);
        }
        
        button.addActionListener(e -> {
            if (user.getRole() == com.enumeration.Role.SECURITY) {
                com.frame.dashboard.security.SecurityFoundReportPanel dialog = new com.frame.dashboard.security.SecurityFoundReportPanel(user, reportManager, () -> {
                    reportManager.reload();
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

    private UserDashboardComponents.ReportCard createUserReportCard(LostReport report) {
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
                createReportActionButtons(report)
        );
    }

    private JPanel createReportActionButtons(LostReport report) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (canEditReport(report)) {
            actions.add(createEditReportButton(report));
        }

        actions.add(createDeleteReportButton(report));
        return actions;
    }

    private JButton createEditReportButton(LostReport report) {
        JButton button = UserDashboardComponents.iconButton(
                UserDashboardComponents.ActionIconType.EDIT,
                "Edit laporan",
                Color.WHITE,
                EDIT,
                EDIT_HOVER
        );
        button.addActionListener(event -> openEditReportDialog(report));
        return button;
    }

    private void refreshSecurityFoundReportGrid() {
        ArrayList<FoundReport> myReports = new ArrayList<>();
        for (FoundReport report : reportManager.getFoundReports()) {
            if (isMine(user, report)) {
                myReports.add(report);
            }
        }

        myReports.sort((first, second) -> second.getDate().compareTo(first.getDate()));

        String keyword = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        int count = 0;
        for (FoundReport report : myReports) {
            boolean matchSearch = keyword.isEmpty()
                    || contains(report.getReportId(), keyword)
                    || (report.getItem() != null && contains(report.getItem().getName(), keyword))
                    || contains(report.getFoundLocation(), keyword);
            boolean matchFilter = currentFilter.equals("Semua")
                    || (currentFilter.equals("Valid") && report.getStatus() == ReportStatus.VALID)
                    || (currentFilter.equals("Pending") && report.getStatus() == ReportStatus.PENDING)
                    || (currentFilter.equals("Ditolak") && report.getStatus() == ReportStatus.DITOLAK);

            if (matchSearch && matchFilter) {
                grid.add(createSecurityFoundReportCard(report));
                count++;
            }
        }

        if (count == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }

        grid.revalidate();
        grid.repaint();
    }

    private UserDashboardComponents.ReportCard createSecurityFoundReportCard(FoundReport report) {
        String statusText;
        Color statusColor;

        if (report.getStatus() == ReportStatus.PENDING) {
            statusText = "Pending";
            statusColor = new Color(245, 158, 11);
        } else if (report.getStatus() == ReportStatus.DITOLAK) {
            statusText = "Ditolak";
            statusColor = new Color(220, 38, 38);
        } else if (report.getItem() != null && report.getItem().getStatus() == com.enumeration.ItemStatus.DIKLAIM) {
            statusText = "Sudah Diklaim";
            statusColor = new Color(22, 163, 74);
        } else {
            statusText = "Belum Diklaim";
            statusColor = UserDashboardComponents.PRIMARY_DARK;
        }

        return new UserDashboardComponents.ReportCard(report, statusText, statusColor);
    }

    private JButton createDeleteReportButton(LostReport report) {
        JButton button = UserDashboardComponents.iconButton(
                UserDashboardComponents.ActionIconType.DELETE,
                "Hapus laporan",
                Color.WHITE,
                DELETE,
                DELETE_HOVER
        );
        button.addActionListener(event -> deleteReport(report));
        return button;
    }

    private boolean canEditReport(LostReport report) {
        return report != null
                && report.getStatus() == ReportStatus.PENDING
                && report.isEditable();
    }

    private void openEditReportDialog(LostReport report) {
        if (!canEditReport(report)) {
            AppDialog.warning(this, "Tidak Dapat Diedit", "Laporan hanya dapat diedit saat status pending dan masih dalam batas waktu edit.");
            return;
        }

        com.frame.panel.EditLostReportPanel dialog = new com.frame.panel.EditLostReportPanel(report, reportManager, this::refreshContent);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteReport(LostReport report) {
        if (report == null) {
            return;
        }

        // Laporan yang barangnya sudah ditemukan atau dikembalikan TIDAK BOLEH dihapus oleh user.
        // Supaya log historis ditemukan sama klaim tetep ada karena penting plus tidak perlu nambah method lagi juga buat validasi jadi yang bisa hapus hanya admin nya saja.
        if (report.getItem().getStatus() == com.enumeration.ItemStatus.DITEMUKAN || 
            report.getItem().getStatus() == com.enumeration.ItemStatus.DIKLAIM) {
            AppDialog.error(this, "Tidak Dapat Dihapus", "Laporan Tidak Dapat Dihapus Karena Barang Sudah Ditemukan Atau Dikembalikan.\nData Ini Disimpan Sebagai Bukti Historis.");
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
            refreshReportGrid();
            AppDialog.success(this, "Laporan Dihapus", "Laporan beserta data barang berhasil dihapus dari sistem.");
        } else {
            AppDialog.error(this, "Gagal Menghapus", "Laporan tidak dapat dihapus. Silakan coba lagi.");
        }
    }

    private boolean isMine(User user, LostReport report) {
        return user != null
                && report.getUser() != null
                && user.getUserId().equals(report.getUser().getUserId());
    }

    private boolean isMine(User user, Report report) {
        return user != null
                && report != null
                && report.getUser() != null
                && user.getUserId().equals(report.getUser().getUserId());
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private static class RoundedActionButton extends JButton {

        private final Color rolloverColor;
        private final int radius;
        private final String fullText;
        private final String compactText;

        RoundedActionButton(String fullText, String compactText, Color rolloverColor, int radius) {
            super(fullText);
            this.fullText = fullText;
            this.compactText = compactText;
            this.rolloverColor = rolloverColor;
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            updateDisplayedText();
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

        private void updateDisplayedText() {
            int fullWidth = getFontMetrics(getFont()).stringWidth(fullText) + getInsets().left + getInsets().right + 8;
            String nextText = getWidth() > 0 && getWidth() < fullWidth ? compactText : fullText;
            if (!nextText.equals(getText())) {
                setText(nextText);
            }
        }
    }
}
