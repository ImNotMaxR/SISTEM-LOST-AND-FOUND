package com.frame.dashboard;

import com.frame.dashboard.security.SecurityStoragePanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import com.frame.LoginFrame;
import com.frame.AppDialog;
import com.frame.dashboard.user.FoundItemsPanel;
import com.frame.dashboard.user.LostItemsPanel;
import com.frame.dashboard.user.UserClaimsPanel;
import com.frame.dashboard.user.UserDashboardComponents;
import com.frame.dashboard.user.UserHomePanel;
import com.frame.dashboard.user.UserProfilePanel;
import com.frame.dashboard.user.UserReportsPanel;
import com.managers.ClaimManager;
import com.managers.ReportManager;
import com.model.Mahasiswa;
import com.model.User;
import com.service.AuthService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class DashboardUser extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(DashboardUser.class.getName());

    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_REPORTS = "reports";
    private static final String PAGE_FOUND_ITEMS = "found";
    private static final String PAGE_LOST_ITEMS = "lost";
    private static final String PAGE_CLAIMS = "claims";
    private static final String PAGE_PROFILE = "profile";
    private static final String PAGE_STORAGE = "storage";
    private static final String PAGE_CREATE_FOUND = "create_found";

    private final User currentUser;
    private final ReportManager reportManager;
    private final ClaimManager claimManager;
    private final CardLayout contentLayout;
    private final JPanel pageContainer;
    private final LinkedHashMap<String, SidebarButton> navigationButtons;

    public DashboardUser() {
        this(AuthService.getCurrentUser());
    }

    public DashboardUser(User currentUser) {
        this.currentUser = currentUser;
        this.reportManager = new ReportManager();
        this.claimManager = new ClaimManager();
        this.contentLayout = new CardLayout();
        this.pageContainer = new JPanel(contentLayout);
        this.navigationButtons = new LinkedHashMap<>();
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
        showPage(PAGE_DASHBOARD);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                stabilizeVisiblePage();
            }
        });
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Dashboard User - Sistem Lost & Found");
        setResizable(true);

        java.awt.Rectangle screenBounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int width = Math.min(1360, Math.max(1024, screenBounds.width - 80));
        int height = Math.min(820, Math.max(680, screenBounds.height - 60));
        int minWidth = Math.min(1120, Math.max(960, screenBounds.width - 160));
        int minHeight = Math.min(700, Math.max(620, screenBounds.height - 140));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(minWidth, minHeight));
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        root.add(createSidebarScrollPane(), BorderLayout.WEST);
        root.add(createPageContainer(), BorderLayout.CENTER);

        return root;
    }

    // =========================
    // Sidebar UI
    // =========================

    private JScrollPane createSidebarScrollPane() {
        JScrollPane scrollPane = new JScrollPane(
                createSidebarPanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(320, 760));
        applySidebarScrollStyle(scrollPane);
        return scrollPane;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(320, 760));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UserDashboardComponents.BORDER));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 16, 0, 16);

        gbc.gridy = 0;
        gbc.insets = new Insets(20, 22, 24, 22);
        sidebar.add(createLogoLabel(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 16, 20, 16);
        sidebar.add(createUserCard(), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 16, 8, 16);
        sidebar.add(createSectionLabel("UTAMA"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 8, 12, 16);
        sidebar.add(createNavigationButton(PAGE_DASHBOARD, "Dashboard", "icon_house.png"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 16, 8, 16);
        sidebar.add(createSectionLabel("LAPORAN"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(createNavigationButton(PAGE_REPORTS, "Laporan Saya", "icon_file.png"), gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(14, 16, 8, 16);
        sidebar.add(createSectionLabel("PENCARIAN"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(createNavigationButton(PAGE_FOUND_ITEMS, "Lihat Barang Ditemukan", "icon_zoom.png"), gbc);

        gbc.gridy = 8;
        sidebar.add(createNavigationButton(PAGE_LOST_ITEMS, "Lihat Barang Dicari", "icon_location.png"), gbc);

        if (currentUser.getRole() != com.enumeration.Role.SECURITY) {
            gbc.gridy = 9;
            gbc.insets = new Insets(14, 16, 8, 16);
            sidebar.add(createSectionLabel("KLAIM"), gbc);

            gbc.gridy = 10;
            gbc.insets = new Insets(0, 8, 8, 16);
            sidebar.add(createNavigationButton(PAGE_CLAIMS, "Klaim Saya", "icon_handbag.png"), gbc);
        } else {
            gbc.gridy = 9;
            gbc.insets = new Insets(14, 16, 8, 16);
            sidebar.add(createSectionLabel("STORAGE"), gbc);

            gbc.gridy = 10;
            gbc.insets = new Insets(0, 8, 8, 16);
            sidebar.add(createNavigationButton(PAGE_STORAGE, "Storage Record", "icon_briefcase.png"), gbc);
        }

        gbc.gridy = 11;
        gbc.insets = new Insets(14, 16, 8, 16);
        sidebar.add(createSectionLabel("AKUN"), gbc);

        gbc.gridy = 12;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(createNavigationButton(PAGE_PROFILE, "Profil Saya", "icon_person.png"), gbc);

        gbc.gridy = 13;
        gbc.weighty = 1;
        sidebar.add(new JPanel(), gbc);

        gbc.gridy = 14;
        gbc.weighty = 0;
        gbc.insets = new Insets(16, 8, 22, 16);
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
            protected void paintThumb(java.awt.Graphics g, javax.swing.JComponent c, java.awt.Rectangle thumbBounds) {
                if (!c.isEnabled() || thumbBounds.isEmpty()) {
                    return;
                }
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintTrack(java.awt.Graphics g, javax.swing.JComponent c, java.awt.Rectangle trackBounds) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
    }

    // =========================
    // Page Content UI
    // =========================

    private JPanel createPageContainer() {
        pageContainer.setOpaque(false);
        rebuildPages();
        return pageContainer;
    }

    private void rebuildPages() {
        pageContainer.removeAll();
        pageContainer.add(UserDashboardComponents.scroll(new UserHomePanel(currentUser, reportManager)), PAGE_DASHBOARD);
        pageContainer.add(new UserReportsPanel(currentUser, reportManager), PAGE_REPORTS);
        pageContainer.add(new FoundItemsPanel(reportManager), PAGE_FOUND_ITEMS);
        pageContainer.add(new LostItemsPanel(reportManager), PAGE_LOST_ITEMS);
        pageContainer.add(new UserClaimsPanel(currentUser, claimManager), PAGE_CLAIMS);
        pageContainer.add(new UserProfilePanel(currentUser), PAGE_PROFILE);
        if (currentUser.getRole() == com.enumeration.Role.SECURITY) {
            pageContainer.add(new SecurityStoragePanel(currentUser.getUserId()), PAGE_STORAGE);
        }
    }

    // =========================
    // Sidebar Component Factories
    // =========================

    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/assets/icon-lost-found-COLOR.png");

        if (logoUrl != null) {
            ImageIcon original = new ImageIcon(logoUrl);
            int width = 154;
            int height = Math.max(32, (int) Math.round(width / ((double) original.getIconWidth() / original.getIconHeight())));
            Image image = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(image));
        } else {
            logo.setText("Lost & Found");
            logo.setForeground(UserDashboardComponents.PRIMARY_DARK);
            logo.setFont(new Font("Poppins", Font.BOLD, 14));
        }

        return logo;
    }

    private JPanel createUserCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(new Color(249, 252, 255), 18);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(13, 14, 13, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        card.add(UserDashboardComponents.label("USER", 10, Font.BOLD, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 0, 0);
        card.add(UserDashboardComponents.label(getDisplayName(), 14, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(UserDashboardComponents.label(getUserDetail(), 12, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        return card;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = UserDashboardComponents.label(text, 11, Font.BOLD, UserDashboardComponents.TEXT_MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return label;
    }

    private SidebarButton createNavigationButton(String pageKey, String text, String iconName) {
        SidebarButton button = new SidebarButton(text, iconName);
        button.addActionListener(event -> showPage(pageKey));
        navigationButtons.put(pageKey, button);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        try {
            java.net.URL url = getClass().getResource("/assets/PNG/64x64/icon_sign.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(12);
            }
        } catch (Exception e) {}
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setForeground(new Color(220, 38, 38));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(254, 226, 226)); // Light red
                button.setForeground(new Color(220, 38, 38));   // Dark red text
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(UserDashboardComponents.TEXT_DARK);
            }
        });
        
        button.addActionListener(event -> {
            boolean confirmed = AppDialog.confirm(
                    this,
                    "Konfirmasi Logout",
                    "Anda yakin ingin keluar dari dashboard?",
                    "Logout",
                    "Batal"
            );
            if (!confirmed) {
                return;
            }
            dispose();
            new LoginFrame().setVisible(true);
        });
        return button;
    }

    // =========================
    // Navigation Actions
    // =========================

    private void showPage(String pageKey) {
        contentLayout.show(pageContainer, pageKey);
        for (String menuKey : navigationButtons.keySet()) {
            navigationButtons.get(menuKey).setActive(menuKey.equals(pageKey));
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

    // =========================
    // User Display Helpers
    // =========================

    private String getDisplayName() {
        if (currentUser == null || currentUser.getName() == null || currentUser.getName().isBlank()) {
            return "Pengguna";
        }
        return currentUser.getName();
    }

    private String getUserDetail() {
        if (currentUser instanceof Mahasiswa) {
            Mahasiswa m = (Mahasiswa) currentUser;
            return m.getNim() != null && !m.getNim().isBlank() ? "NIM " + m.getNim() : "MAHASISWA";
        } else if (currentUser instanceof com.model.Dosen) {
            com.model.Dosen d = (com.model.Dosen) currentUser;
            return d.getNip() != null && !d.getNip().isBlank() ? "NIP " + d.getNip() : "DOSEN";
        } else if (currentUser instanceof com.model.Staff) {
            com.model.Staff s = (com.model.Staff) currentUser;
            return s.getStaffID() != null && !s.getStaffID().isBlank() ? "ID: " + s.getStaffID() : "STAFF";
        }

        return currentUser != null ? currentUser.getRole().name() : "Belum Login";
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

        java.awt.EventQueue.invokeLater(() -> new DashboardUser().setVisible(true));
    }

    // =========================
    // Custom UI Components
    // =========================

    private static class SidebarButton extends JButton {

        private boolean active;
        private ImageIcon iconImage;

        SidebarButton(String text, String iconName) {
            super(text);
            try {
                java.net.URL url = getClass().getResource("/assets/PNG/64x64/" + iconName);
                if (url != null) {
                    Image img = new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    this.iconImage = new ImageIcon(img);
                }
            } catch (Exception e) {
                this.iconImage = null;
            }
            if (this.iconImage != null) {
                setIcon(this.iconImage);
            }
            setIconTextGap(12);
            
            setPreferredSize(new Dimension(270, 46));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(JButton.LEFT);
            setFont(new Font("Poppins", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            setActive(false);
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!active) {
                        setBackground(new Color(248, 250, 252));
                        setForeground(UserDashboardComponents.PRIMARY);
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!active) {
                        setBackground(Color.WHITE);
                        setForeground(UserDashboardComponents.TEXT_MUTED);
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setBackground(UserDashboardComponents.PRIMARY);
                setForeground(Color.WHITE);
                setFont(new Font("Poppins", Font.BOLD, 14));
            } else {
                setBackground(Color.WHITE);
                setForeground(UserDashboardComponents.TEXT_MUTED);
                setFont(new Font("Poppins", Font.PLAIN, 14));
            }
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(UserDashboardComponents.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(235, 244, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
