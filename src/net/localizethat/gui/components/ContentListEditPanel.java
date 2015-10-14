/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.components;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.localizethat.Main;
import net.localizethat.gui.models.ContentListTableModel;
import net.localizethat.gui.tabpanels.AbstractTabPanel;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContent;

/**
 * Panel/component that shows a list of (Editable)LocaleContent items and allows
 * (editing or) reviewing them. The list can come from a ParseableFile, from a
 * Update Product, a QA query, etc.
 * @author rpalomares
 */
public class ContentListEditPanel extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    private final EntityManagerFactory emf;
    private L10n targetLocale;

    /**
     * Creates new form ContentListEditPanel
     */
    public ContentListEditPanel() {
        emf = Main.emf;
        initComponents();
        this.contentEditionPanel.setAssociatedTable(contentListTable.getTable());
        this.contentListTable.addTableListSelectionListener(contentEditionPanel);
    }

    public ContentListEditPanel(EntityManager entityManager, List<LocaleContent> lcList,
            L10n targetLocale) {
        emf = Main.emf;
        this.entityManager = entityManager;
        this.targetLocale = targetLocale;
        initComponents();

        if (!Beans.isDesignTime() && !entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        
        // We want the LocaleContent items to be managed in entityManager, so we
        // need to merge them one by one, creating a new list
        List<LocaleContent> managedLcList = new ArrayList<>(lcList.size());
        for(LocaleContent lc : lcList) {
            managedLcList.add(entityManager.find(lc.getClass(), lc.getId()));
        }
        
        this.contentEditionPanel.setAssociatedTable(contentListTable.getTable());
        this.contentListTable.addTableListSelectionListener(contentEditionPanel);
        ContentListTableModel tableModel = contentListTable.getTableModel();
        tableModel.setLocalizationCode(targetLocale);
        tableModel.replaceData(managedLcList);
    }

    public L10n getTargetLocale() {
        return targetLocale;
    }

    public void setTargetLocale(L10n targetLocale) {
        this.targetLocale = targetLocale;
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
        jSplitPane2 = new javax.swing.JSplitPane();
        contentListTable = new net.localizethat.gui.components.ContentListTable();
        contentEditionPanel = new net.localizethat.gui.components.ContentEditionPanel(entityManager, contentListTable.getTable());

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        contentListTable.setMinimumSize(new java.awt.Dimension(635, 150));
        contentListTable.setPreferredSize(new java.awt.Dimension(806, 357));
        jSplitPane2.setLeftComponent(contentListTable);
        jSplitPane2.setRightComponent(contentEditionPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 728, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.localizethat.gui.components.ContentEditionPanel contentEditionPanel;
    private net.localizethat.gui.components.ContentListTable contentListTable;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JSplitPane jSplitPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        // Nothing to do here
    }

    @Override
    public void onTabPanelRemoved() {
        // Nothing to do here
    }

}
