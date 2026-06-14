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
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

    private static final Color PRIMARY_DARK = new Color(44, 94, 173);
    private static final Color PRIMARY = new Color(21, 145, 220);
    private static final Color PRIMARY_LIGHT = new Color(75, 184, 250);
    private static final Color SURFACE_LIGHT = new Color(189, 227, 242);
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login - Sistem Lost & Found");
        setMinimumSize(new Dimension(920, 560));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(createBrandWrapper(), BorderLayout.WEST);
        root.add(createFormPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createBrandWrapper() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        wrapper.setPreferredSize(new Dimension(470, 560));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapper.add(createBrandPanel(), gbc);

        return wrapper;
    }

    private JPanel createBrandPanel() {
        RoundedGradientPanel panel = new RoundedGradientPanel(PRIMARY_DARK, PRIMARY_LIGHT, 28);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(44, 46, 44, 46));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel logo = createLogoLabel(330, 180);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 92, 0);
        panel.add(logo, gbc);

        JLabel subtitle = new JLabel("<html>Temukan kembali barang hilang dengan lebih mudah.</html>");
        subtitle.setForeground(new Color(229, 246, 255));
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 27));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(subtitle, gbc);

        JLabel caption = new JLabel("<html>Laporkan, cek, dan kelola data lost & found dalam satu sistem.</html>");
        caption.setForeground(new Color(229, 246, 255));
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(caption, gbc);

        return panel;
    }

    private JLabel createLogoLabel(int width, int height) {
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/assets/icon-lost-found.png");

        if (logoUrl != null) {
            ImageIcon originalIcon = new ImageIcon(logoUrl);
            double imageRatio = (double) originalIcon.getIconWidth() / originalIcon.getIconHeight();
            int scaledWidth = width;
            int scaledHeight = (int) Math.round(width / imageRatio);

            if (scaledHeight > height) {
                scaledHeight = height;
                scaledWidth = (int) Math.round(height * imageRatio);
            }

            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    scaledWidth,
                    scaledHeight,
                    Image.SCALE_SMOOTH
            );
            logo.setIcon(new ImageIcon(scaledImage));
        } else {
            logo.setText("Lost & Found");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        }

        logo.setPreferredSize(new Dimension(width, height));
        return logo;
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
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(68, 52, 54, 64));

        JPanel form = new JPanel(new GridBagLayout());
        form.setLayout(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(22, 34, 22, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel logoSmall = new JLabel("Lost & Found");
        logoSmall.setForeground(PRIMARY_DARK);
        logoSmall.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(logoSmall, gbc);

        JLabel title = new JLabel("Login Sistem");
        title.setForeground(TEXT_DARK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(title, gbc);

        JLabel subtitle = new JLabel("Silakan masuk menggunakan akun yang sudah terdaftar.");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 38, 0);
        form.add(subtitle, gbc);

        JLabel usernameLabel = createFieldLabel("Username");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(usernameLabel, gbc);

        txtUsername = createTextField();
        txtUsername.setToolTipText("Masukkan username");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(txtUsername, gbc);

        JLabel passwordLabel = createFieldLabel("Password");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(passwordLabel, gbc);

        txtPassword = createPasswordField();
        txtPassword.setToolTipText("Masukkan password");
        txtPassword.addActionListener(this::btnLoginActionPerformed);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 40, 0);
        form.add(txtPassword, gbc);

        btnLogin = createLoginButton();
        btnLogin.addActionListener(this::btnLoginActionPerformed);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 28, 0);
        form.add(btnLogin, gbc);

        JPanel line = new JPanel();
        line.setBackground(new Color(203, 213, 225));
        line.setPreferredSize(new Dimension(360, 1));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(line, gbc);

        JLabel hint = new JLabel("Pastikan database sudah berjalan sebelum login.");
        hint.setForeground(TEXT_MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 9;
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
        JTextField field = new RoundedTextField(18);
        styleInput(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new RoundedPasswordField(18);
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setPreferredSize(new Dimension(360, 44));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(248, 252, 255));
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(183, 213, 232), 18, 1),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        field.setOpaque(false);
    }

    private JButton createLoginButton() {
        JButton button = new RoundedButton("Login", 22);
        button.setPreferredSize(new Dimension(360, 46));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(PRIMARY_DARK);
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

    private static class RoundedGradientPanel extends JPanel {

        private final Color startColor;
        private final Color endColor;
        private final int radius;

        RoundedGradientPanel(Color startColor, Color endColor, int radius) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new java.awt.GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ShadowPanel extends JPanel {

        private final Color backgroundColor;
        private final Color shadowColor;
        private final int radius;
        private final int shadowSize = 12;

        ShadowPanel(Color backgroundColor, int radius, Color shadowColor) {
            this.backgroundColor = backgroundColor;
            this.radius = radius;
            this.shadowColor = shadowColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = shadowSize; i > 0; i--) {
                int alpha = Math.max(2, 28 - (i * 2));
                g2.setColor(new Color(
                        shadowColor.getRed(),
                        shadowColor.getGreen(),
                        shadowColor.getBlue(),
                        alpha
                ));
                g2.fillRoundRect(
                        shadowSize - i,
                        shadowSize - i + 4,
                        getWidth() - ((shadowSize - i) * 2) - shadowSize,
                        getHeight() - ((shadowSize - i) * 2) - shadowSize,
                        radius + i,
                        radius + i
                );
            }

            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    shadowSize,
                    shadowSize,
                    getWidth() - (shadowSize * 2),
                    getHeight() - (shadowSize * 2),
                    radius,
                    radius
            );
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedPanel extends JPanel {

        private final Color backgroundColor;
        private final int radius;

        RoundedPanel(Color backgroundColor, int radius) {
            this.backgroundColor = backgroundColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTextField extends JTextField {

        private final int radius;

        RoundedTextField(int radius) {
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedPasswordField extends JPasswordField {

        private final int radius;

        RoundedPasswordField(int radius) {
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {

        private final int radius;

        RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? PRIMARY : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedLineBorder extends javax.swing.border.AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }
}
