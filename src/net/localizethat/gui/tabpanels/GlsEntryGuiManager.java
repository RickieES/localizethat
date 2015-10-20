/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

import java.beans.Beans;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.localizethat.Main;
import net.localizethat.gui.models.GlosEntryTableModel;
import net.localizethat.gui.models.GlosTranslationTableModel;
import net.localizethat.model.Glossary;
import net.localizethat.model.GlsEntry;
import net.localizethat.model.GlsTranslation;
import net.localizethat.model.L10n;
import net.localizethat.model.PartOfSpeech;
import net.localizethat.util.gui.JStatusBar;

/**
 * Glossary entries and translations management GUI
 * @author rpalomares
 */
public class GlsEntryGuiManager extends AbstractTabPanel {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    TableRowSorter<GlosEntryTableModel> entriesRowSorter;
    TableRowSorter<GlosTranslationTableModel> translationsRowSorter;
    JStatusBar statusBar;

    /**
     * Creates new form GlsEntryGuiManager
     */
    public GlsEntryGuiManager() {
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();

        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }

        for(PartOfSpeech pos : PartOfSpeech.values()) {
            glsePoSCombo.addItem(pos);
        }

        entriesRowSorter = new TableRowSorter<>(glosEntryTableModel);
        glseTable.setRowSorter(entriesRowSorter);
        glseFilterField.getDocument().addDocumentListener(
                new FilterDocumentListener(glosEntryTableModel, entriesRowSorter, glseFilterField));

        translationsRowSorter = new TableRowSorter<>(glosTranslationTableModel);
        glstTable.setRowSorter(translationsRowSorter);
        glstFilterField.getDocument().addDocumentListener(
                new FilterDocumentListener(glosTranslationTableModel, translationsRowSorter, glstFilterField));


        glseTable.getSelectionModel().addListSelectionListener(new GlsEntryTableRowListener());
        glstTable.getSelectionModel().addListSelectionListener(new GlsTranslationTableRowListener());
    }

    private void enableEntriesPanelControls(boolean activate) {
        glseFilterField.setEnabled(activate);
        glseTable.setEnabled(activate);
        glseTermField.setEnabled(activate);
        glsePoSCombo.setEnabled(activate);
        glseCommentTextArea.setEnabled(activate);
        newGlseButton.setEnabled(activate);
        if (activate) {
            saveGlseButton.setEnabled(glseTable.getSelectedRow() != -1);
            deleteGlseButton.setEnabled(glseTable.getSelectedRow() != -1);
        } else {
            saveGlseButton.setEnabled(false);
            deleteGlseButton.setEnabled(false);
        }
    }

    private void enableTranslationsPanelControls(boolean activate) {
        glstFilterField.setEnabled(activate);
        glstTable.setEnabled(activate);
        glstValueField.setEnabled(activate);
        glstCommentTextArea.setEnabled(activate);
        newGlstButton.setEnabled(activate);
        if (activate) {
            saveGlstButton.setEnabled(glstTable.getSelectedRow() != -1);
            deleteGlstButton.setEnabled(glstTable.getSelectedRow() != -1);
        } else {
            saveGlstButton.setEnabled(false);
            deleteGlstButton.setEnabled(false);
        }
    }

    private void clearEntryDetailFields() {
        glseTermField.setText("");
        glsePoSCombo.setSelectedIndex(-1);
        glseCommentTextArea.setText("");

    }

    private void clearTranslationDetailFields() {
        glstValueField.setText("");
        glstCommentTextArea.setText("");
    }

    private void refreshGlossaryList() {
        TypedQuery<Glossary> glosQuery = entityManager.createNamedQuery("Glossary.findAll",
                Glossary.class);
        glossaryComboModel.clearAll();
        glossaryComboModel.addAll(glosQuery.getResultList());
    }

    private void refreshL10nList() {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        l10nComboModel.clearAll();
        l10nComboModel.addAll(l10nQuery.getResultList());
    }

    private void refreshGlsEntryList() {
        Glossary g = (Glossary) glosSelCombo.getSelectedItem();
        TypedQuery<GlsEntry> glseQuery = entityManager.createNamedQuery("GlsEntry.findAllForGlossary",
                GlsEntry.class);
        glosEntryTableModel.clearAll();

        if (g != null) {
            glseQuery.setParameter("gid", g);
            glosEntryTableModel.addAll(glseQuery.getResultList());
            clearEntryDetailFields();
        }
    }

    private void refreshGlsTranslationList() {
        int selectedRow = glseTable.getSelectedRow();
        L10n l = (L10n) localeSelCombo.getSelectedItem();
        TypedQuery<GlsTranslation> glstQuery = entityManager.createNamedQuery("GlsTranslation.findByEntryAndLocale",
                GlsTranslation.class);
        glosTranslationTableModel.clearAll();

        if ((selectedRow != -1) && (l != null)) {
            selectedRow = glseTable.convertRowIndexToModel(selectedRow);
            GlsEntry ge = glosEntryTableModel.getElement(selectedRow);
            glstQuery.setParameter("glseid", ge);
            glstQuery.setParameter("l10nid", l);
            glosTranslationTableModel.addAll(glstQuery.getResultList());
            clearTranslationDetailFields();
        }
    }

    private void applyFilter(TableModel tm, TableRowSorter trs, String filter) {
        RowFilter<TableModel, Object> rf;
        //If current expression doesn't parse, don't update.
        try {
            // (?i) adds case insensitive flag to the RegEx
            rf = RowFilter.regexFilter("(?i)" + filter, 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        trs.setRowFilter(rf);

    }

    private boolean validateOnGlsEntrySave(GlsEntry selectedGe) {
        // Validation 1: the term must not be empty
        if (glseTermField.getText().trim().isEmpty()) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: glossary entry term can't be empty",
                    "The glossary entry must not be an empty term");
            return false;
        }

        // Validation 2: the glossary term can't exist already in the database
        // for the same glossary, except in the same item
        TypedQuery<GlsEntry> validationQuery = entityManager.createNamedQuery(
                "GlsEntry.findByGlsAndTermAndPoS", GlsEntry.class);
        validationQuery.setParameter("glosid", (Glossary) glosSelCombo.getSelectedItem());
        validationQuery.setParameter("glseterm", glseTermField.getText());
        validationQuery.setParameter("partofspeech", (PartOfSpeech) glsePoSCombo.getSelectedItem());
        List<GlsEntry> listGlsEntries = validationQuery.getResultList();
        int listLength = listGlsEntries.size();
        boolean isOk;
        if (listLength == 0) {
            isOk = true;
        } else if (listLength == 1) {
            GlsEntry glseInDB = listGlsEntries.get(0);
            isOk = (Objects.equals(glseInDB.getId(), selectedGe.getId()));
        } else {
            // This should never be reached, since we don't allow more than one glossary entry
            // with the same parent glossary, term and part of speech, but it is checked just
            // as defensive programming
            isOk = false;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: glossary entry already exists",
                    "There is already one entry in the database for the selected glossary "
                    + "with the same term and part of speech values");
            return false;
        }
        return true;
    }

    private boolean validateOnGlsTranslationSave(GlsTranslation selectedGt) {
        // Validation 1: the translation must not be empty
        if (this.glstValueField.getText().trim().isEmpty()) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: glossary translation value can't be empty",
                    "The glossary translation must not be an empty one");
            return false;
        }

        // Validation 2: the glossary translation can't exist already in the database
        // for the same glossary, term and locale, except in the same item
        TypedQuery<GlsTranslation> validationQuery = entityManager.createNamedQuery(
                "GlsTranslation.findByEntryAndLocaleAndValue", GlsTranslation.class);
        validationQuery.setParameter("glseid", selectedGt.getGlseId());
        validationQuery.setParameter("l10nid", (L10n) localeSelCombo.getSelectedItem());
        validationQuery.setParameter("value", glstValueField.getText());
        List<GlsTranslation> listGlsTranslations = validationQuery.getResultList();
        int listLength = listGlsTranslations.size();
        boolean isOk;
        if (listLength == 0) {
            isOk = true;
        } else if (listLength == 1) {
            GlsTranslation glstInDB = listGlsTranslations.get(0);
            isOk = (Objects.equals(glstInDB.getId(), selectedGt.getId()));
        } else {
            // This should never be reached, since we don't allow more than one glossary
            // translation with the same parent glossary entry, locale and value, but it
            // is checked just as defensive programming
            isOk = false;
        }
        if (!isOk) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error while saving: glossary translation already exists",
                    "There is already one entry in the database for the selected glossary "
                    + "entry with the same locale and translation values");
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

        entityManager = emf.createEntityManager();
        glosEntryTableModel = new net.localizethat.gui.models.GlosEntryTableModel();
        glosTranslationTableModel = new net.localizethat.gui.models.GlosTranslationTableModel();
        l10nComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        glossaryComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<Glossary>();
        glossarySelectorPanel = new javax.swing.JPanel();
        glosSelLabel = new javax.swing.JLabel();
        glosSelCombo = new javax.swing.JComboBox<Glossary>();
        localeSelLabel = new javax.swing.JLabel();
        localeSelCombo = new javax.swing.JComboBox<L10n>();
        glosMainLangLabel = new javax.swing.JLabel();
        glosMainLangField = new javax.swing.JTextField();
        detailPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        glosEntriesPanel = new javax.swing.JPanel();
        glseFilterLabel = new javax.swing.JLabel();
        glseFilterField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        glseTable = new javax.swing.JTable();
        glseDetailPanel = new javax.swing.JPanel();
        glseTermLabel = new javax.swing.JLabel();
        glseTermField = new javax.swing.JTextField();
        glsePosELabel = new javax.swing.JLabel();
        glsePoSCombo = new javax.swing.JComboBox<PartOfSpeech>();
        glseCommentLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        glseCommentTextArea = new javax.swing.JTextArea();
        glseCreationDateLabel = new javax.swing.JLabel();
        glseCreationDateField = new javax.swing.JTextField();
        glseLastUpdatedLabel = new javax.swing.JLabel();
        glseLastUpdatedField = new javax.swing.JTextField();
        newGlseButton = new javax.swing.JButton();
        saveGlseButton = new javax.swing.JButton();
        deleteGlseButton = new javax.swing.JButton();
        glosTrnsPanel = new javax.swing.JPanel();
        glstFilterLabel = new javax.swing.JLabel();
        glstFilterField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        glstTable = new javax.swing.JTable();
        glstDetailPanel = new javax.swing.JPanel();
        glstValueLabel = new javax.swing.JLabel();
        glstValueField = new javax.swing.JTextField();
        glstCommentLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        glstCommentTextArea = new javax.swing.JTextArea();
        glstCreationDateLabel = new javax.swing.JLabel();
        glstCreationDateField = new javax.swing.JTextField();
        glstLastUpdatedLabel = new javax.swing.JLabel();
        glstLastUpdatedField = new javax.swing.JTextField();
        newGlstButton = new javax.swing.JButton();
        saveGlstButton = new javax.swing.JButton();
        deleteGlstButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        glosSelLabel.setLabelFor(glosSelCombo);
        glosSelLabel.setText("Glossary to work with:");

        glosSelCombo.setModel(glossaryComboModel);
        glosSelCombo.addActionListener(formListener);

        localeSelLabel.setLabelFor(localeSelCombo);
        localeSelLabel.setText("Target language:");

        localeSelCombo.setModel(l10nComboModel);
        localeSelCombo.addActionListener(formListener);

        glosMainLangLabel.setLabelFor(glosMainLangField);
        glosMainLangLabel.setText("Main language:");

        glosMainLangField.setEditable(false);
        glosMainLangField.setEnabled(false);

        javax.swing.GroupLayout glossarySelectorPanelLayout = new javax.swing.GroupLayout(glossarySelectorPanel);
        glossarySelectorPanel.setLayout(glossarySelectorPanelLayout);
        glossarySelectorPanelLayout.setHorizontalGroup(
            glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glossarySelectorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(glosMainLangLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glosSelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(glosSelCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glosMainLangField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localeSelLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localeSelCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        glossarySelectorPanelLayout.setVerticalGroup(
            glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glossarySelectorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosSelLabel)
                    .addComponent(glosSelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(localeSelLabel)
                    .addComponent(localeSelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glossarySelectorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glosMainLangField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glosMainLangLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        detailPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jSplitPane1.setResizeWeight(0.5);

        glseFilterLabel.setDisplayedMnemonic('F');
        glseFilterLabel.setLabelFor(glseFilterField);
        glseFilterLabel.setText("Filter:");

        glseFilterField.setToolTipText("Enter text to filter entries");

        glseTable.setModel(glosEntryTableModel);
        glseTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(glseTable);

        glseTermLabel.setDisplayedMnemonic('T');
        glseTermLabel.setLabelFor(glseTermField);
        glseTermLabel.setText("Term:");

        glsePosELabel.setDisplayedMnemonic('P');
        glsePosELabel.setLabelFor(glsePoSCombo);
        glsePosELabel.setText("Part of speech:");

        glseCommentLabel.setDisplayedMnemonic('C');
        glseCommentLabel.setLabelFor(glseCommentTextArea);
        glseCommentLabel.setText("Comment:");

        glseCommentTextArea.setColumns(20);
        glseCommentTextArea.setLineWrap(true);
        glseCommentTextArea.setRows(5);
        jScrollPane2.setViewportView(glseCommentTextArea);

        glseCreationDateLabel.setLabelFor(glseCreationDateField);
        glseCreationDateLabel.setText("Creation Date:");

        glseCreationDateField.setEditable(false);
        glseCreationDateField.setEnabled(false);

        glseLastUpdatedLabel.setLabelFor(glseLastUpdatedField);
        glseLastUpdatedLabel.setText("Last Updated On:");

        glseLastUpdatedField.setEditable(false);
        glseLastUpdatedField.setEnabled(false);

        newGlseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-new.png"))); // NOI18N
        newGlseButton.setText("New");
        newGlseButton.addActionListener(formListener);

        saveGlseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-save.png"))); // NOI18N
        saveGlseButton.setText("Save");
        saveGlseButton.addActionListener(formListener);

        deleteGlseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteGlseButton.setText("Delete");
        deleteGlseButton.addActionListener(formListener);

        javax.swing.GroupLayout glseDetailPanelLayout = new javax.swing.GroupLayout(glseDetailPanel);
        glseDetailPanel.setLayout(glseDetailPanelLayout);
        glseDetailPanelLayout.setHorizontalGroup(
            glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glseDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(glseDetailPanelLayout.createSequentialGroup()
                        .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glseCreationDateLabel)
                            .addComponent(glseLastUpdatedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glseCreationDateField)
                            .addComponent(glseLastUpdatedField)))
                    .addGroup(glseDetailPanelLayout.createSequentialGroup()
                        .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glsePosELabel)
                            .addComponent(glseTermLabel)
                            .addComponent(glseCommentLabel))
                        .addGap(29, 29, 29)
                        .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glsePoSCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .addComponent(glseTermField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, glseDetailPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newGlseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveGlseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteGlseButton)))
                .addContainerGap())
        );
        glseDetailPanelLayout.setVerticalGroup(
            glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glseDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glseTermLabel)
                    .addComponent(glseTermField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glsePoSCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glsePosELabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(glseCommentLabel)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glseCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glseCreationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glseLastUpdatedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glseLastUpdatedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(glseDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteGlseButton)
                    .addComponent(saveGlseButton)
                    .addComponent(newGlseButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout glosEntriesPanelLayout = new javax.swing.GroupLayout(glosEntriesPanel);
        glosEntriesPanel.setLayout(glosEntriesPanelLayout);
        glosEntriesPanelLayout.setHorizontalGroup(
            glosEntriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glosEntriesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glosEntriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(glseDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(glosEntriesPanelLayout.createSequentialGroup()
                        .addComponent(glseFilterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(glseFilterField))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        glosEntriesPanelLayout.setVerticalGroup(
            glosEntriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glosEntriesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glosEntriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glseFilterLabel)
                    .addComponent(glseFilterField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(glseDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(glosEntriesPanel);

        glstFilterLabel.setDisplayedMnemonic('i');
        glstFilterLabel.setLabelFor(glstFilterField);
        glstFilterLabel.setText("Filter:");

        glstFilterField.setToolTipText("Enter text to filter entries");

        glstTable.setModel(glosTranslationTableModel);
        glstTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(glstTable);

        glstValueLabel.setDisplayedMnemonic('r');
        glstValueLabel.setLabelFor(glstValueField);
        glstValueLabel.setText("Translation:");

        glstCommentLabel.setDisplayedMnemonic('o');
        glstCommentLabel.setLabelFor(glstCommentTextArea);
        glstCommentLabel.setText("Comment:");

        glstCommentTextArea.setColumns(20);
        glstCommentTextArea.setLineWrap(true);
        glstCommentTextArea.setRows(5);
        jScrollPane4.setViewportView(glstCommentTextArea);

        glstCreationDateLabel.setLabelFor(glseCreationDateField);
        glstCreationDateLabel.setText("Creation Date:");

        glstCreationDateField.setEditable(false);
        glstCreationDateField.setEnabled(false);

        glstLastUpdatedLabel.setLabelFor(glseLastUpdatedField);
        glstLastUpdatedLabel.setText("Last Updated On:");

        glstLastUpdatedField.setEditable(false);
        glstLastUpdatedField.setEnabled(false);

        newGlstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-new.png"))); // NOI18N
        newGlstButton.setText("New");
        newGlstButton.addActionListener(formListener);

        saveGlstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/document-save.png"))); // NOI18N
        saveGlstButton.setText("Save");
        saveGlstButton.addActionListener(formListener);

        deleteGlstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/edit-delete.png"))); // NOI18N
        deleteGlstButton.setText("Delete");
        deleteGlstButton.addActionListener(formListener);

        javax.swing.GroupLayout glstDetailPanelLayout = new javax.swing.GroupLayout(glstDetailPanel);
        glstDetailPanel.setLayout(glstDetailPanelLayout);
        glstDetailPanelLayout.setHorizontalGroup(
            glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glstDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(glstDetailPanelLayout.createSequentialGroup()
                        .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glstCreationDateLabel)
                            .addComponent(glstLastUpdatedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glstCreationDateField)
                            .addComponent(glstLastUpdatedField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, glstDetailPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newGlstButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveGlstButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteGlstButton))
                    .addGroup(glstDetailPanelLayout.createSequentialGroup()
                        .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glstValueLabel)
                            .addComponent(glstCommentLabel))
                        .addGap(51, 51, 51)
                        .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(glstValueField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))))
                .addContainerGap())
        );
        glstDetailPanelLayout.setVerticalGroup(
            glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glstDetailPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glstValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glstValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glstCommentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glstCreationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(glstCreationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glstLastUpdatedLabel)
                    .addComponent(glstLastUpdatedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(glstDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteGlstButton)
                    .addComponent(saveGlstButton)
                    .addComponent(newGlstButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout glosTrnsPanelLayout = new javax.swing.GroupLayout(glosTrnsPanel);
        glosTrnsPanel.setLayout(glosTrnsPanelLayout);
        glosTrnsPanelLayout.setHorizontalGroup(
            glosTrnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, glosTrnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glosTrnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(glstDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, glosTrnsPanelLayout.createSequentialGroup()
                        .addComponent(glstFilterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(glstFilterField))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        glosTrnsPanelLayout.setVerticalGroup(
            glosTrnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glosTrnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glosTrnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(glstFilterLabel)
                    .addComponent(glstFilterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(glstDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(glosTrnsPanel);

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glossarySelectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(glossarySelectorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == glosSelCombo) {
                GlsEntryGuiManager.this.glosSelComboActionPerformed(evt);
            }
            else if (evt.getSource() == localeSelCombo) {
                GlsEntryGuiManager.this.localeSelComboActionPerformed(evt);
            }
            else if (evt.getSource() == newGlseButton) {
                GlsEntryGuiManager.this.newGlseButtonActionPerformed(evt);
            }
            else if (evt.getSource() == saveGlseButton) {
                GlsEntryGuiManager.this.saveGlseButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteGlseButton) {
                GlsEntryGuiManager.this.deleteGlseButtonActionPerformed(evt);
            }
            else if (evt.getSource() == newGlstButton) {
                GlsEntryGuiManager.this.newGlstButtonActionPerformed(evt);
            }
            else if (evt.getSource() == saveGlstButton) {
                GlsEntryGuiManager.this.saveGlstButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteGlstButton) {
                GlsEntryGuiManager.this.deleteGlstButtonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void newGlseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGlseButtonActionPerformed
        GlsEntry ge = new GlsEntry();
        ge.setGlosId((Glossary) glosSelCombo.getSelectedItem());
        ge.setComment("");
        ge.setTerm("");
        ge.setPartOfSpeech(PartOfSpeech.OTHER);
        ge.setCreationDate(new Date());
        ge.setLastUpdate(new Date());
        try {
            entityManager.persist(ge);
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            statusBar.setText(JStatusBar.LogMsgType.INFO,
                    "New entry added, use detail fields to complete it");
            glosEntryTableModel.addElement(ge);
            int index = glosEntryTableModel.getIndexOf(ge);

            // The below line triggers a java.lang.ArrayIndexOutOfBoundsException,
            // despite the number of lines in both the model and the view being equal
            // glosEntryTableModel.fireTableRowsInserted(index, index);
            // See: http://stackoverflow.com/questions/26020724/java-firetablerowsinsertedint-int-with-rowsorter
            //
            // So I workaround with this less efficient method
            glosEntryTableModel.fireTableDataChanged();

            glseTable.changeSelection(glseTable.convertRowIndexToView(index), 0, false, false);
            glseTermField.requestFocus();
        } catch (Exception ex) {
            Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating entry",
                    "Error while creating glossary entry", ex);
        }
    }//GEN-LAST:event_newGlseButtonActionPerformed

    private void glosSelComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glosSelComboActionPerformed
        Glossary g = (Glossary) glosSelCombo.getSelectedItem();
        L10n l = (L10n) localeSelCombo.getSelectedItem();

        if (g != null) {
            glosMainLangField.setText(g.getL10nId().getCode());
            if (l != null && l.getCode().equals(glosMainLangField.getText())) {
                statusBar.setWarnText("Selected locale cannnot match glossary master language, reverting");
                localeSelCombo.setSelectedIndex(-1);
            }
            refreshGlsEntryList();
            refreshGlsTranslationList();
            enableEntriesPanelControls(true);
            enableTranslationsPanelControls(false);
        }
    }//GEN-LAST:event_glosSelComboActionPerformed

    private void localeSelComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localeSelComboActionPerformed
        L10n l = (L10n) localeSelCombo.getSelectedItem();

        // We can't accept that the selected locale equals the main locale of the selected glossary
        if ((l != null) && (l.getCode().equals(glosMainLangField.getText()))) {
            statusBar.setWarnText("Selected locale cannnot match glossary master language, reverting");
            localeSelCombo.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_localeSelComboActionPerformed

    private void saveGlseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGlseButtonActionPerformed
        int index = glseTable.convertRowIndexToModel(glseTable.getSelectedRow());
        if (!entityManager.isJoinedToTransaction()) {
            entityManager.getTransaction().begin();
        }
        GlsEntry ge = entityManager.find(GlsEntry.class,
                glosEntryTableModel.getElement(index).getId());

        // validateOnSave will report the specific problem in the status bar
        if (!validateOnGlsEntrySave(ge)) {
            return;
        }

        ge.setTerm(glseTermField.getText());
        ge.setPartOfSpeech((PartOfSpeech) glsePoSCombo.getSelectedItem());
        ge.setComment(glseCommentTextArea.getText());
        ge.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            glosEntryTableModel.fireTableRowsUpdated(index, index);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Entry changes saved");
        } catch (Exception ex) {
            Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving entry changes",
                    "Error while saving glossary entry changes", ex);
        }
    }//GEN-LAST:event_saveGlseButtonActionPerformed

    private void newGlstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGlstButtonActionPerformed
        int selectedRow = glseTable.getSelectedRow();
        L10n l = (L10n) localeSelCombo.getSelectedItem();

        if ((selectedRow != -1) && (l != null)) {
            GlsTranslation gt = new GlsTranslation();
            selectedRow = glseTable.convertRowIndexToModel(selectedRow);
            GlsEntry ge = glosEntryTableModel.getElement(selectedRow);
            gt.setGlseId(ge);
            gt.setL10nId(l);
            gt.setValue("");
            gt.setComment("");
            gt.setCreationDate(new Date());
            gt.setLastUpdate(new Date());
            try {
                entityManager.persist(gt);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                glosTranslationTableModel.addElement(gt);
                int index = glosTranslationTableModel.getIndexOf(gt);
                glosTranslationTableModel.fireTableRowsInserted(index, index);
                glstTable.changeSelection(glstTable.convertRowIndexToView(index), 0, false, false);
                glstValueField.requestFocus();
                statusBar.setText(JStatusBar.LogMsgType.INFO,
                        "New translation added, use detail fields to complete it");
            } catch (Exception ex) {
                Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while creating translation",
                        "Error while creating glossary translation", ex);
            }
        }
    }//GEN-LAST:event_newGlstButtonActionPerformed

    private void saveGlstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGlstButtonActionPerformed
        int index = glstTable.convertRowIndexToModel(glstTable.getSelectedRow());
        if (!entityManager.isJoinedToTransaction()) {
            entityManager.getTransaction().begin();
        }
        GlsTranslation gt = entityManager.find(GlsTranslation.class,
                glosTranslationTableModel.getElement(index).getId());

        // validateOnSave will report the specific problem in the status bar
        if (!validateOnGlsTranslationSave(gt)) {
            return;
        }

        entityManager.refresh(gt);
        gt.setValue(glstValueField.getText());
        gt.setComment(glstCommentTextArea.getText());
        gt.setLastUpdate(new Date());
        try {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
            glosTranslationTableModel.fireTableRowsUpdated(index, index);
            statusBar.setText(JStatusBar.LogMsgType.INFO, "Translation changes saved");
        } catch (Exception ex) {
            Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while saving translation changes",
                    "Error while saving glossary translation changes", ex);
        }
    }//GEN-LAST:event_saveGlstButtonActionPerformed

    private void deleteGlseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteGlseButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected glossary entry and associated translations?",
                "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            int index = glseTable.convertRowIndexToModel(glseTable.getSelectedRow());

            GlsEntry ge = glosEntryTableModel.getElement(index);
            try {
                // Remove and commit all children GlsTranslations from this GlsEntry
                for(GlsTranslation gt : ge.getGlsTranslationCollection()) {
                    entityManager.remove(gt);
                }
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();

                // Clear the GlsTranslation collection and remove GlsEntry
                ge.setGlsTranslationCollection(null);
                entityManager.remove(ge);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();


                glosEntryTableModel.fireTableRowsDeleted(index, index);
                glosTranslationTableModel.fireTableDataChanged();
                refreshGlsEntryList();
                refreshGlsTranslationList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Glossary entry deleted");
            }
            catch (IllegalArgumentException ex) {
                Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting glossary entry", ex);
            }
        }
    }//GEN-LAST:event_deleteGlseButtonActionPerformed

    private void deleteGlstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteGlstButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(this.getParent(),
                "Really delete the selected glossary translation?", "Confirm deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            int index = glstTable.convertRowIndexToModel(glstTable.getSelectedRow());

            GlsTranslation gt = glosTranslationTableModel.getElement(index);
            GlsEntry ge = gt.getGlseId();
            try {
                ge.getGlsTranslationCollection().remove(gt);
                entityManager.remove(gt);
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
                glosTranslationTableModel.fireTableRowsDeleted(index, index);
                refreshGlsTranslationList();
                statusBar.setText(JStatusBar.LogMsgType.INFO, "Glossary translation deleted");
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(GlsEntryGuiManager.class.getName()).log(Level.SEVERE, null, ex);
                statusBar.logMessage(JStatusBar.LogMsgType.ERROR, "Error while deleting",
                        "Error while deleting glossary translation", ex);
            }
        }
    }//GEN-LAST:event_deleteGlstButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteGlseButton;
    private javax.swing.JButton deleteGlstButton;
    private javax.swing.JPanel detailPanel;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JPanel glosEntriesPanel;
    private net.localizethat.gui.models.GlosEntryTableModel glosEntryTableModel;
    private javax.swing.JTextField glosMainLangField;
    private javax.swing.JLabel glosMainLangLabel;
    private javax.swing.JComboBox<Glossary> glosSelCombo;
    private javax.swing.JLabel glosSelLabel;
    private net.localizethat.gui.models.GlosTranslationTableModel glosTranslationTableModel;
    private javax.swing.JPanel glosTrnsPanel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Glossary> glossaryComboModel;
    private javax.swing.JPanel glossarySelectorPanel;
    private javax.swing.JLabel glseCommentLabel;
    private javax.swing.JTextArea glseCommentTextArea;
    private javax.swing.JTextField glseCreationDateField;
    private javax.swing.JLabel glseCreationDateLabel;
    private javax.swing.JPanel glseDetailPanel;
    private javax.swing.JTextField glseFilterField;
    private javax.swing.JLabel glseFilterLabel;
    private javax.swing.JTextField glseLastUpdatedField;
    private javax.swing.JLabel glseLastUpdatedLabel;
    private javax.swing.JComboBox<PartOfSpeech> glsePoSCombo;
    private javax.swing.JLabel glsePosELabel;
    private javax.swing.JTable glseTable;
    private javax.swing.JTextField glseTermField;
    private javax.swing.JLabel glseTermLabel;
    private javax.swing.JLabel glstCommentLabel;
    private javax.swing.JTextArea glstCommentTextArea;
    private javax.swing.JTextField glstCreationDateField;
    private javax.swing.JLabel glstCreationDateLabel;
    private javax.swing.JPanel glstDetailPanel;
    private javax.swing.JTextField glstFilterField;
    private javax.swing.JLabel glstFilterLabel;
    private javax.swing.JTextField glstLastUpdatedField;
    private javax.swing.JLabel glstLastUpdatedLabel;
    private javax.swing.JTable glstTable;
    private javax.swing.JTextField glstValueField;
    private javax.swing.JLabel glstValueLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> l10nComboModel;
    private javax.swing.JComboBox<L10n> localeSelCombo;
    private javax.swing.JLabel localeSelLabel;
    private javax.swing.JButton newGlseButton;
    private javax.swing.JButton newGlstButton;
    private javax.swing.JButton saveGlseButton;
    private javax.swing.JButton saveGlstButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onTabPanelAdded() {
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();
        }
        refreshGlossaryList();
        refreshL10nList();
        refreshGlsEntryList();
        refreshGlsTranslationList();
        if ((glossaryComboModel.getSize() < 1) || (l10nComboModel.getSize() < 2)) {
            JOptionPane.showMessageDialog(this,
                    "You need at least one glossary and two locales defined!",
                    "Not enough glossaries or locales", JOptionPane.ERROR_MESSAGE);
        }
        enableEntriesPanelControls(false);
        enableTranslationsPanelControls(false);
    }

    @Override
    public void onTabPanelRemoved() {
        glosMainLangField.setText("");
        glosSelCombo.setSelectedIndex(-1);
        localeSelCombo.setSelectedIndex(-1);
        glseFilterField.setText("");
        glstFilterField.setText("");
        clearEntryDetailFields();
        clearTranslationDetailFields();
        enableEntriesPanelControls(false);
        enableTranslationsPanelControls(false);
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
        entityManager.close();
        entityManager = null;
    }

    private class GlsEntryTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = glseTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedRow = glseTable.convertRowIndexToModel(selectedRow);
                GlsEntry ge = glosEntryTableModel.getElement(selectedRow);
                glseTermField.setText(ge.getTerm());
                glsePoSCombo.setSelectedItem(ge.getPartOfSpeech());
                glseCreationDateField.setText(ge.getCreationDate().toString());
                glseLastUpdatedField.setText(ge.getLastUpdate().toString());
                glseCommentTextArea.setText(ge.getComment());
                refreshGlsTranslationList();
                enableEntriesPanelControls(true);
                enableTranslationsPanelControls(localeSelCombo.getSelectedIndex() != -1);
            }
            statusBar.clearText();
        }
    }

    private class GlsTranslationTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = glstTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedRow = glstTable.convertRowIndexToModel(selectedRow);
                GlsTranslation gt = glosTranslationTableModel.getElement(selectedRow);
                glstValueField.setText(gt.getValue());
                glstCreationDateField.setText(gt.getCreationDate().toString());
                glstLastUpdatedField.setText(gt.getLastUpdate().toString());
                glstCommentTextArea.setText(gt.getComment());
                enableTranslationsPanelControls(true);
            }
            statusBar.clearText();
        }
    }

    private class FilterDocumentListener implements DocumentListener {
        private final TableModel tm;
        private final TableRowSorter trs;
        private final JTextField filter;

        protected FilterDocumentListener(TableModel tm, TableRowSorter trs, JTextField filter) {
            this.tm = tm;
            this.trs = trs;
            this.filter = filter;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            applyFilter(tm, trs, filter.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyFilter(tm, trs, filter.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyFilter(tm, trs, filter.getText());
        }
    }
}
