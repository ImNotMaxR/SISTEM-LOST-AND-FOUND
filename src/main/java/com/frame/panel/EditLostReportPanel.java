package com.frame.panel;

import com.database.DBConnection;
import com.frame.AppDialog;
import com.frame.dashboard.user.UserDashboardComponents;
import com.managers.ItemManager;
import com.managers.ReportManager;
import com.model.Category;
import com.model.Item;
import com.model.Report;
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
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.JDialog;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class EditLostReportPanel extends JDialog {

    private static final String TITLE = "Edit Laporan";
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(900, 650);

    private final Report existingReport;
    private final ReportManager reportManager;
    private final ItemManager itemManager;
    private final Runnable onReportSaved;

    private JTextField itemNameField;
    private JTextField lostLocationField;
    private JTextArea itemDescriptionArea;
    private JTextArea reportDescriptionArea;
    private JComboBox<Category> categoryComboBox;
    
    private PhotoPreviewPanel photoPreviewPanel;
    private File selectedPhotoFile;

    public EditLostReportPanel(Report report, ReportManager reportManager, Runnable onReportSaved) {
        super();
        this.existingReport = report;
        this.reportManager = reportManager;
        this.itemManager = new ItemManager();
        this.onReportSaved = onReportSaved;
        initComponents();
        populateFields();
    }

    private void populateFields() {
        if (existingReport != null) {
            itemNameField.setText(existingReport.getItem().getName());
            itemDescriptionArea.setText(existingReport.getItem().getDescription());
            lostLocationField.setText(UserDashboardComponents.location(existingReport));
            reportDescriptionArea.setText(existingReport.getDescription());
            
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                Category cat = categoryComboBox.getItemAt(i);
                if (cat.getCategoryID().equals(existingReport.getItem().getCategory().getCategoryID())) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            if (existingReport.getPhotoPath() != null) {
                photoPreviewPanel.image = new ImageIcon(existingReport.getPhotoPath()).getImage();
                photoPreviewPanel.repaint();
            }
        }
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

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        String subtitle = existingReport instanceof com.model.FoundReport 
            ? "Ubah Data Laporan Barang Ditemukan Yang Ingin Anda Perbarui."
            : "Ubah Data Laporan Barang Hilang Yang Ingin Anda Perbarui.";
        headerPanel.add(UserDashboardComponents.section(TITLE, subtitle), BorderLayout.WEST);
        
        root.add(headerPanel, BorderLayout.NORTH);

        // Content Panel (2 Columns)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        // Left Column: Form
        gbc.gridx = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 15);

        JScrollPane formScroll = new JScrollPane(createFormPanel());
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        formScroll.getVerticalScrollBar().setOpaque(false);
        formScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, javax.swing.JComponent c, Rectangle trackBounds) {}
            @Override
            protected void paintThumb(Graphics g, javax.swing.JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0, 0)); return btn;
            }
        });

        contentPanel.add(formScroll, gbc);

        // Right Column: Photo
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 15, 0, 0);
        contentPanel.add(createPhotoPanel(), gbc);

        root.add(contentPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton btnBatal = createSecondaryButton("Batal");
        btnBatal.addActionListener(e -> dispose());
        JButton btnSubmit = createPrimaryButton("Submit");
        btnSubmit.addActionListener(e -> submitLostReport());
        
        footerPanel.add(btnBatal);
        footerPanel.add(btnSubmit);
        
        root.add(footerPanel, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createFormPanel() {
        UserDashboardComponents.RoundedPanel panel = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 20, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        itemNameField = createTextField("Contoh: Tumbler Tuku", 50);
        addField(panel, gbc, 0, "Nama barang*", itemNameField, 0.0);

        itemDescriptionArea = createTextArea("Ciri-Ciri Barang, Warna, Merek, Tanda Khusus", 300);
        addField(panel, gbc, 1, "Deskripsi barang", createTextAreaScroll(itemDescriptionArea), 1.0);

        String locLabel = existingReport instanceof com.model.FoundReport ? "Lokasi ditemukan" : "Lokasi hilang";
        lostLocationField = createTextField("Contoh: Lab Komputer FIF Lt.2", 100);
        addField(panel, gbc, 2, locLabel, lostLocationField, 0.0);

        reportDescriptionArea = createTextArea("Kronologi singkat kehilangan", 500);
        addField(panel, gbc, 3, "Deskripsi laporan", createTextAreaScroll(reportDescriptionArea), 1.0);

        categoryComboBox = createCategoryComboBox();
        addField(panel, gbc, 4, "Pilih kategori", categoryComboBox, 0.0);

        gbc.gridy = 10;
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
        JLabel titleLabel = UserDashboardComponents.label(label, 13, Font.BOLD, UserDashboardComponents.TEXT_DARK);
        panel.add(titleLabel, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.weighty = weighty;
        gbc.fill = (weighty > 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(input, gbc);
    }

    private JPanel createPhotoPanel() {
        photoPreviewPanel = new PhotoPreviewPanel(this::choosePhoto);
        return photoPreviewPanel;
    }

    private JTextField createTextField(String placeholder, int limit) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        field.setPreferredSize(new Dimension(300, 45));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setForeground(UserDashboardComponents.TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength - length + text.length()) - limit;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (text.length() > 0) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
        return field;
    }

    private JTextArea createTextArea(String placeholder, int limit) {
        JTextArea area = new JTextArea(5, 20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(170, 170, 170));
                    g2.setFont(getFont());
                    g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Poppins", Font.PLAIN, 13));
        area.setForeground(UserDashboardComponents.TEXT_DARK);
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        area.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { area.repaint(); }
            public void focusLost(FocusEvent e) { area.repaint(); }
        });
        
        ((AbstractDocument) area.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength - length + text.length()) - limit;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (text.length() > 0) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
        return area;
    }

    private JPanel createTextAreaScroll(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Do not paint track to keep it transparent
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
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
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
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
        comboBox.setForeground(UserDashboardComponents.TEXT_DARK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(230, 230, 230), 16, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 0)
        ));
        
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(UserDashboardComponents.TEXT_DARK);
                        
                        int w = getWidth();
                        int h = getHeight();
                        int[] xPoints = {w / 2 - 5, w / 2, w / 2 + 5};
                        int[] yPoints = {h / 2 - 2, h / 2 + 3, h / 2 - 2};
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawPolyline(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(30, 0));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return btn;
            }
        });
        
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
                label.setText(category.getName());
            }
            return label;
        });

        for (Category category : loadCategories()) {
            comboBox.addItem(category);
        }
        return comboBox;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(UserDashboardComponents.PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(UserDashboardComponents.TEXT_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(new Color(220, 220, 220), 20, 1),
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

        selectedPhotoFile = file;
        photoPreviewPanel.setImageFile(file);
    }

    private void submitLostReport() {
        String itemName = itemNameField.getText().trim();
        String itemDescription = itemDescriptionArea.getText().trim();
        String lostLocation = lostLocationField.getText().trim();
        String reportDescription = reportDescriptionArea.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        try {
            if (existingReport instanceof com.model.LostReport) {
                reportManager.editLostReport((com.model.LostReport) existingReport, itemName, itemDescription, lostLocation, reportDescription, category, selectedPhotoFile, itemManager);
            } else if (existingReport instanceof com.model.FoundReport) {
                reportManager.editFoundReport((com.model.FoundReport) existingReport, itemName, itemDescription, lostLocation, reportDescription, category, selectedPhotoFile, itemManager);
            }

            AppDialog.success(this, "Berhasil", "Laporan Anda Telah Berhasil Diperbarui!");
            if (onReportSaved != null) {
                onReportSaved.run();
            }
            dispose();

        } catch (com.exception.ValidationException e) {
            AppDialog.warning(this, "Data Tidak Valid", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            AppDialog.error(this, "Terjadi Kesalahan", "Terjadi kesalahan sistem:\n" + e.getMessage());
        }
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

    private boolean isSupportedImage(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new EditLostReportPanel(null, new ReportManager(), null).setVisible(true));
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
        private boolean isHovered = false;

        public PhotoPreviewPanel(Runnable onClick) {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (image != null) {
                        isHovered = true;
                        repaint();
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
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
                String text1 = "Unggah foto barang di sini";
                String text2 = "Pilih dari komputer";
                
                int blockY = (height - 140) / 2;
                
                int cx = width / 2;
                int cy = blockY + 30;
                
                g2.setColor(UserDashboardComponents.PRIMARY_DARK);
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                g2.drawLine(cx - 20, cy + 8, cx - 20, cy + 20);
                g2.drawLine(cx - 20, cy + 20, cx + 20, cy + 20);
                g2.drawLine(cx + 20, cy + 20, cx + 20, cy + 8);
                
                g2.drawLine(cx, cy + 8, cx, cy - 18);
                g2.drawLine(cx - 10, cy - 8, cx, cy - 18);
                g2.drawLine(cx + 10, cy - 8, cx, cy - 18);

                g2.setFont(new Font("Poppins", Font.BOLD, 14));
                g2.setColor(UserDashboardComponents.TEXT_DARK);
                int text1W = g2.getFontMetrics().stringWidth(text1);
                g2.drawString(text1, (width - text1W) / 2, blockY + 80);
                
                g2.setColor(UserDashboardComponents.PRIMARY);
                int btnW = 160;
                int btnH = 36;
                int btnX = (width - btnW) / 2;
                int btnY = blockY + 100;
                g2.fillRoundRect(btnX, btnY, btnW, btnH, 18, 18);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 13));
                int text2W = g2.getFontMetrics().stringWidth(text2);
                g2.drawString(text2, (width - text2W) / 2, btnY + 23);

            } else {
                g2.setClip(new java.awt.geom.RoundRectangle2D.Double(2, 2, width - 4, height - 4, 18, 18));
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);
                double scale = Math.min((double) width / imageWidth, (double) height / imageHeight);
                int drawWidth = (int) Math.round(imageWidth * scale);
                int drawHeight = (int) Math.round(imageHeight * scale);
                int x = (width - drawWidth) / 2;
                int y = (height - drawHeight) / 2;
                g2.drawImage(image, x, y, drawWidth, drawHeight, this);
                
                if (isHovered) {
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(2, 2, width - 4, height - 4, 18, 18);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Poppins", Font.BOLD, 15));
                    String hoverText = "Ganti foto";
                    FontMetrics fm = g2.getFontMetrics();
                    int textW = fm.stringWidth(hoverText);
                    int textH = fm.getAscent();
                    g2.drawString(hoverText, (width - textW) / 2, (height + textH) / 2);
                }
            }
            g2.dispose();
        }
    }
}
