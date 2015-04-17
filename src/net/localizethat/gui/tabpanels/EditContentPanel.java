/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.util.Enumeration;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import net.localizethat.Main;
import net.localizethat.gui.models.LocaleNodeTreeModel;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocaleNode;
import net.localizethat.model.Product;
import net.localizethat.util.gui.JStatusBar;

/**
 *
 * @author rpalomares
 */
public class EditContentPanel extends AbstractTabPanel {
    private LocaleNodeTreeModel lntm;
    private final EntityManagerFactory emf;
    private final JStatusBar statusBar;
    private final TreeListeners tl;

    /**
     * Creates new form EditContentPanel
     */
    public EditContentPanel() {
        Product p;

        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();
        initComponents();
        tl = new TreeListeners();

        TypedQuery<Product> productQuery = entityManager.createNamedQuery("Product.findByName",
                Product.class);
        productQuery.setParameter("name", "Thunderbird Central");
        p = productQuery.getSingleResult();

        refreshTree(p);
        dataTree.addTreeSelectionListener(tl);
        dataTree.addTreeExpansionListener(tl);
    }

    private void refreshTree(Product p) {
        entityManager.refresh(p);
        lntm = LocaleNodeTreeModel.createFromProduct(p);
        dataTree.setModel(lntm);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager = emf.createEntityManager();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTree = new javax.swing.JTree();
        rightSidePanel = new javax.swing.JPanel();
        nodeInfoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        nodeContentPanel = new javax.swing.JPanel();
        lObjectDetailPanel = new javax.swing.JPanel();

        jScrollPane1.setViewportView(dataTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jLabel1.setText("Path:");

        jTextField1.setEditable(false);
        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout nodeInfoPanelLayout = new javax.swing.GroupLayout(nodeInfoPanel);
        nodeInfoPanel.setLayout(nodeInfoPanelLayout);
        nodeInfoPanelLayout.setHorizontalGroup(
            nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodeInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jTextField1)
                .addContainerGap())
        );
        nodeInfoPanelLayout.setVerticalGroup(
            nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodeInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout nodeContentPanelLayout = new javax.swing.GroupLayout(nodeContentPanel);
        nodeContentPanel.setLayout(nodeContentPanelLayout);
        nodeContentPanelLayout.setHorizontalGroup(
            nodeContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );
        nodeContentPanelLayout.setVerticalGroup(
            nodeContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(nodeContentPanel);

        javax.swing.GroupLayout lObjectDetailPanelLayout = new javax.swing.GroupLayout(lObjectDetailPanel);
        lObjectDetailPanel.setLayout(lObjectDetailPanelLayout);
        lObjectDetailPanelLayout.setHorizontalGroup(
            lObjectDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );
        lObjectDetailPanelLayout.setVerticalGroup(
            lObjectDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 226, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(lObjectDetailPanel);

        javax.swing.GroupLayout rightSidePanelLayout = new javax.swing.GroupLayout(rightSidePanel);
        rightSidePanel.setLayout(rightSidePanelLayout);
        rightSidePanelLayout.setHorizontalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nodeInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
        rightSidePanelLayout.setVerticalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addComponent(nodeInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2))
        );

        jSplitPane1.setRightComponent(rightSidePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree dataTree;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel lObjectDetailPanel;
    private javax.swing.JPanel nodeContentPanel;
    private javax.swing.JPanel nodeInfoPanel;
    private javax.swing.JPanel rightSidePanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        Product p;
        TypedQuery<Product> productQuery = entityManager.createNamedQuery("Product.findByName",
                Product.class);
        productQuery.setParameter("name", "Thunderbird Central");
        p = productQuery.getSingleResult();
        refreshTree(p);
    }

    @Override
    public void onTabPanelRemoved() {
        // Nothing to do here
    }

    class TreeListeners implements TreeSelectionListener, TreeExpansionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    dataTree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }
            
            LocaleNode nodeObject = (LocaleNode) node.getUserObject();
            if (node.isLeaf()) {
                if (nodeObject instanceof LocaleFile) {
                    // TODO Send signal to update panels
                }
            }
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    event.getPath().getLastPathComponent();
            
            if (node == null) {
                return;
            }
            
            // LocaleNode nodeObject = (LocaleNode) node.getUserObject();
            for(Enumeration e = node.children(); e.hasMoreElements();) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement();
                LocaleNode nodeObject = (LocaleNode) childNode.getUserObject();
                if (nodeObject instanceof LocaleContainer) {
                    loadGrandChildNodes(childNode, (LocaleContainer) nodeObject);
                    // TODO Notify the model?
                }
            }
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            // Nothing to do here
        }
        
        private void loadGrandChildNodes(DefaultMutableTreeNode childNode, LocaleContainer childNodeObject) {
            for(LocaleNode grandChildNodeObject : childNodeObject.getChildren()) {
                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
                childNode.add(grandChildNode);
            }
            for(LocaleFile grandChildNodeObject : childNodeObject.getFileChildren()) {
                DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(grandChildNodeObject);
                childNode.add(grandChildNode);
            }
        }
    }
}