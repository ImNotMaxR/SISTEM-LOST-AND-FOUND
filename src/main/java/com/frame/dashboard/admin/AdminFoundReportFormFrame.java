package com.frame.dashboard.admin;

import com.database.DBConnection;
import com.enumeration.ItemStatus;
import com.enumeration.ReportStatus;
import com.frame.AppDialog;
import com.frame.dashboard.shared.DashboardUi;
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.model.Admin;
import com.model.Category;
import com.model.FoundReport;
import com.model.Item;
import com.model.LostReport;
import com.model.User;
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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AdminFoundReportFormFrame extends JDialog {

    private static final String TITLE = "Tambah Barang Temuan";
    private static final String SUBTITLE = "Isi Data Barang Yang Ditemukan Agar Bisa Dikelola Dan Diklaim Pengguna.";
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(900, 650);

    private final User user;
    private final ReportManager reportManager;
    private final ItemManager itemManager;
    private final Runnable onReportSaved;

    private JTextField itemNameField;
    private JTextField foundLocationField;
    private JTextArea itemDescriptionArea;
    private JTextArea reportDescriptionArea;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<String> lostReportComboBox;
    private ArrayList<LostReport> pendingLostReports;
    private PhotoPreviewPanel photoPreviewPanel;
    private File selectedPhotoFile;

    // -------------------------------------------------------------------------
    // Frame Setup
    // -------------------------------------------------------------------------

    public AdminFoundReportFormFrame(User user, ReportManager reportManager, Runnable onReportSaved) {
        super((java.awt.Frame) null, TITLE, true);
        this.user = user;
        this.reportManager = reportManager == null ? new ReportManager() : reportManager;
        this.itemManager = new ItemManager();
        this.onReportSaved = onReportSaved;
        initComponents();
    }

    private void initComponents() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(MINIMUM_FRAME_SIZE);
        setPreferredSize(new Dimension(960, 700));
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
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(DashboardUi.section(TITLE, SUBTITLE), BorderLayout.WEST);
        root.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 15);
        JScrollPane formScroll = new JScrollPane(createFormPanel());
        styleScrollPane(formScroll);
        contentPanel.add(formScroll, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 15, 0, 0);
        contentPanel.add(createPhotoPanel(), gbc);
        root.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton cancelButton = createSecondaryButton("Batal");
        cancelButton.addActionListener(event -> dispose());
        JButton submitButton = createPrimaryButton("Submit");
        submitButton.addActionListener(event -> submitFoundReport());

        footerPanel.add(cancelButton);
        footerPanel.add(submitButton);
        root.add(footerPanel, BorderLayout.SOUTH);

        return root;
    }

    // -------------------------------------------------------------------------
    // Form UI
    // -------------------------------------------------------------------------

    private JPanel createFormPanel() {
        DashboardUi.RoundedPanel panel = new DashboardUi.RoundedPanel(Color.WHITE, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 20, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        pendingLostReports = new ArrayList<>();
        if (reportManager != null) {
            for (LostReport report : reportManager.getLostReports()) {
                if (report.getStatus() == ReportStatus.PENDING) {
                    pendingLostReports.add(report);
                }
            }
        }

        lostReportComboBox = createLostReportComboBox();
        addField(panel, gbc, 0, "Pilih Laporan Kehilangan Terkait (Opsional)", lostReportComboBox, 0.0);

        itemNameField = createTextField("Contoh: Tumbler Tuku", 50);
        addField(panel, gbc, 1, "Nama barang*", itemNameField, 0.0);

        itemDescriptionArea = createTextArea("Ciri-Ciri Barang, Warna, Merek, Tanda Khusus", 300);
        addField(panel, gbc, 2, "Deskripsi barang*", createTextAreaScroll(itemDescriptionArea), 1.0);

        foundLocationField = createTextField("Contoh: Lab Komputer FIF Lt.2", 100);
        addField(panel, gbc, 3, "Lokasi ditemukan*", foundLocationField, 0.0);

        reportDescriptionArea = createTextArea("Kronologi singkat penemuan", 500);
        addField(panel, gbc, 4, "Deskripsi laporan*", createTextAreaScroll(reportDescriptionArea), 1.0);

        categoryComboBox = createCategoryComboBox();
        addField(panel, gbc, 5, "Pilih kategori*", categoryComboBox, 0.0);

        lostReportComboBox.addActionListener(event -> applySelectedLostReport());

        gbc.gridy = 12;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 0, 0);
        JLabel noteLabel = new JLabel("*Mohon tidak membuat laporan palsu.");
        noteLabel.setFont(new Font("Poppins", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(150, 150, 150));
        panel.add(noteLabel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component input, double weighty) {
        gbc.gridy = row * 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(row == 0 ? 0 : 20, 0, 6, 0);
        panel.add(DashboardUi.label(label, 13, Font.BOLD, DashboardUi.TEXT_DARK), gbc);

        gbc.gridy = row * 2 + 1;
        gbc.weighty = weighty;
        gbc.fill = weighty > 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(input, gbc);
    }

    // -------------------------------------------------------------------------
    // Matched Lost Report UI
    // -------------------------------------------------------------------------

    private JComboBox<String> createLostReportComboBox() {
        String[] options = new String[pendingLostReports.size() + 1];
        options[0] = "-- Item Baru (Tidak ada kecocokan) --";
        for (int i = 0; i < pendingLostReports.size(); i++) {
            LostReport report = pendingLostReports.get(i);
            options[i + 1] = report.getReportId() + " - " + report.getItem().getName() + " (" + report.getUser().getName() + ")";
        }

        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setPreferredSize(new Dimension(300, 45));
        comboBox.setFont(new Font("Poppins", Font.BOLD, 13));
        comboBox.setForeground(DashboardUi.TEXT_DARK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 0)
        ));
        comboBox.setUI(createComboBoxUI());
        return comboBox;
    }

    private void applySelectedLostReport() {
        int selectedIndex = lostReportComboBox.getSelectedIndex();
        if (selectedIndex > 0) {
            LostReport report = pendingLostReports.get(selectedIndex - 1);
            itemNameField.setText(report.getItem().getName());
            itemDescriptionArea.setText(report.getItem().getDescription());
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                Category category = categoryComboBox.getItemAt(i);
                if (category.getCategoryID().equals(report.getItem().getCategory().getCategoryID())) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            itemNameField.setEnabled(false);
            itemDescriptionArea.setEnabled(false);
            categoryComboBox.setEnabled(false);
            applyMatchedPhoto(report);
            return;
        }

        itemNameField.setText("");
        itemDescriptionArea.setText("");
        itemNameField.setEnabled(true);
        itemDescriptionArea.setEnabled(true);
        categoryComboBox.setEnabled(true);
        clearSelectedPhoto();
    }

    private void applyMatchedPhoto(LostReport report) {
        if (report == null || report.getPhotoPath() == null || report.getPhotoPath().isBlank()) {
            clearSelectedPhoto();
            return;
        }

        File photoFile = new File(report.getPhotoPath());
        if (!photoFile.exists() || !isSupportedImage(photoFile)) {
            clearSelectedPhoto();
            return;
        }

        selectedPhotoFile = photoFile;
        if (photoPreviewPanel != null) {
            photoPreviewPanel.setImageFile(photoFile);
        }
    }

    private void clearSelectedPhoto() {
        selectedPhotoFile = null;
        if (photoPreviewPanel != null) {
            photoPreviewPanel.clearImage();
        }
    }

    // -------------------------------------------------------------------------
    // Field Factories
    // -------------------------------------------------------------------------

    private JPanel createPhotoPanel() {
        photoPreviewPanel = new PhotoPreviewPanel(this::choosePhoto);
        return photoPreviewPanel;
    }

    private JTextField createTextField(String placeholder, int limit) {
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
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new LimitedDocumentFilter(limit));
        return field;
    }

    private JTextArea createTextArea(String placeholder, int limit) {
        JTextArea area = new JTextArea(5, 20) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) graphics.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    g2.drawString(placeholder, getInsets().left, graphics.getFontMetrics().getAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Poppins", Font.PLAIN, 13));
        area.setForeground(DashboardUi.TEXT_DARK);
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        ((AbstractDocument) area.getDocument()).setDocumentFilter(new LimitedDocumentFilter(limit));
        return area;
    }

    private JPanel createTextAreaScroll(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        styleScrollbar(scrollPane.getVerticalScrollBar());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        wrapper.setPreferredSize(new Dimension(300, 110));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JComboBox<Category> createCategoryComboBox() {
        JComboBox<Category> comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(300, 45));
        comboBox.setFont(new Font("Poppins", Font.BOLD, 13));
        comboBox.setForeground(DashboardUi.TEXT_DARK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new DashboardUi.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 0)
        ));
        comboBox.setUI(createComboBoxUI());
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) new DefaultListCellRenderer()
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Poppins", Font.PLAIN, 13));
            label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            if (isSelected) {
                label.setBackground(new Color(240, 245, 250));
            } else {
                label.setBackground(Color.WHITE);
            }
            if (value instanceof Category) {
                Category category = (Category) value;
                label.setText(category.getName() + " *butuh dokumen saat klaim");
            }
            return label;
        });
        for (Category category : loadCategories()) {
            comboBox.addItem(category);
        }
        return comboBox;
    }

    private BasicComboBoxUI createComboBoxUI() {
        return new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        super.paintComponent(graphics);
                        Graphics2D g2 = (Graphics2D) graphics.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(DashboardUi.TEXT_DARK);
                        int width = getWidth();
                        int height = getHeight();
                        int[] xPoints = {width / 2 - 5, width / 2, width / 2 + 5};
                        int[] yPoints = {height / 2 - 2, height / 2 + 3, height / 2 - 2};
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawPolyline(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(30, 0));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return button;
            }
        };
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
        if (file.length() / (1024 * 1024) > 2) {
            AppDialog.warning(this, "Ukuran File Terlalu Besar", "Ukuran foto maksimal 2 MB.");
            return;
        }

        selectedPhotoFile = file;
        photoPreviewPanel.setImageFile(file);
    }

    private void submitFoundReport() {
        if (user == null) {
            AppDialog.error(this, "Gagal Menyimpan", "Data admin tidak ditemukan. Silakan login ulang.");
            return;
        }

        String itemName = itemNameField.getText().trim();
        String itemDescription = itemDescriptionArea.getText().trim();
        String foundLocation = foundLocationField.getText().trim();
        String reportDescription = reportDescriptionArea.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        if (itemName.isEmpty() || itemDescription.isEmpty() || foundLocation.isEmpty()
                || reportDescription.isEmpty() || category == null) {
            AppDialog.warning(this, "Data Belum Lengkap", "Semua Input Wajib Diisi Sebelum Menyimpan Barang Temuan.");
            return;
        }

        int selectedIndex = lostReportComboBox.getSelectedIndex();
        LostReport matched = selectedIndex > 0 ? pendingLostReports.get(selectedIndex - 1) : null;
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try {
            Item item;
            if (matched != null) {
                item = matched.getItem();
            } else {
                String itemId = "ITM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                item = new Item(itemId, itemName, itemDescription, category, foundLocation);
                item.setStatus(ItemStatus.DITEMUKAN);
                itemManager.addItem(item);
            }

            FoundReport report = new FoundReport(reportId, user, item, reportDescription, foundLocation);
            if (matched != null) {
                report.setMatchedLostReport(matched);
            }
            if (selectedPhotoFile != null) {
                report.setPhotoPath(selectedPhotoFile.getAbsolutePath());
            }
            reportManager.addReport(report);
            reportManager.validateReport(reportId, ReportStatus.VALID, currentAdmin(), null);

            if (onReportSaved != null) {
                onReportSaved.run();
            }

            AppDialog.success(this, "Barang Temuan Disimpan", "Barang temuan berhasil ditambahkan.");
            dispose();
        } catch (Exception exception) {
            AppDialog.error(this, "Terjadi Kesalahan", "Gagal menyimpan barang temuan: " + exception.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Data Helpers
    // -------------------------------------------------------------------------

    private Admin currentAdmin() {
        return user instanceof Admin ? (Admin) user : null;
    }

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
                        resultSet.getString("name")
                ));
            }
        } catch (SQLException exception) {
            AppDialog.error(this, "Gagal Memuat Kategori", exception.getMessage());
        }
        return categories;
    }

    // -------------------------------------------------------------------------
    // Style Helpers
    // -------------------------------------------------------------------------

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        styleScrollbar(scrollPane.getVerticalScrollBar());
    }

    private void styleScrollbar(JScrollBar scrollbar) {
        scrollbar.setUnitIncrement(16);
        scrollbar.setPreferredSize(new Dimension(8, 0));
        scrollbar.setOpaque(false);
        scrollbar.setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
                g2.dispose();
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
                return button;
            }
        });
    }

    // -------------------------------------------------------------------------
    // Validation Helpers
    // -------------------------------------------------------------------------

    private boolean isSupportedImage(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    // -------------------------------------------------------------------------
    // Custom Components
    // -------------------------------------------------------------------------

    private static class LimitedDocumentFilter extends DocumentFilter {
        private final int limit;

        LimitedDocumentFilter(int limit) {
            this.limit = limit;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            int currentLength = fb.getDocument().getLength();
            int overLimit = (currentLength - length + text.length()) - limit;
            if (overLimit > 0) {
                text = text.substring(0, text.length() - overLimit);
            }
            if (!text.isEmpty()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
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

        void clearImage() {
            this.image = null;
            this.hovered = false;
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
            String title = "Unggah foto barang di sini";
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
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (width - titleWidth) / 2, blockY + 80);

            g2.setColor(DashboardUi.PRIMARY);
            int buttonWidth = 160;
            int buttonHeight = 36;
            int buttonX = (width - buttonWidth) / 2;
            int buttonY = blockY + 100;
            g2.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 18, 18);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Poppins", Font.BOLD, 13));
            int textWidth = g2.getFontMetrics().stringWidth(button);
            g2.drawString(button, (width - textWidth) / 2, buttonY + 23);
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
