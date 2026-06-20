package com.frame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
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
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class AppDialog {

    private static final Color PRIMARY_DARK = new Color(44, 94, 173);
    private static final Color PRIMARY = new Color(21, 145, 220);
    private static final Color SURFACE = new Color(247, 251, 254);
    private static final Color BORDER = new Color(214, 226, 239);
    private static final Color TEXT_DARK = new Color(8, 20, 38);
    private static final Color TEXT_MUTED = new Color(83, 108, 135);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color ERROR = new Color(220, 38, 38);
    private static final Color SUCCESS = new Color(22, 163, 74);

    private AppDialog() {
    }

    public static void success(Component parent, String title, String message) {
        show(parent, title, message, SUCCESS, new String[]{"OK"}, 0);
    }

    public static void warning(Component parent, String title, String message) {
        show(parent, title, message, WARNING, new String[]{"OK"}, 0);
    }

    public static void error(Component parent, String title, String message) {
        show(parent, title, message, ERROR, new String[]{"OK"}, 0);
    }

    public static boolean confirm(Component parent, String title, String message, String confirmText, String cancelText) {
        return show(parent, title, message, PRIMARY, new String[]{cancelText, confirmText}, 1) == 1;
    }

    public static String promptText(Component parent, String title, String message, String confirmText, String cancelText) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        String[] result = {null};

        RoundedPanel card = new RoundedPanel(Color.WHITE, 24);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(620, 410));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 24, 1),
                BorderFactory.createEmptyBorder(28, 30, 26, 30)
        ));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        content.add(titleLabel, gbc);

        JLabel messageLabel = new JLabel("<html><body style='width:540px'>" + escape(message) + "</body></html>");
        messageLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 12, 0);
        content.add(messageLabel, gbc);

        JTextArea input = new JTextArea();
        input.setRows(6);
        input.setFont(new Font("Poppins", Font.PLAIN, 13));
        input.setForeground(TEXT_DARK);
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputScroll.setBorder(new RoundedLineBorder(BORDER, 16, 1));
        inputScroll.setPreferredSize(new Dimension(540, 150));
        inputScroll.setMinimumSize(new Dimension(540, 150));
        applyPromptScrollStyle(inputScroll);
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(inputScroll, gbc);
        card.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints actionGbc = new GridBagConstraints();
        actionGbc.gridy = 0;
        actionGbc.insets = new Insets(0, 8, 0, 0);
        actionGbc.anchor = GridBagConstraints.EAST;

        JButton cancelButton = new DialogButton(cancelText, false, PRIMARY);
        cancelButton.addActionListener(event -> dialog.dispose());
        actions.add(cancelButton, actionGbc);

        JButton confirmButton = new DialogButton(confirmText, true, PRIMARY);
        confirmButton.addActionListener(event -> {
            result[0] = input.getText();
            dialog.dispose();
        });
        actionGbc.gridx = 1;
        actions.add(confirmButton, actionGbc);
        dialog.getRootPane().setDefaultButton(confirmButton);
        card.add(actions, BorderLayout.SOUTH);

        JPanel shadow = new JPanel(new BorderLayout());
        shadow.setOpaque(false);
        shadow.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        shadow.add(card, BorderLayout.CENTER);

        dialog.setContentPane(shadow);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        SwingUtilities.invokeLater(input::requestFocusInWindow);
        dialog.setVisible(true);
        return result[0];
    }

    private static void applyPromptScrollStyle(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = PRIMARY;
                trackColor = SURFACE;
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
            protected void paintThumb(Graphics graphics, JComponent component, java.awt.Rectangle thumbBounds) {
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
            protected void paintTrack(Graphics graphics, JComponent component, java.awt.Rectangle trackBounds) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(trackColor);
                graphics2D.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                graphics2D.dispose();
            }
        });
    }

    private static int show(Component parent, String title, String message, Color accent, String[] buttons, int defaultIndex) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int[] selected = {-1};

        RoundedPanel card = new RoundedPanel(Color.WHITE, 24);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 210));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 24, 1),
                BorderFactory.createEmptyBorder(28, 30, 26, 30)
        ));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 16);
        content.add(new DialogIcon(accent), gbc);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints textGbc = new GridBagConstraints();
        textGbc.gridx = 0;
        textGbc.weightx = 1;
        textGbc.fill = GridBagConstraints.HORIZONTAL;
        textGbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        textGbc.gridy = 0;
        textPanel.add(titleLabel, textGbc);

        JLabel messageLabel = new JLabel("<html><body style='width:300px'>" + escape(message) + "</body></html>");
        messageLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_MUTED);
        textGbc.gridy = 1;
        textGbc.insets = new Insets(8, 0, 0, 0);
        textPanel.add(messageLabel, textGbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(textPanel, gbc);
        card.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints actionGbc = new GridBagConstraints();
        actionGbc.gridy = 0;
        actionGbc.insets = new Insets(0, 8, 0, 0);
        actionGbc.anchor = GridBagConstraints.EAST;

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            boolean primary = i == defaultIndex;
            JButton button = new DialogButton(buttons[i], primary, accent);
            button.addActionListener(event -> {
                selected[0] = index;
                dialog.dispose();
            });
            actionGbc.gridx = i;
            actions.add(button, actionGbc);
            if (primary) {
                dialog.getRootPane().setDefaultButton(button);
            }
        }

        card.add(actions, BorderLayout.SOUTH);

        JPanel shadow = new JPanel(new BorderLayout());
        shadow.setOpaque(false);
        shadow.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        shadow.add(card, BorderLayout.CENTER);

        dialog.setContentPane(shadow);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return selected[0];
    }

    private static String escape(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static class DialogIcon extends JPanel {

        private final Color color;

        DialogIcon(Color color) {
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(48, 48));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 24));
            g2.fillOval(0, 0, 48, 48);
            g2.setColor(color);
            g2.fillOval(15, 15, 18, 18);
            g2.dispose();
        }
    }

    private static class DialogButton extends JButton {

        private final boolean primary;
        private final Color accent;

        DialogButton(String text, boolean primary, Color accent) {
            super(text);
            this.primary = primary;
            this.accent = accent;
            setPreferredSize(new Dimension(Math.max(92, text.length() * 11), 38));
            setFont(new Font("Poppins", Font.BOLD, 13));
            setForeground(primary ? Color.WHITE : PRIMARY_DARK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background;
            if (primary) {
                background = getModel().isPressed() ? accent.darker() : accent;
            } else {
                background = getModel().isPressed() ? new Color(226, 240, 253) : SURFACE;
            }
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            if (!primary) {
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedPanel extends JPanel {

        private final Color color;
        private final int radius;

        RoundedPanel(Color color, int radius) {
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

    private static class RoundedLineBorder extends AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedLineBorder(Color color, int radius, int thickness) {
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
}
