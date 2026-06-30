package com.frame.dashboard.shared;

import com.frame.AppDialog;
import com.managers.ClaimManager;
import com.model.Claim;
import com.model.Item;
import com.model.Report;
import com.model.User;
import com.model.VerificationDocument;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClaimVerificationFrame extends JDialog {

    private static final String TITLE = "Verifikasi Klaim Barang";
    private static final String SUBTITLE = "Lengkapi Data Kepemilikan Untuk Mengajukan Klaim.";
    private static final Dimension FRAME_SIZE = new Dimension(900, 640);

    private final User user;
    private final Report report;
    private final ClaimManager claimManager;
    private final Runnable onSaved;

    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private PhotoPreviewPanel photoPreviewPanel;
    private String selectedPhotoPath;

    // -------------------------------------------------------------------------
    // Frame Setup
    // -------------------------------------------------------------------------

    public ClaimVerificationFrame(User user, Report report, ClaimManager claimManager, Runnable onSaved) {
        super((java.awt.Frame) null, TITLE, true);
        this.user = user;
        this.report = report;
        this.claimManager = claimManager;
        this.onSaved = onSaved;
        initComponents();
    }

    private void initComponents() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(FRAME_SIZE);
        setPreferredSize(FRAME_SIZE);
        setResizable(false);
        setContentPane(createMainPanel());
        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // Main Layout
    // -------------------------------------------------------------------------

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DashboardUi.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        root.add(DashboardUi.section(TITLE, SUBTITLE), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 16);
        content.add(createFormPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 16, 0, 0);
        photoPreviewPanel = new PhotoPreviewPanel(this::choosePhoto);
        content.add(photoPreviewPanel, gbc);

        root.add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JButton cancelButton = createSecondaryButton("Batal");
        cancelButton.addActionListener(event -> dispose());
        JButton submitButton = createPrimaryButton("Ajukan Klaim");
        submitButton.addActionListener(event -> submitClaim());
        footer.add(cancelButton);
        footer.add(submitButton);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    // -------------------------------------------------------------------------
    // Form UI
    // -------------------------------------------------------------------------

    private JPanel createFormPanel() {
        DashboardUi.RoundedPanel panel = new DashboardUi.RoundedPanel(Color.WHITE, 20);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(DashboardUi.BORDER, 20, 1),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        nameField = createTextField("Nama lengkap sesuai identitas");
        addField(panel, gbc, 0, "Nama*", nameField);

        addressField = createTextField("Alamat tempat tinggal");
        addField(panel, gbc, 1, "Alamat*", addressField);

        phoneField = createTextField("Contoh: 081234567890");
        addField(panel, gbc, 2, "No telepon*", phoneField);

        gbc.gridy = 6;
        gbc.insets = new Insets(18, 0, 0, 0);
        panel.add(createItemSummary(), gbc);

        gbc.gridy = 7;
        gbc.weighty = 1;
        panel.add(createVerticalSpacer(), gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input) {
        gbc.gridy = row * 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(row == 0 ? 0 : 18, 0, 6, 0);
        panel.add(DashboardUi.label(label, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(input, gbc);
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) graphics.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    int y = (getHeight() - graphics.getFontMetrics().getHeight()) / 2 + graphics.getFontMetrics().getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        field.setPreferredSize(new Dimension(300, 45));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setForeground(DashboardUi.TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        return field;
    }

    private JPanel createItemSummary() {
        DashboardUi.RoundedPanel panel = new DashboardUi.RoundedPanel(new Color(248, 250, 252), 16);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 236, 244), 16, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Item item = report == null ? null : report.getItem();
        panel.add(DashboardUi.label("Barang Yang Diklaim", 12, Font.BOLD, DashboardUi.TEXT_MUTED), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        panel.add(DashboardUi.label(item == null ? "-" : item.getName(), 16, Font.BOLD, DashboardUi.TEXT_DARK), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 0, 0);
        String category = item == null || item.getCategory() == null ? "Tanpa kategori" : item.getCategory().getName();
        panel.add(DashboardUi.label(category, 12, Font.PLAIN, DashboardUi.TEXT_MUTED), gbc);
        return panel;
    }

    private JPanel createVerticalSpacer() {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        return spacer;
    }

    // -------------------------------------------------------------------------
    // Button UI
    // -------------------------------------------------------------------------

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(DashboardUi.PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(DashboardUi.TEXT_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(220, 220, 220), 20, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new RoundedButton(text);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        return button;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void choosePhoto() {
        FileDialog fileDialog = new FileDialog(this, "Pilih Foto Bukti Kepemilikan", FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
        });
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            return;
        }

        File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
        selectedPhotoPath = file.getAbsolutePath();
        photoPreviewPanel.setImageFile(file);
    }

    private void submitClaim() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        boolean confirm = AppDialog.confirm(this, "Konfirmasi Klaim", "Ajukan klaim barang dengan data verifikasi ini?", "Ajukan", "Batal");
        if (!confirm) {
            return;
        }

        try {
            claimManager.submitClaim(user, report, name, address, phone, selectedPhotoPath);
            if (onSaved != null) {
                onSaved.run();
            }
            AppDialog.success(this, "Klaim Berhasil", "Permintaan klaim Anda telah diajukan dan menunggu persetujuan.");
            dispose();
        } catch (com.exception.ValidationException e) {
            AppDialog.warning(this, "Validasi Gagal", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            AppDialog.error(this, "Terjadi Kesalahan", "Terjadi kesalahan sistem:\n" + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Custom Components
    // -------------------------------------------------------------------------

    private static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color background = getModel().isPressed() ? getBackground().darker() : getBackground();
            if (getModel().isRollover() && !getModel().isPressed()) {
                background = new Color(
                        Math.max(0, background.getRed() - 8),
                        Math.max(0, background.getGreen() - 8),
                        Math.max(0, background.getBlue() - 8)
                );
            }
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private class PhotoPreviewPanel extends JPanel {
        private Image image;
        private boolean hovered;

        PhotoPreviewPanel(Runnable onClick) {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent event) {
                    if (image != null) {
                        hovered = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent event) {
                    hovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent event) {
                    onClick.run();
                }
            });
        }

        void setImageFile(File file) {
            this.image = new ImageIcon(file.getAbsolutePath()).getImage();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, width, height, 20, 20);
            Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2.setStroke(dashed);
            g2.setColor(new Color(200, 220, 240));
            g2.drawRoundRect(1, 1, width - 3, height - 3, 20, 20);
            if (image == null) {
                paintEmptyState(g2, width, height);
            } else {
                paintImageState(g2, width, height);
            }
            g2.dispose();
        }

        private void paintEmptyState(Graphics2D g2, int width, int height) {
            String title = "Unggah bukti kepemilikan";
            String button = "Pilih dari komputer";
            int blockY = (height - 140) / 2;
            int cx = width / 2;
            int cy = blockY + 30;
            g2.setColor(DashboardUi.PRIMARY_DARK);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - 20, cy + 8, cx - 20, cy + 20);
            g2.drawLine(cx - 20, cy + 20, cx + 20, cy + 20);
            g2.drawLine(cx + 20, cy + 20, cx + 20, cy + 8);
            g2.drawLine(cx, cy + 8, cx, cy - 18);
            g2.drawLine(cx - 10, cy - 8, cx, cy - 18);
            g2.drawLine(cx + 10, cy - 8, cx, cy - 18);
            g2.setFont(new Font("Poppins", Font.BOLD, 14));
            g2.setColor(DashboardUi.TEXT_DARK);
            g2.drawString(title, (width - g2.getFontMetrics().stringWidth(title)) / 2, blockY + 80);
            g2.setColor(DashboardUi.PRIMARY);
            int buttonWidth = 170;
            int buttonHeight = 36;
            int buttonX = (width - buttonWidth) / 2;
            int buttonY = blockY + 100;
            g2.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 18, 18);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Poppins", Font.BOLD, 13));
            g2.drawString(button, (width - g2.getFontMetrics().stringWidth(button)) / 2, buttonY + 23);
        }

        private void paintImageState(Graphics2D g2, int width, int height) {
            g2.setClip(new java.awt.geom.RoundRectangle2D.Double(2, 2, width - 4, height - 4, 18, 18));
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);
            double scale = Math.min((double) width / imageWidth, (double) height / imageHeight);
            int drawWidth = (int) Math.round(imageWidth * scale);
            int drawHeight = (int) Math.round(imageHeight * scale);
            int x = (width - drawWidth) / 2;
            int y = (height - drawHeight) / 2;
            g2.drawImage(image, x, y, drawWidth, drawHeight, this);
            if (hovered) {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(2, 2, width - 4, height - 4, 18, 18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 15));
                String hoverText = "Ganti foto";
                FontMetrics metrics = g2.getFontMetrics();
                g2.drawString(hoverText, (width - metrics.stringWidth(hoverText)) / 2, (height + metrics.getAscent()) / 2);
            }
        }
    }
}
