package com.frame.panel;

import com.database.DBConnection;
import com.frame.AppDialog;
import com.frame.dashboard.user.UserDashboardComponents;
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.model.Category;
import com.model.Item;
import com.model.LostReport;
import com.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LostReportPanel extends JFrame {

    private static final String TITLE = "Buat Laporan Barang Hilang";
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(720, 620);
    private static final Dimension FIELD_SIZE = new Dimension(520, 42);
    private static final Dimension TEXT_AREA_SIZE = new Dimension(520, 92);
    private static final Dimension PHOTO_PREVIEW_SIZE = new Dimension(520, 220);

    private final User user;
    private final ReportManager reportManager;
    private final ItemManager itemManager;
    private final Runnable onReportSaved;

    private JTextField itemNameField;
    private JTextField lostLocationField;
    private JTextArea itemDescriptionArea;
    private JTextArea reportDescriptionArea;
    private JComboBox<Category> categoryComboBox;
    private JLabel selectedPhotoLabel;
    private PhotoPreviewPanel photoPreviewPanel;
    private JButton removePhotoButton;
    private File selectedPhotoFile;

    public LostReportPanel(User user, ReportManager reportManager, Runnable onReportSaved) {
        this.user = user;
        this.reportManager = reportManager;
        this.itemManager = new ItemManager();
        this.onReportSaved = onReportSaved;
        initComponents();
    }

    public LostReportPanel() {
        this(null, new ReportManager(), null);
    }

    // =========================
    // UI Initialization
    // =========================

    private void initComponents() {
        configureFrame();
        setContentPane(createMainPanel());
        pack();
        setLocationRelativeTo(null);
    }

    private void configureFrame() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(MINIMUM_FRAME_SIZE);
        setPreferredSize(calculatePreferredFrameSize());
        setResizable(true);
    }

    private Dimension calculatePreferredFrameSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(920, screenSize.width - 160);
        int height = Math.min(820, screenSize.height - 120);
        return new Dimension(Math.max(MINIMUM_FRAME_SIZE.width, width), Math.max(MINIMUM_FRAME_SIZE.height, height));
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UserDashboardComponents.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        content.add(UserDashboardComponents.section(TITLE, "Isi data barang hilang yang ingin kamu laporkan."), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(22, 0, 0, 0);
        content.add(createFormCard(), gbc);

        root.add(UserDashboardComponents.scroll(content), BorderLayout.CENTER);
        return root;
    }

    private JPanel createFormCard() {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 22);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 22, 1),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();

        itemNameField = createTextField();
        addField(card, gbc, 0, "Nama Barang", itemNameField);

        itemDescriptionArea = createTextArea();
        addField(card, gbc, 1, "Deskripsi Barang", createTextAreaScroll(itemDescriptionArea));

        lostLocationField = createTextField();
        addField(card, gbc, 2, "Lokasi Hilang", lostLocationField);

        reportDescriptionArea = createTextArea();
        addField(card, gbc, 3, "Deskripsi Laporan", createTextAreaScroll(reportDescriptionArea));

        categoryComboBox = createCategoryComboBox();
        addField(card, gbc, 4, "Kategori", categoryComboBox);

        addField(card, gbc, 5, "Foto Barang", createPhotoPicker());

        gbc.gridy = 6;
        gbc.insets = new Insets(24, 0, 0, 0);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        card.add(createActionPanel(), gbc);

        return card;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component input) {
        gbc.gridy = row;
        gbc.insets = new Insets(row == 0 ? 0 : 16, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints fieldGbc = UserDashboardComponents.contentConstraints();
        fieldGbc.gridy = 0;
        wrapper.add(UserDashboardComponents.label(label, 13, Font.BOLD, UserDashboardComponents.TEXT_MUTED), fieldGbc);

        fieldGbc.gridy = 1;
        fieldGbc.insets = new Insets(7, 0, 0, 0);
        wrapper.add(input, fieldGbc);

        panel.add(wrapper, gbc);
    }

    // =========================
    // Component Factories
    // =========================

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(FIELD_SIZE);
        field.setFont(new Font("Poppins", Font.PLAIN, 14));
        field.setForeground(UserDashboardComponents.TEXT_DARK);
        field.setBackground(new Color(249, 252, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 16, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Poppins", Font.PLAIN, 14));
        area.setForeground(UserDashboardComponents.TEXT_DARK);
        area.setBackground(new Color(249, 252, 255));
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return area;
    }

    private JScrollPane createTextAreaScroll(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(TEXT_AREA_SIZE);
        scrollPane.setBorder(new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 16, 1));
        scrollPane.getViewport().setBackground(new Color(249, 252, 255));
        return scrollPane;
    }

    private JComboBox<Category> createCategoryComboBox() {
        JComboBox<Category> comboBox = new JComboBox<>();
        comboBox.setPreferredSize(FIELD_SIZE);
        comboBox.setFont(new Font("Poppins", Font.PLAIN, 14));
        comboBox.setForeground(UserDashboardComponents.TEXT_DARK);
        comboBox.setBackground(new Color(249, 252, 255));
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 16, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) new DefaultListCellRenderer()
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Poppins", Font.PLAIN, 13));
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            if (value instanceof Category) {
                Category category = (Category) value;
                label.setText(category.getName()
                        + (category.isVerificationRequired() ? " - butuh dokumen saat klaim" : ""));
            }
            return label;
        });

        for (Category category : loadCategories()) {
            comboBox.addItem(category);
        }
        return comboBox;
    }

    private JPanel createPhotoPicker() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        photoPreviewPanel = createPhotoPreviewPanel();
        selectedPhotoLabel = UserDashboardComponents.label("Belum ada foto dipilih", 13, Font.PLAIN, UserDashboardComponents.TEXT_MUTED);
        JButton chooseButton = createSecondaryButton("Pilih Foto");
        chooseButton.addActionListener(event -> choosePhoto());
        removePhotoButton = createSecondaryButton("Hapus Foto");
        removePhotoButton.addActionListener(event -> clearPhoto());
        removePhotoButton.setEnabled(false);

        GridBagConstraints gbc = UserDashboardComponents.contentConstraints();
        gbc.gridy = 0;
        panel.add(photoPreviewPanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        JPanel pickerRow = new JPanel(new BorderLayout(12, 0));
        pickerRow.setOpaque(false);
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 0;
        buttonGbc.insets = new Insets(0, 0, 0, 8);
        buttonPanel.add(chooseButton, buttonGbc);
        buttonGbc.gridx = 1;
        buttonGbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(removePhotoButton, buttonGbc);

        pickerRow.add(selectedPhotoLabel, BorderLayout.CENTER);
        pickerRow.add(buttonPanel, BorderLayout.EAST);
        panel.add(pickerRow, gbc);

        return panel;
    }

    private PhotoPreviewPanel createPhotoPreviewPanel() {
        PhotoPreviewPanel preview = new PhotoPreviewPanel();
        preview.setPreferredSize(PHOTO_PREVIEW_SIZE);
        preview.setMinimumSize(new Dimension(360, 180));
        preview.setBorder(new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 18, 1));
        return preview;
    }

    private JPanel createActionPanel() {
        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        JButton cancelButton = createSecondaryButton("Batal");
        cancelButton.addActionListener(event -> dispose());
        actions.add(cancelButton, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton submitButton = createPrimaryButton("Simpan Laporan");
        submitButton.addActionListener(event -> submitLostReport());
        actions.add(submitButton, gbc);

        return actions;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(UserDashboardComponents.PRIMARY_DARK);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(UserDashboardComponents.PRIMARY_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 16, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new RoundedButton(text);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        return button;
    }

    // =========================
    // Form Actions
    // =========================

    private void choosePhoto() {
        FileDialog fileDialog = new FileDialog(this, "Pilih Foto Barang", FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
        });
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            return;
        }

        File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
        if (!isSupportedImage(file)) {
            AppDialog.warning(this, "Format Tidak Didukung", "Foto harus berformat JPG atau PNG.");
            return;
        }

        selectedPhotoFile = file;
        selectedPhotoLabel.setText(file.getName());
        updatePhotoPreview(file);
        updatePhotoButtonState();
    }

    private void updatePhotoPreview(File file) {
        photoPreviewPanel.setImageFile(file);
    }

    private void clearPhoto() {
        selectedPhotoFile = null;
        selectedPhotoLabel.setText("Belum ada foto dipilih");
        photoPreviewPanel.clearImage();
        updatePhotoButtonState();
    }

    private void updatePhotoButtonState() {
        if (removePhotoButton != null) {
            removePhotoButton.setEnabled(selectedPhotoFile != null);
        }
    }

    private void submitLostReport() {
        if (user == null) {
            AppDialog.error(this, "Gagal Membuat Laporan", "Data user tidak ditemukan. Silakan login ulang.");
            return;
        }

        String itemName = itemNameField.getText().trim();
        String itemDescription = itemDescriptionArea.getText().trim();
        String lostLocation = lostLocationField.getText().trim();
        String reportDescription = reportDescriptionArea.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        if (itemName.isEmpty() || itemDescription.isEmpty() || lostLocation.isEmpty()
                || reportDescription.isEmpty() || category == null) {
            AppDialog.warning(this, "Data Belum Lengkap", "Semua input wajib diisi sebelum menyimpan laporan.");
            return;
        }

        String itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Item item = new Item(itemId, itemName, itemDescription, category, lostLocation);
        itemManager.addItem(item);

        LostReport report = new LostReport(reportId, user, item, reportDescription, lostLocation);
        if (selectedPhotoFile != null) {
            report.addEvidence(selectedPhotoFile.getAbsolutePath());
        }
        reportManager.addReport(report);

        if (onReportSaved != null) {
            onReportSaved.run();
        }

        AppDialog.success(this, "Laporan Disimpan", "Laporan barang hilang berhasil dibuat.");
        dispose();
    }

    // =========================
    // Data Helpers
    // =========================

    private ArrayList<Category> loadCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getString("category_id"),
                        resultSet.getString("name"),
                        resultSet.getBoolean("request_verification")
                ));
            }
        } catch (SQLException exception) {
            AppDialog.error(this, "Gagal Memuat Kategori", exception.getMessage());
        }

        return categories;
    }

    private boolean isSupportedImage(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LostReportPanel().setVisible(true));
    }

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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class PhotoPreviewPanel extends JPanel {

        private Image image;

        PhotoPreviewPanel() {
            setOpaque(false);
            setBackground(new Color(239, 246, 252));
            setFont(new Font("Poppins", Font.BOLD, 14));
            setForeground(UserDashboardComponents.TEXT_MUTED);
        }

        void setImageFile(File file) {
            this.image = new ImageIcon(file.getAbsolutePath()).getImage();
            repaint();
        }

        void clearImage() {
            this.image = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            java.awt.Shape round = new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18);
            g2.setClip(round);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            if (image == null || image.getWidth(this) <= 0 || image.getHeight(this) <= 0) {
                paintPlaceholder(g2);
            } else {
                paintCoverImage(g2);
            }

            g2.setClip(null);
            g2.dispose();
            super.paintComponent(graphics);
        }

        private void paintPlaceholder(Graphics2D g2) {
            g2.setColor(UserDashboardComponents.TEXT_MUTED);
            g2.setFont(getFont());
            String text = "Preview Foto";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(text, (getWidth() - textWidth) / 2, y);
        }

        private void paintCoverImage(Graphics2D g2) {
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);
            double scale = Math.min((double) getWidth() / imageWidth, (double) getHeight() / imageHeight);
            int drawWidth = (int) Math.round(imageWidth * scale);
            int drawHeight = (int) Math.round(imageHeight * scale);
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;
            g2.drawImage(image, x, y, drawWidth, drawHeight, this);
        }
    }
}
