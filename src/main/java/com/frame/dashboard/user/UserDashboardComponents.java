package com.frame.dashboard.user;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;

import com.model.Claim;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;

public final class UserDashboardComponents {

    private static final String FONT_FAMILY = "Poppins";
    private static final String EMPTY_TEXT = "-";
    private static final String TEXT_SEPARATOR = " - ";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Dimension REPORT_CARD_SIZE = new Dimension(320, 360);
    private static final Dimension CLAIM_CARD_SIZE = new Dimension(320, 360);
    private static final Dimension PHOTO_SIZE = new Dimension(320, 220);
    private static final Dimension CARD_PHOTO_SIZE = new Dimension(320, 160);
    private static final Dimension PHOTO_MINIMUM_SIZE = new Dimension(280, 160);
    private static final int CARD_ACTION_HEIGHT = 32;
    private static final int STATUS_BADGE_MIN_WIDTH = 78;

    public static final Color PRIMARY_DARK = new Color(44, 94, 173);
    public static final Color PRIMARY = new Color(21, 145, 220);
    public static final Color PRIMARY_LIGHT = new Color(75, 184, 250);
    public static final Color SURFACE = new Color(247, 251, 254);
    public static final Color BORDER = new Color(214, 226, 239);
    public static final Color TEXT_DARK = new Color(8, 20, 38);
    public static final Color TEXT_MUTED = new Color(83, 108, 135);
    public static final Color ORANGE = new Color(255, 128, 77);

    private UserDashboardComponents() {
    }

    // =========================
    // Basic Factories
    // =========================

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, style, size));
        label.setForeground(color);
        return label;
    }

    public static JTextArea paragraph(String text, int size) {
        JTextArea area = new JTextArea(text == null || text.isBlank() ? EMPTY_TEXT : text);
        area.setFont(new Font(FONT_FAMILY, Font.PLAIN, size));
        area.setForeground(TEXT_MUTED);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static JScrollPane scroll(Component content) {
        JScrollPane scrollPane = new JScrollPane(new ViewportWidthPanel(content));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        applyScrollBarStyle(scrollPane);
        scrollPane.addHierarchyListener(event -> {
            if ((event.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && scrollPane.isShowing()) {
                resetScrollPosition(scrollPane);
            }
        });
        return scrollPane;
    }

    public static void resetScrollPosition(Component component) {
        java.util.ArrayList<JScrollPane> scrollPanes = new java.util.ArrayList<>();
        collectScrollPanes(component, scrollPanes);
        if (scrollPanes.isEmpty()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            resetScrollPanes(scrollPanes);

            javax.swing.Timer timer = new javax.swing.Timer(40, null);
            final int[] runCount = {0};
            timer.addActionListener(event -> {
                resetScrollPanes(scrollPanes);
                runCount[0]++;
                if (runCount[0] >= 5) {
                    timer.stop();
                }
            });
            timer.setRepeats(true);
            timer.start();
        });
    }

    private static void collectScrollPanes(Component component, java.util.ArrayList<JScrollPane> scrollPanes) {
        if (component instanceof JScrollPane) {
            scrollPanes.add((JScrollPane) component);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                collectScrollPanes(child, scrollPanes);
            }
        }
    }

    private static void resetScrollPanes(java.util.ArrayList<JScrollPane> scrollPanes) {
        for (JScrollPane scrollPane : scrollPanes) {
            scrollToTop(scrollPane);
        }
    }

    private static void scrollToTop(JScrollPane scrollPane) {
        scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
        scrollPane.getVerticalScrollBar().setValue(0);
        scrollPane.getHorizontalScrollBar().setValue(0);
    }

    public static void clearTextFocusOnBackgroundClick(JComponent root) {
        root.setFocusable(true);
        installTextFocusClearer(root, root);
    }

    private static void installTextFocusClearer(Component component, JComponent focusTarget) {
        if (!(component instanceof JTextComponent)) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (focusOwner instanceof JTextComponent) {
                        focusTarget.requestFocusInWindow();
                    }
                }
            });
        }

        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                installTextFocusClearer(child, focusTarget);
            }
            container.addContainerListener(new ContainerAdapter() {
                @Override
                public void componentAdded(ContainerEvent event) {
                    installTextFocusClearer(event.getChild(), focusTarget);
                }
            });
        }
    }

    private static void applyScrollBarStyle(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = PRIMARY;
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

    public static JPanel section(String title, String subtitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label(title, 25, Font.BOLD, TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(label(subtitle, 14, Font.PLAIN, TEXT_MUTED), gbc);

        return panel;
    }

    public static JPanel cardGrid() {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 18, 18));
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel statGrid() {
        JPanel panel = new JPanel(new ResponsiveCardLayout(18, 18, 260));
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel responsiveGrid(int minimumCardWidth) {
        JPanel panel = new JPanel(new ResponsiveCardLayout(18, 18, minimumCardWidth));
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel responsiveActionRow(Component leftContent, Component rightContent) {
        JPanel panel = new JPanel(new ResponsiveActionLayout(14));
        panel.setOpaque(false);
        panel.add(leftContent);
        panel.add(rightContent);
        return panel;
    }

    public static GridBagConstraints contentConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    public static JPanel emptyState(String text) {
        RoundedPanel panel = new RoundedPanel(Color.WHITE, 20);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 20, 1),
                BorderFactory.createEmptyBorder(36, 28, 36, 28)
        ));
        panel.add(label(text, 15, Font.PLAIN, TEXT_MUTED));
        return panel;
    }

    public static class SearchField extends JTextField {
        private final String placeholder;
        public SearchField(String placeholder) {
            super(20);
            this.placeholder = placeholder;
            setFont(new Font("Poppins", Font.PLAIN, 14));
            setForeground(TEXT_DARK);
            setCaretColor(TEXT_DARK);
            setPreferredSize(new Dimension(250, 40));
            setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 20, 1),
                BorderFactory.createEmptyBorder(5, 35, 5, 15)
            ));
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent event) {
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent event) {
                    repaint();
                }
            });
            getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent event) {
                    repaint();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent event) {
                    repaint();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent event) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g2.drawString("\uD83D\uDD0D", 12, getHeight() / 2 + 5); 

            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setFont(getFont());
                g2.drawString(placeholder, 35, getHeight() / 2 + 5);
            }
            g2.dispose();
        }
    }

    public static class FilterPill extends JButton {
        private boolean active;
        public FilterPill(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font("Poppins", Font.PLAIN, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            updateStyle();
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!FilterPill.this.active) {
                        setForeground(PRIMARY_DARK);
                        setBorder(BorderFactory.createCompoundBorder(
                            new RoundedLineBorder(PRIMARY, 16, 1),
                            BorderFactory.createEmptyBorder(6, 14, 6, 14)
                        ));
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!FilterPill.this.active) {
                        updateStyle();
                    }
                }
            });
        }
        
        public void setActive(boolean active) {
            this.active = active;
            updateStyle();
        }
        
        public boolean isActivePill() {
            return active;
        }
        
        private void updateStyle() {
            if (active) {
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(TEXT_DARK, 16, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)
                ));
            } else {
                setForeground(TEXT_MUTED);
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 16, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)
                ));
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            if (active) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // =========================
    // Text Helpers
    // =========================

    public static String date(Report report) {
        if (report == null || report.getDate() == null) {
            return EMPTY_TEXT;
        }
        return report.getDate().format(DISPLAY_DATE_FORMAT);
    }

    public static String date(Claim claim) {
        if (claim == null || claim.getDateClaim() == null) {
            return EMPTY_TEXT;
        }
        return claim.getDateClaim().format(DISPLAY_DATE_FORMAT);
    }

    public static String category(Report report) {
        if (report == null || report.getItem() == null || report.getItem().getCategory() == null) {
            return "Tanpa Kategori";
        }
        return report.getItem().getCategory().getName();
    }

    public static String location(Report report) {
        if (report instanceof LostReport) {
            return ((LostReport) report).getLostLocation();
        }
        if (report instanceof FoundReport) {
            return ((FoundReport) report).getFoundLocation();
        }
        return report != null && report.getItem() != null ? report.getItem().getLocation() : EMPTY_TEXT;
    }

    // =========================
    // Base Components
    // =========================

    public static JButton plainButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setForeground(TEXT_MUTED);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return button;
    }

    public static class RoundedPanel extends JPanel {

        private final Color color;
        private final int radius;

        public RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class GradientPanel extends JPanel {

        private final Color start;
        private final Color end;
        private final int radius;

        public GradientPanel(Color start, Color end, int radius) {
            this.start = start;
            this.end = end;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new java.awt.GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(255, 255, 255, 36));
            g2.fillOval(getWidth() - 64, -28, 96, 96);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedLineBorder extends AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }

    private static class ViewportWidthPanel extends JPanel implements Scrollable {

        ViewportWidthPanel(Component content) {
            super(new GridBagLayout());
            setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            add(content, gbc);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(16, visibleRect.height - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static class ResponsiveCardLayout implements LayoutManager {

        private final int horizontalGap;
        private final int verticalGap;
        private final int minimumCardWidth;

        ResponsiveCardLayout(int horizontalGap, int verticalGap, int minimumCardWidth) {
            this.horizontalGap = horizontalGap;
            this.verticalGap = verticalGap;
            this.minimumCardWidth = minimumCardWidth;
        }

        @Override
        public void addLayoutComponent(String name, Component component) {
        }

        @Override
        public void removeLayoutComponent(Component component) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return calculateLayoutSize(parent, parent.getWidth() > 0 ? parent.getWidth() : 900);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return calculateLayoutSize(parent, minimumCardWidth);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int componentCount = getVisibleComponentCount(parent);
                if (componentCount == 0) {
                    return;
                }

                int availableWidth = Math.max(1, parent.getWidth() - insets.left - insets.right);
                int columns = calculateColumnCount(availableWidth, componentCount);
                int cardWidth = Math.max(1, (availableWidth - (horizontalGap * (columns - 1))) / columns);
                int x = insets.left;
                int y = insets.top;
                int column = 0;
                int rowHeight = 0;

                for (Component component : parent.getComponents()) {
                    if (!component.isVisible()) {
                        continue;
                    }

                    Dimension preferred = component.getPreferredSize();
                    int cardHeight = preferred.height;
                    component.setBounds(x, y, cardWidth, cardHeight);
                    rowHeight = Math.max(rowHeight, cardHeight);

                    column++;
                    if (column >= columns) {
                        column = 0;
                        x = insets.left;
                        y += rowHeight + verticalGap;
                        rowHeight = 0;
                    } else {
                        x += cardWidth + horizontalGap;
                    }
                }
            }
        }

        private Dimension calculateLayoutSize(Container parent, int width) {
            Insets insets = parent.getInsets();
            int componentCount = getVisibleComponentCount(parent);
            if (componentCount == 0) {
                return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
            }

            int availableWidth = Math.max(1, width - insets.left - insets.right);
            int columns = calculateColumnCount(availableWidth, componentCount);
            int rows = (int) Math.ceil(componentCount / (double) columns);
            int maxHeight = 0;
            for (Component component : parent.getComponents()) {
                if (component.isVisible()) {
                    maxHeight = Math.max(maxHeight, component.getPreferredSize().height);
                }
            }

            int cardWidth = Math.max(1, (availableWidth - (horizontalGap * (columns - 1))) / columns);
            int preferredWidth = insets.left + insets.right + (columns * cardWidth) + ((columns - 1) * horizontalGap);
            int preferredHeight = insets.top + insets.bottom + (rows * maxHeight) + ((rows - 1) * verticalGap);
            return new Dimension(preferredWidth, preferredHeight);
        }

        private int calculateColumnCount(int availableWidth, int componentCount) {
            if (availableWidth < minimumCardWidth) {
                return 1;
            }
            int columns = Math.max(1, (availableWidth + horizontalGap) / (minimumCardWidth + horizontalGap));
            return Math.min(componentCount, columns);
        }

        private int getVisibleComponentCount(Container parent) {
            int count = 0;
            for (Component component : parent.getComponents()) {
                if (component.isVisible()) {
                    count++;
                }
            }
            return count;
        }
    }

    private static class ResponsiveActionLayout implements LayoutManager {

        private final int gap;

        ResponsiveActionLayout(int gap) {
            this.gap = gap;
        }

        @Override
        public void addLayoutComponent(String name, Component component) {
        }

        @Override
        public void removeLayoutComponent(Component component) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component left = getComponent(parent, 0);
                Component right = getComponent(parent, 1);
                if (left == null) {
                    return new Dimension(0, 0);
                }

                Dimension leftSize = left.getPreferredSize();
                Dimension rightSize = right == null ? new Dimension(0, 0) : right.getPreferredSize();
                Insets insets = parent.getInsets();
                int width = leftSize.width + (right == null ? 0 : gap + rightSize.width) + insets.left + insets.right;
                int height = Math.max(leftSize.height, rightSize.height) + insets.top + insets.bottom;
                return new Dimension(width, height);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component left = getComponent(parent, 0);
                Component right = getComponent(parent, 1);
                Dimension leftSize = left == null ? new Dimension(0, 0) : left.getMinimumSize();
                Dimension rightSize = right == null ? new Dimension(0, 0) : right.getMinimumSize();
                Insets insets = parent.getInsets();
                int width = Math.max(leftSize.width, rightSize.width) + insets.left + insets.right;
                int height = leftSize.height + (right == null ? 0 : gap + rightSize.height) + insets.top + insets.bottom;
                return new Dimension(width, height);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component left = getComponent(parent, 0);
                Component right = getComponent(parent, 1);
                if (left == null) {
                    return;
                }

                Insets insets = parent.getInsets();
                int availableWidth = Math.max(0, parent.getWidth() - insets.left - insets.right);
                Dimension leftSize = left.getPreferredSize();
                Dimension rightSize = right == null ? new Dimension(0, 0) : right.getPreferredSize();
                boolean fitsInOneRow = right == null || leftSize.width + gap + rightSize.width <= availableWidth;

                if (fitsInOneRow) {
                    int rowHeight = Math.max(leftSize.height, rightSize.height);
                    left.setBounds(insets.left, insets.top, Math.max(0, availableWidth - rightSize.width - gap), rowHeight);
                    if (right != null) {
                        right.setBounds(
                                insets.left + availableWidth - rightSize.width,
                                insets.top + (rowHeight - rightSize.height) / 2,
                                rightSize.width,
                                rightSize.height
                        );
                    }
                    return;
                }

                left.setBounds(insets.left, insets.top, availableWidth, leftSize.height);
                if (right != null) {
                    right.setBounds(insets.left, insets.top + leftSize.height + gap, availableWidth, rightSize.height);
                }
            }
        }

        private Component getComponent(Container parent, int index) {
            return parent.getComponentCount() > index ? parent.getComponent(index) : null;
        }
    }

    // =========================
    // Dashboard Cards
    // =========================

    public static class StatCard extends GradientPanel {

        public StatCard(String title, String value, String subtitle, Color start, Color end) {
            super(start, end, 18);
            setPreferredSize(new Dimension(230, 128));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(label(title, 15, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(12, 0, 8, 0);
            add(label(value, 34, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(label(subtitle, 13, Font.PLAIN, new Color(236, 248, 255)), gbc);
        }
    }

    public static class ReportCard extends RoundedPanel {

        public ReportCard(Report report, String statusText, Color statusColor) {
            this(report, statusText, statusColor, null);
        }

        public ReportCard(Report report, String statusText, Color statusColor, JComponent actionContent) {
            super(Color.WHITE, 20);
            setPreferredSize(REPORT_CARD_SIZE);
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(new PhotoPanel(report, CARD_PHOTO_SIZE), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label(report.getItem().getName(), 20, Font.BOLD, TEXT_DARK), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(2, 0, 0, 0);
            add(label(report.getReportId() + TEXT_SEPARATOR + category(report) + TEXT_SEPARATOR + date(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(paragraph(report.getDescription(), 13), gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(12, 0, 0, 0);
            add(label("Lokasi: " + location(report), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(createCardActions(statusText, statusColor, actionContent), gbc);
            installCardMouseHandlers(this, report);
        }

        private String location(Report report) {
            return UserDashboardComponents.location(report);
        }

        private JPanel createCardActions(String statusText, Color statusColor, JComponent actionContent) {
            JPanel actions = new JPanel(new GridBagLayout());
            actions.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            actions.add(createStatusBadge(statusText, statusColor), gbc);

            if (actionContent != null) {
                gbc.gridx = 1;
                gbc.weightx = 0;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.insets = new Insets(0, 8, 0, 0);
                actions.add(actionContent);
            }
            return actions;
        }

        private void installCardMouseHandlers(Component component, Report report) {
            if (component instanceof JButton) {
                return;
            }

            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    applyHoverBorder();
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    java.awt.Point point = SwingUtilities.convertPoint(
                            event.getComponent(),
                            event.getPoint(),
                            ReportCard.this
                    );
                    if (!ReportCard.this.contains(point)) {
                        applyNormalBorder();
                    }
                }

                @Override
                public void mouseClicked(MouseEvent event) {
                    openReportDetail(report);
                }
            });

            if (component instanceof Container) {
                for (Component child : ((Container) component).getComponents()) {
                    installCardMouseHandlers(child, report);
                }
            }
        }

        private void applyHoverBorder() {
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(new Color(150, 180, 225), 22, 2),
                    BorderFactory.createEmptyBorder(10, 11, 14, 11)
            ));
            repaint();
        }

        private void applyNormalBorder() {
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            repaint();
        }

        private void openReportDetail(Report report) {
            try {
                new ReportDetailFrame(report).setVisible(true);
            } catch (Throwable ex) {
                // fallback: ignore
            }
        }
    }

    public static class ClaimCard extends RoundedPanel {

        public ClaimCard(Claim claim) {
            super(Color.WHITE, 20);
            setPreferredSize(CLAIM_CARD_SIZE);
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 22, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            GridBagConstraints gbc = createHorizontalConstraints();

            gbc.gridy = 0;
            add(new ClaimPhotoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(8, 0, 0, 0);
            add(label(claim.getItem().getName(), 20, Font.BOLD, TEXT_DARK), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(2, 0, 0, 0);
            add(label(getClaimMetaText(claim), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(paragraph(claim.getItem().getDescription(), 13), gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(12, 0, 0, 0);
            add(label("Lokasi: " + getClaimItemLocation(claim), 13, Font.PLAIN, TEXT_MUTED), gbc);

            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 0, 0, 0);
            add(createClaimStatusBadge(claim.getStatus().name()), gbc);
        }

        private String getClaimMetaText(Claim claim) {
            String reportId = claim.getRelatedReportId() == null ? EMPTY_TEXT : claim.getRelatedReportId();
            return reportId + TEXT_SEPARATOR + getClaimCategoryName(claim) + TEXT_SEPARATOR + date(claim);
        }

        private String getClaimCategoryName(Claim claim) {
            if (claim.getItem() == null || claim.getItem().getCategory() == null) {
                return "Tanpa Kategori";
            }
            return claim.getItem().getCategory().getName();
        }

        private String getClaimItemLocation(Claim claim) {
            if (claim.getItem() == null || claim.getItem().getLocation() == null || claim.getItem().getLocation().isBlank()) {
                return EMPTY_TEXT;
            }
            return claim.getItem().getLocation();
        }
    }

    private static GridBagConstraints createHorizontalConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    public enum ActionIconType {
        EDIT,
        DELETE
    }

    public static JButton iconButton(ActionIconType type, String tooltip, Color foreground, Color background, Color hoverBackground) {
        return iconButton(type, tooltip, foreground, background, hoverBackground, new Dimension(42, 36), 16);
    }

    public static JButton iconButton(ActionIconType type, String tooltip, Color foreground, Color background, Color hoverBackground, Dimension size, int radius) {
        JButton button = new IconActionButton(new ActionIcon(type, foreground), background, hoverBackground, radius);
        button.setToolTipText(tooltip);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        return button;
    }

    private static class IconActionButton extends JButton {

        private final Color backgroundColor;
        private final Color hoverBackgroundColor;
        private final int radius;

        IconActionButton(Icon icon, Color backgroundColor, Color hoverBackgroundColor, int radius) {
            super(icon);
            this.backgroundColor = backgroundColor;
            this.hoverBackgroundColor = hoverBackgroundColor;
            this.radius = radius;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isPressed() ? backgroundColor.darker() : backgroundColor;
            if (getModel().isRollover() && !getModel().isPressed()) {
                fill = hoverBackgroundColor;
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class ActionIcon implements Icon {

        private static final int SIZE = 18;

        private final ActionIconType type;
        private final Color color;

        ActionIcon(ActionIconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (type == ActionIconType.EDIT) {
                g2.drawLine(4, 13, 13, 4);
                g2.drawLine(11, 3, 15, 7);
                g2.drawLine(3, 14, 7, 15);
                g2.drawLine(3, 14, 4, 10);
            } else {
                g2.drawLine(5, 6, 13, 6);
                g2.drawLine(7, 4, 11, 4);
                g2.drawLine(6, 8, 7, 15);
                g2.drawLine(12, 8, 11, 15);
                g2.drawLine(7, 15, 11, 15);
                g2.drawLine(8, 9, 8, 13);
                g2.drawLine(10, 9, 10, 13);
            }

            g2.dispose();
        }
    }

    private static RoundedPanel createStatusBadge(String statusText, Color statusColor) {
        RoundedPanel badge = new RoundedPanel(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 24), 18);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(calculateStatusBadgeWidth(statusText), CARD_ACTION_HEIGHT));
        badge.setMinimumSize(badge.getPreferredSize());
        badge.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        badge.add(label(statusText, 12, Font.BOLD, statusColor));
        return badge;
    }

    private static RoundedPanel createClaimStatusBadge(String statusText) {
        Color statusColor = claimStatusColor(statusText);
        return createStatusBadge("Status: " + statusText, statusColor);
    }

    private static Color claimStatusColor(String statusText) {
        if ("VALID".equals(statusText)) {
            return new Color(22, 163, 74);
        }
        if ("DITOLAK".equals(statusText)) {
            return new Color(220, 38, 38);
        }
        return PRIMARY_DARK;
    }

    private static int calculateStatusBadgeWidth(String statusText) {
        String safeText = statusText == null ? EMPTY_TEXT : statusText;
        return Math.max(STATUS_BADGE_MIN_WIDTH, safeText.length() * 10 + 24);
    }

    // =========================
    // Image Components
    // =========================

    public static class PhotoPanel extends JPanel {

        private final Report report;
        private final boolean showTitleOverlay;

        PhotoPanel(Report report) {
            this(report, false, PHOTO_SIZE);
        }

        public PhotoPanel(Report report, Dimension size) {
            this(report, false, size);
        }

        public PhotoPanel(Report report, boolean showTitleOverlay, Dimension size) {
            this.report = report;
            this.showTitleOverlay = showTitleOverlay;
            setPreferredSize(size);
            setMinimumSize(new Dimension(Math.min(size.width, PHOTO_MINIMUM_SIZE.width), Math.min(size.height, PHOTO_MINIMUM_SIZE.height)));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            java.awt.geom.RoundRectangle2D round = new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setClip(round);

            String path = report != null ? report.getPhotoPath() : null;
            if (path != null && !path.isBlank() && new File(path).exists()) {
                Image image = new ImageIcon(path).getImage();
                int imgW = image.getWidth(this);
                int imgH = image.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    g2.setColor(new Color(239, 246, 252));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                    double scale = Math.min(1.0, Math.min((double) getWidth() / imgW, (double) getHeight() / imgH));
                    int drawW = (int) Math.round(imgW * scale);
                    int drawH = (int) Math.round(imgH * scale);
                    int x = (getWidth() - drawW) / 2;
                    int y = (getHeight() - drawH) / 2;
                    g2.drawImage(image, x, y, drawW, drawH, this);
                } else {
                    g2.setPaint(new java.awt.GradientPaint(0, 0, PRIMARY_LIGHT, getWidth(), getHeight(), PRIMARY_DARK));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }
            } else {
                g2.setPaint(new java.awt.GradientPaint(0, 0, PRIMARY_LIGHT, getWidth(), getHeight(), PRIMARY_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 44));
                g2.fillOval(getWidth() - 95, -35, 130, 130);
                if (showTitleOverlay && report != null && report.getItem() != null) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Poppins", Font.BOLD, 16));
                    String name = report.getItem().getName();
                    g2.drawString(name.length() > 34 ? name.substring(0, 34) + "..." : name, 18, getHeight() - 24);
                }
            }

            g2.setClip(null);
            g2.dispose();
        }
    }

    private static class ClaimPhotoPanel extends JPanel {

        ClaimPhotoPanel() {
            setPreferredSize(CARD_PHOTO_SIZE);
            setMinimumSize(PHOTO_MINIMUM_SIZE);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new java.awt.GradientPaint(0, 0, PRIMARY_LIGHT, getWidth(), getHeight(), PRIMARY_DARK));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(new Color(255, 255, 255, 44));
            g2.fillOval(getWidth() - 95, -35, 130, 130);
            g2.dispose();
        }
    }
}
