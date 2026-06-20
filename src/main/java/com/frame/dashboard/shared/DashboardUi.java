package com.frame.dashboard.shared;

import com.model.Claim;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
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
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class DashboardUi {

    private static final String FONT_FAMILY = "Poppins";
    private static final String EMPTY_TEXT = "-";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static final Color PRIMARY_DARK = new Color(44, 94, 173);
    public static final Color PRIMARY = new Color(21, 145, 220);
    public static final Color PRIMARY_LIGHT = new Color(75, 184, 250);
    public static final Color SURFACE = new Color(247, 251, 254);
    public static final Color BORDER = new Color(214, 226, 239);
    public static final Color TEXT_DARK = new Color(8, 20, 38);
    public static final Color TEXT_MUTED = new Color(83, 108, 135);
    public static final Color ORANGE = new Color(255, 128, 77);

    private DashboardUi() {
    }

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, style, size));
        label.setForeground(color);
        return label;
    }

    public static JTextArea paragraph(String text, int size) {
        JTextArea area = new JTextArea(normalizeText(text));
        area.setFont(new Font(FONT_FAMILY, Font.PLAIN, size));
        area.setForeground(TEXT_MUTED);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static JPanel section(String title, String subtitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints constraints = contentConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridy = 0;
        panel.add(label(title, 25, Font.BOLD, TEXT_DARK), constraints);
        constraints.gridy = 1;
        constraints.insets = new Insets(4, 0, 0, 0);
        panel.add(label(subtitle, 14, Font.PLAIN, TEXT_MUTED), constraints);
        return panel;
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
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        return constraints;
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
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && scrollPane.isShowing()) {
                resetScrollPosition(scrollPane);
            }
        });
        return scrollPane;
    }

    public static void resetScrollPosition(Component component) {
        ArrayList<JScrollPane> scrollPanes = new ArrayList<>();
        collectScrollPanes(component, scrollPanes);
        if (scrollPanes.isEmpty()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            resetScrollPanes(scrollPanes);
            Timer timer = new Timer(40, null);
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

    private static void collectScrollPanes(Component component, ArrayList<JScrollPane> scrollPanes) {
        if (component instanceof JScrollPane) {
            scrollPanes.add((JScrollPane) component);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                collectScrollPanes(child, scrollPanes);
            }
        }
    }

    private static void resetScrollPanes(ArrayList<JScrollPane> scrollPanes) {
        for (JScrollPane scrollPane : scrollPanes) {
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
            scrollPane.getVerticalScrollBar().setValue(0);
            scrollPane.getHorizontalScrollBar().setValue(0);
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
            protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
                if (!component.isEnabled() || thumbBounds.isEmpty()) {
                    return;
                }
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(thumbColor);
                graphics2D.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                graphics2D.dispose();
            }

            @Override
            protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(trackColor);
                graphics2D.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                graphics2D.dispose();
            }
        });
    }

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

    public static String location(Report report) {
        if (report instanceof LostReport) {
            return ((LostReport) report).getLostLocation();
        }
        if (report instanceof FoundReport) {
            return ((FoundReport) report).getFoundLocation();
        }
        return report != null && report.getItem() != null ? report.getItem().getLocation() : EMPTY_TEXT;
    }

    private static String normalizeText(String text) {
        return text == null || text.isBlank() ? EMPTY_TEXT : text;
    }

    public static JButton plainButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
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
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(color);
            graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            graphics2D.dispose();
            super.paintComponent(graphics);
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
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setPaint(new java.awt.GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            graphics2D.setColor(new Color(255, 255, 255, 36));
            graphics2D.fillOval(getWidth() - 64, -28, 96, 96);
            graphics2D.dispose();
            super.paintComponent(graphics);
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
        public Insets getBorderInsets(Component component) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(color);
            graphics2D.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            graphics2D.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
            graphics2D.dispose();
        }
    }

    public static class SearchField extends JTextField {
        private final String placeholder;

        public SearchField(String placeholder) {
            super(20);
            this.placeholder = placeholder;
            setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
            setForeground(TEXT_DARK);
            setCaretColor(TEXT_DARK);
            setPreferredSize(new Dimension(250, 40));
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedLineBorder(BORDER, 20, 1),
                    BorderFactory.createEmptyBorder(5, 35, 5, 15)
            ));
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent event) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent event) {
                    repaint();
                }
            });
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent event) {
                    repaint();
                }

                @Override
                public void removeUpdate(DocumentEvent event) {
                    repaint();
                }

                @Override
                public void changedUpdate(DocumentEvent event) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(TEXT_MUTED);
            graphics2D.setStroke(new BasicStroke(1.6f));
            int centerY = getHeight() / 2;
            graphics2D.drawOval(13, centerY - 6, 10, 10);
            graphics2D.drawLine(21, centerY + 3, 26, centerY + 8);
            if (getText().isEmpty() && !isFocusOwner()) {
                graphics2D.setFont(getFont());
                graphics2D.drawString(placeholder, 35, centerY + 5);
            }
            graphics2D.dispose();
        }
    }

    public static class FilterPill extends JButton {
        private boolean active;

        public FilterPill(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            updateStyle();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    if (!FilterPill.this.active) {
                        setForeground(PRIMARY_DARK);
                        setBorder(BorderFactory.createCompoundBorder(
                                new RoundedLineBorder(PRIMARY, 16, 1),
                                BorderFactory.createEmptyBorder(6, 14, 6, 14)
                        ));
                    }
                }

                @Override
                public void mouseExited(MouseEvent event) {
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
        protected void paintComponent(Graphics graphics) {
            if (active) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(TEXT_DARK);
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                graphics2D.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static class ViewportWidthPanel extends JPanel implements Scrollable {
        ViewportWidthPanel(Component content) {
            super(new GridBagLayout());
            setOpaque(false);
            GridBagConstraints constraints = contentConstraints();
            constraints.gridy = 0;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
            add(content, constraints);
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
                        right.setBounds(insets.left + availableWidth - rightSize.width, insets.top + ((rowHeight - rightSize.height) / 2), rightSize.width, rightSize.height);
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
}