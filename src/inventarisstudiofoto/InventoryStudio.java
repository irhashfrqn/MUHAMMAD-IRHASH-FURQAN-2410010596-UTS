/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventarisstudiofoto;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.text.ParseException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author MUHAMMAD IRHASH FURQAN - 2410010596
 * Look and Feel pada App ini menggunakan FlatLaf
 */
public class InventoryStudio extends javax.swing.JFrame {


    /**
     * Creates new form InventoryStudio
     */
    public InventoryStudio() {
        initComponents();
        
        
        
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardPanel.add(inventarisPanel, "inventaris");
        cardPanel.add(penggunaanPanel, "penggunaan");
        cardPanel.add(datakaryawanPanel, "karyawan");
        
        
        //menambahkan placeholder pada textfield, disediakan oleh FlatLaf
        kodeInventarisForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mis. 234");
        namaBarangForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Barang");
        merkForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Misal: Sony/Canon,dll");
        tipeForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mis. A6600/EOS RP/V850II");
        hargaBeliForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mis. 1.800.000");
        //placeholder pada form karyawan
        IDKaryawanForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mis. 1, 2, 5");
        namaLengkapForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Lengkap");
        emailForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mis. irhash@gmail.com");
        nomorHPForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Misal 6281235..");
        nomorWAForm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Misal 6281235..");
//        IDKaryawanForm.setText("");
        
        
        // Tambahkan custom renderer dan editor untuk kolom Action
        loadInventoryData();
        
        //load daata yang diselect pada tabel inventaris ke form agar bisa diedit
        dataInventoryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Mengecek apakah ada baris yang dipilih
                if (!e.getValueIsAdjusting() && dataInventoryTable.getSelectedRow() != -1) {
                    int selectedRow = dataInventoryTable.getSelectedRow();
                    String kodeInventaris = dataInventoryTable.getValueAt(selectedRow, 0).toString();
                    getDataByKodeInventaris(kodeInventaris); // Ambil data dari database

                    // Atur tombol dan form kode inventory
                    saveDataBtn.setEnabled(false); // Nonaktifkan tombol saveDataBtn
                    saveEditBtn.setEnabled(true);  // Aktifkan tombol saveEditBtn
                    kodeInventarisForm.setEnabled(false);// Nonaktifkan kodeInventarisForm textfield
                    newKodeInventarisForm.setEnabled(true);// aktifkan newKodeInventarisForm textfield
                } else {
                    // Jika tidak ada baris yang dipilih (unselect), kosongkan form
                    clearFormInven();
                }
            }
        });
        dataInventoryTable.setAutoCreateRowSorter(true);//mengatur tabel data bisa di sorting otomatis

        kodeInventarisForm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validasiAngka(evt);
            }
        });
        
        newKodeInventarisForm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validasiAngka(evt);
            }
        });
        
        nomorHPForm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validasiAngka(evt);
            }
        });
        
        nomorWAForm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validasiAngka(evt);
            }
        });
        
        hargaBeliForm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                formatUangWithKeyAdapter();
            }
        });


        
        //jika row pada tabel inventaris barang diklik maka menampilkan data ke form kiri
        dataInventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = dataInventoryTable.rowAtPoint(evt.getPoint()); // Baris yang diklik
                int col = dataInventoryTable.columnAtPoint(evt.getPoint()); // Kolom yang diklik

                // Pastikan klik hanya pada kolom "Status"
                if (col == dataInventoryTable.getColumn("Status Barang").getModelIndex()) {
                    String status = (String) dataInventoryTable.getValueAt(row, col);
                    if ("Digunakan Crew".equalsIgnoreCase(status)) {
                        // Ambil data inventaris berdasarkan baris
                        String kodeInventaris = (String) dataInventoryTable.getValueAt(row, dataInventoryTable.getColumn("Kode Inventaris").getModelIndex());

                        // Alihkan ke panel "Penggunaan" dan muat data terkait
                        showPenggunaanPanel(kodeInventaris);
                    }
                }
            }
        });
        //jika row pada tabel penggunaan barang diklik maka menampilkan data ke form kiri
        dataPenggunaanTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Mengecek apakah ada baris yang dipilih
                if (!e.getValueIsAdjusting() && dataPenggunaanTable.getSelectedRow() != -1) {
                    int selectedRow = dataPenggunaanTable.getSelectedRow();
                    String kodePenggunaan = dataPenggunaanTable.getValueAt(selectedRow, 0).toString();
                    String idInvenSelected = dataPenggunaanTable.getValueAt(selectedRow, 1).toString();

                    
                    // Load combobox inventaris dengan data yang sesuai
                    loadComboBoxInventaris(idInvenSelected);
                    System.out.print(idInvenSelected);
                    // Ambil data dari database berdasarkan kode penggunaan
                    getDataByKodePenggunaan(kodePenggunaan);
                    
                    

                    // Atur tombol 
                    NewGunaDataBtn.setEnabled(false); // Nonaktifkan tombol NewGunaDataBtn
                    saveEditGunaBtn.setEnabled(true);  // Aktifkan tombol saveEditGunaBtn
                } else {
                    // Jika tidak ada baris yang dipilih (unselect), kosongkan form
                    clearFormPenggunaan();
                }
            }
        });
        //jika row pada tabel karyawan diklik maka menampilkan data ke form kiri
        dataKaryawanTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Mengecek apakah ada baris yang dipilih
                if (!e.getValueIsAdjusting() && dataKaryawanTable.getSelectedRow() != -1) {
                    int selectedRow = dataKaryawanTable.getSelectedRow();
                    String idkaryawan = dataKaryawanTable.getValueAt(selectedRow, 0).toString();

                    getDataByIDKaryawan(idkaryawan);
                    
                    

                    // Atur tombol tambah dan edit karyawan
                    saveNewKaryawanBtn.setEnabled(false); // Nonaktifkan tombol saveNewKaryawanBtn
                    saveEditKaryawanBtn.setEnabled(true);  // Aktifkan tombol saveEditKaryawanBtn
                } else {
                    // Jika tidak ada baris yang dipilih (unselect), kosongkan form
                    clearFormKaryawan();
                }
            }
        });
        
        hiddenOldIDKaryawan.setVisible(false);

        
        // Batasi jumlah karakter di masing-masing form yang perlu dibatasi inputnya
        FormHandler.batasiForm(kodeInventarisForm, 5);  // Membatasi kodeInventarisForm hingga 5 karakter
        FormHandler.batasiForm(namaBarangForm, 30);  // Membatasi namaBarangForm hingga 30 karakter        
        FormHandler.batasiForm(merkForm, 30);
        FormHandler.batasiForm(tipeForm, 30);
        
        //batasan jumlah karakter form penggunaan barang
        FormHandler.batasiForm(kodePenggunaanForm, 11);
        
        //batasan jumlah karakter form karyawan
        FormHandler.batasiForm(IDKaryawanForm, 11);  // Membatasi kodeInventarisForm hingga 5 karakter
        FormHandler.batasiForm(namaLengkapForm, 100);  // Membatasi namaBarangForm hingga 30 karakter        
        FormHandler.batasiForm(emailForm, 100);
        FormHandler.batasiForm(nomorHPForm, 15);         
        FormHandler.batasiForm(nomorWAForm, 15); 


        //load data untuk pilihan barang pada form penggunaan barang inventaris
        loadComboBoxInventaris(null);
        
        //load data untuk pilihan karyawan pada form penggunaan barang inventaris
        loadComboBoxKaryawan();
        
        clearFormInven();        
        clearFormPenggunaan();
        clearFormKaryawan();


        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        inventarisPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataInventoryTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        kodeInventarisForm = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        namaBarangForm = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        merkForm = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tipeForm = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        hargaBeliForm = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tglBeliForm = new com.toedter.calendar.JDateChooser();
        statusBarangForm = new javax.swing.JComboBox<>();
        saveDataBtn = new javax.swing.JButton();
        clearFormInvenBtn = new javax.swing.JButton();
        jenisAlatForm = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        saveEditBtn = new javax.swing.JButton();
        deleteDataBtn = new javax.swing.JButton();
        newKodeInventarisForm = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        newKodeLabel = new javax.swing.JLabel();
        kodeLabel = new javax.swing.JLabel();
        refreshDataInventoryBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        penggunaanPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataPenggunaanTable = new javax.swing.JTable();
        kodePenggunaanForm = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        karyawanForm = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        statusPenggunaanForm = new javax.swing.JComboBox<>();
        tglPengembalianForm = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tglPenggunaanForm = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        selectInventarisForm = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        keteranganGunaForm = new javax.swing.JTextArea();
        refreshGunaTabelBtn = new javax.swing.JButton();
        NewGunaDataBtn = new javax.swing.JButton();
        saveEditGunaBtn = new javax.swing.JButton();
        deleteDataGunaBtn = new javax.swing.JButton();
        clearGunaFormBtn = new javax.swing.JButton();
        datakaryawanPanel = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        dataKaryawanTable = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        refreshKaryawanTableBtn = new javax.swing.JButton();
        saveNewKaryawanBtn = new javax.swing.JButton();
        saveEditKaryawanBtn = new javax.swing.JButton();
        deleteKaryawanBtn = new javax.swing.JButton();
        clearFormKaryawanBtn = new javax.swing.JButton();
        IDKaryawanForm = new javax.swing.JTextField();
        namaLengkapForm = new javax.swing.JTextField();
        emailForm = new javax.swing.JTextField();
        nomorHPForm = new javax.swing.JTextField();
        nomorWAForm = new javax.swing.JTextField();
        jabatanForm = new javax.swing.JComboBox<>();
        idkLabel = new javax.swing.JLabel();
        hiddenOldIDKaryawan = new javax.swing.JTextField();
        menuPanel = new javax.swing.JPanel();
        inventoryBtn = new javax.swing.JButton();
        penggunaanBtn = new javax.swing.JButton();
        datakaryawanBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        exitBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMaximumSize(new java.awt.Dimension(1280, 640));
        setMinimumSize(new java.awt.Dimension(1280, 640));
        setResizable(false);
        setSize(new java.awt.Dimension(1280, 640));

        mainPanel.setMaximumSize(new java.awt.Dimension(1280, 640));
        mainPanel.setMinimumSize(new java.awt.Dimension(1280, 640));
        mainPanel.setName(""); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1280, 640));

        cardPanel.setForeground(new java.awt.Color(51, 51, 51));
        cardPanel.setLayout(new java.awt.CardLayout());

        inventarisPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dataInventoryTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dataInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Inventaris", "Nama Barang", "Merk", "Tipe", "Jenis Alat", "Status Barang", "Harga Beli", "Tgl. Beli"
            }
        ));
        dataInventoryTable.setAlignmentY(1.0F);
        dataInventoryTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataInventoryTable.setCellSelectionEnabled(true);
        dataInventoryTable.setNextFocusableComponent(inventoryBtn);
        dataInventoryTable.setRowHeight(24);
        jScrollPane1.setViewportView(dataInventoryTable);

        inventarisPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 0, 730, 640));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Kode Inventaris");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        kodeInventarisForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        kodeInventarisForm.setNextFocusableComponent(namaBarangForm);
        kodeInventarisForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kodeInventarisFormActionPerformed(evt);
            }
        });
        kodeInventarisForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kodeInventarisFormKeyReleased(evt);
            }
        });
        jPanel1.add(kodeInventarisForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 200, 30));

        jLabel7.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Nama Barang");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        namaBarangForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        namaBarangForm.setNextFocusableComponent(merkForm);
        jPanel1.add(namaBarangForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 200, 30));

        jLabel8.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Merk");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        merkForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        merkForm.setNextFocusableComponent(tipeForm);
        jPanel1.add(merkForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, 200, 30));

        jLabel9.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tipe");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, -1));

        tipeForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        tipeForm.setNextFocusableComponent(statusBarangForm);
        jPanel1.add(tipeForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 200, 30));

        jLabel10.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Jenis Alat");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, -1, -1));

        jLabel11.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Status Barang");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

        hargaBeliForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        hargaBeliForm.setNextFocusableComponent(tglBeliForm);
        hargaBeliForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                hargaBeliFormKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                hargaBeliFormKeyTyped(evt);
            }
        });
        jPanel1.add(hargaBeliForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 360, 200, 30));

        jLabel12.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Harga Beli");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, -1, -1));

        jLabel13.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Tanggal Beli");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, -1, -1));

        tglBeliForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        tglBeliForm.setNextFocusableComponent(saveDataBtn);
        jPanel1.add(tglBeliForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 400, 200, 30));

        statusBarangForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        statusBarangForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ada di Studio", "Digunakan Crew", "Rusak", "Sedang diperbaiki", "Terjual" }));
        statusBarangForm.setNextFocusableComponent(jenisAlatForm);
        jPanel1.add(statusBarangForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 280, 200, 30));

        saveDataBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        saveDataBtn.setText("Tambah Data");
        saveDataBtn.setNextFocusableComponent(deleteDataBtn);
        saveDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataBtnActionPerformed(evt);
            }
        });
        jPanel1.add(saveDataBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 440, 150, 30));

        clearFormInvenBtn.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        clearFormInvenBtn.setText("Bersihkan Form");
        clearFormInvenBtn.setBorderPainted(false);
        clearFormInvenBtn.setNextFocusableComponent(dataInventoryTable);
        clearFormInvenBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFormInvenBtnActionPerformed(evt);
            }
        });
        jPanel1.add(clearFormInvenBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, 360, 30));

        jenisAlatForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jenisAlatForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Body Kamera", "Lensa Kamera", "Gimbal", "Flash", "Continuous Light", "Tripod", "Lightstand", "Alat Lighting", "Filter", "Perkabelan", "Alat Lainnya" }));
        jenisAlatForm.setNextFocusableComponent(hargaBeliForm);
        jPanel1.add(jenisAlatForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 320, 200, 30));

        jLabel14.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Rp");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 370, -1, -1));

        saveEditBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        saveEditBtn.setText("Simpan Perubahan");
        saveEditBtn.setNextFocusableComponent(saveDataBtn);
        saveEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEditBtnActionPerformed(evt);
            }
        });
        jPanel1.add(saveEditBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 205, 30));

        deleteDataBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        deleteDataBtn.setText("Hapus Data");
        deleteDataBtn.setBorderPainted(false);
        deleteDataBtn.setNextFocusableComponent(refreshDataInventoryBtn);
        deleteDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDataBtnActionPerformed(evt);
            }
        });
        jPanel1.add(deleteDataBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 150, 30));

        newKodeInventarisForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        newKodeInventarisForm.setNextFocusableComponent(namaBarangForm);
        newKodeInventarisForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newKodeInventarisFormKeyReleased(evt);
            }
        });
        jPanel1.add(newKodeInventarisForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 200, 30));

        jLabel15.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Ubah Kode Inventaris");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        newKodeLabel.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        newKodeLabel.setForeground(new java.awt.Color(255, 255, 255));
        newKodeLabel.setText("Pastikan Kode Inventaris belum digunakan");
        jPanel1.add(newKodeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(169, 140, 200, -1));

        kodeLabel.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        kodeLabel.setForeground(new java.awt.Color(255, 255, 255));
        kodeLabel.setText("Pastikan Kode Inventaris belum digunakan");
        jPanel1.add(kodeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(169, 90, 200, -1));

        refreshDataInventoryBtn.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        refreshDataInventoryBtn.setText("Refresh Tabel");
        refreshDataInventoryBtn.setBorderPainted(false);
        refreshDataInventoryBtn.setNextFocusableComponent(clearFormInvenBtn);
        refreshDataInventoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshDataInventoryBtnActionPerformed(evt);
            }
        });
        jPanel1.add(refreshDataInventoryBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 480, 200, 30));

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Data Inventaris Studio");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        inventarisPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 640));

        cardPanel.add(inventarisPanel, "card2");

        penggunaanPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Data Penggunaan Inventaris");
        penggunaanPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        dataPenggunaanTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dataPenggunaanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Penggunaan", "Kode Inventaris", "Barang", "Nama Pengguna", "Tgl Penggunaan", "Tgl Pengembalian", "Status", "Keterangan"
            }
        ));
        dataPenggunaanTable.setAlignmentY(1.0F);
        dataPenggunaanTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataPenggunaanTable.setRowHeight(40);
        jScrollPane2.setViewportView(dataPenggunaanTable);

        penggunaanPanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 0, 730, 640));

        kodePenggunaanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        kodePenggunaanForm.setNextFocusableComponent(namaBarangForm);
        kodePenggunaanForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kodePenggunaanFormActionPerformed(evt);
            }
        });
        kodePenggunaanForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kodePenggunaanFormKeyReleased(evt);
            }
        });
        penggunaanPanel.add(kodePenggunaanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 200, 30));

        jLabel16.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Kode Penggunaan");
        penggunaanPanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jLabel17.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Keterangan");
        penggunaanPanel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

        karyawanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        karyawanForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", " " }));
        karyawanForm.setNextFocusableComponent(tglPenggunaanForm);
        penggunaanPanel.add(karyawanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 200, 30));

        jLabel18.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Inventaris");
        penggunaanPanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        statusPenggunaanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        statusPenggunaanForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Digunakan", "Dikembalikan", "Rusak" }));
        statusPenggunaanForm.setNextFocusableComponent(kodePenggunaanForm);
        penggunaanPanel.add(statusPenggunaanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 200, 30));

        tglPengembalianForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        tglPengembalianForm.setNextFocusableComponent(statusPenggunaanForm);
        penggunaanPanel.add(tglPengembalianForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, 200, 30));

        jLabel19.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Nama Karyawan");
        penggunaanPanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        jLabel20.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Tgl. Penggunaan");
        penggunaanPanel.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        tglPenggunaanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        tglPenggunaanForm.setNextFocusableComponent(tglPengembalianForm);
        penggunaanPanel.add(tglPenggunaanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 200, 30));

        jLabel21.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Tgl. Pengembalian");
        penggunaanPanel.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        selectInventarisForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        selectInventarisForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", " " }));
        selectInventarisForm.setNextFocusableComponent(karyawanForm);
        penggunaanPanel.add(selectInventarisForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 200, 30));

        jLabel22.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Status Barang");
        penggunaanPanel.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, -1));

        keteranganGunaForm.setColumns(20);
        keteranganGunaForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        keteranganGunaForm.setRows(5);
        keteranganGunaForm.setNextFocusableComponent(NewGunaDataBtn);
        jScrollPane3.setViewportView(keteranganGunaForm);

        penggunaanPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 280, 200, 120));

        refreshGunaTabelBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        refreshGunaTabelBtn.setText("Refresh Tabel");
        refreshGunaTabelBtn.setBorderPainted(false);
        refreshGunaTabelBtn.setNextFocusableComponent(dataPenggunaanTable);
        refreshGunaTabelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshGunaTabelBtnActionPerformed(evt);
            }
        });
        penggunaanPanel.add(refreshGunaTabelBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 570, 200, 30));

        NewGunaDataBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        NewGunaDataBtn.setText("Tambah Data");
        NewGunaDataBtn.setNextFocusableComponent(saveEditGunaBtn);
        NewGunaDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewGunaDataBtnActionPerformed(evt);
            }
        });
        penggunaanPanel.add(NewGunaDataBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 410, 200, 30));

        saveEditGunaBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        saveEditGunaBtn.setText("Simpan Perubahan");
        saveEditGunaBtn.setEnabled(false);
        saveEditGunaBtn.setNextFocusableComponent(deleteDataGunaBtn);
        saveEditGunaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEditGunaBtnActionPerformed(evt);
            }
        });
        penggunaanPanel.add(saveEditGunaBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 450, 200, 30));

        deleteDataGunaBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        deleteDataGunaBtn.setText("Hapus Data");
        deleteDataGunaBtn.setBorderPainted(false);
        deleteDataGunaBtn.setNextFocusableComponent(clearGunaFormBtn);
        deleteDataGunaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDataGunaBtnActionPerformed(evt);
            }
        });
        penggunaanPanel.add(deleteDataGunaBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 490, 200, 30));

        clearGunaFormBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clearGunaFormBtn.setText("Bersihkan Form");
        clearGunaFormBtn.setBorderPainted(false);
        clearGunaFormBtn.setNextFocusableComponent(refreshGunaTabelBtn);
        clearGunaFormBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearGunaFormBtnActionPerformed(evt);
            }
        });
        penggunaanPanel.add(clearGunaFormBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 530, 200, 30));

        cardPanel.add(penggunaanPanel, "card3");

        datakaryawanPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Data Karyawan");
        datakaryawanPanel.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        dataKaryawanTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dataKaryawanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Karyawan", "Nama Karyawan", "Email", "No. HP", "No. WA", "Jabatan"
            }
        ));
        dataKaryawanTable.setAlignmentY(1.0F);
        dataKaryawanTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        dataKaryawanTable.setNextFocusableComponent(datakaryawanBtn);
        dataKaryawanTable.setRowHeight(24);
        jScrollPane4.setViewportView(dataKaryawanTable);

        datakaryawanPanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 0, 730, 640));

        jLabel24.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("ID Karyawan");
        datakaryawanPanel.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jLabel26.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Nama Lengkap");
        datakaryawanPanel.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jLabel27.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Email");
        datakaryawanPanel.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, -1, -1));

        jLabel28.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Nomor HP");
        datakaryawanPanel.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        jLabel29.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Nomor Whatsapp");
        datakaryawanPanel.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, -1));

        jLabel30.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Jabatan");
        datakaryawanPanel.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, -1, -1));

        refreshKaryawanTableBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        refreshKaryawanTableBtn.setText("Refresh Tabel");
        refreshKaryawanTableBtn.setBorderPainted(false);
        refreshKaryawanTableBtn.setNextFocusableComponent(dataKaryawanTable);
        refreshKaryawanTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshKaryawanTableBtnActionPerformed(evt);
            }
        });
        datakaryawanPanel.add(refreshKaryawanTableBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 560, 200, 30));

        saveNewKaryawanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        saveNewKaryawanBtn.setText("Tambah Data");
        saveNewKaryawanBtn.setNextFocusableComponent(saveEditKaryawanBtn);
        saveNewKaryawanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewKaryawanBtnActionPerformed(evt);
            }
        });
        datakaryawanPanel.add(saveNewKaryawanBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 310, 200, 30));

        saveEditKaryawanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        saveEditKaryawanBtn.setText("Simpan Perubahan");
        saveEditKaryawanBtn.setNextFocusableComponent(deleteKaryawanBtn);
        saveEditKaryawanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEditKaryawanBtnActionPerformed(evt);
            }
        });
        datakaryawanPanel.add(saveEditKaryawanBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 350, 200, 30));

        deleteKaryawanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        deleteKaryawanBtn.setText("Hapus Data");
        deleteKaryawanBtn.setBorderPainted(false);
        deleteKaryawanBtn.setNextFocusableComponent(clearFormKaryawanBtn);
        deleteKaryawanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteKaryawanBtnActionPerformed(evt);
            }
        });
        datakaryawanPanel.add(deleteKaryawanBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 390, 200, 30));

        clearFormKaryawanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clearFormKaryawanBtn.setText("Bersihkan Form");
        clearFormKaryawanBtn.setBorderPainted(false);
        clearFormKaryawanBtn.setNextFocusableComponent(refreshKaryawanTableBtn);
        clearFormKaryawanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFormKaryawanBtnActionPerformed(evt);
            }
        });
        datakaryawanPanel.add(clearFormKaryawanBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 430, 200, 30));

        IDKaryawanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        IDKaryawanForm.setNextFocusableComponent(namaLengkapForm);
        IDKaryawanForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDKaryawanFormActionPerformed(evt);
            }
        });
        IDKaryawanForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                IDKaryawanFormKeyReleased(evt);
            }
        });
        datakaryawanPanel.add(IDKaryawanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 200, 30));

        namaLengkapForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        namaLengkapForm.setNextFocusableComponent(emailForm);
        namaLengkapForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapFormActionPerformed(evt);
            }
        });
        namaLengkapForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                namaLengkapFormKeyReleased(evt);
            }
        });
        datakaryawanPanel.add(namaLengkapForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 200, 30));

        emailForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        emailForm.setNextFocusableComponent(nomorHPForm);
        emailForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailFormActionPerformed(evt);
            }
        });
        emailForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                emailFormKeyReleased(evt);
            }
        });
        datakaryawanPanel.add(emailForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, 200, 30));

        nomorHPForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        nomorHPForm.setNextFocusableComponent(nomorWAForm);
        nomorHPForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomorHPFormActionPerformed(evt);
            }
        });
        nomorHPForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nomorHPFormKeyReleased(evt);
            }
        });
        datakaryawanPanel.add(nomorHPForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 200, 30));

        nomorWAForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        nomorWAForm.setNextFocusableComponent(jabatanForm);
        nomorWAForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomorWAFormActionPerformed(evt);
            }
        });
        nomorWAForm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nomorWAFormKeyReleased(evt);
            }
        });
        datakaryawanPanel.add(nomorWAForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, 200, 30));

        jabatanForm.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jabatanForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Fotografer", "Videografer", "Freelancer" }));
        jabatanForm.setNextFocusableComponent(saveNewKaryawanBtn);
        datakaryawanPanel.add(jabatanForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 270, 200, 30));

        idkLabel.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        idkLabel.setForeground(new java.awt.Color(255, 255, 255));
        idkLabel.setText("Pastikan ID Karyawan belum digunakan");
        datakaryawanPanel.add(idkLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 200, -1));
        datakaryawanPanel.add(hiddenOldIDKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 70, -1));

        cardPanel.add(datakaryawanPanel, "card4");

        inventoryBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        inventoryBtn.setText("Inventaris");
        inventoryBtn.setBorder(null);
        inventoryBtn.setNextFocusableComponent(penggunaanBtn);
        inventoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryBtnActionPerformed(evt);
            }
        });

        penggunaanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        penggunaanBtn.setText("Penggunaan");
        penggunaanBtn.setBorder(null);
        penggunaanBtn.setNextFocusableComponent(datakaryawanBtn);
        penggunaanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                penggunaanBtnActionPerformed(evt);
            }
        });

        datakaryawanBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        datakaryawanBtn.setText("Data Karyawan");
        datakaryawanBtn.setBorder(null);
        datakaryawanBtn.setNextFocusableComponent(inventoryBtn);
        datakaryawanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datakaryawanBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Studio");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Inventory");

        exitBtn.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        exitBtn.setText("Keluar");
        exitBtn.setBorder(null);
        exitBtn.setBorderPainted(false);
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inventoryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(penggunaanBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(datakaryawanBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGroup(menuPanelLayout.createSequentialGroup()
                        .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(exitBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator1)
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(inventoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(penggunaanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datakaryawanBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(menuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(157, 157, 157))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cardPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void penggunaanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_penggunaanBtnActionPerformed
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "penggunaan");
        loadPenggunaanData("");
        //load data untuk pilihan barang pada form penggunaan barang inventaris
        loadComboBoxInventaris(null);
        
        //load data untuk pilihan karyawan pada form penggunaan barang inventaris
        loadComboBoxKaryawan();
        
        clearFormPenggunaan();
    }//GEN-LAST:event_penggunaanBtnActionPerformed

    private void datakaryawanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datakaryawanBtnActionPerformed
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "karyawan");
        loadKaryawanData();
        clearFormKaryawan();
    }//GEN-LAST:event_datakaryawanBtnActionPerformed

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitBtnActionPerformed

    private void inventoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryBtnActionPerformed
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "inventaris");
        loadInventoryData();
    }//GEN-LAST:event_inventoryBtnActionPerformed

    private void clearFormInvenBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFormInvenBtnActionPerformed
//        kodeInventarisForm.setText("");
//        namaBarangForm.setText("");
//        merkForm.setText("");
//        tipeForm.setText("");
//        hargaBeliForm.setText("");
        clearFormInven();
    }//GEN-LAST:event_clearFormInvenBtnActionPerformed

    private void hargaBeliFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hargaBeliFormKeyReleased
//        formatUang();
    }//GEN-LAST:event_hargaBeliFormKeyReleased

    private void hargaBeliFormKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hargaBeliFormKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_hargaBeliFormKeyTyped

    private void deleteDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDataBtnActionPerformed
        String kodeInventaris = kodeInventarisForm.getText().trim(); // Ambil kode inventaris dari form
        if (kodeInventaris.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode inventaris tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Menampilkan konfirmasi untuk penghapusan
        int confirmation = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);
        
        // Jika pengguna memilih YES
        if (confirmation == JOptionPane.YES_OPTION) {
            // Panggil fungsi untuk menghapus data
            deleteData(kodeInventaris);
        }
    }//GEN-LAST:event_deleteDataBtnActionPerformed

    private void saveEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEditBtnActionPerformed
        saveEditDataInventaris(); // Buat metode untuk menyimpan perubahan (implementasinya ada di bawah)

        // Atur ulang tombol
        saveDataBtn.setEnabled(true);  // Aktifkan tombol saveDataBtn
        saveEditBtn.setEnabled(false); // Nonaktifkan tombol saveEditBtn
        kodeInventarisForm.setEnabled(true);// Nonaktifkan kodeInventarisForm textfield
        newKodeInventarisForm.setEnabled(false);// aktifkan newKodeInventarisForm textfield
        
                    

        // Bersihkan form setelah simpan
        clearFormInven();
    }//GEN-LAST:event_saveEditBtnActionPerformed

    private void saveDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDataBtnActionPerformed
        saveNewData();
    }//GEN-LAST:event_saveDataBtnActionPerformed

    private void newKodeInventarisFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newKodeInventarisFormKeyReleased
        String newkodeInventaris = newKodeInventarisForm.getText().trim();
        String oldkodeInventaris = kodeInventarisForm.getText().trim();

        if (!newkodeInventaris.isEmpty()) {
            if(newkodeInventaris.equals(oldkodeInventaris)){//jika kode inventaris baru sama dengan kode inventaris lama
                newKodeLabel.setText(""); // Kosongkan label jika field kosong
            }else{
                // Mengecek apakah kode inventaris sudah ada di database
                if (isKodeInventarisExist(newkodeInventaris)) {
                    newKodeLabel.setText("Kode sudah digunakan!"); // Menampilkan pesan jika kode sudah ada
                    newKodeLabel.setForeground(Color.RED); // Mengubah warna teks menjadi merah
                    saveEditBtn.setEnabled(false);//tombol simpan edit menjadi disabled
                } else {
                    newKodeLabel.setText("Kode tersedia!"); // Menampilkan pesan jika kode belum ada
                    newKodeLabel.setForeground(Color.GREEN); // Mengubah warna teks menjadi hijau
                    saveEditBtn.setEnabled(true);//tombol simpan edit enabled
                }
            }
        } else {
            newKodeLabel.setText(""); // Kosongkan label jika field kosong
            saveEditBtn.setEnabled(true);//tombol simpan edit menjadi disabled

        }
    }//GEN-LAST:event_newKodeInventarisFormKeyReleased

    private void kodeInventarisFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kodeInventarisFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kodeInventarisFormActionPerformed

    private void kodeInventarisFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodeInventarisFormKeyReleased
        String kodeInventaris = kodeInventarisForm.getText().trim();
        if (!kodeInventaris.isEmpty()) {
            // Mengecek apakah kode inventaris sudah ada di database
            if (isKodeInventarisExist(kodeInventaris)) {
                kodeLabel.setText("Kode sudah digunakan!"); // Menampilkan pesan jika kode sudah ada
                kodeLabel.setForeground(Color.RED); // Mengubah warna teks menjadi merah
                saveDataBtn.setEnabled(false);//tombol simpan data menjadi disabled

            } else {
                kodeLabel.setText("Kode tersedia!"); // Menampilkan pesan jika kode belum ada
                kodeLabel.setForeground(Color.GREEN); // Mengubah warna teks menjadi hijau
                saveDataBtn.setEnabled(true);//tombol simpan data menjadi disabled

            }
        } else {
            kodeLabel.setText(""); // Kosongkan label jika field kosong
            saveDataBtn.setEnabled(true);//tombol simpan edit menjadi disabled

        }
    }//GEN-LAST:event_kodeInventarisFormKeyReleased

    private void refreshDataInventoryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshDataInventoryBtnActionPerformed
        loadInventoryData();
    }//GEN-LAST:event_refreshDataInventoryBtnActionPerformed

    private void kodePenggunaanFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kodePenggunaanFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kodePenggunaanFormActionPerformed

    private void kodePenggunaanFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodePenggunaanFormKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_kodePenggunaanFormKeyReleased

    private void refreshGunaTabelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshGunaTabelBtnActionPerformed
        loadPenggunaanData("");        
    }//GEN-LAST:event_refreshGunaTabelBtnActionPerformed

    private void NewGunaDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewGunaDataBtnActionPerformed
        saveNewDataPenggunaan();
    }//GEN-LAST:event_NewGunaDataBtnActionPerformed

    private void saveEditGunaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEditGunaBtnActionPerformed
        saveEditDataPenggunaan();
    }//GEN-LAST:event_saveEditGunaBtnActionPerformed

    private void deleteDataGunaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDataGunaBtnActionPerformed
        String kodePenggunaan = kodePenggunaanForm.getText().trim(); // Ambil kode inventaris dari form
        if (kodePenggunaan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode inventaris tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Menampilkan konfirmasi untuk penghapusan
        int confirmation = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data penggunaan ini?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);
        
        // Jika pengguna memilih YES
        if (confirmation == JOptionPane.YES_OPTION) {
            // Panggil fungsi untuk menghapus data
            deleteDataPenggunaan(kodePenggunaan);
        }
        
        
    }//GEN-LAST:event_deleteDataGunaBtnActionPerformed

    private void clearGunaFormBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearGunaFormBtnActionPerformed
        clearFormPenggunaan();
    }//GEN-LAST:event_clearGunaFormBtnActionPerformed

    private void refreshKaryawanTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshKaryawanTableBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_refreshKaryawanTableBtnActionPerformed

    private void saveNewKaryawanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewKaryawanBtnActionPerformed
        saveNewDataKaryawan();
    }//GEN-LAST:event_saveNewKaryawanBtnActionPerformed

    private void saveEditKaryawanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEditKaryawanBtnActionPerformed
        saveEditDataKaryawan();
    }//GEN-LAST:event_saveEditKaryawanBtnActionPerformed

    private void deleteKaryawanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteKaryawanBtnActionPerformed
        String idkaryawan = IDKaryawanForm.getText().trim(); // Ambil kode inventaris dari form
        if (idkaryawan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID Karyawan tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Menampilkan konfirmasi untuk penghapusan
        int confirmation = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data karyawan ini?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);

        // Jika pengguna memilih YES
        if (confirmation == JOptionPane.YES_OPTION) {
            // Panggil fungsi untuk menghapus data
            deleteDataKaryawan(idkaryawan);
        }
    }//GEN-LAST:event_deleteKaryawanBtnActionPerformed

    private void clearFormKaryawanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFormKaryawanBtnActionPerformed
        clearFormKaryawan();
    }//GEN-LAST:event_clearFormKaryawanBtnActionPerformed

    private void IDKaryawanFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDKaryawanFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDKaryawanFormActionPerformed

    private void IDKaryawanFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IDKaryawanFormKeyReleased
        String newIDKaryawan = IDKaryawanForm.getText().trim();
        String oldIDKaryawan = hiddenOldIDKaryawan.getText().trim();
        if (!newIDKaryawan.isEmpty()) {//cek apakah id karyawan baru itu tidak kosong
            if(!newIDKaryawan.equalsIgnoreCase(oldIDKaryawan)){//jika id karyawan baru tidak sama dengan id karyawan lama maka baru dicek isExist
                // Mengecek apakah kode inventaris sudah ada di database
                if (isIDKaryawanExist(newIDKaryawan)) {//cek lagi apakah id karyawan yg diinput sudah digunakan
                    idkLabel.setText("ID sudah digunakan!"); // Menampilkan pesan jika kode sudah ada
                    idkLabel.setForeground(Color.RED); // Mengubah warna teks menjadi merah
                    saveEditKaryawanBtn.setEnabled(false);//tombol simpan edit menjadi disabled
                } else {//JIKA TERSEDIA ATAU BELUM DIGUNAKAN
                    idkLabel.setText("ID tersedia!"); // Menampilkan pesan jika kode belum ada
                    idkLabel.setForeground(Color.GREEN); // Mengubah warna teks menjadi hijau
                    saveEditBtn.setEnabled(true);//tombol simpan edit enabled
                    saveEditKaryawanBtn.setEnabled(true);//tombol simpan edit menjadi enabled
                }
            }else{//jika id karyawan baru == id karyawan lama, atur kondisi
                idkLabel.setText(""); // Menampilkan pesan jika kode belum ada
                idkLabel.setForeground(Color.white); // Mengubah warna teks menjadi hijau
                saveEditBtn.setEnabled(true);//tombol simpan edit enabled
                saveEditKaryawanBtn.setEnabled(true);//tombol simpan edit menjadi enabled
            }
        }else {
            idkLabel.setText(""); // Kosongkan label jika field kosong
            saveEditKaryawanBtn.setEnabled(true);//tombol simpan edit menjadi disabled
            System.out.print(">ID BARU kosong \n");

        }
            
            

        
    }//GEN-LAST:event_IDKaryawanFormKeyReleased

    private void namaLengkapFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapFormActionPerformed

    private void namaLengkapFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_namaLengkapFormKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapFormKeyReleased

    private void emailFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailFormActionPerformed

    private void emailFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailFormKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_emailFormKeyReleased

    private void nomorHPFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomorHPFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorHPFormActionPerformed

    private void nomorHPFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nomorHPFormKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorHPFormKeyReleased

    private void nomorWAFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomorWAFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorWAFormActionPerformed

    private void nomorWAFormKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nomorWAFormKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorWAFormKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            // Set theme to Atom One Dark
            UIManager.setLookAndFeel(new FlatDarkLaf());
            // Optional: You can use other themes like "FlatLightLaf" for a lighter theme
            
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventoryStudio().setVisible(true);
            }
        });
    }
//    private void formatUang() {
//        String text = hargaBeliForm.getText().replaceAll("[^0-9]", ""); // Hanya ambil angka
//        if (!text.isEmpty()) {
//            long value = Long.parseLong(text);
//            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//            symbols.setGroupingSeparator('.'); // Menggunakan titik sebagai pemisah ribuan
//            DecimalFormat formatter = new DecimalFormat("#,###", symbols);
//
//            String formatted = formatter.format(value);
//            hargaBeliForm.setText(formatted); // Update text field dengan format angka
//        }
//    }
    
    private void formatUangWithKeyAdapter() {
    // Menyimpan posisi kursor untuk mempertahankan posisi setelah format
    int caretPosition = hargaBeliForm.getCaretPosition();

    // Ambil teks dan filter hanya angka
    String text = hargaBeliForm.getText().replaceAll("[^0-9]", "");
    if (!text.isEmpty()) {
        try {
            // Parsing angka dan format ke uang
            long value = Long.parseLong(text);

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Menggunakan titik untuk ribuan
            DecimalFormat formatter = new DecimalFormat("#,###", symbols);

            String formatted = formatter.format(value);
            hargaBeliForm.setText(formatted);

            // Set ulang posisi kursor agar user experience tetap baik
            hargaBeliForm.setCaretPosition(Math.min(caretPosition, hargaBeliForm.getText().length()));

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    } else {
        hargaBeliForm.setText(""); // Kosongkan field jika input kosong
    }
}


    
    private String formatTanggal(Date tanggal) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy"); // Format yang diinginkan
        return formatter.format(tanggal); // Mengembalikan tanggal yang sudah diformat
    }
    
    private String formatDateTime(Date tanggal) {
//        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy"); // Format yang diinginkan
        
//        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd MMM yyyy");
        return outputFormat.format(tanggal); // Mengembalikan tanggal yang sudah diformat

    }
    private String formatTgl(Date tanggal) {
//        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy"); // Format yang diinginkan
        
//        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormattgl = new SimpleDateFormat("d MMM yyyy");
        return outputFormattgl.format(tanggal); // Mengembalikan tanggal yang sudah diformat

    }
    
    private void validasiAngka(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Batalkan input
            
        }
    }


    
    // Method untuk format harga menjadi format uang
    public String convertFormatUang(float harga) {
        
        // Mengatur pemisah ribuan menggunakan Locale Indonesia (titik sebagai pemisah ribuan)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Menggunakan titik sebagai pemisah ribuan

        // Format uang dengan simbol "Rp" dan pemisah ribuan titik
        DecimalFormat formatter = new DecimalFormat("Rp #,###", symbols);
        return formatter.format(harga);
        
    }
    
    // Method untuk format harga menjadi format uang, tanpa Rp didepan
    public String convertFormatUangWORP(float harga) {
        
        // Mengatur pemisah ribuan menggunakan Locale Indonesia (titik sebagai pemisah ribuan)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Menggunakan titik sebagai pemisah ribuan

        // Format uang dengan simbol "Rp" dan pemisah ribuan titik
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(harga);
        
    }

    
    private void loadInventoryData() { //mengambil data dari db dan meload di tabel inventaris
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT * FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) dataInventoryTable.getModel();
            model.setRowCount(0); // Menghapus data lama di tabel

            while (rs.next()) {
                // Ambil harga dan konversi ke format uang
                String hargaBeli = rs.getString("hargabeli");
                String formattedHarga = convertFormatUang(Float.parseFloat(hargaBeli));
                
                Date tglBeli = rs.getDate("tglbeli");
                String formattedTanggal = formatTanggal(tglBeli); // Format tanggal ke "d MMMM yyyy"
                
                
                
                Object[] row = {
                    rs.getString("id_inven"),
                    rs.getString("namabarang"),
                    rs.getString("merk"),
                    rs.getString("tipe"),
                    rs.getString("jenisalat"),
                    rs.getString("statusbarang"),
                    formattedHarga, // Harga yang sudah diformat
                    formattedTanggal
                };
                model.addRow(row);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void getDataByKodeInventaris(String kodeInventaris) { //mengambil data dari db dan meload di form edit, berdasarkan kodeInventaris yang dipilih pada tabel inventaris
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT * FROM inventory WHERE id_inven = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeInventaris);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                
                String hargaBeli = rs.getString("hargabeli");
                String formattedHarga = convertFormatUangWORP(Float.parseFloat(hargaBeli));
                
                kodeInventarisForm.setText(rs.getString("id_inven"));
                namaBarangForm.setText(rs.getString("namabarang"));
                merkForm.setText(rs.getString("merk"));
                tipeForm.setText(rs.getString("tipe"));
                hargaBeliForm.setText(formattedHarga);

                //menampilkan data pada jcombobox
                jenisAlatForm.setSelectedItem(rs.getString("jenisalat"));
                statusBarangForm.setSelectedItem(rs.getString("statusbarang"));

                 // Menampilkan data pada JDateChooser (tanggal beli)
                Date tglBeli = rs.getDate("tglbeli"); // Ambil data dari ResultSet
                tglBeliForm.setDate(tglBeli); // Set data ke JDateChooser

            } else {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isKodeInventarisExist(String kodeInventaris) {//mengecek apakah kode tersebut tersedia
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT COUNT(*) FROM inventory WHERE id_inven = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeInventaris);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); // Mendapatkan jumlah data yang ditemukan
                return count > 0; // Jika lebih dari 0 berarti kode sudah ada
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default jika terjadi error atau tidak ditemukan
    }
    
    private boolean isIDKaryawanExist(String kodeInventaris) {//mengecek apakah kode tersebut tersedia
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT COUNT(*) FROM karyawan WHERE id_karyawan = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeInventaris);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); // Mendapatkan jumlah data yang ditemukan
                return count > 0; // Jika lebih dari 0 berarti kode sudah ada
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default jika terjadi error atau tidak ditemukan
    }

    
    private void saveEditDataInventaris() {
        try {
            Connection conn = MySQLConnection.connect();

            // Ambil data dari form
            String kodeInventaris = newKodeInventarisForm.getText().trim(); // Ambil data kode inventaris baru
            String namaBarang = namaBarangForm.getText();
            String merk = merkForm.getText();
            String tipe = tipeForm.getText();
            String jenisAlat = jenisAlatForm.getSelectedItem().toString();
            String statusBarang = statusBarangForm.getSelectedItem().toString();
            String hargaBeli = hargaBeliForm.getText().replaceAll("[^0-9]", ""); // Hapus koma dan karakter non-numerik
            int hargabeliparse = Integer.parseInt(hargaBeli);
            java.sql.Date tglBeli = new java.sql.Date(tglBeliForm.getDate().getTime());

            PreparedStatement stmt = null;

            // Mengecek apakah kode inventaris baru diisi atau tidak
            if (kodeInventaris.isEmpty()) {
                // Jika tidak diisi, maka id_inven tidak diubah
                String sql = "UPDATE inventory SET namabarang = ?, merk = ?, tipe = ?, jenisalat = ?, statusbarang = ?, hargabeli = ?, tglbeli = ? WHERE id_inven = ?";
                stmt = conn.prepareStatement(sql);

                stmt.setString(1, namaBarang);
                stmt.setString(2, merk);
                stmt.setString(3, tipe);
                stmt.setString(4, jenisAlat);
                stmt.setString(5, statusBarang);
                stmt.setInt(6, hargabeliparse);
                stmt.setDate(7, tglBeli);
                stmt.setString(8, kodeInventarisForm.getText()); // Menggunakan kode inventaris yang lama (id_inven)
            } else {
                // Jika diisi, maka kode inventaris akan diubah
                try {
                    int kodeInventarisInt = Integer.parseInt(kodeInventaris); // Mengonversi kodeInventaris menjadi integer
                    String sql = "UPDATE inventory SET id_inven = ?, namabarang = ?, merk = ?, tipe = ?, jenisalat = ?, statusbarang = ?, hargabeli = ?, tglbeli = ? WHERE id_inven = ?";
                    stmt = conn.prepareStatement(sql);

                    stmt.setInt(1, kodeInventarisInt); // Menggunakan kode inventaris baru yang diinputkan
                    stmt.setString(2, namaBarang);
                    stmt.setString(3, merk);
                    stmt.setString(4, tipe);
                    stmt.setString(5, jenisAlat);
                    stmt.setString(6, statusBarang);
                    stmt.setInt(7, hargabeliparse);
                    stmt.setDate(8, tglBeli);
                    stmt.setString(9, kodeInventarisForm.getText()); // Menggunakan kode inventaris yang lama (id_inven)
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Kode Inventaris harus berupa angka!");
                    return; // Menghentikan proses jika terjadi error pada konversi kode inventaris
                }
            }

            // Menjalankan query dan menampilkan pesan sukses
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data "+namaBarang+" berhasil diubah!");
            conn.close();

            // Setelah update, refresh data pada tabel
            loadInventoryData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengubah data!");
        }
    }

    private void getDataByKodePenggunaan(String id_penggunaan) {
        
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT p.id_penggunaan, p.id_inven, p.id_karyawan, i.namabarang, i.merk, i.tipe, i.jenisalat, " +
                  "k.nama_lengkap, p.tanggal_penggunaan, p.tanggal_pengembalian, p.status, p.keterangan " +
                  "FROM penggunaan_inventaris p " +
                  "JOIN inventory i ON p.id_inven = i.id_inven " +
                  "JOIN karyawan k ON p.id_karyawan = k.id_karyawan " +
                  "WHERE p.id_penggunaan = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id_penggunaan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                kodePenggunaanForm.setEnabled(false);
                // Mengambil data dari ResultSet dan menampilkan ke form
                kodePenggunaanForm.setText(rs.getString("id_penggunaan")); // Set kode penggunaan
                keteranganGunaForm.setText(rs.getString("keterangan")); // Set keterangan

                // Set data untuk comboBox Inventaris (menggunakan id_inven, nama_barang)
                String kodeInventaris = rs.getString("id_inven");
                selectInventarisForm.setSelectedItem(kodeInventaris + " - " + rs.getString("namabarang"));

                // Set data untuk comboBox Karyawan (id_karyawan, nama_lengkap)
                String kodeKaryawan = rs.getString("id_karyawan");
                karyawanForm.setSelectedItem(kodeKaryawan + " - " + rs.getString("nama_lengkap"));

                // Menampilkan tanggal penggunaan dan tanggal pengembalian ke JDateChooser
                Date tglPenggunaan = rs.getDate("tanggal_penggunaan");
                tglPenggunaanForm.setDate(tglPenggunaan);

                Date tglPengembalian = rs.getDate("tanggal_pengembalian");
                tglPengembalianForm.setDate(tglPengembalian);

                // Set status inventaris (Digunakan, Dikembalikan, Rusak)
                String statusInventaris = rs.getString("status");
                statusPenggunaanForm.setSelectedItem(statusInventaris);

            } else {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void saveEditDataPenggunaan() {
        try {
            Connection conn = MySQLConnection.connect();
            conn.setAutoCommit(false); // Mulai transaksi

            // Ambil data lama berdasarkan id_penggunaan
            String sqlGetOldData = "SELECT id_inven, status FROM penggunaan_inventaris WHERE id_penggunaan = ?";
            PreparedStatement stmtGetOldData = conn.prepareStatement(sqlGetOldData);
            stmtGetOldData.setString(1, kodePenggunaanForm.getText());
            ResultSet rsOldData = stmtGetOldData.executeQuery();

            String oldIdInven = null;
            String oldStatusPenggunaan = null;
            if (rsOldData.next()) {
                oldIdInven = rsOldData.getString("id_inven"); // id barang yang lama
                oldStatusPenggunaan = rsOldData.getString("status"); // status penggunaan yang lama
            }

            // Ambil data baru dari form
            String idPenggunaan = kodePenggunaanForm.getText().trim(); // ID penggunaan
            String kodeInventaris = selectInventarisForm.getSelectedItem().toString().split(" - ")[0].trim(); // ID inventaris
            String idKaryawan = karyawanForm.getSelectedItem().toString().split(" - ")[0].trim(); // ID karyawan
            String namaBarang = namaBarangForm.getText().trim(); // Nama barang
            String merk = merkForm.getText().trim(); // Merk barang
            String tipe = tipeForm.getText().trim(); // Tipe barang
            String jenisAlat = jenisAlatForm.getSelectedItem().toString(); // Jenis alat
            String statusPenggunaan = statusPenggunaanForm.getSelectedItem().toString(); // Status penggunaan
            String keterangan = keteranganGunaForm.getText().trim(); // Keterangan

            // Format tanggal
            java.sql.Date tanggalPenggunaan = new java.sql.Date(tglPenggunaanForm.getDate().getTime());
            java.sql.Date tanggalPengembalian = new java.sql.Date(tglPengembalianForm.getDate().getTime());

            // Perbarui data penggunaan
            String sqlUpdatePenggunaan = "UPDATE penggunaan_inventaris SET id_inven = ?, id_karyawan = ?, " +
                    "tanggal_penggunaan = ?, tanggal_pengembalian = ?, status = ?, keterangan = ? WHERE id_penggunaan = ?";
            PreparedStatement stmtUpdatePenggunaan = conn.prepareStatement(sqlUpdatePenggunaan);
            stmtUpdatePenggunaan.setString(1, kodeInventaris);
            stmtUpdatePenggunaan.setString(2, idKaryawan);
            stmtUpdatePenggunaan.setDate(3, tanggalPenggunaan);
            stmtUpdatePenggunaan.setDate(4, tanggalPengembalian);
            stmtUpdatePenggunaan.setString(5, statusPenggunaan);
            stmtUpdatePenggunaan.setString(6, keterangan);
            stmtUpdatePenggunaan.setString(7, idPenggunaan);
            stmtUpdatePenggunaan.executeUpdate();

            // Perbarui status barang di inventory jika status penggunaan berubah
            if (!statusPenggunaan.equals(oldStatusPenggunaan)) {
                // Ubah status barang lama menjadi "Ada di Studio" jika perlu
                if (oldIdInven != null) {
                    String sqlUpdateOldInven = "UPDATE inventory SET statusbarang = 'Ada di Studio' WHERE id_inven = ?";
                    PreparedStatement stmtUpdateOldInven = conn.prepareStatement(sqlUpdateOldInven);
                    stmtUpdateOldInven.setString(1, oldIdInven);
                    stmtUpdateOldInven.executeUpdate();
                }

                // Update status barang baru sesuai status penggunaan
                String newStatus = "";
                switch (statusPenggunaan) {
                    case "Digunakan":
                        newStatus = "Digunakan Crew";
                        break;
                    case "Dikembalikan":
                        newStatus = "Ada di Studio";
                        break;
                    case "Rusak":
                        newStatus = "Rusak";
                        break;
                    default:
                        break;
                }

                // Update status barang baru
                String sqlUpdateNewInven = "UPDATE inventory SET statusbarang = ? WHERE id_inven = ?";
                PreparedStatement stmtUpdateNewInven = conn.prepareStatement(sqlUpdateNewInven);
                stmtUpdateNewInven.setString(1, newStatus);
                stmtUpdateNewInven.setString(2, kodeInventaris);
                stmtUpdateNewInven.executeUpdate();
            }

            // Commit transaksi
            conn.commit();
            JOptionPane.showMessageDialog(this, "Data penggunaan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadPenggunaanData(null); // Muat ulang data penggunaan

            // Menutup koneksi
            conn.close();

            // Setelah update, refresh data pada tabel
            clearFormPenggunaan();
            loadPenggunaanData(""); // Menyegarkan data yang ditampilkan di form
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengubah data penggunaan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    
    //mengambil data nama inventaris dari database untuk ditampilkan di combobox (penggunaanPanel) berdasarkan baraang yang ready di studio
    private void loadComboBoxInventaris(String currentIdInven) {
        try {
            Connection conn = MySQLConnection.connect();
            // Query untuk mengambil data inventaris "Ada di Studio" atau barang lama yang digunakan
            String sql = "SELECT id_inven, namabarang FROM inventory " +
                         "WHERE statusbarang = 'Ada di Studio' OR id_inven = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentIdInven); // Tambahkan ID barang lama yang digunakan
            ResultSet rs = stmt.executeQuery();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                String item = rs.getString("id_inven") + " - " + rs.getString("namabarang");
                model.addElement(item);
            }
            selectInventarisForm.setModel(model);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data inventaris!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    //mengambil data nama inventaris dari database untuk ditampilkan di combobox (penggunaanPanel)
    private void loadCBInvenAll() {
        try {
            // Membuat koneksi ke database
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT id_inven, namabarang FROM inventory WHERE statusbarang = 'Ada di Studio' ";  // Query untuk mengambil data id_inven dan namabarang
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Membuat DefaultComboBoxModel untuk combo box
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            // Menambahkan item dari database ke dalam combo box
            while (rs.next()) {
                // Mengambil id_inven dan namabarang
                String item = rs.getString("id_inven") + " - " + rs.getString("namabarang");
                model.addElement(item); // Menambahkan item ke dalam model combo box
            }

            // Mengatur model combo box
            selectInventarisForm.setModel(model);

            // Menutup koneksi
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data inventaris!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //mengambil data karyawan dari database untuk ditampilkan di combobox (penggunaanPanel)
    private void loadComboBoxKaryawan() {
        try {
            // Membuat koneksi ke database
            Connection conn = MySQLConnection.connect();
            String sql = "SELECT id_karyawan, nama_lengkap, email, no_hp, no_wa, jabatan FROM karyawan";  // Query untuk mengambil data karyawan
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Membuat DefaultComboBoxModel untuk combo box
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            // Menambahkan item dari database ke dalam combo box
            while (rs.next()) {
                // Membuat item yang menampilkan nama_lengkap tetapi menyimpan id_karyawan
//                String item = rs.getString("nama_lengkap");
                model.addElement(rs.getString("id_karyawan") + " - " + rs.getString("nama_lengkap")); // Menambahkan id_karyawan dan nama_lengkap ke dalam model combo box
            }

            // Mengatur model combo box
            karyawanForm.setModel(model);

            // Menutup koneksi
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data karyawan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void saveNewData() {//menyimpan data baru
        try {
            Connection conn = MySQLConnection.connect();
            String sql = "INSERT INTO inventory (id_inven, namabarang, merk, tipe, jenisalat, statusbarang, hargabeli, tglbeli) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Validasi input wajib
            if (kodeInventarisForm.getText().trim().isEmpty() || 
                namaBarangForm.getText().trim().isEmpty() || 
                jenisAlatForm.getSelectedItem() == null || 
                statusBarangForm.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Silakan lengkapi data yang wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            stmt.setString(1, kodeInventarisForm.getText().trim());
            stmt.setString(2, namaBarangForm.getText().trim());
            stmt.setString(3, merkForm.getText().trim());
            stmt.setString(4, tipeForm.getText().trim());
            stmt.setString(5, jenisAlatForm.getSelectedItem().toString());
            stmt.setString(6, statusBarangForm.getSelectedItem().toString());
            String hargaBeli = hargaBeliForm.getText().replaceAll("[^0-9]", ""); // Menghapus karakter selain angka
            stmt.setString(7, hargaBeli);

            // Pastikan tglBeliForm memiliki nilai
            if (tglBeliForm.getDate() != null) {
                stmt.setDate(8, new java.sql.Date(tglBeliForm.getDate().getTime()));
            } else {
                stmt.setDate(8, null); // Jika tidak ada tanggal, simpan null
            }

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data baru berhasil ditambahkan!");
            conn.close();

            // Refresh tabel setelah menambah data baru
            loadInventoryData();

            // Bersihkan form
            clearFormInven();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data baru!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveNewDataPenggunaan() { // Menyimpan data penggunaan baru
        try {
            Connection conn = MySQLConnection.connect();

            // SQL untuk memasukkan data baru ke tabel penggunaan_inventaris
            String sql = "INSERT INTO penggunaan_inventaris (id_inven, id_karyawan, tanggal_penggunaan, tanggal_pengembalian, status, keterangan) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);


            // Validasi input wajib
            if (selectInventarisForm.getSelectedItem() == null || 
                karyawanForm.getSelectedItem() == null || 
                tglPenggunaanForm.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Silakan lengkapi data yang wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil data dari form
            String kodeInventaris = selectInventarisForm.getSelectedItem().toString().split(" - ")[0].trim(); // Ambil ID inventaris
            String idKaryawan = karyawanForm.getSelectedItem().toString().split(" - ")[0].trim(); // Ambil ID karyawan
            java.sql.Date tanggalPenggunaan = new java.sql.Date(tglPenggunaanForm.getDate().getTime());
            java.sql.Date tanggalPengembalian = new java.sql.Date(tglPengembalianForm.getDate().getTime());
            String status = statusPenggunaanForm.getSelectedItem().toString();
            String keterangan = keteranganGunaForm.getText().trim();

            // Set parameter untuk statement
            stmt.setString(1, kodeInventaris); // ID Inventaris
            stmt.setString(2, idKaryawan); // ID Karyawan
            stmt.setDate(3, tanggalPenggunaan); // Tanggal Penggunaan
            stmt.setDate(4, tanggalPengembalian); // Tanggal Pengembalian
            stmt.setString(5, status); // Status
            stmt.setString(6, keterangan); // Keterangan

            // Menjalankan query insert data baru dan menampilkan pesan sukses
            stmt.executeUpdate();

            // SQL untuk memperbarui status barang di tabel inventory
            String sqlEditInven = "UPDATE inventory SET statusbarang = ? WHERE id_inven = ?";
            PreparedStatement stmtEditInven = conn.prepareStatement(sqlEditInven);

            // Status barang diubah berdasarkan status penggunaan
            String statusBarang = "Digunakan Crew"; // Sesuaikan dengan kebutuhan
            stmtEditInven.setString(1, statusBarang);
            stmtEditInven.setString(2, kodeInventaris);

            // Eksekusi query update
            stmtEditInven.executeUpdate();


            JOptionPane.showMessageDialog(null, "Data penggunaan baru berhasil ditambahkan!");

            // Menutup koneksi
            conn.close();

            // Setelah insert, refresh data pada tabel
            loadPenggunaanData(""); // Menyegarkan data yang ditampilkan di form

            // Bersihkan form setelah simpan
            clearFormPenggunaan();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data penggunaan baru!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveNewDataKaryawan() {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Mengambil data dari form
            String idKaryawan = IDKaryawanForm.getText().trim();
            String namaLengkap = namaLengkapForm.getText().trim();
            String email = emailForm.getText().trim();
            String nomorHP = nomorHPForm.getText().trim();
            String nomorWA = nomorWAForm.getText().trim();
            String jabatan = jabatanForm.getSelectedItem().toString();

            // Query SQL untuk memasukkan data karyawan baru
            String sqlInsert = "INSERT INTO karyawan (id_karyawan, nama_lengkap, email, no_hp, no_wa, jabatan) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);

            // Set nilai parameter untuk query
            stmt.setString(1, idKaryawan);
            stmt.setString(2, namaLengkap);
            stmt.setString(3, email);
            stmt.setString(4, nomorHP);
            stmt.setString(5, nomorWA);
            stmt.setString(6, jabatan);

            // Eksekusi query untuk menambahkan data
            stmt.executeUpdate();

            // Menampilkan pesan sukses
            JOptionPane.showMessageDialog(this, "Data karyawan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Menutup koneksi
            conn.close();

            // Memuat ulang data karyawan di tabel
            loadKaryawanData();
            clearFormKaryawan();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data karyawan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveEditDataKaryawan() {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Mengambil data dari form
            String idKaryawan = IDKaryawanForm.getText().trim();
            String oldidKaryawan = hiddenOldIDKaryawan.getText().trim();
            String namaLengkap = namaLengkapForm.getText().trim();
            String email = emailForm.getText().trim();
            String nomorHP = nomorHPForm.getText().trim();
            String nomorWA = nomorWAForm.getText().trim();
            String jabatan = jabatanForm.getSelectedItem().toString();


            // Cek apakah ID karyawan berubah
            if (idKaryawan.equals(oldidKaryawan)) {//jika tidak berubah
                // Jika tidak berubah, lakukan update
                String sqlUpdate = "UPDATE karyawan SET nama_lengkap = ?, email = ?, no_hp = ?, no_wa = ?, jabatan = ?"
                        + " WHERE id_karyawan = ?";
                PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
                    // Set nilai parameter untuk query
                stmt.setString(1, namaLengkap);
                stmt.setString(2, email);
                stmt.setString(3, nomorHP);
                stmt.setString(4, nomorWA);
                stmt.setString(5, jabatan);
                stmt.setString(6, idKaryawan);            



                // Eksekusi query untuk memperbarui data
                stmt.executeUpdate();
            }else {//jika id karyawan berubah
                String sqlUpdate = "UPDATE karyawan SET nama_lengkap = ?, email = ?, no_hp = ?, no_wa = ?, jabatan = ?, id_karyawan = ?"
                        + " WHERE id_karyawan = ?";
                PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
                    // Set nilai parameter untuk query
                stmt.setString(1, namaLengkap);
                stmt.setString(2, email);
                stmt.setString(3, nomorHP);
                stmt.setString(4, nomorWA);
                stmt.setString(5, jabatan);
                stmt.setString(6, idKaryawan);//idkaryawan baru
                stmt.setString(7, oldidKaryawan);//idkaryawan lama

                // Eksekusi query untuk memperbarui data
                stmt.executeUpdate();
            }


            // Menampilkan pesan sukses
            JOptionPane.showMessageDialog(this, "Data karyawan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Menutup koneksi
            conn.close();

            // Memuat ulang data karyawan di tabel
            loadKaryawanData();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memperbarui data karyawan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    
    private void clearFormInven() {
        kodeInventarisForm.setText("");
        newKodeInventarisForm.setText("");
        namaBarangForm.setText("");
        merkForm.setText("");
        tipeForm.setText("");
        jenisAlatForm.setSelectedIndex(0);
        statusBarangForm.setSelectedIndex(0);
        hargaBeliForm.setText("");
        tglBeliForm.setDate(null);
        saveDataBtn.setEnabled(true);  // Aktifkan tombol saveDataBtn
        saveEditBtn.setEnabled(false); // Nonaktifkan tombol saveEditBtn
        kodeInventarisForm.setEnabled(true);// Nonaktifkan kodeInventarisForm textfield
        newKodeInventarisForm.setEnabled(false);// aktifkan newKodeInventarisForm textfield
        kodeLabel.setText(""); 
        newKodeLabel.setText(""); 
    }
    
    private void clearFormPenggunaan() {
        kodePenggunaanForm.setEnabled(true);
        kodePenggunaanForm.setText("");
        
        tglPenggunaanForm.setDate(null);        
        tglPengembalianForm.setDate(null);
        statusPenggunaanForm.setSelectedIndex(0);
        NewGunaDataBtn.setEnabled(true);  // Aktifkan tombol NewGunaDataBtn
        saveEditGunaBtn.setEnabled(false); // Nonaktifkan tombol saveEditGunaBtn
        keteranganGunaForm.setText(""); 
        loadComboBoxKaryawan();
        loadComboBoxInventaris("");
//        karyawanForm.setSelectedIndex(0);
//        selectInventarisForm.setSelectedIndex(0);
    }
    
    private void clearFormKaryawan() {
        IDKaryawanForm.setEnabled(true);
        hiddenOldIDKaryawan.setText("");
        namaLengkapForm.setText("");
        emailForm.setText("");
        nomorHPForm.setText("");
        nomorWAForm.setText("");
        IDKaryawanForm.setText("");
        jabatanForm.setSelectedIndex(0);
        // Atur tombol tambah dan edit karyawan
        saveNewKaryawanBtn.setEnabled(true);  // Aktifkan tombol NewGunaDataBtn
        saveEditKaryawanBtn.setEnabled(false); // Nonaktifkan tombol saveEditGunaBtn
        keteranganGunaForm.setText(""); 
        idkLabel.setText("");
    }
    
    
    private void deleteData(String kodeInventaris) {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Query untuk menghapus data berdasarkan kode inventaris
            String sql = "DELETE FROM inventory WHERE id_inven = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeInventaris);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Menampilkan pesan sukses jika data berhasil dihapus
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Menampilkan pesan gagal jika tidak ada data yang dihapus
                JOptionPane.showMessageDialog(null, "Kode inventaris tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
            // Setelah penghapusan, lakukan refresh data tabel
            loadInventoryData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void deleteDataPenggunaan(String kodePenggunaan) {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Query untuk menghapus data berdasarkan kode inventaris
            String sql = "DELETE FROM penggunaan_inventaris WHERE id_penggunaan = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodePenggunaan);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Menampilkan pesan sukses jika data berhasil dihapus
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Menampilkan pesan gagal jika tidak ada data yang dihapus
                JOptionPane.showMessageDialog(null, "Kode Penggunaan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
            // Setelah penghapusan, lakukan refresh data tabel
            loadPenggunaanData("");
            clearFormPenggunaan();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteDataKaryawan(String idkaryawan) {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Query untuk menghapus data berdasarkan kode inventaris
            String sql = "DELETE FROM karyawan WHERE id_karyawan = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idkaryawan);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Menampilkan pesan sukses jika data berhasil dihapus
                JOptionPane.showMessageDialog(null, "Data Karyawan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Menampilkan pesan gagal jika tidak ada data yang dihapus
                JOptionPane.showMessageDialog(null, "Kode Karyawan Penggunaan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
            // Setelah penghapusan, lakukan refresh data tabel
            loadPenggunaanData("");
            clearFormKaryawan();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    // Metode untuk berpindah ke panel penggunaan
    private void showPenggunaanPanel(String kodeInventaris) {
        // Ganti panel utama menjadi panel "Penggunaan"
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "penggunaan");

        // Muat data terkait inventaris berdasarkan kodeInventaris
        loadPenggunaanData(kodeInventaris);
    }

    private void setColumnWidths(JTable table) {
        // Mendapatkan model kolom tabel
        TableColumnModel columnModel = table.getColumnModel();

        // Atur lebar kolom sesuai kebutuhan
        columnModel.getColumn(0).setPreferredWidth(100); // Kode Penggunaan
        columnModel.getColumn(1).setPreferredWidth(100); // Kode Inventaris
        columnModel.getColumn(2).setPreferredWidth(300); // Barang
        columnModel.getColumn(3).setPreferredWidth(150); // Nama Pengguna
        columnModel.getColumn(4).setPreferredWidth(150); // Tanggal Penggunaan
        columnModel.getColumn(5).setPreferredWidth(150); // Tanggal Pengembalian
        columnModel.getColumn(6).setPreferredWidth(100); // Status
        columnModel.getColumn(7).setPreferredWidth(200); // Keterangan

        // Opsional: Mengatur minimum dan maksimum lebar kolom
        columnModel.getColumn(2).setMinWidth(200); // Barang tidak boleh lebih kecil dari 200 px
        columnModel.getColumn(2).setMaxWidth(400); // Barang tidak boleh lebih besar dari 400 px
    }

    // Metode untuk memuat data penggunaan berdasarkan kode inventaris
    private void loadPenggunaanData(String kodeInventaris) {
    try {
        Connection conn = MySQLConnection.connect();
        String sql;
        PreparedStatement stmt;

        if (kodeInventaris == null || kodeInventaris.trim().isEmpty()) {
            // Jika kodeInventaris kosong, ambil semua data
            sql = "SELECT p.id_penggunaan, p.id_inven, p.id_karyawan, i.namabarang, i.merk, i.tipe, i.jenisalat, " +
                  "k.nama_lengkap, p.tanggal_penggunaan, p.tanggal_pengembalian, p.status, p.keterangan " +
                  "FROM penggunaan_inventaris p " +
                  "JOIN inventory i ON p.id_inven = i.id_inven " +
                  "JOIN karyawan k ON p.id_karyawan = k.id_karyawan ";
            stmt = conn.prepareStatement(sql);
        } else {
            // Jika kodeInventaris ada, ambil data sesuai kode
            sql = "SELECT p.id_penggunaan, p.id_inven, p.id_karyawan, i.namabarang, i.merk, i.tipe, i.jenisalat, " +
                  "k.nama_lengkap, p.tanggal_penggunaan, p.tanggal_pengembalian, p.status, p.keterangan " +
                  "FROM penggunaan_inventaris p " +
                  "JOIN inventory i ON p.id_inven = i.id_inven " +
                  "JOIN karyawan k ON p.id_karyawan = k.id_karyawan " +
                  "WHERE p.id_inven = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeInventaris);
        }

        ResultSet rs = stmt.executeQuery();
//        System.out.print(stmt);

        DefaultTableModel model = (DefaultTableModel) dataPenggunaanTable.getModel();
        model.setRowCount(0); // Bersihkan tabel penggunaan sebelumnya
//        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd MMM yyyy");
        while (rs.next()) {
            // Menggabungkan data barang
            String barang = String.format("%s, %s, %s, %s",
                    rs.getString("namabarang"),
                    rs.getString("merk"),
                    rs.getString("tipe"),
                    rs.getString("jenisalat"));

            // Mengambil dan memformat tanggal
//            if(rs.getDate("tanggal_penggunaan").isEmpty)
            Date tglGuna = rs.getDate("tanggal_penggunaan");
            String tanggalPenggunaan = (tglGuna != null) ? formatTanggal(tglGuna) : "";

            Date tglKembali = rs.getDate("tanggal_pengembalian");
            String tanggalPengembalian = (tglKembali != null) ? formatTanggal(tglKembali) : "";

            // Menambahkan data ke tabel
            Object[] row = {
                rs.getString("id_penggunaan"),
                rs.getString("id_inven"),
                barang, // Menambahkan kolom "Barang"
                rs.getString("nama_lengkap"), // Nama karyawan
                tanggalPenggunaan,
                tanggalPengembalian,
                rs.getString("status"),
                rs.getString("keterangan"),
            };
            model.addRow(row);
        }

        setColumnWidths(dataPenggunaanTable);
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat data penggunaan!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void loadKaryawanData() {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Query SQL untuk mengambil data karyawan
            String sqlSelect = "SELECT * FROM karyawan";
            PreparedStatement stmt = conn.prepareStatement(sqlSelect);
            ResultSet rs = stmt.executeQuery();

            // Mendapatkan model tabel
            DefaultTableModel model = (DefaultTableModel) dataKaryawanTable.getModel();

            // Menghapus data lama sebelum menambahkan data baru
            model.setRowCount(0);

            // Menambahkan data ke tabel
            while (rs.next()) {
                String idKaryawan = rs.getString("id_karyawan");
                String namaLengkap = rs.getString("nama_lengkap");
                String email = rs.getString("email");
                String nomorHP = rs.getString("no_hp");
                String nomorWA = rs.getString("no_wa");
                String jabatan = rs.getString("jabatan");

                // Menambahkan baris ke model tabel
                model.addRow(new Object[]{idKaryawan, namaLengkap, email, nomorHP, nomorWA, jabatan});
            }

            // Menutup koneksi
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data karyawan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getDataByIDKaryawan(String idKaryawan) {
        try {
            // Membuka koneksi ke database
            Connection conn = MySQLConnection.connect();

            // Query SQL untuk mengambil data karyawan berdasarkan id_karyawan
            String sqlSelect = "SELECT * FROM karyawan WHERE id_karyawan = ?";
            PreparedStatement stmt = conn.prepareStatement(sqlSelect);
            stmt.setString(1, idKaryawan); // Menetapkan parameter id_karyawan
            ResultSet rs = stmt.executeQuery();

            // Memeriksa apakah data ditemukan
            if (rs.next()) {
                // Mengambil data dari ResultSet dan memasukkan ke dalam form
                String namaLengkap = rs.getString("nama_lengkap");
                String idkaryawan = rs.getString("id_karyawan");
                String email = rs.getString("email");
                String nomorHP = rs.getString("no_hp");
                String nomorWA = rs.getString("no_wa");
                String jabatan = rs.getString("jabatan");

                // Memasukkan data ke form
                hiddenOldIDKaryawan.setText(idkaryawan);
//                System.out.print(hiddenOldIDKaryawan.getText());
                IDKaryawanForm.setText(idkaryawan);
                namaLengkapForm.setText(namaLengkap);
                emailForm.setText(email);
                nomorHPForm.setText(nomorHP);
                nomorWAForm.setText(nomorWA);
                jabatanForm.setSelectedItem(jabatan); // Menggunakan JComboBox untuk jabatan
            }

            // Menutup koneksi
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data karyawan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    







    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDKaryawanForm;
    private javax.swing.JButton NewGunaDataBtn;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JButton clearFormInvenBtn;
    private javax.swing.JButton clearFormKaryawanBtn;
    private javax.swing.JButton clearGunaFormBtn;
    private javax.swing.JTable dataInventoryTable;
    private javax.swing.JTable dataKaryawanTable;
    private javax.swing.JTable dataPenggunaanTable;
    private javax.swing.JButton datakaryawanBtn;
    private javax.swing.JPanel datakaryawanPanel;
    private javax.swing.JButton deleteDataBtn;
    private javax.swing.JButton deleteDataGunaBtn;
    private javax.swing.JButton deleteKaryawanBtn;
    private javax.swing.JTextField emailForm;
    private javax.swing.JButton exitBtn;
    private javax.swing.JTextField hargaBeliForm;
    private javax.swing.JTextField hiddenOldIDKaryawan;
    private javax.swing.JLabel idkLabel;
    private javax.swing.JPanel inventarisPanel;
    private javax.swing.JButton inventoryBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox<String> jabatanForm;
    private javax.swing.JComboBox<String> jenisAlatForm;
    private javax.swing.JComboBox<String> karyawanForm;
    private javax.swing.JTextArea keteranganGunaForm;
    private javax.swing.JTextField kodeInventarisForm;
    private javax.swing.JLabel kodeLabel;
    private javax.swing.JTextField kodePenggunaanForm;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JTextField merkForm;
    private javax.swing.JTextField namaBarangForm;
    private javax.swing.JTextField namaLengkapForm;
    private javax.swing.JTextField newKodeInventarisForm;
    private javax.swing.JLabel newKodeLabel;
    private javax.swing.JTextField nomorHPForm;
    private javax.swing.JTextField nomorWAForm;
    private javax.swing.JButton penggunaanBtn;
    private javax.swing.JPanel penggunaanPanel;
    private javax.swing.JButton refreshDataInventoryBtn;
    private javax.swing.JButton refreshGunaTabelBtn;
    private javax.swing.JButton refreshKaryawanTableBtn;
    private javax.swing.JButton saveDataBtn;
    private javax.swing.JButton saveEditBtn;
    private javax.swing.JButton saveEditGunaBtn;
    private javax.swing.JButton saveEditKaryawanBtn;
    private javax.swing.JButton saveNewKaryawanBtn;
    private javax.swing.JComboBox<String> selectInventarisForm;
    private javax.swing.JComboBox<String> statusBarangForm;
    private javax.swing.JComboBox<String> statusPenggunaanForm;
    private com.toedter.calendar.JDateChooser tglBeliForm;
    private com.toedter.calendar.JDateChooser tglPengembalianForm;
    private com.toedter.calendar.JDateChooser tglPenggunaanForm;
    private javax.swing.JTextField tipeForm;
    // End of variables declaration//GEN-END:variables
}
