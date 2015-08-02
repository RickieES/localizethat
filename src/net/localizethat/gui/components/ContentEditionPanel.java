/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.components;

import java.beans.Beans;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.localizethat.Main;
import net.localizethat.gui.models.ContentListTableModel;
import net.localizethat.model.EditableLocaleContent;
import net.localizethat.model.LTKeyValuePair;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.TranslationStatus;
import net.localizethat.model.jpa.JPAHelperBundle;
import net.localizethat.model.jpa.LocaleContentJPAHelper;

/**
 * Content edition panel
 *
 * This panel allows to edit LTContent siblings of a target locale. The target
 * locale is defined in the ContentListTableModel instance
 * @author rpalomares
 */
public class ContentEditionPanel extends javax.swing.JPanel implements ListSelectionListener {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf;
    private EntityManager entityManager;
    private JTable associatedTable;
    private ContentListTableModel tableModel;
    private ContentListTableModel.ContentListObject selectedLObject;
    private JPAHelperBundle jhb;
    private final EllipsisUnicodeCharKeyAdapter ellipsisCharKeyAdapter;

    /**
     * Creates new form ContentEditionPanel
     */
    public ContentEditionPanel() {
        if (Beans.isDesignTime()) {
        } else {
            emf = Main.emf;
            entityManager = emf.createEntityManager();
            jhb = JPAHelperBundle.getInstance(entityManager);
        }
        initComponents();
        for(TranslationStatus ts : TranslationStatus.values()) {
            trnsStatusCombo.addItem(ts);
        }
        ellipsisCharKeyAdapter = new EllipsisUnicodeCharKeyAdapter();
        trnsTextArea.addKeyListener(ellipsisCharKeyAdapter);
    }

    public ContentEditionPanel(EntityManager entityManager, JTable associatedTable) {
        if (!Beans.isDesignTime()) {
            emf = Main.emf;
            this.entityManager = entityManager;
            jhb = JPAHelperBundle.getInstance(entityManager);
        }
        initComponents();
        for(TranslationStatus ts : TranslationStatus.values()) {
            trnsStatusCombo.addItem(ts);
        }
        ellipsisCharKeyAdapter = new EllipsisUnicodeCharKeyAdapter();
        trnsTextArea.addKeyListener(ellipsisCharKeyAdapter);

        if (!Beans.isDesignTime() && !entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        setAssociatedTable(associatedTable);
    }

    public final void setAssociatedTable(JTable associatedTable) {
        if (associatedTable != null) {
            this.associatedTable = associatedTable;
            this.tableModel = (ContentListTableModel) this.associatedTable.getModel();
        }
    }

    /**
     * This method checks if there is anything to be updated in the target locale, to
     * avoid wasting resources modifying the DB when there is no reason to do it
     * @return true if something has to be updated/persisted in the DB
     */
    private boolean hasTargetLocaleChanged(LocaleContent lc) {
        boolean result;
        EditableLocaleContent elc = null;
        LTKeyValuePair lkvp;
        LTKeyValuePair connAccessKey;
        LTKeyValuePair connCommandKey;


        if (lc instanceof EditableLocaleContent) {
            elc = (EditableLocaleContent) lc;
        }

        if (lc != null) {
            result = (trnsTextArea.getText() != null)
                    && (!trnsTextArea.getText().isEmpty())
                    && (trnsTextArea.getText().compareTo(lc.getTextValue()) != 0);
            result |= (lc.isKeepOriginal() != keepOriginalCheck.isSelected());

            if (elc != null) {
                if (trnsStatusCombo.getSelectedItem() != null) {
                    result |= (!trnsStatusCombo.getSelectedItem().equals(elc.getTrnsStatus()));
                }

                if (elc instanceof LTKeyValuePair) {
                    lkvp = (LTKeyValuePair) elc;
                    connAccessKey = lkvp.getConnAccesskey();
                    connCommandKey = lkvp.getConnCommandkey();
                    result |= ((connAccessKey != null)
                            && (connAccessKey.getTextValue() != null)
                            && (connAccessKey.getTextValue().compareTo(accessKeyField.getText()) != 0));
                    result |= ((connCommandKey != null)
                            && (connCommandKey.getTextValue() != null)
                            && (connCommandKey.getTextValue().compareTo(commandKeyField.getText()) != 0));
                }
            }
        } else {
            result = (trnsTextArea.getText() != null)
                    && (!trnsTextArea.getText().isEmpty());
            result |= (keepOriginalCheck.isSelected());
        }
        return result;
    }

    private void updateTargetLocale() {
        LocaleContent lc = selectedLObject.getSiblingNode();
        LocaleContentJPAHelper lcntjh = jhb.getLocaleContentJPAHelper();

        if (!hasTargetLocaleChanged(lc)) {
            return;
        }

        if (lc == null) {
            if (!lcntjh.createRecursively(selectedLObject.getOriginalNode(),
                    tableModel.getLocalizationCode(),
                    true)) {
                Main.mainWindow.getStatusBar().setErrorText(
                        "Error creating the localized value in DB");
            }
        }

        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }

        EditableLocaleContent elc = (EditableLocaleContent) selectedLObject.getOriginalNode()
                .getTwinByLocale(tableModel.getLocalizationCode());
        elc = entityManager.merge(elc);
        // Update in-memory table model with the merged object from EntityManager
        selectedLObject.setSiblingNode(elc);

        elc.setTextValue(trnsTextArea.getText());
        elc.setKeepOriginal(keepOriginalCheck.isSelected());
        elc.setTrnsStatus((TranslationStatus) trnsStatusCombo.getSelectedItem());
        elc.setLastUpdate(new Date());
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        upperPanel = new javax.swing.JPanel();
        origLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        origTextPane = new javax.swing.JTextPane();
        commentTButton = new javax.swing.JToggleButton();
        lowerPanel = new javax.swing.JPanel();
        trnsLabel = new javax.swing.JLabel();
        sugButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        trnsTextArea = new javax.swing.JTextArea();
        copyOrigButton = new javax.swing.JButton();
        keepOriginalCheck = new javax.swing.JCheckBox();
        trnsStatusCombo = new javax.swing.JComboBox<TranslationStatus>();
        accessKeyLabel = new javax.swing.JLabel();
        accessKeyField = new javax.swing.JTextField();
        commandKeyLabel = new javax.swing.JLabel();
        commandKeyField = new javax.swing.JTextField();
        advancedPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        prevButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        origLabel.setText("Original____");

        origTextPane.setEditable(false);
        origTextPane.setText("1\n2\n3\n4\n5");
        jScrollPane1.setViewportView(origTextPane);

        commentTButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-comment.png"))); // NOI18N
        commentTButton.setToolTipText("Toogle between original value <-> comment");
        commentTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentTButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout upperPanelLayout = new javax.swing.GroupLayout(upperPanel);
        upperPanel.setLayout(upperPanelLayout);
        upperPanelLayout.setHorizontalGroup(
            upperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upperPanelLayout.createSequentialGroup()
                .addGroup(upperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(upperPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(commentTButton))
                    .addGroup(upperPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(origLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addContainerGap())
        );
        upperPanelLayout.setVerticalGroup(
            upperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upperPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(upperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(upperPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(upperPanelLayout.createSequentialGroup()
                        .addComponent(origLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commentTButton)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jSplitPane1.setTopComponent(upperPanel);

        trnsLabel.setDisplayedMnemonic('T');
        trnsLabel.setLabelFor(trnsTextArea);
        trnsLabel.setText("Translation");

        sugButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-dialog-information.png"))); // NOI18N
        sugButton.setToolTipText("Cycle over suggestions");

        trnsTextArea.setColumns(20);
        trnsTextArea.setLineWrap(true);
        trnsTextArea.setRows(3);
        trnsTextArea.setText("1\n2\n3\n4\n5");
        trnsTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(trnsTextArea);

        copyOrigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-edit-copy.png"))); // NOI18N
        copyOrigButton.setToolTipText("Copy from original value");
        copyOrigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyOrigButtonActionPerformed(evt);
            }
        });

        keepOriginalCheck.setMnemonic('K');
        keepOriginalCheck.setText("Keep original value");
        keepOriginalCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keepOriginalCheckActionPerformed(evt);
            }
        });

        trnsStatusCombo.setToolTipText("Translation Status");
        trnsStatusCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trnsStatusComboActionPerformed(evt);
            }
        });

        accessKeyLabel.setText("AK:");
        accessKeyLabel.setToolTipText("Accesskey");

        commandKeyLabel.setText("CK:");
        commandKeyLabel.setToolTipText("Commandkey");

        commandKeyField.setToolTipText("");

        javax.swing.GroupLayout lowerPanelLayout = new javax.swing.GroupLayout(lowerPanel);
        lowerPanel.setLayout(lowerPanelLayout);
        lowerPanelLayout.setHorizontalGroup(
            lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lowerPanelLayout.createSequentialGroup()
                        .addGap(0, 12, Short.MAX_VALUE)
                        .addComponent(keepOriginalCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(trnsStatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(accessKeyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(accessKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandKeyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lowerPanelLayout.createSequentialGroup()
                        .addGroup(lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lowerPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(copyOrigButton)
                                    .addComponent(sugButton)))
                            .addComponent(trnsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        lowerPanelLayout.setVerticalGroup(
            lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lowerPanelLayout.createSequentialGroup()
                        .addComponent(trnsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(copyOrigButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sugButton)
                        .addGap(0, 20, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keepOriginalCheck)
                    .addComponent(trnsStatusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accessKeyLabel)
                    .addComponent(accessKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commandKeyLabel)
                    .addComponent(commandKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(lowerPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        jTabbedPane1.addTab("Main", mainPanel);

        javax.swing.GroupLayout advancedPanelLayout = new javax.swing.GroupLayout(advancedPanel);
        advancedPanel.setLayout(advancedPanelLayout);
        advancedPanelLayout.setHorizontalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );
        advancedPanelLayout.setVerticalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Details", advancedPanel);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-go-next.png"))); // NOI18N
        nextButton.setMnemonic('N');
        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-go-previous.png"))); // NOI18N
        prevButton.setMnemonic('P');
        prevButton.setText("Prev.");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-system-log-out.png"))); // NOI18N
        exitButton.setMnemonic('C');
        exitButton.setText("Close");

        quitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-process-stop.png"))); // NOI18N
        quitButton.setMnemonic('x');
        quitButton.setText("Exit");
        quitButton.setToolTipText("");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(prevButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(prevButton)
                    .addComponent(exitButton)
                    .addComponent(quitButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Main");
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        boolean foundEditable;
        int editingRowInView;
        int editingRowInModel;
        LocaleContent lc;
        EditableLocaleContent elc;

        editingRowInView = associatedTable.getSelectedRow();
        editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
        updateTargetLocale();
        tableModel.fireTableRowsUpdated(editingRowInModel, editingRowInModel);

        do {
            editingRowInView++;
            foundEditable = false;

            if (editingRowInView < associatedTable.getRowCount()) {
                editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
                lc = tableModel.getElementAt(editingRowInModel).getOriginalNode();
                foundEditable = lc.isEditable();

                if (foundEditable) {
                    elc = (EditableLocaleContent) lc;
                    associatedTable.getSelectionModel().setSelectionInterval(editingRowInView, editingRowInView);
                    editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
                    tableModel.fireTableRowsUpdated(editingRowInModel, editingRowInModel);
                }
            }
        } while ((editingRowInView < associatedTable.getRowCount()) && !foundEditable);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        boolean foundEditable;
        int editingRowInView;
        int editingRowInModel;
        LocaleContent lc;
        EditableLocaleContent elc;

        editingRowInView = associatedTable.getSelectedRow();
        editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
        updateTargetLocale();
        tableModel.fireTableRowsUpdated(editingRowInModel, editingRowInModel);

        do {
            editingRowInView--;
            foundEditable = false;

            if (editingRowInView > 0) {
                editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
                lc = tableModel.getElementAt(editingRowInModel).getOriginalNode();
                foundEditable = lc.isEditable();

                if (foundEditable) {
                    elc = (EditableLocaleContent) lc;
                    associatedTable.getSelectionModel().setSelectionInterval(editingRowInView, editingRowInView);
                    editingRowInModel = associatedTable.convertRowIndexToModel(editingRowInView);
                    tableModel.fireTableRowsUpdated(editingRowInModel, editingRowInModel);
                }
            }
        } while ((editingRowInView > 0) && !foundEditable);
    }//GEN-LAST:event_prevButtonActionPerformed

    private void commentTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commentTButtonActionPerformed
        LTKeyValuePair origNode = (LTKeyValuePair) selectedLObject.getOriginalNode();
        if (commentTButton.isSelected()) {
            origTextPane.setText(origNode.getComment().getTextValue());
        } else {
            origTextPane.setText(origNode.getTextValue());
        }
    }//GEN-LAST:event_commentTButtonActionPerformed

    private void keepOriginalCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keepOriginalCheckActionPerformed
        if (keepOriginalCheck.isSelected()) {
            trnsTextArea.setText("");
        }
    }//GEN-LAST:event_keepOriginalCheckActionPerformed

    private void copyOrigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyOrigButtonActionPerformed
        trnsTextArea.setText(origTextPane.getText());
    }//GEN-LAST:event_copyOrigButtonActionPerformed

    private void trnsStatusComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trnsStatusComboActionPerformed
        TranslationStatus t = (TranslationStatus) trnsStatusCombo.getSelectedItem();
        if (t != null) {
            trnsStatusCombo.setToolTipText(t.description());
        } else {
           trnsStatusCombo.setToolTipText("");
        }
    }//GEN-LAST:event_trnsStatusComboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accessKeyField;
    private javax.swing.JLabel accessKeyLabel;
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField commandKeyField;
    private javax.swing.JLabel commandKeyLabel;
    private javax.swing.JToggleButton commentTButton;
    private javax.swing.JButton copyOrigButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox keepOriginalCheck;
    private javax.swing.JPanel lowerPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel origLabel;
    private javax.swing.JTextPane origTextPane;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton quitButton;
    private javax.swing.JButton sugButton;
    private javax.swing.JLabel trnsLabel;
    private javax.swing.JComboBox<TranslationStatus> trnsStatusCombo;
    private javax.swing.JTextArea trnsTextArea;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        commentTButton.setEnabled(false);
        int selectedRow = associatedTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = associatedTable.convertRowIndexToModel(selectedRow);
            selectedLObject = tableModel.getElementAt(selectedRow);
            LocaleContent origLc = selectedLObject.getOriginalNode();
            LocaleContent trnsLc = origLc.getTwinByLocale(tableModel.getLocalizationCode());

            if (origLc instanceof EditableLocaleContent) {
                origTextPane.setText(((EditableLocaleContent) origLc).getTextValue());

                EditableLocaleContent trnsElc = (EditableLocaleContent) trnsLc;

                if (trnsElc != null) {
                    trnsTextArea.setText(trnsElc.getTextValue());
                    trnsStatusCombo.setSelectedItem(trnsElc.getTrnsStatus());
                    keepOriginalCheck.setSelected(trnsElc.isKeepOriginal());
                    
                    if (trnsElc instanceof LTKeyValuePair) {
                        LTKeyValuePair lkvp = (LTKeyValuePair) trnsElc;
                        LTKeyValuePair connAccessKey = lkvp.getConnAccesskey();
                        LTKeyValuePair connCommandKey = lkvp.getConnCommandkey();
                        
                        commentTButton.setEnabled(((LTKeyValuePair) origLc).getComment() != null);
                        
                        if (connAccessKey != null) {
                            accessKeyField.setText(connAccessKey.getTextValue());
                            accessKeyField.setEnabled(true);
                        } else {
                            accessKeyField.setText("");
                            accessKeyField.setEnabled(false);
                        }
                        if (connCommandKey != null) {
                            commandKeyField.setText(connCommandKey.getTextValue());
                            commandKeyField.setEnabled(true);
                        } else {
                            commandKeyField.setText("");
                            commandKeyField.setEnabled(false);
                        }
                    }
                } else {
                    trnsTextArea.setText("");
                    trnsStatusCombo.setSelectedItem(TranslationStatus.Copied);
                    accessKeyField.setText("");
                    accessKeyField.setEnabled(false);
                    commandKeyField.setText("");
                    commandKeyField.setEnabled(false);
                }
            } else {
                origTextPane.setText("");
                trnsTextArea.setText("");
                trnsStatusCombo.setSelectedItem(TranslationStatus.Copied);
                accessKeyField.setText("");
                accessKeyField.setEnabled(false);
                commandKeyField.setText("");
                commandKeyField.setEnabled(false);
            }
        }
    }
}
