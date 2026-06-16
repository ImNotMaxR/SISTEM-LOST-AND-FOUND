package com.frame.dashboard.security;

import com.frame.dashboard.user.UserDashboardComponents;
import com.frame.dashboard.user.WrapLayout;
import com.managers.StorageManager;
import com.managers.ClaimManager;
import com.model.Claim;
import com.enumeration.ClaimStatus;
import com.model.StorageRecord;
import com.enumeration.ItemStatus;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SecurityStoragePanel extends JPanel {
    private final StorageManager storageManager;
    private final ClaimManager claimManager;
    private final String securityId;
    private JPanel contentPanel;
    private JScrollPane scrollPane;

    public SecurityStoragePanel(String securityId) {
        this.storageManager = new StorageManager();
        this.claimManager = new ClaimManager();
        this.securityId = securityId;
        initComponents();
        loadStorageRecords();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UserDashboardComponents.SURFACE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UserDashboardComponents.SURFACE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel titleLabel = UserDashboardComponents.label("Storage Record", 28, Font.BOLD, UserDashboardComponents.TEXT_DARK);
        JLabel subtitleLabel = UserDashboardComponents.label("Kelola barang-barang yang ada di gudang penyimpanan.", 14, Font.PLAIN, UserDashboardComponents.TEXT_MUTED);

        JPanel titleWrapper = new JPanel(new GridLayout(2, 1, 0, 4));
        titleWrapper.setBackground(UserDashboardComponents.SURFACE);
        titleWrapper.add(titleLabel);
        titleWrapper.add(subtitleLabel);

        headerPanel.add(titleWrapper, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        contentPanel = new JPanel(new WrapLayout(WrapLayout.LEFT, 24, 24));
        contentPanel.setBackground(UserDashboardComponents.SURFACE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadStorageRecords() {
        contentPanel.removeAll();
        ArrayList<StorageRecord> records = storageManager.getStorageRecordsBySecurity(securityId);

        if (records.isEmpty()) {
            JLabel emptyLabel = UserDashboardComponents.label("Belum ada barang di storage Anda.", 16, Font.ITALIC, UserDashboardComponents.TEXT_MUTED);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(emptyLabel);
        } else {
            for (StorageRecord record : records) {
                contentPanel.add(createStorageCard(record));
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStorageCard(StorageRecord record) {
        UserDashboardComponents.RoundedPanel card = new UserDashboardComponents.RoundedPanel(Color.WHITE, 20);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(320, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
                new UserDashboardComponents.RoundedLineBorder(UserDashboardComponents.BORDER, 20, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Top: Status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel(record.isReleased() ? "SUDAH DIAMBIL" : "MASIH DISIMPAN");
        statusLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        statusLabel.setForeground(record.isReleased() ? new Color(16, 185, 129) : new Color(245, 158, 11)); // Green vs Amber
        topPanel.add(statusLabel, BorderLayout.WEST);

        // Center: Details
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(12, 0, 4, 0);
        centerPanel.add(UserDashboardComponents.label(record.getItem().getName(), 18, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        centerPanel.add(UserDashboardComponents.label("Lokasi: " + record.getStorageLocation(), 13, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        centerPanel.add(UserDashboardComponents.label("ID Record: " + record.getRecordId(), 12, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);
        
        gbc.gridy = 3;
        centerPanel.add(UserDashboardComponents.label("Tgl Masuk: " + record.getDateStored().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), 12, Font.PLAIN, UserDashboardComponents.TEXT_MUTED), gbc);

        if (record.isReleased() || record.getItem().getStatus() == ItemStatus.DIKLAIM) {
            String ownerName = "Unknown";
            for (Object obj : claimManager.getAll()) {
                Claim c = (Claim) obj;
                if (c.getItem().getItemID().equals(record.getItem().getItemID()) && c.getStatus() == ClaimStatus.VALID) {
                    ownerName = c.getUser().getName();
                    break;
                }
            }
            gbc.gridy = 4;
            centerPanel.add(UserDashboardComponents.label("Pemilik: " + ownerName, 13, Font.BOLD, UserDashboardComponents.TEXT_DARK), gbc);
        }

        // Bottom: Action Button
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JButton btnRelease = new JButton("Release Item");
        btnRelease.setFont(new Font("Poppins", Font.BOLD, 13));
        btnRelease.setFocusPainted(false);
        btnRelease.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (record.isReleased()) {
            btnRelease.setEnabled(false);
            btnRelease.setText("Sudah Diambil");
        } else if (record.getItem().getStatus() != ItemStatus.DIKLAIM) {
            btnRelease.setEnabled(false);
            btnRelease.setToolTipText("Belum berstatus DIKLAIM. Menunggu validasi admin.");
        } else {
            btnRelease.setBackground(UserDashboardComponents.PRIMARY);
            btnRelease.setForeground(Color.WHITE);
            btnRelease.addActionListener(e -> releaseItemAction(record));
        }

        JButton btnDelete = new JButton("Delete Record");
        btnDelete.setFont(new Font("Poppins", Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setBackground(new Color(239, 68, 68)); // Red-500
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteRecordAction(record));

        bottomPanel.add(btnRelease);
        bottomPanel.add(btnDelete);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private void releaseItemAction(StorageRecord record) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin melakukan release pada barang " + record.getItem().getName() + "?", "Konfirmasi Release", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            storageManager.updateStorageReleasedDB(record.getRecordId());
            JOptionPane.showMessageDialog(this, "Item berhasil direlease.");
            loadStorageRecords();
        }
    }

    private void deleteRecordAction(StorageRecord record) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus record ini dari storage? Item tidak akan hilang dari sistem, hanya data penyimpanannya.", "Hapus Storage Record", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            storageManager.delete(record.getRecordId());
            JOptionPane.showMessageDialog(this, "Record berhasil dihapus.");
            loadStorageRecords();
        }
    }
}
