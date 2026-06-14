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

    public FoundItemsPanel(ReportManager reportManager) {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        content.add(UserDashboardComponents.section("Barang Ditemukan", "Barang Yang Sudah Dilaporkan Ditemukan Oleh Petugas."), gbc);

        JPanel grid = UserDashboardComponents.cardGrid();
        for (FoundReport report : reportManager.getFoundReports()) {
            grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.PRIMARY));
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState("Belum Ada Barang Ditemukan."));
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(grid, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
    }
}
