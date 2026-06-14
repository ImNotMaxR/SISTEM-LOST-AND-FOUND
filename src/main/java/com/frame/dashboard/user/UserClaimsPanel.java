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

    public UserClaimsPanel(User user, ClaimManager claimManager) {
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
        content.add(UserDashboardComponents.section("Klaim Saya", "Pantau Status Pengajuan Klaim Barang Milikmu."), gbc);

        JPanel grid = UserDashboardComponents.cardGrid();
        for (Claim claim : claimManager.getAllClaims()) {
            if (isMine(user, claim)) {
                grid.add(new UserDashboardComponents.ClaimCard(claim));
            }
        }
        if (grid.getComponentCount() == 0) {
            grid.add(UserDashboardComponents.emptyState("Kamu Belum Mengajukan Klaim."));
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(14, 0, 0, 0);
        content.add(grid, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
    }

    private boolean isMine(User user, Claim claim) {
        return user != null
                && claim.getUser() != null
                && user.getUserId().equals(claim.getUser().getUserId());
    }
}
