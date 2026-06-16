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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserProfilePanel extends JPanel {

    private static final String TITLE = "Profil Saya";
    private static final String SUBTITLE = "Informasi Akun Yang Sedang Login.";
    private static final int PROFILE_LABEL_WIDTH = 88;
    private static final int PROFILE_ROW_HEIGHT = 22;

    public UserProfilePanel(User user) {
        configurePanel();
        add(UserDashboardComponents.scroll(createContent(user)), BorderLayout.CENTER);
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private JPanel createContent(User user) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(18, 0, 0, 0);
        content.add(createProfileCard(user), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        content.add(new JPanel(), gbc);

        return content;
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
        gbc.anchor = GridBagConstraints.NORTHWEST;

        addProfileRow(card, gbc, 0, "Nama", user != null ? user.getName() : "-");
        addProfileRow(card, gbc, 1, "Username", user != null ? user.getUsername() : "-");
        addProfileRow(card, gbc, 2, "Role", user != null ? user.getRole().name() : "-");
        addProfileRow(card, gbc, 3, "User ID", user != null ? user.getUserId() : "-");

        int nextRow = 4;
        if (user instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) user;
            addProfileRow(card, gbc, nextRow++, "NIM", safe(mahasiswa.getNim()));
            addProfileRow(card, gbc, nextRow++, "Jurusan", safe(mahasiswa.getJurusan()));
            addProfileRow(card, gbc, nextRow++, "Fakultas", safe(mahasiswa.getFakultas()));
            addProfileRow(card, gbc, nextRow, "Kelas", safe(mahasiswa.getKelas()));
        } else if (user instanceof Dosen) {
            Dosen dosen = (Dosen) user;
            addProfileRow(card, gbc, nextRow++, "NIP", safe(dosen.getNip()));
            addProfileRow(card, gbc, nextRow, "Bidang", safe(dosen.getBidang()));
        } else if (user instanceof Staff) {
            Staff staff = (Staff) user;
            addProfileRow(card, gbc, nextRow++, "Staff ID", safe(staff.getStaffID()));
            addProfileRow(card, gbc, nextRow, "Bagian", safe(staff.getBagian()));
        }

        return card;
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 18, 0, 0, 0);

        panel.add(createProfileRow(label, value), gbc);
    }

    private JPanel createProfileRow(String label, String value) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints rowGbc = new GridBagConstraints();
        rowGbc.gridx = 0;
        rowGbc.weightx = 0;
        rowGbc.anchor = GridBagConstraints.WEST;
        rowGbc.fill = GridBagConstraints.NONE;
        JLabel labelText = UserDashboardComponents.label(label, 13, Font.BOLD, UserDashboardComponents.TEXT_MUTED);
        labelText.setPreferredSize(new java.awt.Dimension(PROFILE_LABEL_WIDTH, PROFILE_ROW_HEIGHT));
        row.add(labelText, rowGbc);

        rowGbc.gridx = 1;
        rowGbc.insets = new Insets(0, 8, 0, 8);
        JLabel separator = UserDashboardComponents.label(":", 13, Font.BOLD, UserDashboardComponents.TEXT_MUTED);
        separator.setPreferredSize(new java.awt.Dimension(8, PROFILE_ROW_HEIGHT));
        row.add(separator, rowGbc);

        rowGbc.gridx = 2;
        rowGbc.weightx = 1;
        rowGbc.insets = new Insets(0, 10, 0, 0);
        rowGbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(UserDashboardComponents.label(value, 16, Font.BOLD, UserDashboardComponents.TEXT_DARK), rowGbc);

        return row;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
