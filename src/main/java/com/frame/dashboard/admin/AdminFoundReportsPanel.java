package com.frame.dashboard.admin;

import com.enumeration.ItemStatus;
import com.frame.dashboard.shared.DashboardUi;
import com.frame.dashboard.shared.WrapLayout;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Category;
import com.model.FoundReport;
import com.model.Item;
import com.model.LostReport;
import com.model.User;
import com.service.AuthService;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AdminFoundReportsPanel extends JPanel {

    private static final String EMPTY_VALUE = "-";

    private final ReportManager reportManager;
    private final ClaimManager claimManager;
    private JPanel tableContainer;
    private JTextField searchField;
    private String currentClaimFilter = "Semua";

    // -------------------------------------------------------------------------
    // Panel Setup
    // -------------------------------------------------------------------------

    public AdminFoundReportsPanel(ReportManager reportManager, ClaimManager claimManager) {
        this.reportManager = reportManager;
        this.claimManager = claimManager;
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(DashboardUi.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(44, 42, 48, 42));
        buildContent();
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private void buildContent() {
        GridBagConstraints gbc = DashboardUi.contentConstraints();

        gbc.gridy = 0;
        add(createHeader(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(24, 0, 0, 0);
        add(createFilterSearchPanel(), gbc);

        tableContainer = new JPanel(new GridBagLayout());
        tableContainer.setOpaque(false);
        gbc.gridy = 2;
        gbc.insets = new Insets(22, 0, 0, 0);
        add(tableContainer, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        add(createVerticalSpacer(), gbc);

        refreshTables();
    }

    // -------------------------------------------------------------------------
    // Header UI
    // -------------------------------------------------------------------------

    private JPanel createHeader() {
        return DashboardUi.responsiveActionRow(DashboardUi.section(
                "Kelola Barang Ditemukan",
                "Daftar Laporan Barang Ditemukan Yang Tersedia."
        ), null);
    }

    // -------------------------------------------------------------------------
    // Filter and Search UI
    // -------------------------------------------------------------------------

    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
        pillsPanel.setOpaque(false);

        String[] filters = {"Semua", "Pending", "Valid", "Ditolak", "Belum Diklaim", "Sudah Diklaim"};
        DashboardUi.FilterPill[] pillButtons = new DashboardUi.FilterPill[filters.length];
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            DashboardUi.FilterPill pill = new DashboardUi.FilterPill(filter, filter.equals(currentClaimFilter));
            pillButtons[i] = pill;
            pill.addActionListener(event -> {
                for (DashboardUi.FilterPill otherPill : pillButtons) {
                    otherPill.setActive(false);
                }
                pill.setActive(true);
                currentClaimFilter = filter;
                refreshTables();
            });
            pillsPanel.add(pill);
        }

        searchField = new DashboardUi.SearchField("Cari Barang Ditemukan...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent event) { refreshTables(); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { refreshTables(); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { refreshTables(); }
        });

        JPanel searchWrapper = new JPanel(new GridBagLayout());
        searchWrapper.setOpaque(false);
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 1;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchWrapper.add(searchField, searchGbc);

        return DashboardUi.responsiveActionRow(pillsPanel, searchWrapper);
    }

    // -------------------------------------------------------------------------
    // Table Refresh
    // -------------------------------------------------------------------------

    private void refreshTables() {
        if (tableContainer == null) {
            return;
        }

        tableContainer.removeAll();
        GridBagConstraints gbc = DashboardUi.contentConstraints();
        int row = 0;

        if (shouldShow("Pending")) {
            addTableSection(gbc, row++, "Menunggu Validasi", "Pending");
        }
        if (shouldShow("Valid")) {
            addTableSection(gbc, row++, "Laporan Valid", "Valid");
        }
        if (shouldShow("Ditolak")) {
            addTableSection(gbc, row++, "Laporan Ditolak", "Ditolak");
        }
        if (shouldShow("Belum Diklaim")) {
            addTableSection(gbc, row++, "Belum Diklaim", "Belum Diklaim");
        }
        if (shouldShow("Sudah Diklaim")) {
            addTableSection(gbc, row++, "Sudah Diklaim", "Sudah Diklaim");
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private boolean shouldShow(String filter) {
        return "Semua".equals(currentClaimFilter) || filter.equals(currentClaimFilter);
    }

    private void addTableSection(GridBagConstraints gbc, int row, String title, String filterType) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 24, 0, 0, 0);
        tableContainer.add(createFoundReportsTableSection(title, filterType), gbc);
    }

    // -------------------------------------------------------------------------
    // Table UI
    // -------------------------------------------------------------------------

    private JPanel createFoundReportsTableSection(String title, String filterType) {
        ArrayList<FoundReport> reports = getFilteredReports(filterType);
        JTable table = AdminDashboardComponents.table(
                new String[]{"Report Id", "Pelapor", "Barang", "Kategori", "Lokasi Ditemukan", "Tanggal", "Status Laporan", "Status Klaim", "Diklaim Oleh"},
                createFoundReportRows(reports)
        );
        table.putClientProperty("reports", reports);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                updateTableCursor(table, event);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                table.putClientProperty("hoverRow", -1);
                table.setCursor(Cursor.getDefaultCursor());
                table.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() < 2 || table.getSelectedRow() < 0) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                @SuppressWarnings("unchecked")
                ArrayList<FoundReport> tableReports = (ArrayList<FoundReport>) table.getClientProperty("reports");
                if (modelRow >= 0 && modelRow < tableReports.size()) {
                    FoundReport report = tableReports.get(modelRow);
                    table.clearSelection();
                    openDetail(report);
                    table.clearSelection();
                }
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                updateTableCursor(table, event);
            }
        });
        return AdminDashboardComponents.tableSection(title, table);
    }

    private void updateTableCursor(JTable table, MouseEvent event) {
        int row = table.rowAtPoint(event.getPoint());
        table.putClientProperty("hoverRow", row);
        table.setCursor(row >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        table.repaint();
    }

    // -------------------------------------------------------------------------
    // Data Filtering
    // -------------------------------------------------------------------------

    private ArrayList<FoundReport> getFilteredReports(String filterType) {
        ArrayList<FoundReport> result = new ArrayList<>();
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        for (FoundReport report : reportManager.getFoundReports()) {
            if ("Pending".equals(filterType) && report.getStatus() != com.enumeration.ReportStatus.PENDING) {
                continue;
            }
            if ("Valid".equals(filterType) && report.getStatus() != com.enumeration.ReportStatus.VALID) {
                continue;
            }
            if ("Ditolak".equals(filterType) && report.getStatus() != com.enumeration.ReportStatus.DITOLAK) {
                continue;
            }
            if ("Belum Diklaim".equals(filterType) && (report.getStatus() != com.enumeration.ReportStatus.VALID || (report.getItem() != null && report.getItem().getStatus() == ItemStatus.DIKLAIM))) {
                continue;
            }
            if ("Sudah Diklaim".equals(filterType) && (report.getStatus() != com.enumeration.ReportStatus.VALID || report.getItem() == null || report.getItem().getStatus() != ItemStatus.DIKLAIM)) {
                continue;
            }
            if (!matchesSearch(report, keyword)) {
                continue;
            }
            result.add(report);
        }
        result.sort((first, second) -> second.getDate().compareTo(first.getDate()));
        return result;
    }

    private boolean matchesSearch(FoundReport report, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return contains(report.getReportId(), keyword)
                || contains(report.getFoundLocation(), keyword)
                || (report.getUser() != null && contains(report.getUser().getName(), keyword))
                || (report.getItem() != null && contains(report.getItem().getName(), keyword))
                || (report.getItem() != null && report.getItem().getCategory() != null && contains(report.getItem().getCategory().getName(), keyword))
                || (report.getMatchedLostReport() != null && contains(report.getMatchedLostReport().getReportId(), keyword));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    // -------------------------------------------------------------------------
    // Table Data
    // -------------------------------------------------------------------------

    private Object[][] createFoundReportRows(ArrayList<FoundReport> reports) {
        if (reports.isEmpty()) {
            return new Object[][]{{"Belum Ada Laporan", EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
        }

        Object[][] rows = new Object[reports.size()][9];
        for (int i = 0; i < reports.size(); i++) {
            FoundReport report = reports.get(i);
            Item item = report.getItem();

            rows[i][0] = safe(report.getReportId());
            rows[i][1] = userName(report.getUser());
            rows[i][2] = itemName(item);
            rows[i][3] = categoryName(item);
            rows[i][4] = titleCase(report.getFoundLocation());
            rows[i][5] = DashboardUi.date(report);
            rows[i][6] = titleCase(report.getStatus().name());
            rows[i][7] = report.getItem() != null && report.getItem().getStatus() == ItemStatus.DIKLAIM ? "Sudah Diklaim" : "Belum Diklaim";
            rows[i][8] = getClaimedBy(report);
        }
        return rows;
    }

    private String getClaimedBy(FoundReport report) {
        if (claimManager != null && report.getItem() != null) {
            for (com.model.Claim claim : claimManager.getClaims()) {
                if (claim.getItem().getItemID().equals(report.getItem().getItemID()) && claim.getStatus() == com.enumeration.ClaimStatus.VALID) {
                    return claim.getUser().getName() + " (" + claim.getClaimId() + ")";
                }
            }
        }
        return "-";
    }

    // -------------------------------------------------------------------------
    // Button UI
    // -------------------------------------------------------------------------



    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void openDetail(FoundReport report) {
        new AdminFoundReportDetailFrame(report, reportManager, claimManager, this::reloadTables).setVisible(true);
    }

    private void reloadTables() {
        reportManager.reload();
        refreshTables();
    }

    private JPanel createVerticalSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Text Helpers
    // -------------------------------------------------------------------------

    private String userName(User user) {
        return user == null ? EMPTY_VALUE : titleCase(user.getName());
    }

    private String itemName(Item item) {
        return item == null ? EMPTY_VALUE : titleCase(item.getName());
    }

    private String categoryName(Item item) {
        if (item == null) {
            return EMPTY_VALUE;
        }
        Category category = item.getCategory();
        return category == null ? EMPTY_VALUE : titleCase(category.getName());
    }

    private String matchedReport(LostReport report) {
        return report == null ? "Belum Cocok" : safe(report.getReportId());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? EMPTY_VALUE : value.trim();
    }

    private String titleCase(String value) {
        String safeValue = safe(value);
        if (EMPTY_VALUE.equals(safeValue)) {
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
