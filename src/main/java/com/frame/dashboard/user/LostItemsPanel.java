package com.frame.dashboard.user;

import com.managers.ReportManager;
import com.model.LostReport;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class LostItemsPanel extends JPanel {

    private static final String TITLE = "Barang Dicari";
    private static final String SUBTITLE = "Semua Laporan Barang Hilang yang Masih Perlu Dipantau.";
    private static final String EMPTY_MESSAGE = "Belum Ada Barang Yang Dicari.";

    public LostItemsPanel(ReportManager reportManager) {
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
        content.add(createLostItemsGrid(reportManager), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createLostItemsGrid(ReportManager reportManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        for (LostReport report : reportManager.getLostReports()) {
            grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.ORANGE));
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        return grid;
    }
}
