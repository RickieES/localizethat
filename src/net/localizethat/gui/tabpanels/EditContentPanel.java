/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.util.Enumeration;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import net.localizethat.Main;
import net.localizethat.gui.models.ContentListTableModel;
import net.localizethat.gui.models.LocaleNodeTreeModel;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocaleNode;
import net.localizethat.model.LocalePath;
import net.localizethat.model.Product;
import net.localizethat.util.gui.JStatusBar;

/**
 * Panel that shows the tree of the desired product/paths and, on clicking a parseable file,
 * populates a table and allow (editing or) review of (Editable)LocaleContent items
 * @author rpalomares
 */
public class EditContentPanel extends AbstractTabPanel {
    private LocaleNodeTreeModel lntm;
    private final EntityManagerFactory emf;
    private final JStatusBar statusBar;
    private final TreeListeners tl;
    private L10n targetLocale;

    /**
     * Creates new form EditContentPanel
     */
    public EditContentPanel() {
        Product p;

        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        initComponents();
        tl = new TreeListeners();
        dataTree.addTreeSelectionListener(tl);
        dataTree.addTreeExpansionListener(tl);
        this.contentEditionPanel.setAssociatedTable(contentListTable.getTable());
        this.contentListTable.addTableListSelectionListener(contentEditionPanel);
    }

    public void refreshTree(Product p) {
        p = entityManager.merge(p);
        lntm = LocaleNodeTreeModel.createFromProduct(p);
        dataTree.setModel(lntm);
    }

    public void refreshTree(List<LocalePath> pathList) {
        for(LocalePath lp : pathList) {
            lp = entityManager.merge(lp);
        }
        lntm = LocaleNodeTreeModel.createFromLocalePath(pathList.toArray(new LocalePath[pathList.size()]));
        dataTree.setModel(lntm);
    }

    public L10n getTargetLocale() {
        return targetLocale;
    }

    public void setTargetLocale(L10n targetLocale) {
        this.targetLocale = targetLocale;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
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
        pathLabel = new javax.swing.JLabel();
        pathText = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        contentListTable = new net.localizethat.gui.components.ContentListTable();
        contentEditionPanel = new net.localizethat.gui.components.ContentEditionPanel();

        jScrollPane1.setMinimumSize(new java.awt.Dimension(86, 48));

        dataTree.setMinimumSize(new java.awt.Dimension(83, 48));
        jScrollPane1.setViewportView(dataTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        pathLabel.setText("Path:");

        pathText.setEditable(false);
        pathText.setEnabled(false);

        javax.swing.GroupLayout nodeInfoPanelLayout = new javax.swing.GroupLayout(nodeInfoPanel);
        nodeInfoPanel.setLayout(nodeInfoPanelLayout);
        nodeInfoPanelLayout.setHorizontalGroup(
            nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodeInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pathLabel)
                .addGap(4, 4, 4)
                .addComponent(pathText)
                .addContainerGap())
        );
        nodeInfoPanelLayout.setVerticalGroup(
            nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nodeInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nodeInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathLabel)
                    .addComponent(pathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setLeftComponent(contentListTable);
        jSplitPane2.setRightComponent(contentEditionPanel);

        javax.swing.GroupLayout rightSidePanelLayout = new javax.swing.GroupLayout(rightSidePanel);
        rightSidePanel.setLayout(rightSidePanelLayout);
        rightSidePanelLayout.setHorizontalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nodeInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane2)
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
    private net.localizethat.gui.components.ContentEditionPanel contentEditionPanel;
    private net.localizethat.gui.components.ContentListTable contentListTable;
    private javax.swing.JTree dataTree;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel nodeInfoPanel;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JTextField pathText;
    private javax.swing.JPanel rightSidePanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        // Nothing to do here
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
            pathText.setText(nodeObject.getFilePath());
            if (node.isLeaf()) {
                if (nodeObject instanceof LocaleFile) {
                    LocaleFile lf = (LocaleFile) nodeObject;
                    ContentListTableModel tableModel = contentListTable.getTableModel();
                    tableModel.setLocalizationCode(targetLocale);
                    tableModel.replaceData(lf.getChildren());
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
            lntm.loadGrandChildNodes(childNode, childNodeObject);
        }
    }
}
