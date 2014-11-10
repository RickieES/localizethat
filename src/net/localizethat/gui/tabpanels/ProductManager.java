/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.awt.Color;
import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.localizethat.Main;
import net.localizethat.model.Channel;
import net.localizethat.model.L10n;
import net.localizethat.model.Product;
import net.localizethat.model.ProductSourceType;
import net.localizethat.util.gui.JStatusBar;

/**
 *
 * @author rpalomares
 */
public class ProductManager extends javax.swing.JPanel {
    EntityManagerFactory emf;
    JStatusBar statusBar;
    SimpleDateFormat dateFormat;
    Product selectedProduct;

    /**
     * Creates new form ProductManager
     */
    public ProductManager() {
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();

        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
        refreshProductList();
        refreshL10nList();
        refreshChannelList();

        for(ProductSourceType pst : ProductSourceType.values()) {
            productSourceTypeListModel.addElement(pst);
        }
        prodSourceTypeCombo.invalidate();
        clearProductFields();
        enableProductFields(false);

        productList.getSelectionModel().addListSelectionListener(new ProductManager.ProductListRowListener());
    }

    private void clearProductFields() {
        prodNameField.setText("");
        prodDefL10nCombo.setSelectedIndex(-1);
        prodChannelCombo.setSelectedIndex(-1);
        prodSourceTypeCombo.setSelectedIndex(-1);
        prodNotesArea.setText("");
        prodColorValue.setBackground(Color.gray);
        // TODO add reset for paths once we figure how we will manage them
    }

    private void enableProductFields(boolean activate) {
        prodNameField.setEnabled(activate);
        prodDefL10nCombo.setEnabled(activate);
        prodChannelCombo.setEnabled(activate);
        prodSourceTypeCombo.setEnabled(activate);
        prodNotesArea.setEnabled(activate);
        prodColorLabel.setEnabled(activate);

        // newProductButton.setEnabled(activate); // This is always activated
        saveProductButton.setEnabled(activate);
        deleteProductButton.setEnabled(activate);
        copyProductButton.setEnabled(activate);

        // TODO manage the controls in paths tab, once we figure how we will manage them
    }

    private void refreshProductList() {
        TypedQuery<Product> productQuery = entityManager.createNamedQuery("Product.findAll",
                Product.class);
        productListModel.clearAll();
        productListModel.addAll(productQuery.getResultList());
    }

    private void refreshL10nList() {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        l10nListModel.clearAll();
        l10nListModel.addAll(l10nQuery.getResultList());
    }

    private void refreshChannelList() {
        TypedQuery<Channel> channelQuery = entityManager.createNamedQuery("Channel.findAll",
                Channel.class);
        channelListModel.clearAll();
        channelListModel.addAll(channelQuery.getResultList());
    }

    private boolean validateOnSave() {
        // Validation 1: the product name (case insensitive) can't exist already in the database,
        // except in the same product
        TypedQuery<Product> validationQuery = entityManager.createNamedQuery(
                "Product.findByName", Product.class);
        validationQuery.setParameter("name", prodNameField.getText());
        List<Product> colProd = validationQuery.getResultList();
        int colLength = colProd.size();
        boolean isOk;
        if (colLength == 0) {
            isOk = true;
        } else if (colLength == 1) {
            Product productInDB = colProd.get(0);
            isOk = (Objects.equals(productInDB.getId(), selectedProduct.getId()));
        } else {
            // This should never be reached, since we don't allow more than one product
            // with the same name, but it is checked just as defensive programming
            isOk = false;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: this product already exists",
                    "There is another product already with the same name than you want to save in the database");
            return false;
        }

        // Validation 2: the name must not be empty
        if (prodNameField.getText().trim().length() < 1) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: product name can't be empty",
                    "The product name must not be empty");
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        productListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<Product>();
        entityManager = emf.createEntityManager();
        l10nListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        channelListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<Channel>();
        productSourceTypeListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<ProductSourceType>();
        scrollProductList = new javax.swing.JScrollPane();
        productList = new javax.swing.JList();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        productInfoPanel = new javax.swing.JPanel();
        prodNameLabel = new javax.swing.JLabel();
        prodNameField = new javax.swing.JTextField();
        prodDefL10nLabel = new javax.swing.JLabel();
        prodDefL10nCombo = new javax.swing.JComboBox();
        prodChannelLabel = new javax.swing.JLabel();
        prodChannelCombo = new javax.swing.JComboBox();
        sourceTypeLabel = new javax.swing.JLabel();
        prodSourceTypeCombo = new javax.swing.JComboBox();
        prodColorLabel = new javax.swing.JLabel();
        prodColorValue = new javax.swing.JLabel();
        prodNotesLabel = new javax.swing.JLabel();
        scrollNotesArea = new javax.swing.JScrollPane();
        prodNotesArea = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        creationDateLabel = new javax.swing.JLabel();
        lastUpdatedLabel = new javax.swing.JLabel();
        productDetailsPanel = new javax.swing.JPanel();
        pathsForProductLabel = new javax.swing.JLabel();
        scrollPathTable = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        origPathAddButton = new javax.swing.JButton();
        origPathDelButton = new javax.swing.JButton();
        origPathImportButton = new javax.swing.JButton();
        extraPathField = new net.localizethat.util.gui.JPathField();
        pathL10nCombo = new javax.swing.JComboBox();
        buttonPanel = new javax.swing.JPanel();
        newProductButton = new javax.swing.JButton();
        saveProductButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        copyProductButton = new javax.swing.JButton();

        productList.setModel(productListModel);
        productList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollProductList.setViewportView(productList);

        prodNameLabel.setText("Name:");

        prodNameField.setColumns(32);

        prodDefL10nLabel.setText("Default L10n:");

        prodDefL10nCombo.setModel(l10nListModel);

        prodChannelLabel.setText("Channel:");

        prodChannelCombo.setModel(channelListModel);

        sourceTypeLabel.setText("Source Type:");

        prodSourceTypeCombo.setModel(productSourceTypeListModel);

        prodColorLabel.setText("Mnemonic color:");

        prodColorValue.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        prodColorValue.setText("Sample");
        prodColorValue.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        prodColorValue.setOpaque(true);
        prodColorValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prodColorValueMouseClicked(evt);
            }
        });

        prodNotesLabel.setText("Notes:");

        prodNotesArea.setColumns(20);
        prodNotesArea.setRows(5);
        scrollNotesArea.setViewportView(prodNotesArea);

        jTextField1.setEditable(false);
        jTextField1.setEnabled(false);

        jTextField2.setEditable(false);
        jTextField2.setEnabled(false);

        creationDateLabel.setText("Creation Date:");

        lastUpdatedLabel.setText("Last updated:");

        javax.swing.GroupLayout productInfoPanelLayout = new javax.swing.GroupLayout(productInfoPanel);
        productInfoPanel.setLayout(productInfoPanelLayout);
        productInfoPanelLayout.setHorizontalGroup(
            productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(productInfoPanelLayout.createSequentialGroup()
                            .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(prodChannelLabel)
                                .addComponent(prodDefL10nLabel)
                                .addComponent(prodNameLabel))
                            .addGap(33, 33, 33))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, productInfoPanelLayout.createSequentialGroup()
                            .addComponent(prodColorLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addGroup(productInfoPanelLayout.createSequentialGroup()
                        .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(creationDateLabel)
                            .addComponent(lastUpdatedLabel)
                            .addComponent(prodNotesLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2)
                    .addComponent(prodColorValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prodNameField)
                    .addComponent(prodDefL10nCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(productInfoPanelLayout.createSequentialGroup()
                        .addComponent(prodChannelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prodSourceTypeCombo, 0, 167, Short.MAX_VALUE))
                    .addComponent(scrollNotesArea)
                    .addComponent(jTextField1))
                .addContainerGap())
        );
        productInfoPanelLayout.setVerticalGroup(
            productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodNameLabel)
                    .addComponent(prodNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodDefL10nCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodDefL10nLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodChannelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodSourceTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceTypeLabel)
                    .addComponent(prodChannelLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prodColorLabel)
                    .addComponent(prodColorValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(creationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastUpdatedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollNotesArea, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(prodNotesLabel))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Basic Info", productInfoPanel);

        pathsForProductLabel.setText("Paths for this product");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Path", "Locale"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPathTable.setViewportView(jTable1);

        origPathAddButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-add.png"))); // NOI18N
        origPathAddButton.setMaximumSize(new java.awt.Dimension(46, 26));
        origPathAddButton.setMinimumSize(new java.awt.Dimension(46, 26));
        origPathAddButton.setPreferredSize(new java.awt.Dimension(46, 26));

        origPathDelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-remove.png"))); // NOI18N
        origPathDelButton.setMaximumSize(new java.awt.Dimension(46, 26));
        origPathDelButton.setMinimumSize(new java.awt.Dimension(46, 26));
        origPathDelButton.setPreferredSize(new java.awt.Dimension(46, 26));

        origPathImportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-import.png"))); // NOI18N
        origPathImportButton.setMaximumSize(new java.awt.Dimension(46, 26));
        origPathImportButton.setMinimumSize(new java.awt.Dimension(46, 26));
        origPathImportButton.setPreferredSize(new java.awt.Dimension(46, 26));

        pathL10nCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout productDetailsPanelLayout = new javax.swing.GroupLayout(productDetailsPanel);
        productDetailsPanel.setLayout(productDetailsPanelLayout);
        productDetailsPanelLayout.setHorizontalGroup(
            productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addComponent(pathsForProductLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                                .addComponent(extraPathField, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathL10nCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(scrollPathTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(origPathDelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(origPathAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(origPathImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        productDetailsPanelLayout.setVerticalGroup(
            productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pathsForProductLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addComponent(origPathAddButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(origPathDelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(origPathImportButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPathTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pathL10nCombo)
                    .addComponent(extraPathField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(227, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Locales and Paths", productDetailsPanel);

        newProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-new.png"))); // NOI18N
        newProductButton.setMnemonic('N');
        newProductButton.setText("New");
        newProductButton.setToolTipText("Create a new product");
        newProductButton.setMaximumSize(new java.awt.Dimension(117, 42));
        newProductButton.setMinimumSize(new java.awt.Dimension(117, 42));
        newProductButton.setPreferredSize(new java.awt.Dimension(117, 42));
        newProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProductButtonActionPerformed(evt);
            }
        });

        saveProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-save.png"))); // NOI18N
        saveProductButton.setMnemonic('S');
        saveProductButton.setText("Save");
        saveProductButton.setToolTipText("Edit the selected product");
        saveProductButton.setMaximumSize(new java.awt.Dimension(117, 42));
        saveProductButton.setMinimumSize(new java.awt.Dimension(117, 42));
        saveProductButton.setPreferredSize(new java.awt.Dimension(117, 42));
        saveProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProductButtonActionPerformed(evt);
            }
        });

        deleteProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteProductButton.setMnemonic('D');
        deleteProductButton.setText("Delete");
        deleteProductButton.setToolTipText("Delete this product and all its contents");
        deleteProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProductButtonActionPerformed(evt);
            }
        });

        copyProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-copy.png"))); // NOI18N
        copyProductButton.setText("Copy");
        copyProductButton.setToolTipText("Make a copy of this project");
        copyProductButton.setMaximumSize(new java.awt.Dimension(117, 42));
        copyProductButton.setMinimumSize(new java.awt.Dimension(117, 42));
        copyProductButton.setPreferredSize(new java.awt.Dimension(117, 42));

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(copyProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(newProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteProductButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteProductButton)
                    .addComponent(copyProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollProductList, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollProductList))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void newProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProductButtonActionPerformed
        Product p = new Product();
        p.setName("(new product)");
        p.setL10nId(null); // TODO if we define an user preference for default original L10n
        p.setChannelId(null);
        p.setSrcType(ProductSourceType.HG);
        p.setColor("DDDDDD");
        p.setNotes("");
        p.setCreationDate(new Date());
        p.setLastUpdate(new Date());
        try {
            entityManager.persist(p);
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            statusBar.setText(JStatusBar.LogMsgType.INFO,
                "New item added, use detail fields to complete it");
            productListModel.addElement(p);
            productListModel.setSelectedItem(p);
            productList.setSelectedIndex(productListModel.getSelectedIndex());
            productList.ensureIndexIsVisible(productListModel.getSelectedIndex());
            clearProductFields();
            prodNameField.requestFocus();
        } catch (Exception ex) {
            Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating",
                "Error while creating element", ex);
        }
    }//GEN-LAST:event_newProductButtonActionPerformed

    private void saveProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProductButtonActionPerformed
        // validateOnSave will report the specific problem in the status bar
        if (!validateOnSave()) {
            return;
        }

        Product p = productListModel.getSelectedTypedItem();
        p.setChannelId(channelListModel.getSelectedTypedItem());
        String hexRGBColor = String.format("%02X", prodColorValue.getBackground().getRed()) +
                             String.format("%02X", prodColorValue.getBackground().getGreen()) +
                             String.format("%02X", prodColorValue.getBackground().getBlue());
        p.setColor(hexRGBColor);
        p.setL10nId(l10nListModel.getSelectedTypedItem());
        p.setPathList(null); // TODO build the path list
        p.setName(prodNameField.getText());
        p.setNotes(prodNotesArea.getText());
        p.setSrcType(productSourceTypeListModel.getSelectedTypedItem());
        p.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            refreshProductList();
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Changes saved");
        } catch (Exception ex) {
            Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving",
                "Error while saving changes", ex);
        }

    }//GEN-LAST:event_saveProductButtonActionPerformed

    private void deleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProductButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected product?\n\n" +
                "Deleting a product implies removing all its contents,\n" +
                "which means removing all paths and the files under it\n" +
                "not used by other products.\n" +
                "You could lose thousands of strings!!\n",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            Product p = productListModel.getSelectedTypedItem();
            // TODO implement removal of paths not used by other products
            try {
                entityManager.remove(p);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                refreshProductList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Product deleted");
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting product", ex);
            }
        }
    }//GEN-LAST:event_deleteProductButtonActionPerformed

    private void prodColorValueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prodColorValueMouseClicked
        JLabel tsclLabel = (JLabel) evt.getSource();
        Color newColor = JColorChooser.showDialog(this,
                "Choose mnemonic background color for this project", tsclLabel.getBackground());

        if (newColor != null) {
            tsclLabel.setBackground(newColor);
        }
    }//GEN-LAST:event_prodColorValueMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Channel> channelListModel;
    private javax.swing.JButton copyProductButton;
    private javax.swing.JLabel creationDateLabel;
    private javax.swing.JButton deleteProductButton;
    private javax.persistence.EntityManager entityManager;
    private net.localizethat.util.gui.JPathField extraPathField;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> l10nListModel;
    private javax.swing.JLabel lastUpdatedLabel;
    private javax.swing.JButton newProductButton;
    private javax.swing.JButton origPathAddButton;
    private javax.swing.JButton origPathDelButton;
    private javax.swing.JButton origPathImportButton;
    private javax.swing.JComboBox pathL10nCombo;
    private javax.swing.JLabel pathsForProductLabel;
    private javax.swing.JComboBox prodChannelCombo;
    private javax.swing.JLabel prodChannelLabel;
    private javax.swing.JLabel prodColorLabel;
    private javax.swing.JLabel prodColorValue;
    private javax.swing.JComboBox prodDefL10nCombo;
    private javax.swing.JLabel prodDefL10nLabel;
    private javax.swing.JTextField prodNameField;
    private javax.swing.JLabel prodNameLabel;
    private javax.swing.JTextArea prodNotesArea;
    private javax.swing.JLabel prodNotesLabel;
    private javax.swing.JComboBox prodSourceTypeCombo;
    private javax.swing.JPanel productDetailsPanel;
    private javax.swing.JPanel productInfoPanel;
    private javax.swing.JList productList;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Product> productListModel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<ProductSourceType> productSourceTypeListModel;
    private javax.swing.JButton saveProductButton;
    private javax.swing.JScrollPane scrollNotesArea;
    private javax.swing.JScrollPane scrollPathTable;
    private javax.swing.JScrollPane scrollProductList;
    private javax.swing.JLabel sourceTypeLabel;
    // End of variables declaration//GEN-END:variables

private class ProductListRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = productList.getSelectedIndex();
            if (selectedRow != -1) {
                selectedProduct = productListModel.getElementAt(selectedRow);
                productListModel.setSelectedItem(selectedProduct);
                prodNameField.setText(selectedProduct.getName());
                prodDefL10nCombo.setSelectedItem(selectedProduct.getL10nId());
                prodChannelCombo.setSelectedItem(selectedProduct.getChannelId());
                prodSourceTypeCombo.setSelectedItem(selectedProduct.getSrcType());
                prodNotesArea.setText(selectedProduct.getNotes());
                prodColorValue.setBackground(selectedProduct.returnAwtColor());
                enableProductFields(true);
                // prodCreationDateField.setText(selectedProduct.getCreationDate().toString());
                // prodLastUpdatedField.setText(selectedProduct.getLastUpdate().toString());
            }
        }
    }

}
