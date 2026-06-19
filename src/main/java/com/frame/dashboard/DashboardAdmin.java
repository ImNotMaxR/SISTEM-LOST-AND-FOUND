package com.frame.dashboard;

import com.frame.AppDialog;
import com.frame.dashboard.admin.AdminClaimsPanel;
import com.frame.dashboard.admin.AdminDashboardComponents;
import com.frame.dashboard.admin.AdminFoundReportsPanel;
import com.frame.dashboard.admin.AdminHomePanel;
import com.frame.dashboard.admin.AdminLostReportsPanel;
import com.frame.dashboard.admin.AdminProfilePanel;
import com.frame.dashboard.user.UserDashboardComponents;
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
import javax.swing.plaf.basic.BasicScrollBarUI;

public class DashboardAdmin extends JFrame {

    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_LOST_REPORTS = "lost_reports";
    private static final String PAGE_FOUND_REPORTS = "found_reports";
    private static final String PAGE_CLAIMS = "claims";
    private static final String PAGE_PROFILE = "profile";

    private final ReportManager reportManager;
    private final ClaimManager claimManager;
    private final User currentUser;
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(contentLayout);

    public DashboardAdmin(ReportManager reportManager) {
        this.reportManager = reportManager == null ? new ReportManager() : reportManager;
        this.claimManager = new ClaimManager();
        this.currentUser = AuthService.getCurrentUser();

        setupFrame();
        setContentPane(createMainPanel());
        showPage(PAGE_DASHBOARD);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                stabilizeVisiblePage();
            }
        });
    }

    private void setupFrame() {
        setTitle("Dashboard Admin - Sistem Lost & Found");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.awt.Rectangle screenBounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int width = Math.min(1440, Math.max(1120, screenBounds.width - 80));
        int height = Math.min(860, Math.max(720, screenBounds.height - 60));
        int minWidth = Math.min(1180, Math.max(1024, screenBounds.width - 160));
        int minHeight = Math.min(760, Math.max(660, screenBounds.height - 140));
        setMinimumSize(new Dimension(minWidth, minHeight));
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UserDashboardComponents.SURFACE);
        mainPanel.add(createSidebarScroll(), BorderLayout.WEST);
        mainPanel.add(createPageContainer(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JScrollPane createSidebarScroll() {
        JScrollPane scrollPane = new JScrollPane(createSidebar());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(320, 760));
        applySidebarScrollStyle(scrollPane);
        return scrollPane;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(320, 820));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UserDashboardComponents.BORDER),
                BorderFactory.createEmptyBorder(36, 22, 22, 22)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 38, 0);
        sidebar.add(createLogoLabel(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        sidebar.add(createAdminCard(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(createSectionLabel("UTAMA"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 22, 0);
        sidebar.add(createNavigationButton(PAGE_DASHBOARD, "Dashboard", "\u25A6"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(createSectionLabel("MANAJEMEN"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        sidebar.add(createNavigationButton(PAGE_LOST_REPORTS, "Kelola Laporan Barang Hilang", "\u25A3"), gbc);

        gbc.gridy = 6;
        sidebar.add(createNavigationButton(PAGE_FOUND_REPORTS, "Kelola Barang Ditemukan", "\u25C8"), gbc);

        gbc.gridy = 7;
        sidebar.add(createNavigationButton(PAGE_CLAIMS, "Kelola Klaim", "\u25CC"), gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(18, 0, 12, 0);
        sidebar.add(createSectionLabel("AKUN"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(createNavigationButton(PAGE_PROFILE, "Edit Profil", "\u26AD"), gbc);

        gbc.gridy = 10;
        gbc.weighty = 1;
        sidebar.add(createSidebarSpacer(), gbc);

        gbc.gridy = 11;
        gbc.weighty = 0;
        gbc.insets = new Insets(24, 0, 0, 0);
        sidebar.add(createLogoutButton(), gbc);

        return sidebar;
    }

    private void applySidebarScrollStyle(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(75, 145, 255);
                trackColor = new Color(245, 248, 252);
                thumbDarkShadowColor = thumbColor.darker();
                thumbHighlightColor = thumbColor.brighter();
                thumbLightShadowColor = thumbColor;
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
                graphics2D.fillRoundRect(
                        thumbBounds.x,
                        thumbBounds.y,
                        thumbBounds.width,
                        thumbBounds.height,
                        10,
                        10
                );
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

    private JPanel createSidebarSpacer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createPageContainer() {
        pageContainer.setOpaque(false);
        refreshPageContainer();
        return pageContainer;
    }

    private void refreshPageContainer() {
        pageContainer.removeAll();
        reportManager.reload();
        claimManager.refreshClaimsFromDatabase();
        pageContainer.add(UserDashboardComponents.scroll(new AdminHomePanel(reportManager, claimManager)), PAGE_DASHBOARD);
        pageContainer.add(UserDashboardComponents.scroll(new AdminLostReportsPanel(reportManager)), PAGE_LOST_REPORTS);
        pageContainer.add(UserDashboardComponents.scroll(new AdminFoundReportsPanel(reportManager)), PAGE_FOUND_REPORTS);
        pageContainer.add(UserDashboardComponents.scroll(new AdminClaimsPanel(claimManager)), PAGE_CLAIMS);
        pageContainer.add(new AdminProfilePanel(currentUser), PAGE_PROFILE);
    }

    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/assets/icon-lost-found-COLOR.png");
        if (logoUrl != null) {
            ImageIcon original = new ImageIcon(logoUrl);
            int width = 142;
            int height = Math.max(30, (int) Math.round(width / ((double) original.getIconWidth() / original.getIconHeight())));
            Image image = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(image));
        } else {
            logo.setText("Lost & Found");
            logo.setForeground(UserDashboardComponents.PRIMARY_DARK);
            logo.setFont(new Font("Poppins", Font.BOLD, 15));
        }
        return logo;
    }

    private JPanel createAdminCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(new Color(249, 252, 255), 18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        card.add(UserDashboardComponents.label("ADMIN", 10, Font.BOLD, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(UserDashboardComponents.label(getAdminName(), 14, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        return card;
    }

    private JLabel createSectionLabel(String text) {
        return UserDashboardComponents.label(text, 10, Font.BOLD, UserDashboardComponents.TEXT_MUTED);
    }

    private JButton createNavigationButton(String pageKey, String text, String symbol) {
        JButton button = AdminDashboardComponents.sidebarButton(text, symbol, PAGE_DASHBOARD.equals(pageKey));
        button.addActionListener(event -> showPage(pageKey));
        navigationButtons.put(pageKey, button);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        try {
            java.net.URL url = getClass().getResource("/assets/PNG/64x64/icon_sign.png");
            if (url != null) {
                Image image = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(image));
                button.setIconTextGap(12);
            }
        } catch (Exception exception) {
        }
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setForeground(new Color(220, 38, 38));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                button.setBackground(new Color(254, 226, 226));
                button.setForeground(new Color(220, 38, 38));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(220, 38, 38));
            }
        });
        button.addActionListener(event -> {
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
            openLoginFrame();
        });
        return button;
    }

    private JPanel createPlaceholderPage(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(44, 42, 44, 42));
        panel.add(UserDashboardComponents.emptyState(text));
        return panel;
    }

    private void showPage(String pageKey) {
        if (PAGE_DASHBOARD.equals(pageKey) || PAGE_LOST_REPORTS.equals(pageKey) || PAGE_FOUND_REPORTS.equals(pageKey) || PAGE_CLAIMS.equals(pageKey)) {
            refreshPageContainer();
        }

        contentLayout.show(pageContainer, pageKey);
        for (Map.Entry<String, JButton> entry : navigationButtons.entrySet()) {
            AdminDashboardComponents.setSidebarButtonActive(entry.getValue(), entry.getKey().equals(pageKey));
        }
        pageContainer.setFocusable(true);
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        stabilizeVisiblePage();
    }

    private void stabilizeVisiblePage() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            stabilizeVisiblePageNow();

            javax.swing.Timer timer = new javax.swing.Timer(60, null);
            final int[] runCount = {0};
            timer.addActionListener(event -> {
                stabilizeVisiblePageNow();
                runCount[0]++;
                if (runCount[0] >= 12) {
                    timer.stop();
                }
            });
            timer.setRepeats(true);
            timer.start();
        });
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
        UserDashboardComponents.resetScrollPosition(getVisiblePage());
    }

    private java.awt.Component getVisiblePage() {
        for (java.awt.Component component : pageContainer.getComponents()) {
            if (component.isVisible()) {
                return component;
            }
        }
        return pageContainer;
    }

    private String getAdminName() {
        if (currentUser == null || currentUser.getName() == null || currentUser.getName().isBlank()) {
            return "Admin";
        }
        return currentUser.getName();
    }

    private void openLoginFrame() {
        try {
            JFrame loginFrame = (JFrame) Class.forName("com.frame.LoginFrame").getDeclaredConstructor().newInstance();
            loginFrame.setVisible(true);
        } catch (ReflectiveOperationException exception) {
            AppDialog.error(this, "Gagal Membuka Login", "Frame login tidak dapat dibuka: " + exception.getMessage());
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new DashboardAdmin(new ReportManager()).setVisible(true));
    }
}
