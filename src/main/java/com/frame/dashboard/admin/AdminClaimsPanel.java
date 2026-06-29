package com.frame.dashboard.admin;

import com.enumeration.ClaimStatus;
import com.frame.dashboard.shared.DashboardUi;
import com.frame.dashboard.shared.WrapLayout;
import com.managers.ClaimManager;
import com.model.Claim;
import com.model.Item;
import com.model.User;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AdminClaimsPanel extends JPanel {

    private static final String EMPTY_VALUE = "-";

    private final ClaimManager claimManager;
    private JPanel tableContainer;
    private JTextField searchField;
    private String currentFilter = "Semua";

    // -------------------------------------------------------------------------
    // Panel Setup
    // -------------------------------------------------------------------------

    public AdminClaimsPanel(ClaimManager claimManager) {
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
        return DashboardUi.section(
                "Kelola Klaim",
                "Daftar Pengajuan Klaim Barang Ditemukan Dari Pengguna."
        );
    }

    // -------------------------------------------------------------------------
    // Filter and Search UI
    // -------------------------------------------------------------------------

    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
        pillsPanel.setOpaque(false);

        String[] filters = {"Semua", "Pending", "Diterima", "Ditolak"};
        DashboardUi.FilterPill[] pillButtons = new DashboardUi.FilterPill[filters.length];
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            DashboardUi.FilterPill pill = new DashboardUi.FilterPill(filter, filter.equals(currentFilter));
            pillButtons[i] = pill;
            pill.addActionListener(event -> {
                for (DashboardUi.FilterPill otherPill : pillButtons) {
                    otherPill.setActive(false);
                }
                pill.setActive(true);
                currentFilter = filter;
                refreshTables();
            });
            pillsPanel.add(pill);
        }

        searchField = new DashboardUi.SearchField("Cari Klaim...");
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
            addTableSection(gbc, row++, "Klaim Pending", ClaimStatus.PENDING);
        }
        if (shouldShow("Diterima")) {
            addTableSection(gbc, row++, "Klaim Diterima", ClaimStatus.VALID);
        }
        if (shouldShow("Ditolak")) {
            addTableSection(gbc, row++, "Klaim Ditolak", ClaimStatus.DITOLAK);
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private boolean shouldShow(String filter) {
        return "Semua".equals(currentFilter) || filter.equals(currentFilter);
    }

    private void addTableSection(GridBagConstraints gbc, int row, String title, ClaimStatus status) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 24, 0, 0, 0);
        tableContainer.add(createClaimsTableSection(title, status), gbc);
    }

    // -------------------------------------------------------------------------
    // Table UI
    // -------------------------------------------------------------------------

    private JPanel createClaimsTableSection(String title, ClaimStatus status) {
        ArrayList<Claim> claims = getFilteredClaims(status);
        JTable table = AdminDashboardComponents.table(
                new String[]{"Claim Id", "Pengaju", "Barang", "Kategori", "Report Asal", "Tanggal", "Status"},
                createClaimRows(claims)
        );
        table.putClientProperty("claims", claims);
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
                ArrayList<Claim> tableClaims = (ArrayList<Claim>) table.getClientProperty("claims");
                if (modelRow >= 0 && modelRow < tableClaims.size()) {
                    Claim claim = tableClaims.get(modelRow);
                    table.clearSelection();
                    openDetail(claim);
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

    private ArrayList<Claim> getFilteredClaims(ClaimStatus status) {
        ArrayList<Claim> result = new ArrayList<>();
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        for (Claim claim : claimManager.getClaims()) {
            if (claim.getStatus() != status) {
                continue;
            }
            if (!matchesSearch(claim, keyword)) {
                continue;
            }
            result.add(claim);
        }
        result.sort((first, second) -> second.getDateClaim().compareTo(first.getDateClaim()));
        return result;
    }

    private boolean matchesSearch(Claim claim, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return contains(claim.getClaimId(), keyword)
                || contains(claim.getRelatedReportId(), keyword)
                || (claim.getUser() != null && contains(claim.getUser().getName(), keyword))
                || (claim.getItem() != null && contains(claim.getItem().getName(), keyword))
                || (claim.getItem() != null && claim.getItem().getCategory() != null && contains(claim.getItem().getCategory().getName(), keyword));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    // -------------------------------------------------------------------------
    // Table Data
    // -------------------------------------------------------------------------

    private Object[][] createClaimRows(ArrayList<Claim> claims) {
        if (claims.isEmpty()) {
            return new Object[][]{{"Belum Ada Klaim", EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE}};
        }

        Object[][] rows = new Object[claims.size()][7];
        for (int i = 0; i < claims.size(); i++) {
            Claim claim = claims.get(i);
            Item item = claim.getItem();
            rows[i][0] = safe(claim.getClaimId());
            rows[i][1] = userName(claim.getUser());
            rows[i][2] = itemName(item);
            rows[i][3] = categoryName(item);
            rows[i][4] = safe(claim.getRelatedReportId());
            rows[i][5] = DashboardUi.date(claim);
            rows[i][6] = statusText(claim.getStatus());
        }
        return rows;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void openDetail(Claim claim) {
        new AdminClaimDetailFrame(claim, claimManager, this::reloadTables).setVisible(true);
    }

    private void reloadTables() {
        claimManager.refreshClaimsFromDatabase();
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
        if (item == null || item.getCategory() == null) {
            return EMPTY_VALUE;
        }
        return titleCase(item.getCategory().getName());
    }

    private String statusText(ClaimStatus status) {
        if (status == ClaimStatus.VALID) {
            return "Diterima";
        }
        if (status == ClaimStatus.DITOLAK) {
            return "Ditolak";
        }
        return "Pending";
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
