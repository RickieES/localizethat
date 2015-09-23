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
import net.localizethat.model.L10n;
import net.localizethat.util.gui.JStatusBar;

/**
 * L10n GUI Manager form as a JPanel that can be embedded in a TabPane or a JDialog
 * @author rpalomares
 */
public class L10nGUIManager extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    JStatusBar statusBar;
    SimpleDateFormat dateFormat;
    L10n selectedL10n;

    /**
     * Creates new form L10nGUIManager
     */
    public L10nGUIManager() {
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();

        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
        l10nTable.getSelectionModel().addListSelectionListener(new L10nTableRowListener());
    }

    private void refreshL10nList() {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        l10nTableModel.clearAll();
        l10nTableModel.addAll(l10nQuery.getResultList());
    }

    private boolean validateOnSave() {
        // Validation 1: the L10n code can't be shorter than 2
        if ((l10nCodeField.getText().trim().length() < 2)
                || (l10nCodeField.getText().trim().length() > 6)) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: L10n code is too short or too long",
                    "The L10n code must have at least 2 (example: fr) characters and no more than 6 (example: en-JEY");
            return false;
        }

        // Validation 2: the L10n code (case insensitive) can't exist already in the database,
        // except in the same item
        TypedQuery<L10n> validationQuery = entityManager.createNamedQuery(
                "L10n.findByL10ncode", L10n.class);
        validationQuery.setParameter("code", l10nCodeField.getText());
        List<L10n> listL10n = validationQuery.getResultList();
        int listLength = listL10n.size();
        boolean isOk;
        if (listLength == 0) {
            isOk = true;
        } else if (listLength == 1) {
            L10n l10nInDB = listL10n.get(0);
            isOk = (Objects.equals(l10nInDB.getId(), selectedL10n.getId()));
        } else {
            // This should never be reached, since we don't allow more than one product
            // with the same name, but it is checked just as defensive programming
            isOk = false;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: L10n code already exists",
                    "The L10n code of the entity you want to save already exists in the database");
            return false;
        }

        // Validation 3: the description must not be empty
        if (l10nDescriptionField.getText().trim().length() < 2) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: L10n description can't be empty",
                    "The L10n description must not be empty");
            return false;
        }

        return true;
    }

    private String validateOnDelete(L10n l) {
        boolean okToDelete = true;
        StringBuilder failedCheckLongMessage = new StringBuilder(25);

        // Prepare descriptive message in case checks fail
        failedCheckLongMessage.append("The L10n entry can't be deleted because the ");
        failedCheckLongMessage.append("following items are using it:\n\n");

        // Note that we check all related tables even if we find the L10n item to be removed
        // is used in a previous one, so we can tell the user all the affected entities

        // Check that no Product is using it
        TypedQuery<Long> validationQuery = entityManager.createNamedQuery(
                "Product.countByL10n", Long.class);
        validationQuery.setParameter("l10nid", l);
        long recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("* ").append(recordCount).append(" product(s)\n");
        }

        // Check that no LocalePath is using it
        validationQuery = entityManager.createNamedQuery("LocalePath.countByL10n", Long.class);
        validationQuery.setParameter("l10nid", l);
        recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("* ").append(recordCount).append(" path(s)\n");
        }

        // Check that no LocaleContainer is using it
        validationQuery = entityManager.createNamedQuery("LocaleContainer.countByL10n", Long.class);
        validationQuery.setParameter("l10nid", l);
        recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("* ").append(recordCount).append(" locale container(s) / folder(s) \n");
        }

        // Check that no LocaleFile is using it
        validationQuery = entityManager.createNamedQuery("LocaleFile.countByL10n", Long.class);
        validationQuery.setParameter("l10nid", l);
        recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("* ").append(recordCount).append(" locale file(s)\n");
        }

        // LocaleContent - LTContent
        validationQuery = entityManager.createNamedQuery("LocaleContent.countByL10n", Long.class);
        validationQuery.setParameter("l10nid", l);
        recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("* ").append(recordCount).append(" locale content(s)\n");
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
        l10nTableModel = new net.localizethat.gui.models.L10nTableModel();
        jScrollPane1 = new javax.swing.JScrollPane();
        l10nTable = new javax.swing.JTable();
        lblL10nCode = new javax.swing.JLabel();
        lblL10nDescription = new javax.swing.JLabel();
        lblL10nTeamName = new javax.swing.JLabel();
        lblL10nURL = new javax.swing.JLabel();
        lblL10nCreated = new javax.swing.JLabel();
        lblL10nLastUpdated = new javax.swing.JLabel();
        l10nCodeField = new javax.swing.JTextField();
        l10nDescriptionField = new javax.swing.JTextField();
        l10nTeamNameField = new javax.swing.JTextField();
        l10nUrlField = new javax.swing.JTextField();
        l10nCreationDateField = new javax.swing.JTextField();
        l10nLastUpdatedField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        l10nTable.setModel(l10nTableModel);
        jScrollPane1.setViewportView(l10nTable);

        lblL10nCode.setDisplayedMnemonic('C');
        lblL10nCode.setLabelFor(l10nCodeField);
        lblL10nCode.setText("Code");

        lblL10nDescription.setDisplayedMnemonic('D');
        lblL10nDescription.setLabelFor(l10nDescriptionField);
        lblL10nDescription.setText("Description");

        lblL10nTeamName.setDisplayedMnemonic('T');
        lblL10nTeamName.setLabelFor(lblL10nTeamName);
        lblL10nTeamName.setText("Team Name");

        lblL10nURL.setDisplayedMnemonic('U');
        lblL10nURL.setLabelFor(l10nUrlField);
        lblL10nURL.setText("URL");

        lblL10nCreated.setText("Creation Date");

        lblL10nLastUpdated.setText("Last updated");

        l10nCreationDateField.setEditable(false);
        l10nCreationDateField.setEnabled(false);

        l10nLastUpdatedField.setEditable(false);
        l10nLastUpdatedField.setEnabled(false);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/view-refresh.png"))); // NOI18N
        refreshButton.setMnemonic('R');
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-new.png"))); // NOI18N
        newButton.setMnemonic('N');
        newButton.setText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-save.png"))); // NOI18N
        saveButton.setMnemonic('S');
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteButton.setMnemonic('l');
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(newButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshButton)
                    .addComponent(deleteButton)
                    .addComponent(saveButton)
                    .addComponent(newButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblL10nURL)
                            .addComponent(lblL10nTeamName, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(l10nUrlField, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(l10nTeamNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblL10nLastUpdated)
                            .addComponent(lblL10nCode)
                            .addComponent(lblL10nCreated)
                            .addComponent(lblL10nDescription))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(l10nCodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(l10nDescriptionField, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(l10nCreationDateField)
                            .addComponent(l10nLastUpdatedField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nCode)
                    .addComponent(l10nCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nDescription)
                    .addComponent(l10nDescriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nTeamName)
                    .addComponent(l10nTeamNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nURL)
                    .addComponent(l10nUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nCreated)
                    .addComponent(l10nCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblL10nLastUpdated)
                    .addComponent(l10nLastUpdatedField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refreshL10nList();
        l10nTableModel.fireTableDataChanged();
        statusBar.setInfoText("Data refreshed");
        Date now = new Date();
        refreshButton.setToolTipText("Last refreshed at " + dateFormat.format(now));
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        L10n l = new L10n();
        l.setCode("");
        l.setName("");
        l.setTeamName("");
        l.setUrl("");
        l.setCreationDate(new Date());
        l.setLastUpdate(new Date());
        try {
            entityManager.persist(l);
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            statusBar.setText(JStatusBar.LogMsgType.INFO,
                "New item added, use detail fields to complete it");
            l10nTableModel.addElement(l);
            int index = l10nTableModel.getIndexOf(l);
            l10nTable.setRowSelectionInterval(index, index);
            l10nTable.scrollRectToVisible(l10nTable.getCellRect(index, 0, true));
            l10nCodeField.requestFocus();
        } catch (Exception ex) {
            Logger.getLogger(L10nGUIManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating",
                "Error while creating element", ex);
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        // validateOnSave will report the specific problem in the status bar
        if (!validateOnSave()) {
            return;
        }

        int index = l10nTable.convertRowIndexToModel(l10nTable.getSelectedRow());
        L10n l = l10nTableModel.getElement(index);
        l.setCode(l10nCodeField.getText());
        l.setName(l10nDescriptionField.getText());
        l.setTeamName(l10nTeamNameField.getText());
        l.setUrl(l10nUrlField.getText());
        l.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            l10nTableModel.fireTableRowsUpdated(index, index);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Changes saved");
        } catch (Exception ex) {
            Logger.getLogger(L10nGUIManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving",
                "Error while saving changes", ex);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int index = l10nTable.convertRowIndexToModel(l10nTable.getSelectedRow());
        if (index == -1) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Select a locale item to delete it",
                    "Select a locale in the above table to delete it");
            return;
        }

        L10n l = l10nTableModel.getElement(index);
        String failedCheckMessage = validateOnDelete(l);
        boolean validateOnDeleteCheck = (failedCheckMessage.length() == 0);

        if (!validateOnDeleteCheck) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Sorry, can't delete this locale because it is being used", failedCheckMessage);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected locale?", "Confirm deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            try {
                entityManager.remove(l);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                l10nTableModel.fireTableRowsDeleted(index, index);
                refreshL10nList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Locale deleted");
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(L10nGUIManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting locale", ex);
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField l10nCodeField;
    private javax.swing.JTextField l10nCreationDateField;
    private javax.swing.JTextField l10nDescriptionField;
    private javax.swing.JTextField l10nLastUpdatedField;
    private javax.swing.JTable l10nTable;
    private net.localizethat.gui.models.L10nTableModel l10nTableModel;
    private javax.swing.JTextField l10nTeamNameField;
    private javax.swing.JTextField l10nUrlField;
    private javax.swing.JLabel lblL10nCode;
    private javax.swing.JLabel lblL10nCreated;
    private javax.swing.JLabel lblL10nDescription;
    private javax.swing.JLabel lblL10nLastUpdated;
    private javax.swing.JLabel lblL10nTeamName;
    private javax.swing.JLabel lblL10nURL;
    private javax.swing.JButton newButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
        }

        refreshL10nList();
        selectedL10n = null;
        l10nCodeField.setText("");
        l10nDescriptionField.setText("");
        l10nTeamNameField.setText("");
        l10nUrlField.setText("");
        l10nCreationDateField.setText("");
        l10nLastUpdatedField.setText("");
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

    private class L10nTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = l10nTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedRow = l10nTable.convertRowIndexToModel(selectedRow);
                selectedL10n = l10nTableModel.getElement(selectedRow);
                l10nCodeField.setText(selectedL10n.getCode());
                l10nDescriptionField.setText(selectedL10n.getName());
                l10nTeamNameField.setText(selectedL10n.getTeamName());
                l10nUrlField.setText(selectedL10n.getUrl());
                l10nCreationDateField.setText(selectedL10n.getCreationDate().toString());
                l10nLastUpdatedField.setText(selectedL10n.getLastUpdate().toString());
            }
        }
    }
}
