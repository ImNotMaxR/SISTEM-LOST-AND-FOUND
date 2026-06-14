package com.frame.dashboard.user;

import com.managers.ReportManager;
import com.model.LostReport;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UserReportsPanel extends JPanel {

    public UserReportsPanel(User user, ReportManager reportManager) {
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
        content.add(UserDashboardComponents.section("Laporan saya", "Daftar laporan barang hilang yang dibuat oleh akun ini."), gbc);

        JPanel grid = UserDashboardComponents.cardGrid();
        for (LostReport report : reportManager.getLostReports()) {
            if (isMine(user, report)) {
                grid.add(new UserDashboardComponents.ReportCard(report, report.getStatus().name(), UserDashboardComponents.PRIMARY_DARK));
            }
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState("Kamu belum memiliki laporan."));
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(grid, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
    }

    private boolean isMine(User user, LostReport report) {
        return user != null
                && report.getUser() != null
                && user.getUserId().equals(report.getUser().getUserId());
    }
}
