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
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    private static final String FRAME_TITLE = "Login - Sistem Lost & Found";
    private static final String BRAND_LOGO_PATH = "/assets/icon-lost-found.png";
    private static final String FONT_FAMILY = "Poppins";

    private static final Color PRIMARY_DARK = new Color(44, 94, 173);
    private static final Color PRIMARY = new Color(21, 145, 220);
    private static final Color PRIMARY_LIGHT = new Color(75, 184, 250);
    private static final Color TEXT_DARK = new Color(31, 41, 55);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color FIELD_BACKGROUND = new Color(248, 252, 255);
    private static final Color FIELD_BORDER = new Color(183, 213, 232);
    private static final Color DIVIDER = new Color(203, 213, 225);
    private static final Color BRAND_TEXT = new Color(229, 246, 255);
    private static final Color PLACEHOLDER = new Color(148, 163, 184);

    private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(880, 560);
    private static final Dimension MAXIMUM_INITIAL_SIZE = new Dimension(1160, 720);
    private static final Dimension BRAND_SECTION_SIZE = new Dimension(470, 560);
    private static final Dimension FORM_FIELD_SIZE = new Dimension(360, 44);
    private static final Dimension COMPACT_FIELD_SIZE = new Dimension(280, 44);
    private static final Dimension LOGIN_BUTTON_SIZE = new Dimension(360, 46);
    private static final Dimension COMPACT_BUTTON_SIZE = new Dimension(280, 46);

    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JButton loginButton;

    public LoginFrame() {
        initializeFrame();
    }

    // =========================
    // Frame Setup
    // =========================

    private void initializeFrame() {
        configureWindow();
        setContentPane(createPageLayout());
        pack();
        setLocationRelativeTo(null);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(FRAME_TITLE);
        setMinimumSize(MINIMUM_WINDOW_SIZE);
        setPreferredSize(calculateInitialWindowSize());
        setResizable(true);
    }

    private Dimension calculateInitialWindowSize() {
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width = Math.min(MAXIMUM_INITIAL_SIZE.width, Math.max(MINIMUM_WINDOW_SIZE.width, screenBounds.width - 180));
        int height = Math.min(MAXIMUM_INITIAL_SIZE.height, Math.max(MINIMUM_WINDOW_SIZE.height, screenBounds.height - 140));
        return new Dimension(width, height);
    }

    // =========================
    // Page Layout
    // =========================

    private JPanel createPageLayout() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.add(createBrandSection(), BorderLayout.WEST);
        page.add(createLoginSection(), BorderLayout.CENTER);
        return page;
    }

    private JPanel createBrandSection() {
        JPanel section = new JPanel(new GridBagLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        section.setPreferredSize(BRAND_SECTION_SIZE);
        section.setMinimumSize(new Dimension(380, 0));

        GridBagConstraints gbc = fillBothConstraints();
        section.add(createBrandCard(), gbc);
        return section;
    }

    private JPanel createLoginSection() {
        JPanel section = new JPanel(new GridBagLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createEmptyBorder(52, 52, 48, 64));

        GridBagConstraints gbc = fillBothConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        section.add(createLoginForm(), gbc);
        return section;
    }

    // =========================
    // Brand UI
    // =========================

    private JPanel createBrandCard() {
        RoundedGradientPanel card = new RoundedGradientPanel(PRIMARY_DARK, PRIMARY_LIGHT, 28);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(44, 46, 44, 46));

        GridBagConstraints gbc = horizontalConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 92, 0);
        card.add(createBrandLogo(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(createBrandHeadline(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(createBrandCaption(), gbc);

        return card;
    }

    private JLabel createBrandLogo() {
        return createScaledLogo(330, 180, true);
    }

    private JLabel createBrandHeadline() {
        return createLabel("<html>Temukan kembali barang hilang dengan lebih mudah.</html>", 27, Font.BOLD, BRAND_TEXT);
    }

    private JLabel createBrandCaption() {
        return createLabel("<html>Laporkan, cek, dan kelola data lost & found dalam satu sistem.</html>", 15, Font.PLAIN, BRAND_TEXT);
    }

    // =========================
    // Login Form UI
    // =========================

    private JPanel createLoginForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(22, 34, 22, 34));
        form.setMinimumSize(new Dimension(340, 420));

        GridBagConstraints gbc = horizontalConstraints();
        int row = 0;

        row = addLoginHeader(form, gbc, row);
        row = addCredentialFields(form, gbc, row);
        row = addSubmitArea(form, gbc, row);
        addFlexibleSpacer(form, gbc, row);

        return form;
    }

    private int addLoginHeader(JPanel form, GridBagConstraints gbc, int row) {
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(createProductNameLabel(), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(createLoginTitleLabel(), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 38, 0);
        form.add(createLoginSubtitleLabel(), gbc);
        return row;
    }

    private int addCredentialFields(JPanel form, GridBagConstraints gbc, int row) {
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(createFieldLabel("Username SSO"), gbc);

        usernameInput = createUsernameInput();
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(usernameInput, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(createFieldLabel("Password"), gbc);

        passwordInput = createPasswordInput();
        passwordInput.addActionListener(event -> handleLoginAction());
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 40, 0);
        form.add(passwordInput, gbc);
        return row;
    }

    private int addSubmitArea(JPanel form, GridBagConstraints gbc, int row) {
        loginButton = createLoginButton();
        loginButton.addActionListener(event -> handleLoginAction());
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 28, 0);
        form.add(loginButton, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(createDivider(), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(createLoginHint(), gbc);
        return row;
    }

    private void addFlexibleSpacer(JPanel form, GridBagConstraints gbc, int row) {
        JPanel spacer = new JPanel();
        spacer.setBackground(Color.WHITE);

        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        form.add(spacer, gbc);
    }

    private JLabel createProductNameLabel() {
        return createLabel("Lost & Found", 18, Font.BOLD, PRIMARY_DARK);
    }

    private JLabel createLoginTitleLabel() {
        return createLabel("Login Sistem", 42, Font.BOLD, TEXT_DARK);
    }

    private JLabel createLoginSubtitleLabel() {
        return createLabel("Silakan masuk menggunakan akun yang sudah terdaftar.", 16, Font.PLAIN, TEXT_MUTED);
    }

    // =========================
    // Component Factories
    // =========================

    private JLabel createLabel(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(FONT_FAMILY, style, size));
        return label;
    }

    private JLabel createFieldLabel(String text) {
        return createLabel(text, 13, Font.BOLD, new Color(51, 65, 85));
    }

    private JTextField createUsernameInput() {
        JTextField field = new RoundedTextField(18, "Username SSO *tanpa @student.telkomuniversity.ac.id");
        applyInputStyle(field);
        return field;
    }

    private JPasswordField createPasswordInput() {
        JPasswordField field = new RoundedPasswordField(18, "Password SSO");
        applyInputStyle(field);
        return field;
    }

    private JButton createLoginButton() {
        JButton button = new RoundedButton("Login", 22);
        button.setPreferredSize(LOGIN_BUTTON_SIZE);
        button.setMinimumSize(COMPACT_BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(PRIMARY_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        return button;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(DIVIDER);
        divider.setPreferredSize(new Dimension(FORM_FIELD_SIZE.width, 1));
        divider.setMinimumSize(new Dimension(COMPACT_FIELD_SIZE.width, 1));
        return divider;
    }

    private JLabel createLoginHint() {
        return createLabel("Pastikan database sudah berjalan sebelum login.", 13, Font.PLAIN, TEXT_MUTED);
    }

    private void applyInputStyle(JTextField field) {
        field.setPreferredSize(FORM_FIELD_SIZE);
        field.setMinimumSize(COMPACT_FIELD_SIZE);
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(FIELD_BACKGROUND);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 18, 1),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        field.setOpaque(false);
    }

    private JLabel createScaledLogo(int maxWidth, int maxHeight, boolean whiteFallback) {
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource(BRAND_LOGO_PATH);

        if (logoUrl != null) {
            ImageIcon sourceIcon = new ImageIcon(logoUrl);
            Dimension size = calculateScaledImageSize(sourceIcon, maxWidth, maxHeight);
            Image scaledImage = sourceIcon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaledImage));
        } else {
            logo.setText("Lost & Found");
            logo.setForeground(whiteFallback ? Color.WHITE : PRIMARY_DARK);
            logo.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
        }

        logo.setPreferredSize(new Dimension(maxWidth, maxHeight));
        return logo;
    }

    private Dimension calculateScaledImageSize(ImageIcon sourceIcon, int maxWidth, int maxHeight) {
        double imageRatio = (double) sourceIcon.getIconWidth() / sourceIcon.getIconHeight();
        int width = maxWidth;
        int height = (int) Math.round(width / imageRatio);

        if (height > maxHeight) {
            height = maxHeight;
            width = (int) Math.round(height * imageRatio);
        }

        return new Dimension(width, height);
    }

    // =========================
    // Login Actions
    // =========================

    private void handleLoginAction() {
        LoginCredentials credentials = readCredentials();

        setLoginLoading(true);
        try {
            User user = AuthService.login(credentials.username, credentials.password);
            handleLoginResult(user);
        } catch (com.exception.ValidationException e) {
            AppDialog.error(rootPane, "Login Gagal", e.getMessage());
        } finally {
            setLoginLoading(false);
        }
    }

    private LoginCredentials readCredentials() {
        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());
        return new LoginCredentials(username, password);
    }

    private void setLoginLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "Memproses..." : "Login");
    }

    private void handleLoginResult(User user) {
        if (user == null) {
            return;
        }

        AppDialog.success(rootPane, "Selamat Datang", "Halo, " + user.getName() + ". Login berhasil.");
        openDashboard(user);
        dispose();
    }

    // =========================
    // Navigation
    // =========================

    private void openDashboard(User user) {
        JFrame dashboard = createDashboardFor(user);
        dashboard.setVisible(true);
    }

    private JFrame createDashboardFor(User user) {
        if (user instanceof Admin) {
            return new DashboardAdmin(new ReportManager());
        }
        if (user instanceof Security) {
            return new DashboardSecurity(user);
        }
        return new DashboardUser(user);
    }

    // =========================
    // Constraints Helpers
    // =========================

    private GridBagConstraints fillBothConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private GridBagConstraints horizontalConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    // =========================
    // Application Entry Point
    // =========================

    public static void main(String[] args) {
        applyNimbusLookAndFeel();
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void applyNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException exception) {
            LOGGER.log(java.util.logging.Level.SEVERE, null, exception);
        }
    }

    // =========================
    // Small Data Objects
    // =========================

    private static class LoginCredentials {

        private final String username;
        private final String password;

        LoginCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        boolean isComplete() {
            return !username.isEmpty() && !password.isEmpty();
        }
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
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new java.awt.GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
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
        protected void paintComponent(Graphics graphics) {
            paintRoundedFieldBackground(graphics, this, radius);
            super.paintComponent(graphics);
            if (getText() == null || getText().isEmpty()) {
                paintPlaceholder(graphics, this, placeholder);
            }
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
        protected void paintComponent(Graphics graphics) {
            paintRoundedFieldBackground(graphics, this, radius);
            super.paintComponent(graphics);
            if (getPassword().length == 0) {
                paintPlaceholder(graphics, this, placeholder);
            }
        }
    }

    private static class RoundedButton extends JButton {

        private final int radius;

        RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? PRIMARY : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
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
        public void paintBorder(java.awt.Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }

    private static void paintRoundedFieldBackground(Graphics graphics, JTextField field, int radius) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(field.getBackground());
        g2.fillRoundRect(0, 0, field.getWidth(), field.getHeight(), radius, radius);
        g2.dispose();
    }

    private static void paintPlaceholder(Graphics graphics, JTextField field, String placeholder) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(field.getFont());
        g2.setColor(PLACEHOLDER);
        Insets insets = field.getInsets();
        int y = (field.getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(placeholder, insets.left, y);
        g2.dispose();
    }
}
