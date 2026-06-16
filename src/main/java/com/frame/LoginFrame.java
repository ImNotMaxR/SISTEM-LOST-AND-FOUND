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
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        initComponents();
    }

    // =========================
    // UI Initialization
    // =========================

    private void initComponents() {
        configureFrame();
        setContentPane(createMainPanel());

        pack();
        setLocationRelativeTo(null);
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login - Sistem Lost & Found");
        setMinimumSize(new Dimension(920, 560));
        setResizable(false);
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(createBrandSection(), BorderLayout.WEST);
        root.add(createLoginSection(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createBrandSection() {
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
        wrapper.add(createBrandCard(), gbc);

        return wrapper;
    }

    private JPanel createBrandCard() {
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
        subtitle.setFont(new Font("Poppins", Font.BOLD, 27));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(subtitle, gbc);

        JLabel caption = new JLabel("<html>Laporkan, cek, dan kelola data lost & found dalam satu sistem.</html>");
        caption.setForeground(new Color(229, 246, 255));
        caption.setFont(new Font("Poppins", Font.PLAIN, 15));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(caption, gbc);

        return panel;
    }

    private JPanel createLoginSection() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(68, 52, 54, 64));

        JPanel formPanel = createLoginForm();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapper.add(formPanel, gbc);

        return wrapper;
    }

    private JPanel createLoginForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(22, 34, 22, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel logoSmall = new JLabel("Lost & Found");
        logoSmall.setForeground(PRIMARY_DARK);
        logoSmall.setFont(new Font("Poppins", Font.BOLD, 18));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(logoSmall, gbc);

        JLabel title = new JLabel("Login Sistem");
        title.setForeground(TEXT_DARK);
        title.setFont(new Font("Poppins", Font.BOLD, 42));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(title, gbc);

        JLabel subtitle = new JLabel("Silakan masuk menggunakan akun yang sudah terdaftar.");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Poppins", Font.PLAIN, 17));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 38, 0);
        form.add(subtitle, gbc);

        JLabel usernameLabel = createFieldLabel("Username SSO");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(usernameLabel, gbc);

        usernameField = createUsernameField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(usernameField, gbc);

        JLabel passwordLabel = createFieldLabel("Password");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(passwordLabel, gbc);

        passwordField = createPasswordField();
        passwordField.addActionListener(this::handleLoginAction);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 40, 0);
        form.add(passwordField, gbc);

        loginButton = createLoginButton();
        loginButton.addActionListener(this::handleLoginAction);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 28, 0);
        form.add(loginButton, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(createDivider(), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(createLoginHint(), gbc);

        return form;
    }

    // =========================
    // UI Component Factories
    // =========================

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(51, 65, 85));
        label.setFont(new Font("Poppins", Font.BOLD, 13));
        return label;
    }

    private JTextField createUsernameField() {
        JTextField field = new RoundedTextField(18, "Username SSO *tanpa @student.telkomuniversity.ac.id");
        applyInputStyle(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new RoundedPasswordField(18, "Password SSO");
        applyInputStyle(field);
        return field;
    }

    private void applyInputStyle(JTextField field) {
        field.setPreferredSize(new Dimension(360, 44));
        field.setFont(new Font("Poppins", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(248, 252, 255));
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(183, 213, 232), 18, 1),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        field.setOpaque(false);
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
            logo.setFont(new Font("Poppins", Font.BOLD, 28));
        }

        logo.setPreferredSize(new Dimension(width, height));
        return logo;
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
        button.setFont(new Font("Poppins", Font.BOLD, 15));
        return button;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(new Color(203, 213, 225));
        divider.setPreferredSize(new Dimension(360, 1));
        return divider;
    }

    private JLabel createLoginHint() {
        JLabel hint = new JLabel("Pastikan database sudah berjalan sebelum login.");
        hint.setForeground(TEXT_MUTED);
        hint.setFont(new Font("Poppins", Font.PLAIN, 13));
        return hint;
    }

    // =========================
    // Login Actions
    // =========================

    private void handleLoginAction(ActionEvent evt) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            AppDialog.warning(rootPane, "Login Belum Lengkap", "Username dan Password Wajib diisi.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Memproses...");

        try {
            User user = AuthService.login(username, password);

            if (user != null) {
                openDashboard(user);
            } else {
                AppDialog.error(rootPane, "Login Gagal", AuthService.getLoginError());
            }
        } finally {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        }
    }

    private void openDashboard(User user) {
        AppDialog.success(rootPane, "Selamat Datang", "Halo, " + user.getName() + ". Login berhasil.");

        if (user instanceof Admin) {
            ReportManager rm = new ReportManager();
            DashboardAdmin dAdmin = new DashboardAdmin(rm);
            dAdmin.setVisible(true);
        } else if (user instanceof Security) {
            DashboardSecurity dSecurity = new DashboardSecurity();
            dSecurity.setVisible(true);
        } else {
            DashboardUser dUser = new DashboardUser(user);
            dUser.setVisible(true);
        }

        dispose();
    }

    // =========================
    // Application Entry Point
    // =========================

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

    // =========================
    // Custom UI Components
    // =========================

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

    private static class RoundedTextField extends JTextField {

        private final int radius;
        private final String placeholder;

        RoundedTextField(int radius, String placeholder) {
            this.radius = radius;
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
            paintPlaceholder(g);
        }

        private void paintPlaceholder(Graphics g) {
            if (getText() != null && !getText().isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(new Color(148, 163, 184));
            Insets insets = getInsets();
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }

    private static class RoundedPasswordField extends JPasswordField {

        private final int radius;
        private final String placeholder;

        RoundedPasswordField(int radius, String placeholder) {
            this.radius = radius;
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
            paintPlaceholder(g);
        }

        private void paintPlaceholder(Graphics g) {
            if (getPassword().length > 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(new Color(148, 163, 184));
            Insets insets = getInsets();
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
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
