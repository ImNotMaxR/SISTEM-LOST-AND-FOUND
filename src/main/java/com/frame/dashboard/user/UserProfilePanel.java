package com.frame.dashboard.user;

import com.model.Dosen;
import com.model.Mahasiswa;
import com.model.Staff;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UserProfilePanel extends JPanel {

    public UserProfilePanel(User user) {
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
        content.add(UserDashboardComponents.section("Profil saya", "Informasi akun yang sedang login."), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 0, 0, 0);
        content.add(createProfileCard(user), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
    }

    private JPanel createProfileCard(User user) {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(java.awt.Color.WHITE, 24);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 24, 1),
                BorderFactory.createEmptyBorder(26, 28, 26, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addRow(card, gbc, 0, "Nama", user != null ? user.getName() : "-");
        addRow(card, gbc, 1, "Username", user != null ? user.getUsername() : "-");
        addRow(card, gbc, 2, "Role", user != null ? user.getRole().name() : "-");
        addRow(card, gbc, 3, "User ID", user != null ? user.getUserId() : "-");

        int nextRow = 4;
        if (user instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) user;
            addRow(card, gbc, nextRow++, "NIM", safe(mahasiswa.getNim()));
            addRow(card, gbc, nextRow++, "Jurusan", safe(mahasiswa.getJurusan()));
            addRow(card, gbc, nextRow++, "Fakultas", safe(mahasiswa.getFakultas()));
            addRow(card, gbc, nextRow, "Kelas", safe(mahasiswa.getKelas()));
        } else if (user instanceof Dosen) {
            Dosen dosen = (Dosen) user;
            addRow(card, gbc, nextRow++, "NIP", safe(dosen.getNip()));
            addRow(card, gbc, nextRow, "Bidang", safe(dosen.getBidang()));
        } else if (user instanceof Staff) {
            Staff staff = (Staff) user;
            addRow(card, gbc, nextRow++, "Staff ID", safe(staff.getStaffID()));
            addRow(card, gbc, nextRow, "Bagian", safe(staff.getBagian()));
        }

        return card;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 16, 0, 0, 0);

        JPanel rowPanel = new JPanel(new GridBagLayout());
        rowPanel.setOpaque(false);

        GridBagConstraints rowGbc = new GridBagConstraints();
        rowGbc.gridx = 0;
        rowGbc.weightx = 0;
        rowGbc.anchor = GridBagConstraints.WEST;
        rowPanel.add(UserDashboardComponents.label(label, 13, Font.BOLD, UserDashboardComponents.TEXT_MUTED), rowGbc);

        rowGbc.gridx = 1;
        rowGbc.weightx = 1;
        rowGbc.insets = new Insets(0, 28, 0, 0);
        rowGbc.fill = GridBagConstraints.HORIZONTAL;
        rowPanel.add(UserDashboardComponents.label(value, 16, Font.BOLD, UserDashboardComponents.TEXT_DARK), rowGbc);

        panel.add(rowPanel, gbc);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
