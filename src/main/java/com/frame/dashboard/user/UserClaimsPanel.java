package com.frame.dashboard.user;

import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Claim;
import com.model.Report;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UserClaimsPanel extends JPanel {

    private static final String TITLE = "Klaim Saya";
    private static final String SUBTITLE = "Pantau Status Pengajuan Klaim Barang Milikmu.";
    private static final String EMPTY_MESSAGE = "Kamu Belum Mengajukan Klaim.";

    public UserClaimsPanel(User user, ClaimManager claimManager, ReportManager reportManager) {
        configurePanel();
        claimManager.refreshClaimsFromDatabase();
        if (reportManager != null) {
            reportManager.reload();
        }
        add(UserDashboardComponents.scroll(createContent(user, claimManager, reportManager)), BorderLayout.CENTER);
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent(User user, ClaimManager claimManager, ReportManager reportManager) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(createClaimsGrid(user, claimManager, reportManager), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createClaimsGrid(User user, ClaimManager claimManager, ReportManager reportManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        for (Claim claim : claimManager.getClaims()) {
            if (isMine(user, claim)) {
                grid.add(new UserDashboardComponents.ClaimCard(claim, findRelatedReport(reportManager, claim)));
            }
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState(EMPTY_MESSAGE));
        }
        return grid;
    }

    private boolean isMine(User user, Claim claim) {
        return user != null
                && claim.getUser() != null
                && user.getUserId().equals(claim.getUser().getUserId());
    }

    private Report findRelatedReport(ReportManager reportManager, Claim claim) {
        if (reportManager == null || claim == null || claim.getRelatedReportId() == null) {
            return null;
        }
        Object report = reportManager.findById(claim.getRelatedReportId());
        return report instanceof Report ? (Report) report : null;
    }
}
