package com.frame.dashboard.user;

import com.managers.ReportManager;
import com.model.LostReport;
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

public class LostItemsPanel extends JPanel {

    private static final String TITLE = "Barang Dicari";
    private static final String SUBTITLE = "Semua Laporan Barang Hilang Yang Masih Perlu Dipantau.";
    private static final String EMPTY_MESSAGE = "Belum Ada Barang Yang Dicari.";

    private ReportManager reportManager;
    private JPanel grid;
    private JTextField searchField;

    public LostItemsPanel(ReportManager reportManager) {
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
        
        searchField = createSearchBar();
        JPanel searchContainer = new JPanel(new GridBagLayout());
        searchContainer.setOpaque(false);
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 1;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchContainer.add(searchField, searchGbc);

        JPanel headerPanel = UserDashboardComponents.responsiveActionRow(
                UserDashboardComponents.section(TITLE, SUBTITLE),
                searchContainer
        );

        content.add(headerPanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        grid = UserDashboardComponents.cardGrid();
        content.add(grid, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JTextField createSearchBar() {
        JTextField textField = new UserDashboardComponents.SearchField("Cari Barang...");
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateGrid(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateGrid(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateGrid(textField.getText());
            }
        });
        
        return textField;
    }

    private void updateGrid(String keyword) {
        grid.removeAll();
        
        ArrayList<LostReport> allLostReports = reportManager.getLostReports();
        ArrayList<LostReport> filteredReports = new ArrayList<>();
        
        for (LostReport report : allLostReports) {
            if (report.isValid()) {
                if (keyword.isEmpty() || report.getItem().getName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredReports.add(report);
                }
            }
        }
        
        filteredReports.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        
        for (LostReport report : filteredReports) {
            grid.add(new UserDashboardComponents.ReportCard(report, report.getItem().getStatus().name(), UserDashboardComponents.ORANGE));
        }
        
        if (filteredReports.isEmpty()) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        
        grid.revalidate();
        grid.repaint();
    }
}
