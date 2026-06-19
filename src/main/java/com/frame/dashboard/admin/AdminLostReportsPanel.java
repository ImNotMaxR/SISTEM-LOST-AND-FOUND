package com.frame.dashboard.admin;

import com.enumeration.ReportStatus;
import com.frame.dashboard.user.UserDashboardComponents;
import com.frame.dashboard.user.WrapLayout;
import com.managers.ReportManager;
import com.model.Category;
import com.model.Item;
import com.model.LostReport;
import com.model.User;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AdminLostReportsPanel extends JPanel {

    private static final String EMPTY_VALUE = "-";

    private final ReportManager reportManager;
    private JPanel tableContainer;
    private JTextField searchField;
    private String currentFilter = "Semua";

    public AdminLostReportsPanel(ReportManager reportManager) {
        this.reportManager = reportManager;
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(44, 42, 48, 42));
        buildContent();
    }

    private void buildContent() {
        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();

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

    private JPanel createHeader() {
        return UserDashboardComponents.section(
                "Kelola Laporan Barang Hilang",
                "Daftar Laporan Barang Hilang Yang Dibuat Oleh Pengguna."
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
            pill.addActionListener(event -> {
                for (UserDashboardComponents.FilterPill otherPill : pillButtons) {
                    otherPill.setActive(false);
                }
                pill.setActive(true);
                currentFilter = filter;
                refreshTables();
            });
            pillsPanel.add(pill);
        }

        searchField = new UserDashboardComponents.SearchField("Cari Laporan...");
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

        return UserDashboardComponents.responsiveActionRow(pillsPanel, searchWrapper);
    }

    private void refreshTables() {
        if (tableContainer == null) {
            return;
        }

        tableContainer.removeAll();
        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        int row = 0;

        if (shouldShow("Pending")) {
            addTableSection(gbc, row++, "Laporan Pending", ReportStatus.PENDING);
        }
        if (shouldShow("Valid")) {
            addTableSection(gbc, row++, "Laporan Valid", ReportStatus.VALID);
        }
        if (shouldShow("Ditolak")) {
            addTableSection(gbc, row++, "Laporan Ditolak", ReportStatus.DITOLAK);
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private boolean shouldShow(String filter) {
        return "Semua".equals(currentFilter) || filter.equals(currentFilter);
    }

    private void addTableSection(GridBagConstraints gbc, int row, String title, ReportStatus status) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 24, 0, 0, 0);
        tableContainer.add(createLostReportsTableSection(title, status), gbc);
    }

    private JPanel createLostReportsTableSection(String title, ReportStatus status) {
        ArrayList<LostReport> reports = getFilteredReports(status);
        JTable table = AdminDashboardComponents.table(
                new String[]{"Report Id", "Pelapor", "Barang", "Kategori", "Lokasi Hilang", "Tanggal", "Status"},
                createLostReportRows(reports)
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
                ArrayList<LostReport> tableReports = (ArrayList<LostReport>) table.getClientProperty("reports");
                if (modelRow >= 0 && modelRow < tableReports.size()) {
                    LostReport report = tableReports.get(modelRow);
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

    private ArrayList<LostReport> getFilteredReports(ReportStatus status) {
        ArrayList<LostReport> result = new ArrayList<>();
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        for (LostReport report : reportManager.getLostReports()) {
            if (report.getStatus() != status) {
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

    private boolean matchesSearch(LostReport report, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return contains(report.getReportId(), keyword)
                || contains(report.getLostLocation(), keyword)
                || (report.getUser() != null && contains(report.getUser().getName(), keyword))
                || (report.getItem() != null && contains(report.getItem().getName(), keyword))
                || (report.getItem() != null && report.getItem().getCategory() != null && contains(report.getItem().getCategory().getName(), keyword));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Object[][] createLostReportRows(ArrayList<LostReport> reports) {
        if (reports.isEmpty()) {
            return new Object[][]{{"Belum Ada Laporan", EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
        }

        Object[][] rows = new Object[reports.size()][7];
        for (int i = 0; i < reports.size(); i++) {
            LostReport report = reports.get(i);
            Item item = report.getItem();

            rows[i][0] = safe(report.getReportId());
            rows[i][1] = userName(report.getUser());
            rows[i][2] = itemName(item);
            rows[i][3] = categoryName(item);
            rows[i][4] = titleCase(report.getLostLocation());
            rows[i][5] = UserDashboardComponents.date(report);
            rows[i][6] = titleCase(report.getStatus() == null ? EMPTY_VALUE : report.getStatus().name());
        }
        return rows;
    }

    private void openDetail(LostReport report) {
        new AdminLostReportDetailFrame(report, reportManager, this::reloadTables).setVisible(true);
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
