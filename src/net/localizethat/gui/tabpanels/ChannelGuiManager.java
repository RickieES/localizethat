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
import net.localizethat.model.Channel;
import net.localizethat.util.gui.JStatusBar;

/**
 * Channel GUI Manager form as a JPanel that can be embedded in a TabPane or a JDialog
 * @author rpalomares
 */
public class ChannelGuiManager extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    JStatusBar statusBar;
    SimpleDateFormat dateFormat;
    Channel selectedChannel;

    /**
     * Creates new form ChannelsManager
     */
    public ChannelGuiManager() {
        super();
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();

        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
        channelTable.getSelectionModel().addListSelectionListener(new ChannelGuiManager.ChannelTableRowListener());
    }

    private void refreshChannelList() {
        TypedQuery<Channel> channelQuery = entityManager.createNamedQuery("Channel.findAll",
                Channel.class);
        channelTableModel.clearAll();
        channelTableModel.addAll(channelQuery.getResultList());
    }

    private void enableButtonsAndFields(boolean activate) {
        channelNameField.setEnabled(activate);
        channelDescriptionField.setEnabled(activate);
        channelTagField.setEnabled(activate);
        channelReplacementTextFieldField.setEnabled(activate);
        saveButton.setEnabled(activate);
        refreshButton.setEnabled(activate);
        deleteButton.setEnabled(activate);
    }

    private boolean validateOnSave() {
        // Validation 1: the channel name can't be shorter than 1
        if ((channelNameField.getText().trim().length() < 1)) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: Channel name can't be empty",
                    "The channel name must have at least 1 character");
            return false;
        }

        // Validation 2: the channel name (case insensitive) can't exist already in the database,
        // except in the same item
        TypedQuery<Channel> validationQuery = entityManager.createNamedQuery(
                "Channel.findByName", Channel.class);
        validationQuery.setParameter("name", this.channelNameField.getText());
        List<Channel> chnlList = validationQuery.getResultList();
        int listLength = chnlList.size();
        boolean isOk;
        switch (listLength) {
            case 0:
                isOk = true;
                break;
            case 1:
                Channel channelInDB = chnlList.get(0);
                isOk = (Objects.equals(channelInDB.getId(), selectedChannel.getId()));
                break;
            default:
                // This should never be reached, since we don't allow more than one product
                // with the same name, but it is checked just as defensive programming
                isOk = false;
                break;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: channel name already exists",
                    "The channel name of the entity you want to save already exists in the database");
            return false;
        }

        // Validation 3: the tag can't be empty if the replacement text is not empty, and
        // the reverse must also be true
        if (((channelReplacementTextFieldField.getText().trim().length() > 0)
                    && (channelTagField.getText().trim().length() == 0))
                || ((channelTagField.getText().trim().length() > 0)
                    && (channelReplacementTextFieldField.getText().trim().length() == 0))) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: both tag and replacement text must have value or be empty",
                    "If the tag is empty, the replacement text must also be empty.\n\n"
                    + "Also, if the tag is not empty, the replacement text can't be empty.\n\n"
                    + "Put it in another way: don't provide a tag to be replaced in paths without "
                    + "providing the text that will substitute it, and don't provide a replacement "
                    + "text without providing a tag to mark where the text must be inserted."
            );
            return false;
        }

        return true;
    }

    private String validateOnDelete() {
        boolean okToDelete = true;
        StringBuilder failedCheckLongMessage = new StringBuilder(25);

        // Check that no GlsEntry is using it
        TypedQuery<Long> validationQuery = entityManager.createNamedQuery(
                "Product.countByChannel", Long.class);
        validationQuery.setParameter("channelid", selectedChannel);
        long recordCount = validationQuery.getSingleResult();
        if (recordCount > 0) {
            okToDelete = false;
            failedCheckLongMessage.append("The channel can't be deleted because there are ");
            failedCheckLongMessage.append(recordCount).append(" products associated to it\n");
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
        channelTableModel = new net.localizethat.gui.models.ChannelTableModel();
        jScrollPane1 = new javax.swing.JScrollPane();
        channelTable = new javax.swing.JTable();
        detailPanel = new javax.swing.JPanel();
        channelNameField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        channelDescriptionField = new javax.swing.JTextArea();
        channelTagField = new javax.swing.JTextField();
        channelReplacementTextFieldField = new javax.swing.JTextField();
        channelCreationDateField = new javax.swing.JTextField();
        channelLastUpdatedField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        tagLabel = new javax.swing.JLabel();
        replacementTextLabel = new javax.swing.JLabel();
        creationDateLabel = new javax.swing.JLabel();
        lastUpdatedLabel = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        channelTable.setModel(channelTableModel);
        jScrollPane1.setViewportView(channelTable);

        channelNameField.setToolTipText("A short, identificative name");
        channelNameField.setEnabled(false);

        channelDescriptionField.setColumns(20);
        channelDescriptionField.setRows(5);
        channelDescriptionField.setToolTipText("You can add notes for yourself or others that may come in the future about this channel");
        channelDescriptionField.setEnabled(false);
        jScrollPane2.setViewportView(channelDescriptionField);

        channelTagField.setToolTipText("<html>You will use this tag inside paths of products, and it will be replaced by the replacement text.<br>\nThis allows you to switch a product from channel without having to edit every path.<br>\nHint: surround the tag with brackets that can't be part of the path (e.g.: [aurora])\n</html>");
        channelTagField.setEnabled(false);

        channelReplacementTextFieldField.setToolTipText("Text that will be put in the place of tags in every path of a product before updating, importing or exporting it.");
        channelReplacementTextFieldField.setEnabled(false);

        channelCreationDateField.setEditable(false);
        channelCreationDateField.setEnabled(false);

        channelLastUpdatedField.setEditable(false);
        channelLastUpdatedField.setEnabled(false);

        nameLabel.setDisplayedMnemonic('N');
        nameLabel.setLabelFor(channelNameField);
        nameLabel.setText("Name");

        descriptionLabel.setDisplayedMnemonic('e');
        descriptionLabel.setLabelFor(channelDescriptionField);
        descriptionLabel.setText("Description");

        tagLabel.setDisplayedMnemonic('T');
        tagLabel.setLabelFor(channelTagField);
        tagLabel.setText("Tag");

        replacementTextLabel.setDisplayedMnemonic('x');
        replacementTextLabel.setLabelFor(channelReplacementTextFieldField);
        replacementTextLabel.setText("Replacement text");

        creationDateLabel.setText("Creation date");

        lastUpdatedLabel.setText("Last updated");

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
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteButton.setMnemonic('D');
        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 380, Short.MAX_VALUE)
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(replacementTextLabel)
                            .addComponent(tagLabel)
                            .addComponent(nameLabel)
                            .addComponent(creationDateLabel)
                            .addComponent(lastUpdatedLabel)
                            .addComponent(descriptionLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(channelLastUpdatedField)
                            .addComponent(channelCreationDateField)
                            .addComponent(channelNameField)
                            .addComponent(jScrollPane2)
                            .addComponent(channelTagField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(channelReplacementTextFieldField, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(descriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelTagField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tagLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelReplacementTextFieldField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(replacementTextLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(creationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelLastUpdatedField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastUpdatedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshButton)
                    .addComponent(deleteButton)
                    .addComponent(saveButton)
                    .addComponent(newButton))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refreshChannelList();
        channelTableModel.fireTableDataChanged();
        statusBar.setInfoText("Data refreshed");
        Date now = new Date();
        refreshButton.setToolTipText("Last refreshed at " + dateFormat.format(now));
        enableButtonsAndFields(false);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        Channel c = new Channel();
        c.setName("");
        c.setDescription("");
        c.setReplacementTag("");
        c.setReplacementTag("");
        c.setCreationDate(new Date());
        c.setLastUpdate(new Date());
        try {
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            statusBar.setText(JStatusBar.LogMsgType.INFO,
                "New item added, use detail fields to complete it");
            channelTableModel.addElement(c);
            int index = channelTableModel.getIndexOf(c);
            channelTable.setRowSelectionInterval(index, index);
            channelTable.scrollRectToVisible(channelTable.getCellRect(index, 0, true));
            channelNameField.requestFocus();
        } catch (Exception ex) {
            Logger.getLogger(ChannelGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating",
                "Error while creating element", ex);
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // validateOnSave will report the specific problem in the status bar
        if (!validateOnSave()) {
            return;
        }

        int index = channelTable.convertRowIndexToModel(channelTable.getSelectedRow());
        if (!entityManager.isJoinedToTransaction()) {
            entityManager.getTransaction().begin();
        }
        Channel c = entityManager.find(Channel.class, selectedChannel.getId());
        c.setName(channelNameField.getText());
        c.setDescription(channelDescriptionField.getText());
        c.setReplacementTag(channelTagField.getText());
        c.setReplacementText(channelReplacementTextFieldField.getText());
        c.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            channelTableModel.fireTableRowsUpdated(index, index);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Changes saved");
        } catch (Exception ex) {
            Logger.getLogger(ChannelGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving",
                "Error while saving changes", ex);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected channel?", "Confirm deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            int index = channelTable.convertRowIndexToModel(channelTable.getSelectedRow());

            Channel c = channelTableModel.getElement(index);
            try {
                entityManager.remove(c);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                channelTableModel.fireTableRowsDeleted(index, index);
                refreshChannelList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Channel deleted");
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ChannelGuiManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting channel", ex);
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField channelCreationDateField;
    private javax.swing.JTextArea channelDescriptionField;
    private javax.swing.JTextField channelLastUpdatedField;
    private javax.swing.JTextField channelNameField;
    private javax.swing.JTextField channelReplacementTextFieldField;
    private javax.swing.JTable channelTable;
    private net.localizethat.gui.models.ChannelTableModel channelTableModel;
    private javax.swing.JTextField channelTagField;
    private javax.swing.JLabel creationDateLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel detailPanel;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lastUpdatedLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton newButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel replacementTextLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel tagLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
        }
        refreshChannelList();
        selectedChannel = null;
        channelNameField.setText("");
        channelDescriptionField.setText("");
        channelTagField.setText("");
        channelReplacementTextFieldField.setText("");
        channelCreationDateField.setText("");
        channelLastUpdatedField.setText("");
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

    private class ChannelTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = channelTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedRow = channelTable.convertRowIndexToModel(selectedRow);
                selectedChannel = channelTableModel.getElement(selectedRow);
                channelNameField.setText(selectedChannel.getName());
                channelDescriptionField.setText(selectedChannel.getDescription());
                channelTagField.setText(selectedChannel.getReplacementTag());
                channelReplacementTextFieldField.setText(selectedChannel.getReplacementText());
                channelCreationDateField.setText(selectedChannel.getCreationDate().toString());
                channelLastUpdatedField.setText(selectedChannel.getLastUpdate().toString());
                enableButtonsAndFields(true);
            }
        }
    }
}
