package com.frame.dashboard.security;

import com.frame.dashboard.admin.AdminDashboardComponents;
import com.frame.dashboard.user.UserDashboardComponents;
import com.frame.dashboard.user.WrapLayout;
import com.managers.StorageManager;
import com.model.Category;
import com.model.Item;
import com.model.StorageRecord;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class SecurityStoragePanel extends JPanel {

    private static final String EMPTY_VALUE = "-";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final StorageManager storageManager;
    private final String securityId;
    private JPanel tableContainer;
    private JTextField searchField;
    private String currentFilter = "Semua";

    public SecurityStoragePanel(String securityId) {
        this.storageManager = new StorageManager();
        this.securityId = securityId;
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(44, 42, 48, 42));
        buildContent();
    }

    private void buildContent() {
        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();

        gbc.gridy = 0;
        add(UserDashboardComponents.section(
                "Storage Record",
                "Pantau Barang Yang Masih Disimpan Atau Sudah Diambil Dari Gudang."
        ), gbc);

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
        add(createSpacer(), gbc);

        refreshTable();
    }

    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
        pillsPanel.setOpaque(false);

        String[] filters = {"Semua", "Belum Diambil", "Sudah Diambil"};
        UserDashboardComponents.FilterPill[] pills = new UserDashboardComponents.FilterPill[filters.length];
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            UserDashboardComponents.FilterPill pill = new UserDashboardComponents.FilterPill(filter, filter.equals(currentFilter));
            pills[i] = pill;
            pill.addActionListener(event -> {
                for (UserDashboardComponents.FilterPill other : pills) {
                    other.setActive(false);
                }
                pill.setActive(true);
                currentFilter = filter;
                refreshTable();
            });
            pillsPanel.add(pill);
        }

        searchField = new UserDashboardComponents.SearchField("Cari record storage...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent event) { refreshTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { refreshTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { refreshTable(); }
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

    public void loadStorageRecords() {
        storageManager.reload();
        refreshTable();
    }

    private void refreshTable() {
        if (tableContainer == null) {
            return;
        }

        ArrayList<StorageRecord> records = getFilteredRecords();
        JTable table = AdminDashboardComponents.table(
                new String[]{"Record Id", "Barang", "Kategori", "Lokasi Storage", "Tanggal Masuk", "Tanggal Diambil", "Status"},
                createRows(records)
        );
        table.putClientProperty("records", records);
        installTableInteraction(table);

        tableContainer.removeAll();
        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        tableContainer.add(AdminDashboardComponents.tableSection("Daftar Storage Record", table), gbc);
        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private void installTableInteraction(JTable table) {
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
                if (event.getClickCount() < 1 || table.getSelectedRow() < 0) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                @SuppressWarnings("unchecked")
                ArrayList<StorageRecord> records = (ArrayList<StorageRecord>) table.getClientProperty("records");
                if (modelRow >= 0 && modelRow < records.size()) {
                    openDetail(records.get(modelRow));
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
    }

    private void updateTableCursor(JTable table, MouseEvent event) {
        int row = table.rowAtPoint(event.getPoint());
        table.putClientProperty("hoverRow", row);
        table.setCursor(row >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        table.repaint();
    }

    private ArrayList<StorageRecord> getFilteredRecords() {
        ArrayList<StorageRecord> result = new ArrayList<>();
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        for (StorageRecord record : storageManager.getStorageRecordsBySecurity(securityId)) {
            if (!matchesFilter(record) || !matchesSearch(record, keyword)) {
                continue;
            }
            result.add(record);
        }
        return result;
    }

    private boolean matchesFilter(StorageRecord record) {
        if ("Belum Diambil".equals(currentFilter)) {
            return !record.isReleased();
        }
        if ("Sudah Diambil".equals(currentFilter)) {
            return record.isReleased();
        }
        return true;
    }

    private boolean matchesSearch(StorageRecord record, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        Item item = record.getItem();
        Category category = item == null ? null : item.getCategory();
        return contains(record.getRecordId(), keyword)
                || contains(record.getStorageLocation(), keyword)
                || (item != null && contains(item.getName(), keyword))
                || (item != null && contains(item.getStatus().name(), keyword))
                || (category != null && contains(category.getName(), keyword));
    }

    private Object[][] createRows(ArrayList<StorageRecord> records) {
        if (records.isEmpty()) {
            return new Object[][]{{"Belum Ada Record", EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
        }

        Object[][] rows = new Object[records.size()][7];
        for (int i = 0; i < records.size(); i++) {
            StorageRecord record = records.get(i);
            Item item = record.getItem();
            rows[i][0] = safe(record.getRecordId());
            rows[i][1] = item == null ? EMPTY_VALUE : titleCase(item.getName());
            rows[i][2] = item == null || item.getCategory() == null ? EMPTY_VALUE : titleCase(item.getCategory().getName());
            rows[i][3] = titleCase(record.getStorageLocation());
            rows[i][4] = record.getDateStored() == null ? EMPTY_VALUE : record.getDateStored().format(DATE_TIME_FORMAT);
            rows[i][5] = record.getDateReleased() == null ? EMPTY_VALUE : record.getDateReleased().format(DATE_TIME_FORMAT);
            rows[i][6] = record.isReleased() ? "Sudah Diambil" : "Belum Diambil";
        }
        return rows;
    }

    private void openDetail(StorageRecord record) {
        new SecurityStorageDetailFrame(record, storageManager, this::loadStorageRecords).setVisible(true);
    }

    private JPanel createSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
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
