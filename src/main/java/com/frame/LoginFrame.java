package com.frame;

import com.frame.dashboard.DashboardAdmin;
import com.frame.dashboard.DashboardSecurity;
import com.frame.dashboard.DashboardUser;
import com.managers.ReportManager;
import com.model.Admin;
import com.model.Security;
import com.model.User;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class LoginFrame extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login - Sistem Lost & Found");
        setMinimumSize(new Dimension(860, 520));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(244, 247, 251));
        setContentPane(root);

        root.add(createBrandPanel(), BorderLayout.WEST);
        root.add(createFormPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createBrandPanel() {
        GradientPanel panel = new GradientPanel(new Color(22, 93, 118), new Color(18, 140, 126));
        panel.setPreferredSize(new Dimension(360, 520));
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(46, 42, 46, 42));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel badge = new JLabel("LOST & FOUND");
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 255, 255, 38));
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 26, 0);
        panel.add(badge, gbc);

        JLabel title = new JLabel("<html>Sistem<br>Lost & Found</html>");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("<html>Kelola laporan barang hilang dan ditemukan dengan lebih rapi.</html>");
        subtitle.setForeground(new Color(223, 250, 247));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 52, 0);
        panel.add(subtitle, gbc);

        JPanel infoBox = new JPanel(new GridBagLayout());
        infoBox.setOpaque(false);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        addInfoLine(infoBox, "Data laporan lebih terpusat", 0);
        addInfoLine(infoBox, "Akses sesuai role pengguna", 1);
        addInfoLine(infoBox, "Proses validasi lebih mudah", 2);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(infoBox, gbc);

        return panel;
    }

    private void addInfoLine(JPanel panel, String text, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(row == 0 ? 0 : 12, 0, 0, 0);

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(44, 62, 44, 62));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 232, 240), 1),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Masuk Akun");
        title.setForeground(new Color(31, 41, 55));
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(title, gbc);

        JLabel subtitle = new JLabel("Gunakan username dan password yang terdaftar.");
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        form.add(subtitle, gbc);

        JLabel usernameLabel = createFieldLabel("Username");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(usernameLabel, gbc);

        txtUsername = createTextField();
        txtUsername.setToolTipText("Masukkan username");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(txtUsername, gbc);

        JLabel passwordLabel = createFieldLabel("Password");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(passwordLabel, gbc);

        txtPassword = createPasswordField();
        txtPassword.setToolTipText("Masukkan password");
        txtPassword.addActionListener(this::btnLoginActionPerformed);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 28, 0);
        form.add(txtPassword, gbc);

        btnLogin = createLoginButton();
        btnLogin.addActionListener(this::btnLoginActionPerformed);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(btnLogin, gbc);

        JLabel hint = new JLabel("Pastikan database sudah berjalan sebelum login.");
        hint.setForeground(new Color(100, 116, 139));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(hint, gbc);

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.gridx = 0;
        wrapperGbc.gridy = 0;
        wrapperGbc.fill = GridBagConstraints.BOTH;
        wrapperGbc.weightx = 1;
        wrapperGbc.weighty = 1;
        wrapper.add(form, wrapperGbc);

        return wrapper;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(51, 65, 85));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setPreferredSize(new Dimension(360, 44));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(31, 41, 55));
        field.setBackground(new Color(248, 250, 252));
        field.setCaretColor(new Color(18, 140, 126));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Login");
        button.setPreferredSize(new Dimension(360, 46));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(new Color(18, 140, 126));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return button;
    }

    private void btnLoginActionPerformed(ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    rootPane,
                    "Username dan password wajib diisi.",
                    "Login Belum Lengkap",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");

        try {
            User user = AuthService.login(username, password);

            if (user != null) {
                openDashboard(user);
            } else {
                JOptionPane.showMessageDialog(
                        rootPane,
                        AuthService.getLoginError(),
                        "Login Gagal",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }

    private void openDashboard(User user) {
        JOptionPane.showMessageDialog(rootPane, "Selamat Datang " + user.getName());

        if (user instanceof Admin) {
            ReportManager rm = new ReportManager();
            DashboardAdmin dAdmin = new DashboardAdmin(rm);
            dAdmin.setVisible(true);
        } else if (user instanceof Security) {
            DashboardSecurity dSecurity = new DashboardSecurity();
            dSecurity.setVisible(true);
        } else {
            DashboardUser dUser = new DashboardUser();
            dUser.setVisible(true);
        }

        dispose();
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static class GradientPanel extends JPanel {

        private final Color startColor;
        private final Color endColor;

        GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new java.awt.GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
