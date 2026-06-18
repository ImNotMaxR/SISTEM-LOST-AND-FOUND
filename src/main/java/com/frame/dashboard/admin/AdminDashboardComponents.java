package com.frame.dashboard.admin;

import com.frame.dashboard.user.UserDashboardComponents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class AdminDashboardComponents {

    private static final Color STATUS_PENDING_TEXT = new Color(181, 112, 0);
    private static final Color STATUS_PENDING_BACKGROUND = new Color(255, 246, 229);
    private static final Color STATUS_VALID_TEXT = new Color(20, 125, 78);
    private static final Color STATUS_VALID_BACKGROUND = new Color(226, 250, 238);
    private static final Color STATUS_FOUND_TEXT = new Color(12, 139, 204);
    private static final Color STATUS_FOUND_BACKGROUND = new Color(232, 246, 255);
    private static final Color STATUS_REJECT_TEXT = new Color(184, 34, 44);
    private static final Color STATUS_REJECT_BACKGROUND = new Color(255, 238, 240);
    private static final Color STATUS_DEFAULT_BACKGROUND = new Color(235, 240, 247);

    private AdminDashboardComponents() {
    }

    public static JPanel statRow() {
        return UserDashboardComponents.statGrid();
    }

    public static JPanel statCard(String title, int value, String subtitle, Color start, Color end) {
        return new StatCard(title, String.valueOf(value), subtitle, start, end);
    }

    public static JTable table(String[] columns, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(54);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionBackground(new Color(237, 246, 255));
        table.setSelectionForeground(UserDashboardComponents.TEXT_DARK);
        table.setFont(new Font("Poppins", Font.PLAIN, 12));
        table.setForeground(UserDashboardComponents.TEXT_MUTED);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        for (int i = 0; i < columns.length; i++) {
            if ("STATUS".equalsIgnoreCase(columns[i])) {
                table.getColumnModel().getColumn(i).setCellRenderer(new StatusRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(new TextRenderer(i == 0 || i == 1));
            }
        }

        return table;
    }

    public static JPanel tableSection(String title, JTable table) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        wrapper.add(UserDashboardComponents.label(title, 18, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 0, 0);
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 18);
        card.setLayout(new GridBagLayout());
        card.setMinimumSize(new Dimension(260, Math.max(126, table.getRowHeight() * Math.max(1, table.getRowCount()) + 40)));
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        GridBagConstraints tableGbc = new GridBagConstraints();
        tableGbc.gridx = 0;
        tableGbc.gridy = 0;
        tableGbc.weightx = 1;
        tableGbc.weighty = 1;
        tableGbc.fill = GridBagConstraints.BOTH;
        card.add(table.getTableHeader(), tableGbc);

        tableGbc.gridy = 1;
        card.add(table, tableGbc);
        wrapper.add(card, gbc);

        return wrapper;
    }

    public static JButton sidebarButton(String text, String symbol, boolean active) {
        JButton button = new SidebarButton(text, symbol);
        button.setSelected(active);
        return button;
    }

    public static void setSidebarButtonActive(JButton button, boolean active) {
        button.setSelected(active);
        button.repaint();
    }

    private static class StatCard extends UserDashboardComponents.GradientPanel {

        StatCard(String title, String value, String subtitle, Color start, Color end) {
            super(start, end, 18);
            setPreferredSize(new Dimension(260, 126));
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            add(UserDashboardComponents.label(title, 14, Font.PLAIN, Color.WHITE), gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(8, 0, 4, 0);
            add(UserDashboardComponents.label(value, 28, Font.BOLD, Color.WHITE), gbc);
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(UserDashboardComponents.label(subtitle, 11, Font.PLAIN, new Color(239, 248, 255)), gbc);
        }
    }

    private static class HeaderRenderer extends JLabel implements javax.swing.table.TableCellRenderer {

        HeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(232, 243, 251));
            setForeground(UserDashboardComponents.TEXT_MUTED);
            setFont(new Font("Poppins", Font.BOLD, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private static class TextRenderer extends DefaultTableCellRenderer {

        private final boolean strong;

        TextRenderer(boolean strong) {
            this.strong = strong;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setFont(new Font("Poppins", strong ? Font.BOLD : Font.PLAIN, 12));
            setForeground(strong ? UserDashboardComponents.TEXT_DARK : UserDashboardComponents.TEXT_MUTED);
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 12));
            return this;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {

        StatusRenderer() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                int row, int column) {
            String text = value == null ? "-" : value.toString();
            return new StatusBadge(text, statusForeground(text), statusBackground(text));
        }
    }

    private static class StatusBadge extends JPanel {

        private final String text;
        private final Color foreground;
        private final Color background;

        StatusBadge(String text, Color foreground, Color background) {
            setLayout(new GridBagLayout());
            this.text = text;
            this.foreground = foreground;
            this.background = background;
            setOpaque(false);
            setPreferredSize(new Dimension(96, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Poppins", Font.BOLD, 11));
            g2.setColor(background);
            int horizontalPadding = 12;
            int maxWidth = Math.max(34, getWidth() - 24);
            String displayText = fitText(g2, text, Math.max(12, maxWidth - (horizontalPadding * 2)));
            int width = Math.min(maxWidth, Math.max(56, g2.getFontMetrics().stringWidth(displayText) + (horizontalPadding * 2)));
            int x = Math.min(16, Math.max(0, getWidth() - width));
            int height = 28;
            int y = (getHeight() - height) / 2;
            g2.fillRoundRect(x, y, width, height, 18, 18);
            g2.setColor(foreground);
            int textWidth = g2.getFontMetrics().stringWidth(displayText);
            int textY = y + ((height - g2.getFontMetrics().getHeight()) / 2) + g2.getFontMetrics().getAscent();
            g2.drawString(displayText, x + (width - textWidth) / 2, textY);
            g2.dispose();
        }

        private String fitText(Graphics2D graphics, String value, int maxWidth) {
            if (graphics.getFontMetrics().stringWidth(value) <= maxWidth) {
                return value;
            }

            String ellipsis = "...";
            int ellipsisWidth = graphics.getFontMetrics().stringWidth(ellipsis);
            if (ellipsisWidth >= maxWidth) {
                return ellipsis;
            }

            StringBuilder builder = new StringBuilder(value);
            while (builder.length() > 0
                    && graphics.getFontMetrics().stringWidth(builder.toString()) + ellipsisWidth > maxWidth) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString() + ellipsis;
        }
    }

    private static class SidebarButton extends JButton {

        private final String symbol;
        private boolean active;

        SidebarButton(String text, String symbol) {
            super(text);
            this.symbol = symbol;
            setPreferredSize(new Dimension(270, 46));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Poppins", Font.PLAIN, 14));
            setHorizontalAlignment(LEFT);
            setIconTextGap(12);
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            setActive(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    if (!active) {
                        setForeground(UserDashboardComponents.PRIMARY);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    if (!active) {
                        setForeground(UserDashboardComponents.TEXT_MUTED);
                        repaint();
                    }
                }
            });
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            setActive(selected);
        }

        private void setActive(boolean active) {
            this.active = active;
            setFont(new Font("Poppins", active ? Font.BOLD : Font.PLAIN, 14));
            setForeground(active ? Color.WHITE : UserDashboardComponents.TEXT_MUTED);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(UserDashboardComponents.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(235, 244, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            }
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 15));
            g2.setColor(active ? Color.WHITE : UserDashboardComponents.TEXT_MUTED);
            g2.drawString(symbol, 16, getHeight() / 2 + 5);
            g2.dispose();

            setBorder(BorderFactory.createEmptyBorder(12, 46, 12, 16));
            super.paintComponent(g);
        }
    }

    private static Color statusForeground(String status) {
        String normalized = normalize(status);
        if (normalized.contains("PENDING") || normalized.contains("MENUNGGU")) {
            return STATUS_PENDING_TEXT;
        }
        if (normalized.contains("VALID") || normalized.contains("DITERIMA")) {
            return STATUS_VALID_TEXT;
        }
        if (normalized.contains("DITEMUKAN") || normalized.contains("DICARI")) {
            return STATUS_FOUND_TEXT;
        }
        if (normalized.contains("DITOLAK")) {
            return STATUS_REJECT_TEXT;
        }
        return UserDashboardComponents.PRIMARY_DARK;
    }

    private static Color statusBackground(String status) {
        String normalized = normalize(status);
        if (normalized.contains("PENDING") || normalized.contains("MENUNGGU")) {
            return STATUS_PENDING_BACKGROUND;
        }
        if (normalized.contains("VALID") || normalized.contains("DITERIMA")) {
            return STATUS_VALID_BACKGROUND;
        }
        if (normalized.contains("DITEMUKAN") || normalized.contains("DICARI")) {
            return STATUS_FOUND_BACKGROUND;
        }
        if (normalized.contains("DITOLAK")) {
            return STATUS_REJECT_BACKGROUND;
        }
        return STATUS_DEFAULT_BACKGROUND;
    }

    private static String normalize(String status) {
        return status == null ? "" : status.toUpperCase();
    }
}
