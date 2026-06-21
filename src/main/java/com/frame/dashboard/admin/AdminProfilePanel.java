package com.frame.dashboard.admin;

import com.frame.AppDialog;
import com.frame.dashboard.shared.DashboardUi;
import com.managers.UserManager;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AdminProfilePanel extends JPanel {

    private static final String TITLE = "Edit Profil Admin";
    private static final String SUBTITLE = "Kelola Kredensial Akun Admin Sistem.";

    private final UserManager userManager = new UserManager();
    private User currentUser;

    private JTextField currentUsernameField;
    private JPasswordField oldPasswordForUsernameField;
    private JTextField newUsernameField;

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    // -------------------------------------------------------------------------
    // Panel Setup
    // -------------------------------------------------------------------------

    public AdminProfilePanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(DashboardUi.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        refreshContent();
    }

    private void refreshContent() {
        removeAll();
        JScrollPane scrollPane = DashboardUi.scroll(createContent());
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.revalidate();
            scrollPane.doLayout();
            DashboardUi.resetScrollPosition(scrollPane);
        });
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private JPanel createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = DashboardUi.contentConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(DashboardUi.section(TITLE, SUBTITLE), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createProfileCard(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(createFormsGrid(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        content.add(spacer, gbc);
        return content;
    }

    // -------------------------------------------------------------------------
    // Profile Summary UI
    // -------------------------------------------------------------------------

    private JPanel createProfileCard() {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(Color.WHITE, 24);
        card.setLayout(new BorderLayout());
        card.setBorder(new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 24, 1));

        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(42, 142, 222), getWidth(), getHeight(), new Color(28, 118, 201)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 24, 24, 24);
                g2.fillRect(0, 24, getWidth(), Math.max(0, getHeight() - 24));
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(720, 200));
        hero.setMinimumSize(new Dimension(320, 200));

        GridBagConstraints heroGbc = new GridBagConstraints();
        heroGbc.gridx = 0;
        heroGbc.gridy = 0;
        heroGbc.insets = new Insets(30, 0, 10, 0);
        hero.add(createAvatar(), heroGbc);

        heroGbc.gridy = 1;
        heroGbc.insets = new Insets(0, 0, 4, 0);
        JLabel nameLabel = label(displayName(), 22, Font.BOLD, Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        hero.add(nameLabel, heroGbc);

        heroGbc.gridy = 2;
        heroGbc.insets = new Insets(0, 0, 30, 0);
        JLabel roleLabel = label("Telkom University - Akun Admin", 13, Font.PLAIN, new Color(255, 255, 255, 205));
        roleLabel.setHorizontalAlignment(JLabel.CENTER);
        hero.add(roleLabel, heroGbc);

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        details.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        GridBagConstraints detailGbc = new GridBagConstraints();
        detailGbc.gridx = 0;
        detailGbc.gridy = 0;
        detailGbc.weightx = 1;
        detailGbc.fill = GridBagConstraints.HORIZONTAL;
        detailGbc.insets = new Insets(0, 0, 20, 0);
        details.add(createInfoGrid(), detailGbc);

        detailGbc.gridy = 1;
        detailGbc.insets = new Insets(0, 0, 0, 0);
        JLabel note = new JLabel("Data identitas admin bersifat tetap. Perubahan yang tersedia hanya username dan password.");
        note.setFont(new Font("Poppins", Font.PLAIN, 12));
        note.setForeground(new Color(150, 150, 150));
        note.setHorizontalAlignment(JLabel.CENTER);
        details.add(note, detailGbc);

        card.add(hero, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAvatar() {
        JPanel avatar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(90, 90));
        JLabel icon = new JLabel();
        java.net.URL url = getClass().getResource("/assets/PNG/64x64/icon_person.png");
        if (url != null) {
            Image image = new ImageIcon(url).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            icon.setIcon(new ImageIcon(image));
        } else {
            icon.setText("A");
            icon.setFont(new Font("Poppins", Font.BOLD, 30));
            icon.setForeground(Color.WHITE);
        }
        icon.setHorizontalAlignment(JLabel.CENTER);
        avatar.add(icon, BorderLayout.CENTER);
        return avatar;
    }

    private JPanel createInfoGrid() {
        JPanel grid = DashboardUi.responsiveGrid(180);
        grid.add(createInfoBox("NAMA", displayName()));
        grid.add(createInfoBox("USERNAME", safe(currentUser == null ? null : currentUser.getUsername())));
        grid.add(createInfoBox("ROLE", "ADMIN"));
        return grid;
    }

    private JPanel createInfoBox(String title, String value) {
        DashboardUi.RoundedPanel box = new DashboardUi.RoundedPanel(new Color(248, 250, 252), 16);
        box.setLayout(new GridBagLayout());
        box.setBorder(new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 16, 1));
        box.setPreferredSize(new Dimension(180, 75));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 12, 5, 12);

        JLabel titleLabel = DashboardUi.label(title, 10, Font.BOLD, new Color(130, 140, 150));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        box.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 12, 0, 12);
        String safeValue = safe(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        JLabel valueLabel = new JLabel("<html><div style='text-align:center; width:150px;'>" + safeValue + "</div></html>");
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        valueLabel.setForeground(DashboardUi.TEXT_DARK);
        box.add(valueLabel, gbc);
        return box;
    }

    // -------------------------------------------------------------------------
    // Form Cards UI
    // -------------------------------------------------------------------------

    private JPanel createFormsGrid() {
        JPanel grid = DashboardUi.responsiveGrid(360);
        grid.add(createUsernameCard());
        grid.add(createPasswordCard());
        return grid;
    }

    private JPanel createUsernameCard() {
        DashboardUi.RoundedPanel card = formCard();
        GridBagConstraints gbc = formConstraints();
        addCardHeader(card, gbc, "Edit Username", "Konfirmasi password lama sebelum mengganti username.", "/assets/PNG/64x64/icon_pencil.png");
        currentUsernameField = addTextField(card, gbc, 1, "Username Saat Ini", "", false);
        currentUsernameField.setText(currentUser == null ? "" : currentUser.getUsername());
        oldPasswordForUsernameField = addPasswordField(card, gbc, 3, "Password Lama", "Masukkan password lama");
        newUsernameField = addTextField(card, gbc, 5, "Username Baru", "Isi username baru", true);
        JButton button = submitButton("Simpan Username");
        button.addActionListener(event -> updateUsername());
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(button, gbc);
        return card;
    }

    private JPanel createPasswordCard() {
        DashboardUi.RoundedPanel card = formCard();
        GridBagConstraints gbc = formConstraints();
        addCardHeader(card, gbc, "Ganti Password", "Gunakan password lama untuk validasi keamanan akun.", "/assets/PNG/64x64/icon_padlock.png");
        oldPasswordField = addPasswordField(card, gbc, 1, "Password Lama", "Masukkan password lama");
        newPasswordField = addPasswordField(card, gbc, 3, "Password Baru", "Isi password baru");
        confirmPasswordField = addPasswordField(card, gbc, 5, "Konfirmasi Password Baru", "Ulangi password baru");
        JButton button = submitButton("Simpan Password");
        button.addActionListener(event -> updatePassword());
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(button, gbc);
        return card;
    }

    private DashboardUi.RoundedPanel formCard() {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(Color.WHITE, 24);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 24, 1),
                BorderFactory.createEmptyBorder(26, 28, 28, 28)
        ));
        return card;
    }

    private GridBagConstraints formConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addCardHeader(JPanel card, GridBagConstraints gbc, String title, String subtitle, String iconPath) {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridx = 0;
        hgbc.gridy = 0;
        hgbc.gridheight = 2;
        hgbc.insets = new Insets(0, 0, 0, 14);
        header.add(iconBox(iconPath), hgbc);
        hgbc.gridx = 1;
        hgbc.gridheight = 1;
        hgbc.weightx = 1;
        hgbc.fill = GridBagConstraints.HORIZONTAL;
        hgbc.anchor = GridBagConstraints.WEST;
        hgbc.insets = new Insets(0, 0, 2, 0);
        header.add(DashboardUi.label(title, 18, Font.BOLD, DashboardUi.TEXT_DARK), hgbc);
        hgbc.gridy = 1;
        hgbc.insets = new Insets(0, 0, 0, 0);
        header.add(DashboardUi.label(subtitle, 12, Font.PLAIN, DashboardUi.TEXT_MUTED), hgbc);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 22, 0);
        card.add(header, gbc);
    }

    private JPanel iconBox(String iconPath) {
        DashboardUi.RoundedPanel box = new DashboardUi.RoundedPanel(new Color(239, 246, 255), 14);
        box.setLayout(new GridBagLayout());
        box.setPreferredSize(new Dimension(42, 42));
        JLabel icon = new JLabel();
        java.net.URL url = getClass().getResource(iconPath);
        if (url != null) {
            Image image = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            icon.setIcon(new ImageIcon(image));
        }
        box.add(icon);
        return box;
    }

    // -------------------------------------------------------------------------
    // Field Factories
    // -------------------------------------------------------------------------

    private JTextField addTextField(JPanel card, GridBagConstraints gbc, int row, String label, String placeholder, boolean editable) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 1 ? 0 : 14, 0, 7, 0);
        card.add(DashboardUi.label(label, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        JTextField field = textField(placeholder);
        field.setEditable(editable);
        if (!editable) {
            field.setBackground(new Color(241, 245, 249));
        }
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(field, gbc);
        return field;
    }

    private JPasswordField addPasswordField(JPanel card, GridBagConstraints gbc, int row, String label, String placeholder) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 1 ? 0 : 14, 0, 7, 0);
        card.add(DashboardUi.label(label, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        JPasswordField field = passwordField(placeholder);
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(field, gbc);
        return field;
    }

    private JTextField textField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getText().isEmpty() && !isFocusOwner()) {
                    paintPlaceholder(graphics, this, placeholder);
                }
            }
        };
        styleField(field);
        return field;
    }

    private JPasswordField passwordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    paintPlaceholder(graphics, this, placeholder);
                }
            }
        };
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(0, 44));
        field.setMinimumSize(new Dimension(0, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setForeground(DashboardUi.TEXT_DARK);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(226, 232, 240), 16, 1),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
    }

    private void paintPlaceholder(Graphics graphics, JTextField field, String placeholder) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(148, 163, 184));
        g2.setFont(field.getFont());
        int y = (field.getHeight() - graphics.getFontMetrics().getHeight()) / 2 + graphics.getFontMetrics().getAscent();
        g2.drawString(placeholder, field.getInsets().left, y);
        g2.dispose();
    }

    // -------------------------------------------------------------------------
    // Button UI
    // -------------------------------------------------------------------------

    private JButton submitButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover() ? DashboardUi.PRIMARY_DARK : DashboardUi.PRIMARY;
                if (getModel().isPressed()) fill = fill.darker();
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setPreferredSize(new Dimension(0, 44));
        button.setMinimumSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    private JTextArea text(String value, int size, int style, Color color) {
        JTextArea area = new JTextArea(safe(value));
        area.setFont(new Font("Poppins", style, size));
        area.setForeground(color);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Poppins", style, size));
        label.setForeground(color);
        return label;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void updateUsername() {
        if (currentUser == null) {
            AppDialog.error(this, "Gagal", "Data admin tidak ditemukan.");
            return;
        }
        String oldPassword = new String(oldPasswordForUsernameField.getPassword());
        String newUsername = newUsernameField.getText().trim();
        if (oldPassword.isEmpty() || newUsername.isEmpty()) {
            AppDialog.warning(this, "Form Tidak Lengkap", "Password lama dan username baru wajib diisi.");
            return;
        }
        if (!currentUser.checkPassword(oldPassword)) {
            AppDialog.error(this, "Autentikasi Gagal", "Password lama yang Anda masukkan salah.");
            return;
        }
        if (newUsername.equals(currentUser.getUsername())) {
            AppDialog.warning(this, "Tidak Ada Perubahan", "Username baru sama dengan username saat ini.");
            return;
        }
        userManager.editUser(currentUser, currentUser.getUserId(), newUsername, currentUser.getPassword());
        currentUser.setUsername(newUsername);
        currentUsernameField.setText(newUsername);
        oldPasswordForUsernameField.setText("");
        newUsernameField.setText("");
        AppDialog.success(this, "Berhasil", "Username admin berhasil diubah.");
    }

    private void updatePassword() {
        if (currentUser == null) {
            AppDialog.error(this, "Gagal", "Data admin tidak ditemukan.");
            return;
        }
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AppDialog.warning(this, "Form Tidak Lengkap", "Semua kolom password wajib diisi.");
            return;
        }
        if (!currentUser.checkPassword(oldPassword)) {
            AppDialog.error(this, "Autentikasi Gagal", "Password lama yang Anda masukkan salah.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            AppDialog.error(this, "Konfirmasi Gagal", "Password baru dan konfirmasi tidak cocok.");
            return;
        }
        if (newPassword.length() < 8) {
            AppDialog.warning(this, "Password Lemah", "Password baru minimal 8 karakter.");
            return;
        }
        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            AppDialog.warning(this, "Password Lemah", "Password baru harus mengandung minimal 1 karakter spesial.");
            return;
        }
        userManager.editUser(currentUser, currentUser.getUserId(), currentUser.getUsername(), newPassword);
        currentUser.setPassword(newPassword);
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        AppDialog.success(this, "Berhasil", "Password admin berhasil diubah.");
    }

    // -------------------------------------------------------------------------
    // Text Helpers
    // -------------------------------------------------------------------------

    private String displayName() {
        return currentUser == null || currentUser.getName() == null || currentUser.getName().isBlank()
                ? "Admin"
                : currentUser.getName();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }
}



