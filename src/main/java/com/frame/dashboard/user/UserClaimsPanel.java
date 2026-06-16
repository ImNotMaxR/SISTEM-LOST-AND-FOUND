package com.frame.dashboard.user;

import com.managers.ClaimManager;
import com.model.Claim;
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

    public UserClaimsPanel(User user, ClaimManager claimManager) {
        configurePanel();
        add(UserDashboardComponents.scroll(createContent(user, claimManager)), BorderLayout.CENTER);
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent(User user, ClaimManager claimManager) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(createClaimsGrid(user, claimManager), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
    }

    private JPanel createClaimsGrid(User user, ClaimManager claimManager) {
        JPanel grid = UserDashboardComponents.cardGrid();
        for (Claim claim : claimManager.getAllClaims()) {
            if (isMine(user, claim)) {
                grid.add(new UserDashboardComponents.ClaimCard(claim));
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
}
