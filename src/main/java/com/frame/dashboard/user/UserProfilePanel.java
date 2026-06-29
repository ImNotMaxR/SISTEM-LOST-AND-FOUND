package com.frame.dashboard.user;

import com.frame.AppDialog;
import com.managers.UserManager;
import com.model.Dosen;
import com.model.Mahasiswa;
import com.model.Staff;
import com.model.User;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import java.awt.Image;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class UserProfilePanel extends JPanel {

    private static final String TITLE = "Edit Profil Saya";
    private static final String SUBTITLE = "Sistem Informasi Lost & Found Kampus";

    private User currentUser;
    private final UserManager userManager;

    // Form Fields
    private JTextField usernameSaatIniField;
    private JPasswordField passLamaUserField;
    private JTextField usernameBaruField;
    
    private JPasswordField passLamaPassField;
    private JPasswordField passBaruField;
    private JPasswordField passBaruConfirmField;

    public UserProfilePanel(User user) {
        this.currentUser = user;
        this.userManager = new UserManager();
        configurePanel();
        refreshContent();
    }

    private void configurePanel() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
    }

    private void refreshContent() {
        removeAll();
        JScrollPane scrollPane = UserDashboardComponents.scroll(createContent());
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.revalidate();
            scrollPane.doLayout();
            UserDashboardComponents.resetScrollPosition(scrollPane);
        });
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        
        // Header Texts
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(UserDashboardComponents.section(TITLE, SUBTITLE), gbc);

        // Profile Card (Blue & White)
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createProfileHeaderCard(), gbc);

        // Action Cards (Edit Username & Pass)
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(createActionCardsPanel(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        content.add(spacer, gbc);

        return content;
    }

    private JPanel createProfileHeaderCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 24);
        card.setLayout(new BorderLayout());
        card.setBorder(new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 24, 1));
        
        // Top Blue Section
        JPanel blueTop = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(42, 142, 222), getWidth(), getHeight(), new Color(28, 118, 201)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 24, 24, 24);
                g2.fillRect(0, 24, getWidth(), getHeight() - 24);
                g2.dispose();
            }
        };
        blueTop.setOpaque(false);
        blueTop.setPreferredSize(new Dimension(720, 200));
        blueTop.setMinimumSize(new Dimension(320, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 0, 10, 0);
        
        // Avatar Icon
        ImageIcon avatarIcon = null;
        try {
            java.net.URL url = getClass().getResource("/assets/PNG/64x64/icon_person.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                avatarIcon = new ImageIcon(img);
            }
        } catch (Exception e) {}
        JLabel lblAvatar = new JLabel(avatarIcon);
        
        // Wrap avatar in a circular background panel
        JPanel avatarContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Circle matching the reference image
                g2.setColor(new Color(255, 255, 255, 80)); 
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarContainer.setOpaque(false);
        avatarContainer.setPreferredSize(new Dimension(90, 90));
        avatarContainer.add(lblAvatar, BorderLayout.CENTER);
        
        blueTop.add(avatarContainer, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lblName = new JLabel(currentUser != null ? currentUser.getName() : "Pengguna");
        lblName.setFont(new Font("Poppins", Font.BOLD, 22));
        lblName.setForeground(Color.WHITE);
        blueTop.add(lblName, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        String roleLabel = "Pengguna";
        if (currentUser instanceof Mahasiswa) {
            roleLabel = "Civitas Telkom University • Akun Mahasiswa";
        } else if (currentUser instanceof Dosen) {
            roleLabel = "Tenaga Pendidik Telkom University • Akun Dosen";
        } else if (currentUser instanceof Staff) {
            roleLabel = "Tenaga Kependidikan Telkom University • Akun Staff";
        } else if (currentUser != null) {
            String roleStr = currentUser.getRole().name();
            roleLabel = "Telkom University • Akun " + roleStr.substring(0, 1).toUpperCase() + roleStr.substring(1).toLowerCase();
        }
        
        JLabel lblRole = new JLabel(roleLabel);
        lblRole.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblRole.setForeground(new Color(255, 255, 255, 200));
        blueTop.add(lblRole, gbc);

        // Bottom White Section (Attributes)
        JPanel whiteBottom = new JPanel(new GridBagLayout());
        whiteBottom.setOpaque(false);
        whiteBottom.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridy = 0;
        wgbc.fill = GridBagConstraints.HORIZONTAL;
        wgbc.weightx = 1;
        wgbc.insets = new Insets(0, 0, 20, 0);
        
        JPanel gridPanel = UserDashboardComponents.responsiveGrid(160);
        gridPanel.setOpaque(false);

        // Create specific stat boxes based on Role
        gridPanel.add(createStatBox("NAME", safe(currentUser.getName())));
        
        if (currentUser instanceof Mahasiswa) {
            Mahasiswa m = (Mahasiswa) currentUser;
            gridPanel.add(createStatBox("NIM", safe(m.getNim())));
            gridPanel.add(createStatBox("FAKULTAS", safe(m.getFakultas())));
            gridPanel.add(createStatBox("KELAS", safe(m.getKelas())));
        } else if (currentUser instanceof Dosen) {
            Dosen d = (Dosen) currentUser;
            gridPanel.add(createStatBox("NIP", safe(d.getNip())));
            gridPanel.add(createStatBox("BIDANG", safe(d.getBidang())));
        } else if (currentUser instanceof Staff) {
            Staff s = (Staff) currentUser;
            gridPanel.add(createStatBox("STAFF ID", safe(s.getStaffID())));
            gridPanel.add(createStatBox("BAGIAN", safe(s.getBagian())));
        } else {
            gridPanel.add(createStatBox("USER ID", safe(currentUser.getUserId())));
        }

        whiteBottom.add(gridPanel, wgbc);

        wgbc.gridy = 1;
        JLabel lblNote = new JLabel("Data Akademik Bersifat Tetap Dan Tidak Dapat Diubah Oleh User.");
        lblNote.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNote.setForeground(new Color(150, 150, 150));
        lblNote.setHorizontalAlignment(JLabel.CENTER);
        whiteBottom.add(lblNote, wgbc);

        card.add(blueTop, BorderLayout.NORTH);
        card.add(whiteBottom, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatBox(String title, String value) {
        UserDashboardComponents.RoundedPanel box = new UserDashboardComponents.RoundedPanel(new Color(248, 250, 252), 16);
        box.setBorder(new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1));
        box.setLayout(new GridBagLayout());
        box.setPreferredSize(new Dimension(160, 75));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 10));
        lblTitle.setForeground(new Color(130, 140, 150));
        box.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        String safeValue = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        JLabel lblVal = new JLabel("<html><div style='text-align: center; width: 130px;'>" + safeValue + "</div></html>");
        lblVal.setHorizontalAlignment(JLabel.CENTER);
        lblVal.setFont(new Font("Poppins", Font.BOLD, 13));
        lblVal.setForeground(UserDashboardComponents.TEXT_DARK);
        box.add(lblVal, gbc);

        return box;
    }

    private JPanel createActionCardsPanel() {
        JPanel panel = UserDashboardComponents.responsiveGrid(360);
        panel.add(createEditUsernameCard());
        panel.add(createEditPasswordCard());
        return panel;
    }

    private JPanel createEditUsernameCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 24);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 24, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Header
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(createCardHeader("Edit Username", "Konfirmasi Password Lama Sebelum Username Diganti.", 1), gbc);

        // Form Fields
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Username Saat Ini", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        usernameSaatIniField = createTextField("");
        usernameSaatIniField.setText(currentUser.getUsername());
        usernameSaatIniField.setEditable(false);
        usernameSaatIniField.setBackground(new Color(245, 245, 245));
        card.add(usernameSaatIniField, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Password Lama", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        passLamaUserField = createPasswordField("Masukkan Password Lama");
        card.add(passLamaUserField, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Username Baru", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        usernameBaruField = createTextField("Isi Username Baru");
        card.add(usernameBaruField, gbc);

        // Submit Button
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnSubmit = createSubmitButton("Simpan Username");
        btnSubmit.addActionListener(e -> updateUsername());
        card.add(btnSubmit, gbc);

        return card;
    }

    private JPanel createEditPasswordCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 24);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 24, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Header
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(createCardHeader("Ganti Password", "Gunakan Password Lama Untuk Validasi Keamanan Akun.", 2), gbc);

        // Form Fields
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Password Lama", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        passLamaPassField = createPasswordField("Masukkan Password Lama");
        card.add(passLamaPassField, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Password Baru", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        passBaruField = createPasswordField("Isi Password Baru");
        card.add(passBaruField, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(UserDashboardComponents.label("Konfirmasi Password Baru", 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        passBaruConfirmField = createPasswordField("Ulangi Password Baru");
        card.add(passBaruConfirmField, gbc);

        // Submit Button
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnSubmit = createSubmitButton("Simpan Password");
        btnSubmit.addActionListener(e -> updatePassword());
        card.add(btnSubmit, gbc);

        return card;
    }

    private JPanel createCardHeader(String title, String subtitle, int iconType) {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 15);
        
        String iconPath = (iconType == 1) ? "/assets/PNG/64x64/icon_pencil.png" : "/assets/PNG/64x64/icon_padlock.png";
        ImageIcon icon = null;
        try {
            java.net.URL url = getClass().getResource(iconPath);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            }
        } catch (Exception e) {}
        JLabel lblIcon = new JLabel(icon);
        
        // Container block matching reference image
        JPanel iconContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 247, 255)); // Light blue matching reference
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(40, 40));
        iconContainer.setLayout(new GridBagLayout());
        iconContainer.add(lblIcon);
        
        header.add(iconContainer, gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitle.setForeground(UserDashboardComponents.TEXT_DARK);
        header.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSub.setForeground(new Color(130, 140, 150));
        header.add(lblSub, gbc);

        return header;
    }

    // ==========================================
    // UI Helpers
    // ==========================================

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        field.setPreferredSize(new Dimension(0, 44));
        field.setMinimumSize(new Dimension(0, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setForeground(UserDashboardComponents.TEXT_DARK);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        field.setPreferredSize(new Dimension(0, 44));
        field.setMinimumSize(new Dimension(0, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setForeground(UserDashboardComponents.TEXT_DARK);
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JButton createSubmitButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? UserDashboardComponents.PRIMARY.darker() : UserDashboardComponents.PRIMARY;
                if (getModel().isRollover() && !getModel().isPressed()) {
                    bg = bg.brighter();
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        btn.setPreferredSize(new Dimension(0, 44));
        btn.setMinimumSize(new Dimension(0, 44));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    // ==========================================
    // Update Logic
    // ==========================================

    private void updateUsername() {
        String oldPass = new String(passLamaUserField.getPassword());
        String newUsername = usernameBaruField.getText().trim();

        try {
            userManager.editUser(currentUser, oldPass, newUsername);
            currentUser.setUsername(newUsername);
            
            AppDialog.success(this, "Berhasil", "Username berhasil diubah!");
            
            // Clear fields
            passLamaUserField.setText("");
            usernameBaruField.setText("");
            usernameSaatIniField.setText(newUsername);
        } catch (com.exception.ValidationException e) {
            AppDialog.error(this, "Gagal", e.getMessage());
        }
    }

    private void updatePassword() {
        String oldPass = new String(passLamaPassField.getPassword());
        String newPass = new String(passBaruField.getPassword());
        String confirmPass = new String(passBaruConfirmField.getPassword());

        try {
            userManager.editUser(currentUser, oldPass, newPass, confirmPass);
            currentUser.setPassword(newPass);

            AppDialog.success(this, "Berhasil", "Password berhasil diubah secara permanen!");
            
            // Clear fields
            passLamaPassField.setText("");
            passBaruField.setText("");
            passBaruConfirmField.setText("");
        } catch (com.exception.ValidationException e) {
            AppDialog.error(this, "Gagal", e.getMessage());
        }
    }
}

