package com.frame.dashboard.user;

import com.managers.ReportManager;
import com.model.FoundReport;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class FoundItemsPanel extends JPanel {

    private static final String TITLE = "Barang Ditemukan";
    private static final String SUBTITLE = "Barang Yang Sudah Dilaporkan Ditemukan Oleh Petugas.";
    private static final String EMPTY_MESSAGE = "Belum Ada Barang Ditemukan.";

    public FoundItemsPanel(ReportManager reportManager) {
        configurePanel();
        add(UserDashboardComponents.scroll(createContent(reportManager)), BorderLayout.CENTER);
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent(ReportManager reportManager) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(createFoundItemsGrid(reportManager), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createFoundItemsGrid(ReportManager reportManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        for (FoundReport report : reportManager.getFoundReports()) {
            grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.PRIMARY));
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        return grid;
    }
}
