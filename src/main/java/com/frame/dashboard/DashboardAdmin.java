package com.frame.dashboard;

import com.frame.AppDialog;
import com.frame.LoginFrame;
import com.frame.dashboard.admin.AdminClaimsPanel;
import com.frame.dashboard.admin.AdminDashboardComponents;
import com.frame.dashboard.admin.AdminFoundReportsPanel;
import com.frame.dashboard.admin.AdminHomePanel;
import com.frame.dashboard.admin.AdminLostReportsPanel;
import com.frame.dashboard.admin.AdminProfilePanel;
import com.frame.dashboard.shared.DashboardUi;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.User;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class DashboardAdmin extends JFrame {

    private static final String FRAME_TITLE = "Dashboard Admin - Sistem Lost & Found";
    private static final String LOGO_PATH = "/assets/icon-lost-found-COLOR.png";
    private static final String LOGOUT_ICON_PATH = "/assets/PNG/64x64/icon_sign.png";
    private static final String FONT_FAMILY = "Poppins";

    private static final int SIDEBAR_WIDTH = 320;
    private static final int SIDEBAR_HEIGHT = 820;
    private static final int PAGE_STABILIZE_REPEATS = 12;
    private static final int PAGE_STABILIZE_DELAY_MS = 60;

    private static final Color SIDEBAR_BACKGROUND = Color.WHITE;
    private static final Color USER_CARD_BACKGROUND = new Color(249, 252, 255);
    private static final Color LOGOUT_TEXT = new Color(220, 38, 38);
    private static final Color LOGOUT_HOVER_BACKGROUND = new Color(254, 226, 226);
    private static final Color SCROLLBAR_THUMB = new Color(75, 145, 255);
    private static final Color SCROLLBAR_TRACK = new Color(245, 248, 252);

    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_LOST_REPORTS = "lostReports";
    private static final String PAGE_FOUND_REPORTS = "foundReports";
    private static final String PAGE_CLAIMS = "claims";
    private static final String PAGE_PROFILE = "profile";

    private final ReportManager reportManager;
    private final ClaimManager claimManager;
    private final User currentUser;

    private final CardLayout pageLayout;
    private final JPanel pageContainer;
    private final Map<String, JButton> navigationButtons;

    public DashboardAdmin(ReportManager reportManager) {
        this.reportManager = reportManager == null ? new ReportManager() : reportManager;
        this.claimManager = new ClaimManager();
        this.currentUser = AuthService.getCurrentUser();
        this.pageLayout = new CardLayout();
        this.pageContainer = new JPanel(pageLayout);
        this.navigationButtons = new LinkedHashMap<>();

        initializeFrame();
    }

    // =========================
    // Frame Setup
    // =========================

    private void initializeFrame() {
        configureWindow();
        setContentPane(createMainLayout());
        showPage(PAGE_DASHBOARD);
        installWindowLifecycleHandlers();
    }

    private void configureWindow() {
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Rectangle screenBounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        setMinimumSize(calculateMinimumWindowSize(screenBounds));
        setSize(calculateInitialWindowSize(screenBounds));
        setLocationRelativeTo(null);
    }

    private Dimension calculateInitialWindowSize(Rectangle screenBounds) {
        int width = Math.min(1440, Math.max(1120, screenBounds.width - 80));
        int height = Math.min(860, Math.max(720, screenBounds.height - 60));
        return new Dimension(width, height);
    }

    private Dimension calculateMinimumWindowSize(Rectangle screenBounds) {
        int width = Math.min(1180, Math.max(1024, screenBounds.width - 160));
        int height = Math.min(760, Math.max(660, screenBounds.height - 140));
        return new Dimension(width, height);
    }

    private void installWindowLifecycleHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                stabilizeVisiblePage();
            }
        });
    }

    // =========================
    // Main Layout
    // =========================

    private JPanel createMainLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DashboardUi.SURFACE);
        mainPanel.add(createSidebarScrollPane(), BorderLayout.WEST);
        mainPanel.add(createPageContainer(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createPageContainer() {
        pageContainer.setOpaque(false);
        rebuildPages();
        return pageContainer;
    }

    private void rebuildPages() {
        pageContainer.removeAll();
        refreshDashboardData();
        registerPages();
    }

    private void refreshDashboardData() {
        reportManager.reload();
        claimManager.refreshClaimsFromDatabase();
    }

    private void registerPages() {
        pageContainer.add(DashboardUi.scroll(new AdminHomePanel(reportManager, claimManager)), PAGE_DASHBOARD);
        pageContainer.add(DashboardUi.scroll(new AdminLostReportsPanel(reportManager)), PAGE_LOST_REPORTS);
        pageContainer.add(DashboardUi.scroll(new AdminFoundReportsPanel(reportManager)), PAGE_FOUND_REPORTS);
        pageContainer.add(DashboardUi.scroll(new AdminClaimsPanel(claimManager)), PAGE_CLAIMS);
        pageContainer.add(new AdminProfilePanel(currentUser), PAGE_PROFILE);
    }

    // =========================
    // Sidebar Layout
    // =========================

    private JScrollPane createSidebarScrollPane() {
        JScrollPane scrollPane = new JScrollPane(createSidebarPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(SIDEBAR_BACKGROUND);
        scrollPane.setBackground(SIDEBAR_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 760));
        applySidebarScrollStyle(scrollPane);
        return scrollPane;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(SIDEBAR_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, SIDEBAR_HEIGHT));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, DashboardUi.BORDER),
                BorderFactory.createEmptyBorder(36, 22, 22, 22)
        ));

        GridBagConstraints gbc = sidebarConstraints();
        addSidebarBrand(sidebar, gbc);
        addSidebarUserCard(sidebar, gbc);
        addSidebarNavigation(sidebar, gbc);
        addSidebarFooter(sidebar, gbc);
        return sidebar;
    }

    private void addSidebarBrand(JPanel sidebar, GridBagConstraints gbc) {
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 38, 0);
        sidebar.add(createLogoLabel(), gbc);
    }

    private void addSidebarUserCard(JPanel sidebar, GridBagConstraints gbc) {
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        sidebar.add(createAdminIdentityCard(), gbc);
    }

    private void addSidebarNavigation(JPanel sidebar, GridBagConstraints gbc) {
        addSidebarSection(sidebar, gbc, 2, "UTAMA", new Insets(0, 0, 12, 0));
        addSidebarButton(sidebar, gbc, 3, PAGE_DASHBOARD, "Dashboard", "\u25A6", new Insets(0, 0, 22, 0));

        addSidebarSection(sidebar, gbc, 4, "MANAJEMEN", new Insets(0, 0, 12, 0));
        addSidebarButton(sidebar, gbc, 5, PAGE_LOST_REPORTS, "Kelola Laporan Barang Hilang", "\u25A3", new Insets(0, 0, 10, 0));
        addSidebarButton(sidebar, gbc, 6, PAGE_FOUND_REPORTS, "Kelola Barang Ditemukan", "\u25C8", new Insets(0, 0, 12, 0));
        addSidebarButton(sidebar, gbc, 7, PAGE_CLAIMS, "Kelola Klaim", "\u25CC", new Insets(0, 0, 24, 0));

        addSidebarSection(sidebar, gbc, 8, "AKUN", new Insets(4, 0, 12, 0));
        addSidebarButton(sidebar, gbc, 9, PAGE_PROFILE, "Edit Profil", "\u26AD", new Insets(0, 0, 0, 0));

        gbc.gridy = 10;
        gbc.weighty = 1;
        sidebar.add(createSidebarSpacer(), gbc);
        gbc.weighty = 0;
    }

    private void addSidebarFooter(JPanel sidebar, GridBagConstraints gbc) {
        gbc.gridy = 11;
        gbc.insets = new Insets(24, 0, 0, 0);
        sidebar.add(createLogoutButton(), gbc);
    }

    private void addSidebarSection(JPanel sidebar, GridBagConstraints gbc, int row, String title, Insets insets) {
        gbc.gridy = row;
        gbc.insets = insets;
        sidebar.add(createSectionLabel(title), gbc);
    }

    private void addSidebarButton(JPanel sidebar, GridBagConstraints gbc, int row, String pageKey,
            String title, String symbol, Insets insets) {
        gbc.gridy = row;
        gbc.insets = insets;
        sidebar.add(createNavigationButton(pageKey, title, symbol), gbc);
    }

    // =========================
    // Sidebar Components
    // =========================

    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource(LOGO_PATH);
        if (logoUrl != null) {
            ImageIcon original = new ImageIcon(logoUrl);
            Image image = original.getImage().getScaledInstance(142, calculateLogoHeight(original, 142), Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(image));
        } else {
            logo.setText("Lost & Found");
            logo.setForeground(DashboardUi.PRIMARY_DARK);
            logo.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        }
        return logo;
    }

    private int calculateLogoHeight(ImageIcon icon, int width) {
        double ratio = (double) icon.getIconWidth() / icon.getIconHeight();
        return Math.max(30, (int) Math.round(width / ratio));
    }

    private JPanel createAdminIdentityCard() {
        DashboardUi.RoundedPanel card = new DashboardUi.RoundedPanel(USER_CARD_BACKGROUND, 18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        GridBagConstraints gbc = horizontalConstraints();
        gbc.gridy = 0;
        card.add(DashboardUi.label("ADMIN", 10, Font.BOLD, DashboardUi.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(DashboardUi.label(getAdminDisplayName(), 14, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        return card;
    }

    private JLabel createSectionLabel(String text) {
        return DashboardUi.label(text, 10, Font.BOLD, DashboardUi.TEXT_MUTED);
    }

    private JButton createNavigationButton(String pageKey, String text, String symbol) {
        JButton button = AdminDashboardComponents.sidebarButton(text, symbol, PAGE_DASHBOARD.equals(pageKey));
        button.addActionListener(event -> showPage(pageKey));
        navigationButtons.put(pageKey, button);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        applyLogoutIcon(button);
        applyLogoutButtonStyle(button);
        installLogoutHoverStyle(button);
        button.addActionListener(event -> handleLogoutAction());
        return button;
    }

    private void applyLogoutIcon(JButton button) {
        java.net.URL iconUrl = getClass().getResource(LOGOUT_ICON_PATH);
        if (iconUrl == null) {
            return;
        }
        Image image = new ImageIcon(iconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(image));
        button.setIconTextGap(12);
    }

    private void applyLogoutButtonStyle(JButton button) {
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        button.setForeground(LOGOUT_TEXT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
    }

    private void installLogoutHoverStyle(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                button.setBackground(LOGOUT_HOVER_BACKGROUND);
                button.setForeground(LOGOUT_TEXT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                button.setBackground(Color.WHITE);
                button.setForeground(LOGOUT_TEXT);
            }
        });
    }

    private JPanel createSidebarSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    // =========================
    // Actions
    // =========================

    private void showPage(String pageKey) {
        if (shouldRebuildBeforeShowing(pageKey)) {
            rebuildPages();
        }

        pageLayout.show(pageContainer, pageKey);
        updateActiveNavigation(pageKey);
        clearGlobalFocus();
        stabilizeVisiblePage();
    }

    private boolean shouldRebuildBeforeShowing(String pageKey) {
        return PAGE_DASHBOARD.equals(pageKey)
                || PAGE_LOST_REPORTS.equals(pageKey)
                || PAGE_FOUND_REPORTS.equals(pageKey)
                || PAGE_CLAIMS.equals(pageKey);
    }

    private void updateActiveNavigation(String activePageKey) {
        for (Map.Entry<String, JButton> entry : navigationButtons.entrySet()) {
            AdminDashboardComponents.setSidebarButtonActive(entry.getValue(), entry.getKey().equals(activePageKey));
        }
    }

    private void handleLogoutAction() {
        boolean confirmed = AppDialog.confirm(
                this,
                "Konfirmasi Logout",
                "Anda yakin ingin keluar dari dashboard admin?",
                "Logout",
                "Batal"
        );
        if (!confirmed) {
            return;
        }

        dispose();
        new LoginFrame().setVisible(true);
    }

    // =========================
    // Page Stabilization
    // =========================

    private void stabilizeVisiblePage() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            stabilizeVisiblePageNow();
            startPageStabilizationTimer();
        });
    }

    private void startPageStabilizationTimer() {
        Timer timer = new Timer(PAGE_STABILIZE_DELAY_MS, null);
        final int[] runCount = {0};
        timer.addActionListener(event -> {
            stabilizeVisiblePageNow();
            runCount[0]++;
            if (runCount[0] >= PAGE_STABILIZE_REPEATS) {
                timer.stop();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void stabilizeVisiblePageNow() {
        pageContainer.requestFocusInWindow();
        pageContainer.invalidate();
        pageContainer.revalidate();
        pageContainer.doLayout();
        getContentPane().invalidate();
        getContentPane().doLayout();
        validate();
        repaint();
        DashboardUi.resetScrollPosition(getVisiblePage());
    }

    private void clearGlobalFocus() {
        pageContainer.setFocusable(true);
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private java.awt.Component getVisiblePage() {
        for (java.awt.Component component : pageContainer.getComponents()) {
            if (component.isVisible()) {
                return component;
            }
        }
        return pageContainer;
    }

    // =========================
    // Style Helpers
    // =========================

    private void applySidebarScrollStyle(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = SCROLLBAR_THUMB;
                trackColor = SCROLLBAR_TRACK;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(java.awt.Graphics graphics, javax.swing.JComponent component,
                    java.awt.Rectangle thumbBounds) {
                if (!component.isEnabled() || thumbBounds.isEmpty()) {
                    return;
                }
                java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
                graphics2D.setRenderingHint(
                        java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                );
                graphics2D.setColor(thumbColor);
                graphics2D.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                graphics2D.dispose();
            }

            @Override
            protected void paintTrack(java.awt.Graphics graphics, javax.swing.JComponent component,
                    java.awt.Rectangle trackBounds) {
                java.awt.Graphics2D graphics2D = (java.awt.Graphics2D) graphics.create();
                graphics2D.setRenderingHint(
                        java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                );
                graphics2D.setColor(trackColor);
                graphics2D.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                graphics2D.dispose();
            }
        });
    }

    // =========================
    // Text Helpers
    // =========================

    private String getAdminDisplayName() {
        if (currentUser == null || currentUser.getName() == null || currentUser.getName().isBlank()) {
            return "Admin";
        }
        return currentUser.getName();
    }

    // =========================
    // Constraints Helpers
    // =========================

    private GridBagConstraints sidebarConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
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

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new DashboardAdmin(new ReportManager()).setVisible(true));
    }
}

