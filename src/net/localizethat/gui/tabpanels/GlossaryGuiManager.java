/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.localizethat.Main;
import net.localizethat.model.Glossary;
import net.localizethat.model.L10n;
import net.localizethat.util.gui.JStatusBar;

/**
 * Glossary management GUI
 * @author rpalomares
 */
public class GlossaryGuiManager extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    JStatusBar statusBar;
    SimpleDateFormat dateFormat;
    Glossary selectedGlossary;

    /**
     * Creates new form GlossaryGuiManager
     */
    public GlossaryGuiManager() {
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;

        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();
        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }

        glossaryTable.getSelectionModel().addListSelectionListener(new GlossaryTableRowListener());
    }

    private void refreshGlossaryList() {
        TypedQuery<Glossary> glosQuery = entityManager.createNamedQuery("Glossary.findAll",
                Glossary.class);
        glosTableModel.clearAll();
        glosTableModel.addAll(glosQuery.getResultList());
        glosTableModel.fireTableDataChanged();
    }

    private void refreshL10nList() {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        l10nComboModel.clearAll();
        l10nComboModel.addAll(l10nQuery.getResultList());
    }

    private void enableButtonsAndFields(boolean activate) {
        glosNameField.setEnabled(activate);
        glosVersionField.setEnabled(activate);
        glosMasterLocaleCombo.setEnabled(activate);
        saveGlossaryButton.setEnabled(activate);
        refreshButton.setEnabled(activate);
        deleteGlossaryButton.setEnabled(activate);
    }

    private boolean validateOnSave() {
        // Validation 1: the glossary name must not be empty
        if (glosNameField.getText().trim().isEmpty()) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: Glossary name can't be empty",
                    "The glosasry name must not be empty");
            return false;
        }

        // Validation 2: the glossary name can't exist already in the database,
        // except in the same item
        TypedQuery<Glossary> validationQuery = entityManager.createNamedQuery(
                "Glossary.findByGlosname", Glossary.class);
        validationQuery.setParameter("name", glosNameField.getText());
        List<Glossary> listGlossary = validationQuery.getResultList();
        int listLength = listGlossary.size();
        boolean isOk;
        switch (listLength) {
            case 0:
                isOk = true;
                break;
            case 1:
                Glossary glosInDB = listGlossary.get(0);
                isOk = (Objects.equals(glosInDB.getId(), selectedGlossary.getId()));
                break;
            default:
                // This should never be reached, since we don't allow more than one product
                // with the same name, but it is checked just as defensive programming
                isOk = false;
                break;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: Glossary name already exists",
                    "The glossary name of the entity you want to save already exists in the database");
            return false;
        }

        return true;
    }

    private String validateOnDelete() {
        boolean okToDelete = true;
        StringBuilder failedCheckLongMessage = new StringBuilder(25);

        // Check that no GlsEntry is using it
        TypedQuery<Long> validationQuery = entityManager.createNamedQuery(
                "GlsEntry.countByGlossary", Long.class);
        validationQuery.setParameter("glosid", selectedGlossary);
        long recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("The glossary can't be deleted because there are ");
            failedCheckLongMessage.append(recordCount).append(" entries belonging to it\n");
        }

        if (okToDelete) {
            return "";
        } else {
            return failedCheckLongMessage.toString();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager = emf.createEntityManager();
        glosTableModel = new net.localizethat.gui.models.GlossaryTableModel();
        l10nComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        glossaryTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        newGlossaryButton = new javax.swing.JButton();
        saveGlossaryButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        deleteGlossaryButton = new javax.swing.JButton();
        glosNameLabel = new javax.swing.JLabel();
        glosNameField = new javax.swing.JTextField();
        glosVersionLabel = new javax.swing.JLabel();
        glosVersionField = new javax.swing.JTextField();
        glosCreationDateLabel = new javax.swing.JLabel();
        glosCreationDateField = new javax.swing.JTextField();
        glosLastUpdateLabel = new javax.swing.JLabel();
        glosLastUpdateField = new javax.swing.JTextField();
        glosMasterLocaleLabel = new javax.swing.JLabel();
        glosMasterLocaleCombo = new javax.swing.JComboBox<>();

        FormListener formListener = new FormListener();

        glossaryTable.setAutoCreateRowSorter(true);
        glossaryTable.setModel(glosTableModel);
        glossaryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(glossaryTable);

        newGlossaryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-new.png"))); // NOI18N
        newGlossaryButton.setText("New");
        newGlossaryButton.setToolTipText("Create a new empty glossary");
        newGlossaryButton.addActionListener(formListener);

        saveGlossaryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-save.png"))); // NOI18N
        saveGlossaryButton.setText("Save");
        saveGlossaryButton.setToolTipText("Save changes to selected glossary");
        saveGlossaryButton.setEnabled(false);
        saveGlossaryButton.addActionListener(formListener);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/view-refresh.png"))); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(formListener);

        deleteGlossaryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteGlossaryButton.setText("Delete");
        deleteGlossaryButton.setEnabled(false);
        deleteGlossaryButton.addActionListener(formListener);

        glosNameLabel.setDisplayedMnemonic('N');
        glosNameLabel.setLabelFor(glosNameField);
        glosNameLabel.setText("Name:");

        glosNameField.setEnabled(false);

        glosVersionLabel.setDisplayedMnemonic('V');
        glosVersionLabel.setLabelFor(glosVersionField);
        glosVersionLabel.setText("Version:");

        glosVersionField.setEnabled(false);

        glosCreationDateLabel.setText("Creation Date:");

        glosCreationDateField.setEditable(false);
        glosCreationDateField.setEnabled(false);

        glosLastUpdateLabel.setText("Last Updated on:");

        glosLastUpdateField.setEditable(false);
        glosLastUpdateField.setEnabled(false);

        glosMasterLocaleLabel.setDisplayedMnemonic('M');
        glosMasterLocaleLabel.setLabelFor(glosMasterLocaleCombo);
        glosMasterLocaleLabel.setText("Master Locale:");

        glosMasterLocaleCombo.setModel(l10nComboModel);
        glosMasterLocaleCombo.setEnabled(false);

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(newGlossaryButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(saveGlossaryButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteGlossaryButton))
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(glosVersionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(glosNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(glosNameField)
                    .addComponent(glosVersionField)))
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(glosLastUpdateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glosCreationDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glosMasterLocaleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(glosCreationDateField)
                    .addComponent(glosLastUpdateField)
                    .addComponent(glosMasterLocaleCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        buttonPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {glosCreationDateLabel, glosLastUpdateLabel, glosMasterLocaleLabel, glosNameLabel, glosVersionLabel});

        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosVersionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosVersionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosCreationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosLastUpdateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosLastUpdateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosMasterLocaleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosMasterLocaleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteGlossaryButton)
                    .addComponent(refreshButton)
                    .addComponent(saveGlossaryButton)
                    .addComponent(newGlossaryButton))
                .addContainerGap())
        );

        buttonPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {glosCreationDateLabel, glosLastUpdateLabel, glosMasterLocaleLabel, glosNameLabel, glosVersionLabel});

        buttonPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {glosCreationDateField, glosLastUpdateField, glosMasterLocaleCombo, glosNameField, glosVersionField});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == newGlossaryButton) {
                GlossaryGuiManager.this.newGlossaryButtonActionPerformed(evt);
            }
            else if (evt.getSource() == saveGlossaryButton) {
                GlossaryGuiManager.this.saveGlossaryButtonActionPerformed(evt);
            }
            else if (evt.getSource() == refreshButton) {
                GlossaryGuiManager.this.refreshButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteGlossaryButton) {
                GlossaryGuiManager.this.deleteGlossaryButtonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void deleteGlossaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteGlossaryButtonActionPerformed
        String failedCheckMessage = validateOnDelete();
        boolean validateOnDeleteCheck = (failedCheckMessage.length() == 0);

        if (!validateOnDeleteCheck) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Sorry, can't delete this glossary because it is being used", failedCheckMessage);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected glossary?", "Confirm deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            // int index = glossaryTable.convertRowIndexToModel(glossaryTable.getSelectedRow());
            // Glossary g = glosTableModel.getElement(index);
            try {
                entityManager.remove(selectedGlossary);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                refreshGlossaryList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Glossary deleted");
                enableButtonsAndFields(false);
                selectedGlossary = null;
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(GlossaryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting glossary", ex);
            }
        }
    }//GEN-LAST:event_deleteGlossaryButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refreshL10nList();
        refreshGlossaryList();
        glosTableModel.fireTableDataChanged();
        statusBar.setInfoText("Data refreshed");
        Date now = new Date();
        refreshButton.setToolTipText("Last refreshed at " + dateFormat.format(now));
        enableButtonsAndFields(false);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void saveGlossaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGlossaryButtonActionPerformed
        // validateOnSave will report the specific problem in the status bar
        if (!validateOnSave()) {
            return;
        }

        int index = glossaryTable.convertRowIndexToModel(glossaryTable.getSelectedRow());
        if (!entityManager.isJoinedToTransaction()) {
            entityManager.getTransaction().begin();
        }
        Glossary g = entityManager.find(Glossary.class, selectedGlossary.getId());
        g.setName(glosNameField.getText());
        g.setVersion(glosVersionField.getText());
        g.setLastUpdate(new Date());
        g.setL10nId((L10n) glosMasterLocaleCombo.getSelectedItem());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            glosTableModel.fireTableRowsUpdated(index, index);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Changes saved");
        } catch (Exception ex) {
            Logger.getLogger(GlossaryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving",
                "Error while saving changes", ex);
        }
    }//GEN-LAST:event_saveGlossaryButtonActionPerformed

    private void newGlossaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGlossaryButtonActionPerformed
        Glossary g = new Glossary();
        g.setName("");
        g.setVersion("");
        g.setCreationDate(new Date());
        g.setLastUpdate(new Date());
        g.setL10nId(l10nComboModel.getElementAt(0));
        try {
            // gpc.create(g);
            entityManager.persist(g);
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            statusBar.setText(JStatusBar.LogMsgType.INFO,
                "New item added, use detail fields to complete it");
            glosTableModel.addElement(g);
            int index = glosTableModel.getIndexOf(g);
            glossaryTable.setRowSelectionInterval(index, index);
            glossaryTable.scrollRectToVisible(glossaryTable.getCellRect(index, 0, true));
            enableButtonsAndFields(true);
            glosNameField.requestFocus();
        } catch (Exception ex) {
            Logger.getLogger(GlossaryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating",
                "Error while creating element", ex);
        }
    }//GEN-LAST:event_newGlossaryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteGlossaryButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JTextField glosCreationDateField;
    private javax.swing.JLabel glosCreationDateLabel;
    private javax.swing.JTextField glosLastUpdateField;
    private javax.swing.JLabel glosLastUpdateLabel;
    private javax.swing.JComboBox<L10n> glosMasterLocaleCombo;
    private javax.swing.JLabel glosMasterLocaleLabel;
    private javax.swing.JTextField glosNameField;
    private javax.swing.JLabel glosNameLabel;
    private net.localizethat.gui.models.GlossaryTableModel glosTableModel;
    private javax.swing.JTextField glosVersionField;
    private javax.swing.JLabel glosVersionLabel;
    private javax.swing.JTable glossaryTable;
    private javax.swing.JScrollPane jScrollPane1;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> l10nComboModel;
    private javax.swing.JButton newGlossaryButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveGlossaryButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
        }

        refreshL10nList();
        refreshGlossaryList();
        selectedGlossary = null;
        glosNameField.setText("");
        glosVersionField.setText("");
        glosCreationDateField.setText("");
        glosLastUpdateField.setText("");
        glosMasterLocaleCombo.setSelectedItem(null);
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

    private class GlossaryTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = glossaryTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedRow = glossaryTable.convertRowIndexToModel(selectedRow);
                selectedGlossary = glosTableModel.getElement(selectedRow);
                glosNameField.setText(selectedGlossary.getName());
                glosVersionField.setText(selectedGlossary.getVersion());
                glosCreationDateField.setText(selectedGlossary.getCreationDate().toString());
                glosLastUpdateField.setText(selectedGlossary.getLastUpdate().toString());
                glosMasterLocaleCombo.setSelectedItem(selectedGlossary.getL10nId());
                enableButtonsAndFields(true);
            }
        }
    }
}
