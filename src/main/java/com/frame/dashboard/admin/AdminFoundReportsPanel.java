package com.frame.dashboard.admin;

import com.enumeration.ItemStatus;
import com.frame.dashboard.user.UserDashboardComponents;
import com.frame.dashboard.user.WrapLayout;
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
    private JPanel tableContainer;
    private JTextField searchField;
    private String currentClaimFilter = "Semua";

    public AdminFoundReportsPanel(ReportManager reportManager) {
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
        JButton addButton = createAddButton();
        addButton.addActionListener(event -> new AdminFoundReportFormFrame(
                AuthService.getCurrentUser(),
                reportManager,
                this::reloadTables
        ).setVisible(true));

        return UserDashboardComponents.responsiveActionRow(UserDashboardComponents.section(
                "Kelola Barang Ditemukan",
                "Daftar Laporan Barang Ditemukan Yang Dibuat Oleh Security Dan Admin."
        ), addButton);
    }

    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
        pillsPanel.setOpaque(false);

        String[] filters = {"Semua", "Belum Diklaim", "Sudah Diklaim"};
        UserDashboardComponents.FilterPill[] pillButtons = new UserDashboardComponents.FilterPill[filters.length];
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            UserDashboardComponents.FilterPill pill = new UserDashboardComponents.FilterPill(filter, filter.equals(currentClaimFilter));
            pillButtons[i] = pill;
            pill.addActionListener(event -> {
                for (UserDashboardComponents.FilterPill otherPill : pillButtons) {
                    otherPill.setActive(false);
                }
                pill.setActive(true);
                currentClaimFilter = filter;
                refreshTables();
            });
            pillsPanel.add(pill);
        }

        searchField = new UserDashboardComponents.SearchField("Cari Barang Ditemukan...");
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

        if (shouldShow("Belum Diklaim")) {
            addTableSection(gbc, row++, "Belum Diklaim", false);
        }
        if (shouldShow("Sudah Diklaim")) {
            addTableSection(gbc, row++, "Sudah Diklaim", true);
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private boolean shouldShow(String filter) {
        return "Semua".equals(currentClaimFilter) || filter.equals(currentClaimFilter);
    }

    private void addTableSection(GridBagConstraints gbc, int row, String title, boolean claimed) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 24, 0, 0, 0);
        tableContainer.add(createFoundReportsTableSection(title, claimed), gbc);
    }

    private JPanel createFoundReportsTableSection(String title, boolean claimed) {
        ArrayList<FoundReport> reports = getFilteredReports(claimed);
        JTable table = AdminDashboardComponents.table(
                new String[]{"Report Id", "Pelapor", "Barang", "Kategori", "Lokasi Ditemukan", "Match", "Tanggal", "Status"},
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

    private ArrayList<FoundReport> getFilteredReports(boolean claimed) {
        ArrayList<FoundReport> result = new ArrayList<>();
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        for (FoundReport report : reportManager.getFoundReports()) {
            boolean reportClaimed = report.getItem() != null && report.getItem().getStatus() == ItemStatus.DIKLAIM;
            if (reportClaimed != claimed) {
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

    private Object[][] createFoundReportRows(ArrayList<FoundReport> reports) {
        if (reports.isEmpty()) {
            return new Object[][]{{"Belum Ada Laporan", EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
        }

        Object[][] rows = new Object[reports.size()][8];
        for (int i = 0; i < reports.size(); i++) {
            FoundReport report = reports.get(i);
            Item item = report.getItem();

            rows[i][0] = safe(report.getReportId());
            rows[i][1] = userName(report.getUser());
            rows[i][2] = itemName(item);
            rows[i][3] = categoryName(item);
            rows[i][4] = titleCase(report.getFoundLocation());
            rows[i][5] = matchedReport(report.getMatchedLostReport());
            rows[i][6] = UserDashboardComponents.date(report);
            rows[i][7] = report.getItem() != null && report.getItem().getStatus() == ItemStatus.DIKLAIM ? "Sudah Diklaim" : "Belum Diklaim";
        }
        return rows;
    }

    private JButton createAddButton() {
        JButton button = new JButton("+ Tambah Barang Temuan");
        button.setPreferredSize(new Dimension(230, 42));
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics graphics, JComponent component) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color background = button.getModel().isRollover()
                        ? UserDashboardComponents.PRIMARY_DARK
                        : UserDashboardComponents.PRIMARY;
                g2.setColor(background);
                g2.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), 18, 18);
                g2.dispose();
                super.paint(graphics, component);
            }
        });
        return button;
    }

    private void openDetail(FoundReport report) {
        new AdminFoundReportDetailFrame(report, reportManager, this::reloadTables).setVisible(true);
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
