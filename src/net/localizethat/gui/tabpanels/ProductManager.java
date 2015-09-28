/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.awt.Color;
import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import net.localizethat.gui.models.ListComboBoxGenericModel;
import net.localizethat.model.Channel;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocalePath;
import net.localizethat.model.Product;
import net.localizethat.model.ProductSourceType;
import net.localizethat.model.jpa.JPAHelperBundle;
import net.localizethat.model.jpa.LocaleContainerJPAHelper;
import net.localizethat.system.AppSettings;
import net.localizethat.util.gui.JStatusBar;

/**
 *
 * @author rpalomares
 */
public class ProductManager extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    JStatusBar statusBar;
    SimpleDateFormat dateFormat;
    Product selectedProduct;
    JPAHelperBundle jhb;

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

        jhb = JPAHelperBundle.getInstance(entityManager);

        // Load values into product source type combo
        for(ProductSourceType pst : ProductSourceType.values()) {
            productSourceTypeListModel.addElement(pst);
        }
        prodSourceTypeCombo.invalidate();
        productList.getSelectionModel().addListSelectionListener(
                new ProductManager.ProductListRowListener());
    }

    private void clearProductFields() {
        prodNameField.setText("");
        prodDefL10nCombo.setSelectedIndex(-1);
        prodChannelCombo.setSelectedIndex(-1);
        prodSourceTypeCombo.setSelectedIndex(-1);
        prodNotesArea.setText("");
        prodColorValue.setBackground(Color.gray);
        newPathField.setText("");
        origPathTableModel.clearAll();
        origPathTableModel.fireTableDataChanged();
        targetPathTableModel.clearAll();
        targetPathTableModel.fireTableDataChanged();
    }

    private void enableProductFields(boolean activate) {
        prodNameField.setEnabled(activate);
        prodDefL10nCombo.setEnabled(activate);
        prodChannelCombo.setEnabled(activate);
        prodSourceTypeCombo.setEnabled(activate);
        prodNotesArea.setEnabled(activate);
        prodColorLabel.setEnabled(activate);

        saveProductButton.setEnabled(activate);
        deleteProductButton.setEnabled(activate);
        // copyProductButton.setEnabled(activate); TODO Commented out until we actually implement the feature

        origPathTable.setEnabled(activate);
        targetPathTable.setEnabled(activate);
        newPathField.setEnabled(activate);
        pathL10nCombo.setEnabled(activate);
        addOrigPathButton.setEnabled(activate);
        delOrigPathButton.setEnabled(activate);
        importPathButton.setEnabled(activate);
        addTargetPathButton.setEnabled(activate);
        delTargetPathButton.setEnabled(activate);
    }

    private void refreshProductList() {
        TypedQuery<Product> productQuery = entityManager.createNamedQuery("Product.findAll",
                Product.class);
        productListModel.clearAll();
        productListModel.addAll(productQuery.getResultList());
    }

    private void refreshL10nList(ListComboBoxGenericModel<L10n> listModel) {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        listModel.clearAll();
        listModel.addAll(l10nQuery.getResultList());
    }

    private void refreshChannelList() {
        TypedQuery<Channel> channelQuery = entityManager.createNamedQuery("Channel.findAll",
                Channel.class);
        channelListModel.clearAll();
        channelListModel.addAll(channelQuery.getResultList());
    }

    private void refreshPathList(String queryName, L10n l10n) {
        TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                queryName, LocalePath.class);
        localePathQuery.setParameter("l10nid", selectedProduct.getL10nId());

        switch (queryName) {
            case "LocalePath.findByProductAndL10n":
                localePathQuery.setParameter("productid", selectedProduct.getId());
                origPathTableModel.clearAll();
                origPathTableModel.addAll(localePathQuery.getResultList());
                break;
            case "LocalePath.findByProductAndNotL10n":
                localePathQuery.setParameter("productid", selectedProduct.getId());
                targetPathTableModel.clearAll();
                targetPathTableModel.addAll(localePathQuery.getResultList());
                break;
            case "LocalePath.findByL10n":
                importOrigPathListModel.clearAll();
                importOrigPathListModel.addAll(localePathQuery.getResultList());
                break;
        }
    }

    private void refreshOrigPathList() {
        TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                "LocalePath.findByProductAndL10n", LocalePath.class);
        localePathQuery.setParameter("productid", selectedProduct.getId());
        localePathQuery.setParameter("l10nid", selectedProduct.getL10nId());
        origPathTableModel.clearAll();
        origPathTableModel.addAll(localePathQuery.getResultList());
    }

    private void refreshTargetPathList() {
        TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                "LocalePath.findByProductAndNotL10n", LocalePath.class);
        localePathQuery.setParameter("productid", selectedProduct.getId());
        localePathQuery.setParameter("l10nid", selectedProduct.getL10nId());
        targetPathTableModel.clearAll();
        targetPathTableModel.addAll(localePathQuery.getResultList());
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
            prodNameField.requestFocusInWindow();
            return false;
        }

        // Validation 3: the default language can't be empty
        if (l10nListModel.getSelectedTypedItem() == null) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: default L10n can't be empty",
                    "The product default, or original, language code must not be empty");
            prodDefL10nCombo.requestFocusInWindow();
            return false;
        }

        // Validation 4: the product source type can't be empty
        if (this.prodSourceTypeCombo.getSelectedItem() == null) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: product source type can't be empty",
                    "The product source type must not be empty; if unsure, choose MANUAL");
            prodSourceTypeCombo.requestFocusInWindow();
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
        origPathTableModel = new net.localizethat.gui.models.PathTableModel();
        pathL10nListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        targetPathTableModel = new net.localizethat.gui.models.PathTableModel();
        importPathDialog = new javax.swing.JDialog();
        importPathDialog.pack();
        importPathDialog.setLocationRelativeTo(null);
        jScrollPanePathImportList = new javax.swing.JScrollPane();
        importOrigPathList = new javax.swing.JList<LocalePath>();
        jScrollPanePathImportTable = new javax.swing.JScrollPane();
        importtargetPathTable = new javax.swing.JTable();
        pathsForThisProductLabel = new javax.swing.JLabel();
        targetPathsLabel = new javax.swing.JLabel();
        buttonImportPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        importOrigPathListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<LocalePath>();
        importTargetPathTableModel = new net.localizethat.gui.models.PathTableModel();
        scrollProductList = new javax.swing.JScrollPane();
        productList = new javax.swing.JList<Product>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        productInfoPanel = new javax.swing.JPanel();
        prodNameLabel = new javax.swing.JLabel();
        prodNameField = new javax.swing.JTextField();
        prodDefL10nLabel = new javax.swing.JLabel();
        prodDefL10nCombo = new javax.swing.JComboBox<L10n>();
        prodChannelLabel = new javax.swing.JLabel();
        prodChannelCombo = new javax.swing.JComboBox<Channel>();
        sourceTypeLabel = new javax.swing.JLabel();
        prodSourceTypeCombo = new javax.swing.JComboBox<ProductSourceType>();
        prodColorLabel = new javax.swing.JLabel();
        prodColorValue = new javax.swing.JLabel();
        prodNotesLabel = new javax.swing.JLabel();
        scrollNotesArea = new javax.swing.JScrollPane();
        prodNotesArea = new javax.swing.JTextArea();
        prodCreationDateField = new javax.swing.JTextField();
        prodLastUpdatedField = new javax.swing.JTextField();
        creationDateLabel = new javax.swing.JLabel();
        lastUpdatedLabel = new javax.swing.JLabel();
        productDetailsPanel = new javax.swing.JPanel();
        pathsForProductLabel = new javax.swing.JLabel();
        scrollOriginalPathTable = new javax.swing.JScrollPane();
        origPathTable = new javax.swing.JTable();
        addOrigPathButton = new javax.swing.JButton();
        delOrigPathButton = new javax.swing.JButton();
        importPathButton = new javax.swing.JButton();
        newPathField = new net.localizethat.util.gui.JPathField();
        pathL10nCombo = new javax.swing.JComboBox<L10n>();
        scrollTargetPathTable = new javax.swing.JScrollPane();
        targetPathTable = new javax.swing.JTable();
        addTargetPathButton = new javax.swing.JButton();
        delTargetPathButton = new javax.swing.JButton();
        targetPathsForProductLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        newProductButton = new javax.swing.JButton();
        saveProductButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        copyProductButton = new javax.swing.JButton();

        importPathDialog.setTitle("Import paths to this product");
        importPathDialog.setModal(true);

        importOrigPathList.setModel(importOrigPathListModel);
        importOrigPathList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        importOrigPathList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                importOrigPathListValueChanged(evt);
            }
        });
        jScrollPanePathImportList.setViewportView(importOrigPathList);

        importtargetPathTable.setModel(importTargetPathTableModel);
        importtargetPathTable.setRowSelectionAllowed(false);
        jScrollPanePathImportTable.setViewportView(importtargetPathTable);

        pathsForThisProductLabel.setText("Select one of the available paths for the default locale of this product:");

        targetPathsLabel.setText("The following target paths will be also added to the product:");

        okButton.setText("Import");
        okButton.setToolTipText("Import the selected path into the product");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonImportPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Close this dialog and return to Product Manager");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonImportPanel.add(cancelButton);

        javax.swing.GroupLayout importPathDialogLayout = new javax.swing.GroupLayout(importPathDialog.getContentPane());
        importPathDialog.getContentPane().setLayout(importPathDialogLayout);
        importPathDialogLayout.setHorizontalGroup(
            importPathDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importPathDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(importPathDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPanePathImportTable, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPanePathImportList, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(importPathDialogLayout.createSequentialGroup()
                        .addGroup(importPathDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(targetPathsLabel)
                            .addComponent(pathsForThisProductLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(buttonImportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        importPathDialogLayout.setVerticalGroup(
            importPathDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, importPathDialogLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(pathsForThisProductLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPanePathImportList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetPathsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPanePathImportTable, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonImportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        importPathDialog.getAccessibleContext().setAccessibleParent(this);

        productList.setModel(productListModel);
        productList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollProductList.setViewportView(productList);

        prodNameLabel.setText("Name:");

        prodNameField.setColumns(32);

        prodDefL10nLabel.setText("Default L10n:");

        prodDefL10nCombo.setModel(l10nListModel);

        prodChannelLabel.setText("Channel:");

        prodChannelCombo.setModel(channelListModel);

        sourceTypeLabel.setText("Source type:");

        prodSourceTypeCombo.setModel(productSourceTypeListModel);

        prodColorLabel.setLabelFor(prodColorValue);
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

        prodCreationDateField.setEditable(false);
        prodCreationDateField.setEnabled(false);

        prodLastUpdatedField.setEditable(false);
        prodLastUpdatedField.setEnabled(false);

        creationDateLabel.setText("Creation date:");

        lastUpdatedLabel.setText("Last update:");

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
                    .addComponent(prodLastUpdatedField)
                    .addComponent(prodColorValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prodNameField)
                    .addComponent(prodDefL10nCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(productInfoPanelLayout.createSequentialGroup()
                        .addComponent(prodChannelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prodSourceTypeCombo, 0, 191, Short.MAX_VALUE))
                    .addComponent(scrollNotesArea)
                    .addComponent(prodCreationDateField))
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
                    .addComponent(prodCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(creationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodLastUpdatedField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastUpdatedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollNotesArea, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(prodNotesLabel))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Basic Info", productInfoPanel);

        pathsForProductLabel.setText("Paths in default locale for this product");

        origPathTable.setAutoCreateRowSorter(true);
        origPathTable.setModel(origPathTableModel);
        scrollOriginalPathTable.setViewportView(origPathTable);

        addOrigPathButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-add.png"))); // NOI18N
        addOrigPathButton.setToolTipText("Add the path in the fields below the table");
        addOrigPathButton.setMaximumSize(new java.awt.Dimension(46, 26));
        addOrigPathButton.setMinimumSize(new java.awt.Dimension(46, 26));
        addOrigPathButton.setPreferredSize(new java.awt.Dimension(46, 26));
        addOrigPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrigPathButtonActionPerformed(evt);
            }
        });

        delOrigPathButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-remove.png"))); // NOI18N
        delOrigPathButton.setToolTipText("Remove the selected path from this product");
        delOrigPathButton.setMaximumSize(new java.awt.Dimension(46, 26));
        delOrigPathButton.setMinimumSize(new java.awt.Dimension(46, 26));
        delOrigPathButton.setPreferredSize(new java.awt.Dimension(46, 26));
        delOrigPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delOrigPathButtonActionPerformed(evt);
            }
        });

        importPathButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-import.png"))); // NOI18N
        importPathButton.setToolTipText("Import existing path into this product");
        importPathButton.setMaximumSize(new java.awt.Dimension(46, 26));
        importPathButton.setMinimumSize(new java.awt.Dimension(46, 26));
        importPathButton.setPreferredSize(new java.awt.Dimension(46, 26));
        importPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importPathButtonActionPerformed(evt);
            }
        });

        newPathField.setSelectDirs(true);

        pathL10nCombo.setModel(pathL10nListModel);

        targetPathTable.setAutoCreateRowSorter(true);
        targetPathTable.setModel(targetPathTableModel);
        scrollTargetPathTable.setViewportView(targetPathTable);

        addTargetPathButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-add.png"))); // NOI18N
        addTargetPathButton.setToolTipText("Add the path in the fields above the table");
        addTargetPathButton.setMaximumSize(new java.awt.Dimension(46, 26));
        addTargetPathButton.setMinimumSize(new java.awt.Dimension(46, 26));
        addTargetPathButton.setPreferredSize(new java.awt.Dimension(46, 26));
        addTargetPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTargetPathButtonActionPerformed(evt);
            }
        });

        delTargetPathButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-list-remove.png"))); // NOI18N
        delTargetPathButton.setToolTipText("Remove the selected path from this product");
        delTargetPathButton.setMaximumSize(new java.awt.Dimension(46, 26));
        delTargetPathButton.setMinimumSize(new java.awt.Dimension(46, 26));
        delTargetPathButton.setPreferredSize(new java.awt.Dimension(46, 26));
        delTargetPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTargetPathButtonActionPerformed(evt);
            }
        });

        targetPathsForProductLabel.setText("Paths in other locales for this product");

        javax.swing.GroupLayout productDetailsPanelLayout = new javax.swing.GroupLayout(productDetailsPanel);
        productDetailsPanel.setLayout(productDetailsPanelLayout);
        productDetailsPanelLayout.setHorizontalGroup(
            productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addComponent(pathsForProductLabel)
                        .addGap(0, 261, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, productDetailsPanelLayout.createSequentialGroup()
                        .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                                .addComponent(newPathField, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathL10nCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(scrollTargetPathTable, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollOriginalPathTable, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, productDetailsPanelLayout.createSequentialGroup()
                                .addComponent(targetPathsForProductLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(delOrigPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addOrigPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(importPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(delTargetPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addTargetPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        productDetailsPanelLayout.setVerticalGroup(
            productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pathsForProductLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addComponent(addOrigPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delOrigPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollOriginalPathTable, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newPathField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pathL10nCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(targetPathsForProductLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTargetPathTable, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(productDetailsPanelLayout.createSequentialGroup()
                        .addComponent(addTargetPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(delTargetPathButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Locales and paths", productDetailsPanel);

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
        copyProductButton.setEnabled(false);
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
                        .addComponent(jTabbedPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollProductList))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void newProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProductButtonActionPerformed
        Product p = new Product();
        p.setName("(new product)");
        p.setL10nId(null);
        String defL10nCode = Main.appSettings.getString(AppSettings.PREF_DEFAULT_ORIGINAL_LANGUAGE);
        for(L10n l : l10nListModel.getAll()) {
            if (l.getCode().equals(defL10nCode)) {
                p.setL10nId(l);
                break;
            }
        }
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
        String hexRGBColor = Integer.toHexString(prodColorValue.getBackground().getRGB());
        p.setColor(hexRGBColor);
        p.setL10nId(l10nListModel.getSelectedTypedItem());
        p.setName(prodNameField.getText());
        p.setNotes(prodNotesArea.getText());
        p.setSrcType(productSourceTypeListModel.getSelectedTypedItem());
        p.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            refreshProductList();
            productListModel.setSelectedItem(p);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Changes saved");
        } catch (Exception ex) {
            Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving",
                "Error while saving changes", ex);
        }
        clearProductFields();
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

            LocaleContainerJPAHelper lcHelper = jhb.getLocaleContainerJPAHelper();

            // First we remove the locale paths whose locale container has a default
            // locale twin, since this means that the neither the locale container nor
            // the locale path are foreign keys in other objects
            for(Iterator<LocalePath> iterator = selectedProduct.getPathList().iterator(); iterator.hasNext();) {
                LocalePath lpToRemove = iterator.next();
                if (lpToRemove.getLocaleContainer().getDefLocaleTwin() != null) {
                    lpToRemove.removeProduct(selectedProduct);
                    iterator.remove(); // selectedProduct.removeLocalePath(lpToRemove);
                    // If no more products use this path, we fully remove it
                    if (lpToRemove.getProductList().isEmpty()) {
                        // Remove associated container (recursively) from DB
                        LocaleContainer linkedLc = lpToRemove.getLocaleContainer();
                        // lp.setLocaleContainer(null);
                        lcHelper.removeRecursively(linkedLc);
                        // Remove path from DB
                        entityManager.remove(lpToRemove);
                    }
                }
            }

            /*
            Exception in thread "AWT-EventQueue-0" java.util.ConcurrentModificationException
	at java.util.Vector$Itr.checkForComodification(Vector.java:1184)
	at java.util.Vector$Itr.next(Vector.java:1137)
	at org.eclipse.persistence.indirection.IndirectList$1.next(IndirectList.java:571)
	at net.localizethat.gui.tabpanels.ProductManager.deleteProductButtonActionPerformed(ProductManager.java:776)
 
            */

            // Now that only "default" locale paths remain, we can safely remove them
            // without worrying about orphan siblings
            for(Iterator<LocalePath> iterator = selectedProduct.getPathList().iterator(); iterator.hasNext();) {
                LocalePath lpToRemove = iterator.next();
                lpToRemove.removeProduct(selectedProduct);
                iterator.remove(); // selectedProduct.removeLocalePath(lpToRemove);
                if (lpToRemove.getProductList().isEmpty()) {
                    // Remove associated container (recursively) from DB
                    LocaleContainer linkedLc = lpToRemove.getLocaleContainer();
                    // lp.setLocaleContainer(null);
                    lcHelper.removeRecursively(linkedLc);
                    // Remove path from DB
                    entityManager.remove(lpToRemove);
                }
            }

            try {
                entityManager.remove(selectedProduct);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                refreshProductList();
                refreshOrigPathList();
                refreshTargetPathList();
                clearProductFields();
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

    private void addOrigPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrigPathButtonActionPerformed
        L10n comboPathL10n = pathL10nListModel.getSelectedTypedItem();
        String newPath = newPathField.getText();

        if ((comboPathL10n == null) || (!comboPathL10n.equals(selectedProduct.getL10nId()))) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Mismatch in locales",
                    "Please, select the default locale for this product before adding "
                  + "the path to the above list");
            pathL10nCombo.requestFocus();
            return;
        }

        TypedQuery<Long> validationQuery = entityManager.createNamedQuery(
                "LocalePath.countByPath", Long.class);
        validationQuery.setParameter("path", newPath);
        long count = validationQuery.getSingleResult();
        if (count > 0) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Path already exists",
                    "The path you are trying to add is already defined. Please, instead of "
                  + "adding it, use the Import button");
            newPathField.requestFocus();
            return;
        }

        // Add LocalePath and LocaleContainer();
        LocalePath lp = new LocalePath();
        LocaleContainer lc = new LocaleContainer();

        // Fill path and container fields
        lp.setL10nId(comboPathL10n);
        lp.setPath(newPath);
        lp.setLocaleContainer(lc);
        lp.setCreationDate(new Date());
        lp.setLastUpdate(lp.getCreationDate());

        lc.setName(lp.getPathLastComponent());
        lc.setL10nId(comboPathL10n);
        lc.setCreationDate(new Date());
        lc.setLastUpdate(lc.getCreationDate());

        entityManager.persist(lc);
        entityManager.persist(lp);
        selectedProduct.addChild(lp);
        lp.addChild(selectedProduct);
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        origPathTableModel.addElement(lp);
        int index = origPathTableModel.getIndexOf(lp);
        origPathTable.changeSelection(origPathTable.convertRowIndexToView(index), 0, false, false);
        statusBar.setInfoText("Path added");
    }//GEN-LAST:event_addOrigPathButtonActionPerformed

    private void addTargetPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTargetPathButtonActionPerformed
        L10n comboPathL10n = pathL10nListModel.getSelectedTypedItem();
        String newPath = newPathField.getText();
        int origPathIndex = origPathTable.getSelectedRow();
        LocalePath lpOrig;
        LocaleContainer lcOrig;

        if (origPathIndex == -1) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "No original path selected",
                    "Please, select a path in the Paths for default locale table before adding "
                  + "the target path to the bottom table");
            newPathField.requestFocus();
            return;
        } else {
            origPathIndex = origPathTable.convertRowIndexToModel(origPathIndex);
            lpOrig = origPathTableModel.getElement(origPathIndex);
            lcOrig = (lpOrig.getLocaleContainer().getDefLocaleTwin() == null) ?
                    lpOrig.getLocaleContainer() :
                    lpOrig.getLocaleContainer().getDefLocaleTwin();
        }

        if ((comboPathL10n == null) || (comboPathL10n.equals(selectedProduct.getL10nId()))) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Mismatch in locales",
                    "Please, select a locale other than default for this product before adding "
                  + "the path to the below list");
            pathL10nCombo.requestFocus();
            return;
        }

        TypedQuery<Long> validation1Query = entityManager.createNamedQuery(
                "LocalePath.countByPath", Long.class);
        validation1Query.setParameter("path", newPath);
        long count = validation1Query.getSingleResult();
        if (count > 0) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Path already exists",
                    "The path you are trying to add is already defined. Please, instead of "
                  + "adding it, use the Import button to add the original and associated target paths");
            newPathField.requestFocus();
            return;
        }

        TypedQuery<LocalePath> validation2Query = entityManager.createNamedQuery(
                "LocalePath.findByL10n", LocalePath.class);
        validation2Query.setParameter("l10nid", comboPathL10n);
        List<LocalePath> validationResult = validation2Query.getResultList();
        for(LocalePath lpCheck : validationResult) {
            if (lcOrig.isATwin(lpCheck.getLocaleContainer())) {
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Locale already has a path",
                        "There is already a path with the locale you have specified. Please,"
                      + "use the Import button to add the original and associated target paths");
                newPathField.requestFocus();
                return;
            }
        }

        // Add LocalePath and LocaleContainer();
        LocalePath lp = new LocalePath();
        LocaleContainer lc = new LocaleContainer();

        // Fill path and container fields
        lp.setL10nId(comboPathL10n);
        lp.setPath(newPath);
        lp.setLocaleContainer(lc);
        lp.setCreationDate(new Date());
        lp.setLastUpdate(lp.getCreationDate());

        lc.setName(lp.getPathLastComponent());
        lc.setL10nId(comboPathL10n);
        lc.setDefLocaleTwin(lcOrig);
        lc.setCreationDate(new Date());
        lc.setLastUpdate(lc.getCreationDate());

        entityManager.persist(lc);
        entityManager.persist(lp);
        selectedProduct.addChild(lp);
        lp.addChild(selectedProduct);
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        targetPathTableModel.addElement(lp);
        int index = targetPathTableModel.getIndexOf(lp);
        // targetPathTableModel.fireTableRowsInserted(origPathIndex, origPathIndex);
        targetPathTable.changeSelection(targetPathTable.convertRowIndexToView(index), 0, false, false);
        statusBar.setInfoText("Path added");
    }//GEN-LAST:event_addTargetPathButtonActionPerformed

    private void delOrigPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delOrigPathButtonActionPerformed
        int fullDelete = JOptionPane.NO_OPTION;
        LocalePath lpToRemove;
        LocaleContainer lc;
        LocaleContainerJPAHelper lcHelper;
        int origPathIndex = origPathTable.getSelectedRow();
        if (origPathIndex == -1) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "No original path selected",
                    "Please, select a path in the Paths for default locale table to delete it");
            return;
        } else {
            origPathIndex = origPathTable.convertRowIndexToModel(origPathIndex);
        }

        lpToRemove = origPathTableModel.getElement(origPathIndex);
        lc = lpToRemove.getLocaleContainer();
        lcHelper = jhb.getLocaleContainerJPAHelper();

        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected path from this product?",
                "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        // Build the list of linked paths to this one
        List<LocalePath> fullPathList = new ArrayList<>(5);
        fullPathList.add(lpToRemove);
        for(LocalePath lp : selectedProduct.getPathList()) {
            // If the twin LocaleContainer of the locale container of this path is the
            // locale container of the path to be removed, then the lp path is linked to
            // lpToRemove and must be removed, too
            if (lc != null && lp.getLocaleContainer().getDefLocaleTwin() == lc) {
                fullPathList.add(lp);
            }
        }

        // Dummy loop to fetch the list of products that use lpToRemove
        for(Product p : lpToRemove.getProductList()) { }

        // If this is the only product using the path,
        if (lpToRemove.getProductList().size() == 1) {
            // Ask if it should be entirely removed from the DB
            fullDelete = JOptionPane.showConfirmDialog(this.getParent(),
                    "This path is not used in any other product.\n"
                  + "Would you like to delete it, including ALL the folders, files and strings under it?",
                    "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        }

        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
        }

        // For each path to be removed,
        for(LocalePath lp : fullPathList) {
            // We don't remove the default language LocalePaths until the associated
            // LocalePaths have been fully removed
            if (lp.getLocaleContainer() != lc) {
                // Remove path from this product
                selectedProduct.removeLocalePath(lp);
                lp.removeProduct(selectedProduct);

                if (fullDelete == JOptionPane.YES_OPTION) {
                    // Remove associated container (recursively) from DB
                    LocaleContainer linkedLc = lp.getLocaleContainer();
                    // lp.setLocaleContainer(null);
                    lcHelper.removeRecursively(linkedLc);
                    // Remove path from DB
                    entityManager.remove(lp);
                }
            }
        }

        // Remove default path from this product
        selectedProduct.removeLocalePath(lpToRemove);
        lpToRemove.removeProduct(selectedProduct);

        if (fullDelete == JOptionPane.YES_OPTION) {
            // Remove associated container (recursively) from DB
            LocaleContainer linkedLc = lpToRemove.getLocaleContainer();
            // lp.setLocaleContainer(null);
            lcHelper.removeRecursively(linkedLc);
            // Remove path from DB
            entityManager.remove(lpToRemove);
        }

        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        refreshOrigPathList();
        refreshTargetPathList();
        statusBar.setInfoText("Path deleted");
    }//GEN-LAST:event_delOrigPathButtonActionPerformed

    private void delTargetPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTargetPathButtonActionPerformed
        JOptionPane.showMessageDialog(this.getParent(),
                "To remove a target path, you need to remove the original\n"
              + "path to which the target path is associated", "Delete original path instead",
              JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_delTargetPathButtonActionPerformed

    private void importPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importPathButtonActionPerformed
        // Load existing LocalePaths in the DB for the locale defined as default in this product
        refreshPathList("LocalePath.findByL10n", selectedProduct.getL10nId());
        // Open the dialog and force a refresh to the list and table
        importPathDialog.pack();
        importPathDialog.setVisible(true);
    }//GEN-LAST:event_importPathButtonActionPerformed

    private void importOrigPathListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_importOrigPathListValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        importOrigPathListModel.setSelectedIndex(importOrigPathList.getSelectedIndex());
        okButton.setEnabled(true);
        LocalePath lp = importOrigPathListModel.getSelectedTypedItem();
        importTargetPathTableModel.clearAll();

        if (lp == null) {
            // No LocalePath instance, probably because the event handler has been called
            // as a result of refreshing the importOrigPathListModel
            return;
        }
        LocaleContainer lc = lp.getLocaleContainer();

        if (lc != null) {
            TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                    "LocalePath.findByAssociatedLocaleContainer", LocalePath.class);
            localePathQuery.setParameter("localecontainer", lc);
            importTargetPathTableModel.addAll(localePathQuery.getResultList());
        }
    }//GEN-LAST:event_importOrigPathListValueChanged

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // okButton is disabled until a original path is selected,
        // so the following line should never throw an NPE
        LocalePath lp = importOrigPathListModel.getSelectedTypedItem();

        // We can only import paths not already part of this product
        if (selectedProduct.hasChild(lp)) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Path already associated to this product",
                    "The path selected already exists in this product. You can't import the same path twice.");
            return;
        }

        lp.addChild(selectedProduct);
        selectedProduct.addChild(lp);

        int targetPathCount = importTargetPathTableModel.getRowCount();
        for(int i = 0; i < targetPathCount; i++) {
            LocalePath targetLp = importTargetPathTableModel.getElement(i);
            lp.addChild(selectedProduct);
            selectedProduct.addChild(targetLp);
        }

        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        refreshOrigPathList();
        refreshTargetPathList();
        importPathDialog.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        importPathDialog.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addOrigPathButton;
    private javax.swing.JButton addTargetPathButton;
    private javax.swing.JPanel buttonImportPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Channel> channelListModel;
    private javax.swing.JButton copyProductButton;
    private javax.swing.JLabel creationDateLabel;
    private javax.swing.JButton delOrigPathButton;
    private javax.swing.JButton delTargetPathButton;
    private javax.swing.JButton deleteProductButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JList<LocalePath> importOrigPathList;
    private net.localizethat.gui.models.ListComboBoxGenericModel<LocalePath> importOrigPathListModel;
    private javax.swing.JButton importPathButton;
    private javax.swing.JDialog importPathDialog;
    private net.localizethat.gui.models.PathTableModel importTargetPathTableModel;
    private javax.swing.JTable importtargetPathTable;
    private javax.swing.JScrollPane jScrollPanePathImportList;
    private javax.swing.JScrollPane jScrollPanePathImportTable;
    private javax.swing.JTabbedPane jTabbedPane1;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> l10nListModel;
    private javax.swing.JLabel lastUpdatedLabel;
    private net.localizethat.util.gui.JPathField newPathField;
    private javax.swing.JButton newProductButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTable origPathTable;
    private net.localizethat.gui.models.PathTableModel origPathTableModel;
    private javax.swing.JComboBox<L10n> pathL10nCombo;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> pathL10nListModel;
    private javax.swing.JLabel pathsForProductLabel;
    private javax.swing.JLabel pathsForThisProductLabel;
    private javax.swing.JComboBox<Channel> prodChannelCombo;
    private javax.swing.JLabel prodChannelLabel;
    private javax.swing.JLabel prodColorLabel;
    private javax.swing.JLabel prodColorValue;
    private javax.swing.JTextField prodCreationDateField;
    private javax.swing.JComboBox<L10n> prodDefL10nCombo;
    private javax.swing.JLabel prodDefL10nLabel;
    private javax.swing.JTextField prodLastUpdatedField;
    private javax.swing.JTextField prodNameField;
    private javax.swing.JLabel prodNameLabel;
    private javax.swing.JTextArea prodNotesArea;
    private javax.swing.JLabel prodNotesLabel;
    private javax.swing.JComboBox<ProductSourceType> prodSourceTypeCombo;
    private javax.swing.JPanel productDetailsPanel;
    private javax.swing.JPanel productInfoPanel;
    private javax.swing.JList<Product> productList;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Product> productListModel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<ProductSourceType> productSourceTypeListModel;
    private javax.swing.JButton saveProductButton;
    private javax.swing.JScrollPane scrollNotesArea;
    private javax.swing.JScrollPane scrollOriginalPathTable;
    private javax.swing.JScrollPane scrollProductList;
    private javax.swing.JScrollPane scrollTargetPathTable;
    private javax.swing.JLabel sourceTypeLabel;
    private javax.swing.JTable targetPathTable;
    private net.localizethat.gui.models.PathTableModel targetPathTableModel;
    private javax.swing.JLabel targetPathsForProductLabel;
    private javax.swing.JLabel targetPathsLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
            jhb = JPAHelperBundle.getInstance(entityManager);
        }
        refreshProductList();
        refreshL10nList(l10nListModel);
        refreshL10nList(pathL10nListModel);
        refreshChannelList();
        origPathTableModel.clearAll();
        targetPathTableModel.clearAll();

        clearProductFields();
        enableProductFields(false);
    }

    @Override
    public void onTabPanelRemoved() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.flush();
            entityManager.getTransaction().commit();
        }
        entityManager.close();
        entityManager = null;
    }

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
                prodCreationDateField.setText(selectedProduct.getCreationDate().toString());
                prodLastUpdatedField.setText(selectedProduct.getLastUpdate().toString());
                enableProductFields(true);
                refreshOrigPathList();
                refreshTargetPathList();
                statusBar.clearText();
            }
        }
    }
}
