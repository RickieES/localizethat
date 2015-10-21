/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Collection;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JList;
import net.localizethat.Main;
import net.localizethat.gui.models.ListComboBoxGenericModel;
import net.localizethat.gui.models.SelectableItem;
import net.localizethat.gui.renderers.SelectableListItem;
import net.localizethat.model.L10n;
import net.localizethat.model.LocalePath;
import net.localizethat.model.Product;
import net.localizethat.tasks.ExportProductWorker;
import net.localizethat.util.gui.JStatusBar;

/**
 * Export Product panel, providing the GUI interaction to perform an
 * ExportProductWorker execution
 * @author rpalomares
 */
public class ExportProductPanel extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    private final EntityManagerFactory emf;
    private final JStatusBar statusBar;
    private ExportProductWorker xpw;
    private boolean isResultTabOpened;

    /**
     * Creates new form ExportProductPanel
     */
    public ExportProductPanel() {
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // if (!Beans.isDesignTime() && entityManager == null) {
        //     entityManager = emf.createEntityManager();
        // }
        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
    }
    
    public ExportProductPanel(EntityManager entityManager) {
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        this.entityManager = entityManager;
        initComponents();

        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private void refreshProductList() {
        TypedQuery<Product> productQuery = entityManager.createNamedQuery("Product.findAll",
                Product.class);

        productListModel.clearAll();
        Collection<Product> collProduct = productQuery.getResultList();
        for(Product p : collProduct) {
            productListModel.addElement(new SelectableItem<>(p, false));
        }
    }

    private void refreshL10nList(ListComboBoxGenericModel<L10n> listModel) {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        listModel.clearAll();
        listModel.addAll(l10nQuery.getResultList());
    }

    private void changeProductItemSelectedState(int index) {
        SelectableItem<Product> item = productList.getModel().getElementAt(index);
        item.setSelected(!item.isSelected());
        Rectangle rect = productList.getCellBounds(index, index);
        productList.repaint(rect);
        
        // Remove/add (in this order) paths to the path list
        for (Iterator<SelectableItem<Product>> it = productListModel.iterator(); it.hasNext();) {
            SelectableItem<Product> ip = it.next();
            if (!ip.isSelected()) {
                adjustPathListForProduct(ip.getItem(), ip.isSelected());
            }
        }
        for (Iterator<SelectableItem<Product>> it = productListModel.iterator(); it.hasNext();) {
            SelectableItem<Product> ip = it.next();
            if (ip.isSelected()) {
                adjustPathListForProduct(ip.getItem(), ip.isSelected());
            }
        }
        originalpathsList.repaint();


        // Synchronize the check/uncheck all select box with the list
        // selection status
        boolean allSelected = true;
        for (Iterator<SelectableItem<Product>> it = productListModel.iterator(); it.hasNext();) {
            SelectableItem<Product> ip = it.next();
            allSelected &= ip.isSelected();
        }
        if (allSelected != selectAllProductsCheck.isSelected()) {
            selectAllProductsCheck.setSelected(allSelected);
            selectAllProductsCheck.repaint();
        }
    }

    private void adjustPathListForProduct(Product p, boolean adding) {
        Collection<LocalePath> lc = p.getPathList();

        for(LocalePath lp : lc) {
            // We only deal with the original paths, ie., those of the default
            // L10n of the product
            if (lp.getL10nId() == p.getL10nId()) {
                // If we're adding a product,
                if (adding) {
                    // We look for paths not existing yet, to add them to the list
                    if (!originalPathsListModel.contains(lp)) {
                        originalPathsListModel.addElement(lp);
                    }
                } else {
                    // We're removing a product and proceed the opposite
                    if (originalPathsListModel.contains(lp)) {
                        originalPathsListModel.removeElement(lp);
                    }
                }
            }
        }
        originalPathsListModel.sort();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        if (!Beans.isDesignTime() && entityManager == null) {
            entityManager = emf.createEntityManager();
        }
        productListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<SelectableItem<Product>>();
        originalPathsListModel = new net.localizethat.gui.models.ListComboBoxGenericModel<LocalePath>();
        listL10nModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        selectProductsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productList = new javax.swing.JList<SelectableItem<Product>>();
        originalPathsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        originalpathsList = new javax.swing.JList<LocalePath>();
        selectAllProductsCheck = new javax.swing.JCheckBox();
        targetLocaleLabel = new javax.swing.JLabel();
        targetLocaleCombo = new javax.swing.JComboBox<L10n>();
        removeObsoleteCheck = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        exportOutputArea = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        exportButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        selectProductsLabel.setText("Select products to export:");

        productList.setModel(productListModel);
        productList.setCellRenderer(new SelectableListItem());
        productList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productListMouseClicked(evt);
            }
        });
        productList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                productListKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(productList);

        originalPathsLabel.setText("Original paths included:");

        originalpathsList.setModel(originalPathsListModel);
        originalpathsList.setEnabled(false);
        jScrollPane2.setViewportView(originalpathsList);

        selectAllProductsCheck.setMnemonic('C');
        selectAllProductsCheck.setText("(Un)Check  to (de)select all products");
        selectAllProductsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllProductsCheckActionPerformed(evt);
            }
        });

        targetLocaleLabel.setDisplayedMnemonic('S');
        targetLocaleLabel.setLabelFor(targetLocaleCombo);
        targetLocaleLabel.setText("Select target locale:");

        targetLocaleCombo.setModel(listL10nModel);

        removeObsoleteCheck.setMnemonic('R');
        removeObsoleteCheck.setText("Remove obsolete files from disk");

        exportOutputArea.setEditable(false);
        exportOutputArea.setColumns(20);
        exportOutputArea.setRows(5);
        exportOutputArea.setEnabled(false);
        jScrollPane3.setViewportView(exportOutputArea);

        exportButton.setMnemonic('x');
        exportButton.setText("Export");
        exportButton.setToolTipText("Export selected products");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(exportButton);

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel and close tab");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectAllProductsCheck)
                            .addComponent(selectProductsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(targetLocaleLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(targetLocaleCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(removeObsoleteCheck)
                                    .addComponent(originalPathsLabel))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectProductsLabel)
                    .addComponent(originalPathsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectAllProductsCheck)
                        .addComponent(targetLocaleLabel))
                    .addComponent(targetLocaleCombo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(removeObsoleteCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void productListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productListMouseClicked
        int index = productList.locationToIndex(evt.getPoint());
        changeProductItemSelectedState(index);
    }//GEN-LAST:event_productListMouseClicked

    private void selectAllProductsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllProductsCheckActionPerformed
        for(int i = 0; i < productListModel.getSize(); i++) {
            SelectableItem<Product> item = productListModel.getElementAt(i);
            item.setSelected(selectAllProductsCheck.isSelected());
        }
        productList.repaint();

        // Remove/add (in this order) paths to the path list
        for (Iterator<SelectableItem<Product>> it = productListModel.iterator(); it.hasNext();) {
            SelectableItem<Product> ip = it.next();
            if (!ip.isSelected()) {
                adjustPathListForProduct(ip.getItem(), ip.isSelected());
            }
        }
        for (Iterator<SelectableItem<Product>> it = productListModel.iterator(); it.hasNext();) {
            SelectableItem<Product> ip = it.next();
            if (ip.isSelected()) {
                adjustPathListForProduct(ip.getItem(), ip.isSelected());
            }
        }
        originalpathsList.repaint();
    }//GEN-LAST:event_selectAllProductsCheckActionPerformed

    private void productListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productListKeyTyped
        JList c = (JList) evt.getComponent();
        char keyCode = evt.getKeyChar();
        int index = c.getLeadSelectionIndex();

        if ((keyCode == KeyEvent.VK_SPACE) || (keyCode == KeyEvent.VK_ENTER)) {
            changeProductItemSelectedState(index);
        }
    }//GEN-LAST:event_productListKeyTyped

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if ((xpw != null) && (!xpw.isDone())) {
            xpw.cancel(true);
            statusBar.endProgress();
        } else {
            Main.mainWindow.removeTab(this);
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        L10n l = listL10nModel.getSelectedTypedItem();
        boolean isL10nValid = (l != null);

        for(LocalePath lp : originalPathsListModel.getAll()) {
            L10n ll = lp.getL10nId();
            isL10nValid = isL10nValid && (!l.equals(ll));
        }

        if (!isL10nValid) {
            statusBar.setErrorText("Please, select a valid locale different from used in original paths");
            targetLocaleCombo.requestFocusInWindow();
            return;
        }
        if (xpw != null) {
            xpw.cancel(true);
        }
        xpw = new ExportProductWorker(exportOutputArea, originalPathsListModel.iterator(),
                        removeObsoleteCheck.isSelected(), (L10n) targetLocaleCombo.getSelectedItem());
        statusBar.startUndefProgress();
        xpw.execute();
    }//GEN-LAST:event_exportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JButton exportButton;
    private javax.swing.JTextArea exportOutputArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> listL10nModel;
    private javax.swing.JLabel originalPathsLabel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<LocalePath> originalPathsListModel;
    private javax.swing.JList<LocalePath> originalpathsList;
    private javax.swing.JList<SelectableItem<Product>> productList;
    private net.localizethat.gui.models.ListComboBoxGenericModel<SelectableItem<Product>> productListModel;
    private javax.swing.JCheckBox removeObsoleteCheck;
    private javax.swing.JCheckBox selectAllProductsCheck;
    private javax.swing.JLabel selectProductsLabel;
    private javax.swing.JComboBox<L10n> targetLocaleCombo;
    private javax.swing.JLabel targetLocaleLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        refreshProductList();
        refreshL10nList(listL10nModel);
        originalPathsListModel.clearAll();
        exportOutputArea.setText("");
    }

    @Override
    public void onTabPanelRemoved() {
        if (!isResultTabOpened) {
            entityManager.close();
        }
    }
}
