package com.frame.dashboard;

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
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.managers.UserManager;
import com.model.Mahasiswa;
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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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

    private final User currentUser;
    private final ReportManager reportManager;
    private final ClaimManager claimManager;
    private final ItemManager itemManager;
    private final UserManager userManager;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final LinkedHashMap<String, SidebarButton> sidebarButtons;

    public DashboardUser() {
        this.currentUser = AuthService.getCurrentUser();
        this.reportManager = new ReportManager();
        this.claimManager = new ClaimManager();
        this.itemManager = new ItemManager();
        this.userManager = new UserManager();
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.sidebarButtons = new LinkedHashMap<>();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Dashboard User - Sistem Lost & Found");
        setResizable(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = Math.min(1280, screenSize.width - 120);
        int height = Math.min(780, screenSize.height - 90);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(1120, 700));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        setContentPane(root);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        showPage("dashboard");
    }

    private JScrollPane createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(Color.WHITE);
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
        sidebar.add(sectionLabel("UTAMA"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 8, 12, 16);
        sidebar.add(addMenuButton("dashboard", "Dashboard"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 16, 8, 16);
        sidebar.add(sectionLabel("LAPORAN"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(addMenuButton("reports", "Laporan Saya"), gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(14, 16, 8, 16);
        sidebar.add(sectionLabel("PENCARIAN"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(addMenuButton("found", "Lihat Barang Ditemukan"), gbc);

        gbc.gridy = 8;
        sidebar.add(addMenuButton("lost", "Lihat Barang Dicari"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(14, 16, 8, 16);
        sidebar.add(sectionLabel("KLAIM"), gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(addMenuButton("claims", "Klaim Saya"), gbc);

        gbc.gridy = 11;
        gbc.insets = new Insets(14, 16, 8, 16);
        sidebar.add(sectionLabel("AKUN"), gbc);

        gbc.gridy = 12;
        gbc.insets = new Insets(0, 8, 8, 16);
        sidebar.add(addMenuButton("profile", "Profil Saya"), gbc);

        gbc.gridy = 13;
        gbc.weighty = 1;
        sidebar.add(new JPanel(), gbc);

        gbc.gridy = 14;
        gbc.weighty = 0;
        gbc.insets = new Insets(16, 8, 22, 16);
        sidebar.add(createLogoutButton(), gbc);

        JScrollPane scrollPane = new JScrollPane(sidebar, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
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
        scrollPane.setPreferredSize(new Dimension(248, 760));
        return scrollPane;
    }

    private JPanel createContent() {
        contentPanel.setBackground(UserDashboardComponents.SURFACE);
        contentPanel.add(new UserHomePanel(currentUser, reportManager, claimManager), "dashboard");
        contentPanel.add(new UserReportsPanel(currentUser, reportManager), "reports");
        contentPanel.add(new FoundItemsPanel(reportManager), "found");
        contentPanel.add(new LostItemsPanel(reportManager), "lost");
        contentPanel.add(new UserClaimsPanel(currentUser, claimManager), "claims");
        contentPanel.add(new UserProfilePanel(currentUser), "profile");
        return contentPanel;
    }

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
        card.add(UserDashboardComponents.label(displayName(), 14, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(UserDashboardComponents.label(userDetail(), 12, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        return card;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = UserDashboardComponents.label(text, 11, Font.BOLD, UserDashboardComponents.TEXT_MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return label;
    }

    private SidebarButton addMenuButton(String key, String text) {
        SidebarButton button = new SidebarButton(text);
        button.addActionListener((ActionEvent event) -> showPage(key));
        sidebarButtons.put(key, button);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setForeground(UserDashboardComponents.TEXT_DARK);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
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

    private void showPage(String key) {
        cardLayout.show(contentPanel, key);
        for (String menuKey : sidebarButtons.keySet()) {
            sidebarButtons.get(menuKey).setActive(menuKey.equals(key));
        }
    }

    private String displayName() {
        if (currentUser == null || currentUser.getName() == null || currentUser.getName().isBlank()) {
            return "Pengguna";
        }
        return currentUser.getName();
    }

    private String userDetail() {
        if (currentUser instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) currentUser;
            if (mahasiswa.getNim() != null && !mahasiswa.getNim().isBlank()) {
                return "NIM " + mahasiswa.getNim();
            }
        }

        return currentUser != null ? currentUser.getRole().name() : "Belum Login";
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

        java.awt.EventQueue.invokeLater(() -> new DashboardUser().setVisible(true));
    }

    private static class SidebarButton extends JButton {

        private boolean active;

        SidebarButton(String text) {
            super(text);
            setPreferredSize(new Dimension(224, 38));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(JButton.LEFT);
            setFont(new Font("Poppins", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            setActive(false);
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? Color.WHITE : UserDashboardComponents.TEXT_DARK);
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
