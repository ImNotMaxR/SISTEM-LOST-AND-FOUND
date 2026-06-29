package com.frame.dashboard.user;

import com.managers.ReportManager;
import com.model.FoundReport;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FoundItemsPanel extends JPanel {

    private static final String TITLE = "Barang Ditemukan";
    private static final String SUBTITLE = "Barang Yang Sudah Dilaporkan Ditemukan Oleh Petugas.";
    private static final String EMPTY_MESSAGE = "Belum Ada Barang Ditemukan.";

    private ReportManager reportManager;
    private JPanel grid;
    private JTextField searchField;
    private String currentCategoryFilter = "Semua";

    public FoundItemsPanel(ReportManager reportManager) {
        this.reportManager = reportManager;
        configurePanel();
        JPanel content = createContent();
        UserDashboardComponents.clearTextFocusOnBackgroundClick(content);
        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
        updateGrid(""); // initial load
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        
        JPanel headerPanel = UserDashboardComponents.responsiveActionRow(
                UserDashboardComponents.section(TITLE, SUBTITLE),
                new JPanel() // empty panel instead of search field for the right side of header
        );
        headerPanel.setOpaque(false);
        content.add(headerPanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(createFilterSearchPanel(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 0, 0);
        grid = UserDashboardComponents.cardGrid();
        content.add(grid, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createFilterSearchPanel() {
        JPanel pillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 8));
        pillsPanel.setOpaque(false);
        
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Semua");
        
        try {
            java.sql.Connection conn = com.database.DBConnection.getInstance().getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories ORDER BY name ASC");
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categoryList.add(rs.getString("name"));
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Gagal load kategori: " + e.getMessage());
        }
        
        String[] filters = categoryList.toArray(new String[0]);
        
        UserDashboardComponents.FilterPill[] pillButtons = new UserDashboardComponents.FilterPill[filters.length];
        
        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];
            UserDashboardComponents.FilterPill pill = new UserDashboardComponents.FilterPill(filter, filter.equals(currentCategoryFilter));
            pillButtons[i] = pill;
            pill.addActionListener(e -> {
                for (UserDashboardComponents.FilterPill p : pillButtons) {
                    p.setActive(false);
                }
                pill.setActive(true);
                currentCategoryFilter = filter;
                updateGrid(searchField != null ? searchField.getText() : "");
            });
            pillsPanel.add(pill);
        }

        searchField = new UserDashboardComponents.SearchField("Cari Barang...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateGrid(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { updateGrid(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { updateGrid(searchField.getText()); }
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

    private void updateGrid(String keyword) {
        grid.removeAll();
        
        ArrayList<FoundReport> allFoundReports = reportManager.getFoundReports();
        ArrayList<FoundReport> filteredReports = new ArrayList<>();
        
        for (FoundReport report : allFoundReports) {
            if (report.isValid() && report.getMatchedLostReport() == null) {
                boolean matchSearch = keyword.isEmpty() || report.getItem().getName().toLowerCase().contains(keyword.toLowerCase());
                boolean matchCategory = currentCategoryFilter.equals("Semua") || 
                        (report.getItem().getCategory() != null && report.getItem().getCategory().getName().equals(currentCategoryFilter));
                
                if (matchSearch && matchCategory) {
                    filteredReports.add(report);
                }
            }
        }
        
        filteredReports.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        
        for (FoundReport report : filteredReports) {
            grid.add(new UserDashboardComponents.ReportCard(report, report.getItem().getStatus().name(), UserDashboardComponents.PRIMARY));
        }
        
        if (filteredReports.isEmpty()) {
            String emptyMessage = currentCategoryFilter.equals("Semua") 
                ? EMPTY_MESSAGE 
                : "Tidak ada barang temuan untuk kategori '" + currentCategoryFilter + "' saat ini.";
            
            if (!keyword.isEmpty()) {
                emptyMessage = "Pencarian '" + keyword + "' tidak ditemukan.";
            }
            grid.add(UserDashboardComponents.emptyState(emptyMessage));
        }
        
        grid.revalidate();
        grid.repaint();
    }
}
