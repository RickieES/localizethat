/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.gui.dialogs;

import au.com.bytecode.opencsv.CSV;
import java.beans.Beans;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.localizethat.Main;
import net.localizethat.gui.models.CharsetModel;
import net.localizethat.model.Glossary;
import net.localizethat.model.L10n;
import net.localizethat.tasks.CSVHeaderReaderWorker;
import net.localizethat.tasks.CSVImporterWorker;
import net.localizethat.tasks.CSVImporterWorker.CSVImportSettings;
import net.localizethat.tasks.FileHeadReaderWorker;
import net.localizethat.tasks.FileLinesCounterWorker;
import net.localizethat.util.gui.JStatusBar;
import net.localizethat.util.gui.ModalDialog;
import net.localizethat.util.gui.ModalDialogComponent;
import net.localizethat.util.gui.ProgressBarListener;

/**
 * Import CSV Glossary panel to be used in a modal dialog
 * @author rpalomares
 */
public class ImportCSVGlossaryDialog extends javax.swing.JPanel implements ModalDialogComponent {
    private static final long serialVersionUID = 1L;
    EntityManagerFactory emf;
    JStatusBar statusBar;
    CharsetModel charsetModel;
    List<DefaultComboBoxModel<String>> headerModelList;
    ModalDialog md;
    int fileLines;

    /**
     * Creates new form ImportCSVGlossary
     */
    public ImportCSVGlossaryDialog() {
        statusBar = Main.mainWindow.getStatusBar();
        emf = Main.emf;

        // Retrieve available charsets
        SortedMap<String, Charset> lc = Charset.availableCharsets();
        charsetModel = new CharsetModel(lc.values());

        initComponents();
        if (!Beans.isDesignTime()) {
            entityManager.getTransaction().begin();
        }
        charsetCombo.setModel(charsetModel);
        filenamePathField.addFileFilter(new FileNameExtensionFilter("CSV files", "csv", "txt"));

        // Build a list with references to the header combobox models
        headerModelList = new ArrayList<>(10);
        headerModelList.add((DefaultComboBoxModel<String>) origTermCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) origTermCommentCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) origTermCreationDateCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) origTermLastUpdateCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) origTermPoSCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) trnsValueCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) trnsCommentCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) trnsCreationDateCombo.getModel());
        headerModelList.add((DefaultComboBoxModel<String>) trnsLastUpdateCombo.getModel());

        for(OnExistingTermAction oeta : OnExistingTermAction.values()) {
            this.onExistingTermsCombo.addItem(oeta);
        }

        refreshGlossaryList();
        refreshL10nList();
        retrieveSavedCSVImportSettings();
        // On start, only the first tab is enabled
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
    }

    private void refreshGlossaryList() {
        TypedQuery<Glossary> glosQuery = entityManager.createNamedQuery("Glossary.findAll",
                Glossary.class);

        glosComboModel.clearAll();
        glosComboModel.addAll(glosQuery.getResultList());
    }

    private void refreshL10nList() {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        l10nComboModel.clearAll();
        l10nComboModel.addAll(l10nQuery.getResultList());
    }

    private void retrieveSavedCSVImportSettings() {
        CSVImportSettings cis;
        cis = CSVImporterWorker.CSVImportSettings.recreateFromXML("savedCis.xml");
        if (cis != null) {
            if (cis.getFileToImport() != null) {
                filenamePathField.setText(cis.getFileToImport().getAbsolutePath());
            }
            
            testRunCheck.setSelected(cis.isTestMode());

            if (cis.getGlossary() != null) {
                Glossary selectedGlos = null;
                Glossary savedGlos = cis.getGlossary();
                for(Glossary g : glosComboModel.getAll()) {
                    if (Objects.equals(g.getId(), savedGlos.getId())) {
                        selectedGlos = g;
                        break;
                    }
                }
                if (selectedGlos != null) {
                    importToGlossaryCombo.setSelectedItem(selectedGlos);
                }
            }

            allQuotedCheck.setSelected(cis.isAllFieldsQuoted());

            if (cis.getCharset() != null) {
                charsetCombo.setSelectedItem(cis.getCharset());
            }

            fieldDelimField.setText(String.valueOf(cis.getFieldDelimiter()));

            firstRowHeadersCheck.setSelected(cis.isFirstRowHasHeaders());

            if (cis.getImportLocale() != null) {
                l10nComboModel.setSelectedItem(cis.getImportLocale());
            }

            multValueSeparatorField.setText(String.valueOf(cis.getMultipeValuesSeparator()));
            
            multipleValuesInTrnsCheck.setSelected(cis.isMultipleValuesOnTranslation());

            if (cis.getOnExistingTerms() != null) {
                onExistingTermsCombo.setSelectedItem(cis.getOnExistingTerms());
            }

            firstLineToImportField.setValue(cis.getSkippedLines() + 1);

            textDelimField.setText(String.valueOf(cis.getTextDelimiter()));
        }
    }
    
    public void retrieveSavedCSVImportFieldBindings() {
        CSVImportSettings cis;
        cis = CSVImporterWorker.CSVImportSettings.recreateFromXML("savedCis.xml");
        if (cis != null) {
            // TODO Check why this is not getting retrieved
            origTermCombo.setSelectedIndex(cis.getOrigTermColumn() + 1);
            origTermCommentCombo.setSelectedIndex(cis.getOrigTermCommentColumn() + 1);
            origTermCreationDateCombo.setSelectedIndex(cis.getOrigTermCreationDateColumn() + 1);
            origTermLastUpdateCombo.setSelectedIndex(cis.getOrigTermLastUpdateColumn() + 1);
            origTermPoSCombo.setSelectedIndex(cis.getOrigTermPoSColumn() + 1);
            trnsCommentCombo.setSelectedIndex(cis.getTrnsCommentColumn() + 1);
            trnsCreationDateCombo.setSelectedIndex(cis.getTrnsCreationDateColumn() + 1);
            trnsLastUpdateCombo.setSelectedIndex(cis.getTrnsLastUpdateColumn() + 1);
            trnsLocaleCombo.setSelectedItem(cis.getImportLocale());
            trnsValueCombo.setSelectedIndex(cis.getTrnsValueColumn() + 1);
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
        l10nComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        glosComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<Glossary>();
        tabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        filenameLabel = new javax.swing.JLabel();
        filenamePathField = new net.localizethat.util.gui.JPathField();
        importToGlossaryLabel = new javax.swing.JLabel();
        importToGlossaryCombo = new javax.swing.JComboBox<Glossary>();
        tipImport = new javax.swing.JLabel();
        descrip1Label = new javax.swing.JLabel();
        testRunCheck = new javax.swing.JCheckBox();
        testTipLabel = new javax.swing.JLabel();
        fileFormatPanel = new javax.swing.JPanel();
        charsetLabel = new javax.swing.JLabel();
        charsetCombo = new javax.swing.JComboBox<Charset>();
        fieldDelimLabel = new javax.swing.JLabel();
        fieldDelimField = new javax.swing.JTextField();
        textDelimLabel = new javax.swing.JLabel();
        textDelimField = new javax.swing.JTextField();
        firstLineToImportLabel = new javax.swing.JLabel();
        firstLineToImportField = new javax.swing.JSpinner();
        allQuotedCheck = new javax.swing.JCheckBox();
        firstRowHeadersCheck = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        sampleFileContentTextArea = new javax.swing.JTextArea();
        descrip2Label = new javax.swing.JLabel();
        ofFileLinesLabel = new javax.swing.JLabel();
        importDetailsPanel = new javax.swing.JPanel();
        onExistingTermsLabel = new javax.swing.JLabel();
        onExistingTermsCombo = new javax.swing.JComboBox<OnExistingTermAction>();
        multipleValuesInTrnsCheck = new javax.swing.JCheckBox();
        multValueSeparatorField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        FieldAssignmentPanel = new javax.swing.JPanel();
        origTermLabel = new javax.swing.JLabel();
        origTermCombo = new javax.swing.JComboBox<String>();
        origTermCommentLabel = new javax.swing.JLabel();
        origTermCommentCombo = new javax.swing.JComboBox<String>();
        origTermCreationDateLabel = new javax.swing.JLabel();
        origTermCreationDateCombo = new javax.swing.JComboBox<String>();
        origTermLastUpdateLabel = new javax.swing.JLabel();
        origTermLastUpdateCombo = new javax.swing.JComboBox<String>();
        origTermPoSLabel = new javax.swing.JLabel();
        origTermPoSCombo = new javax.swing.JComboBox<String>();
        trnsValueLabel = new javax.swing.JLabel();
        trnsValueCombo = new javax.swing.JComboBox<String>();
        trnsCommentLabel = new javax.swing.JLabel();
        trnsCommentCombo = new javax.swing.JComboBox<String>();
        trnsCreationDateLabel = new javax.swing.JLabel();
        trnsCreationDateCombo = new javax.swing.JComboBox<String>();
        trnsLastUpdateLabel = new javax.swing.JLabel();
        trnsLastUpdateCombo = new javax.swing.JComboBox<String>();
        trnsLocaleLabel = new javax.swing.JLabel();
        trnsLocaleCombo = new javax.swing.JComboBox<L10n>();
        jLabel1 = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        generalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        filenameLabel.setDisplayedMnemonic('E');
        filenameLabel.setLabelFor(filenamePathField);
        filenameLabel.setText("Enter file name:");

        filenamePathField.setMinimumSize(new java.awt.Dimension(19, 20));
        filenamePathField.setPreferredSize(new java.awt.Dimension(90, 20));
        filenamePathField.setText("glossary.csv");

        importToGlossaryLabel.setDisplayedMnemonic('I');
        importToGlossaryLabel.setLabelFor(importToGlossaryCombo);
        importToGlossaryLabel.setText("Import to glossary:");

        importToGlossaryCombo.setModel(glosComboModel);

        tipImport.setText("Tip: to import in a new glossary, create it first");

        descrip1Label.setText("<html>Choose a CSV file, select a glossary where to import it and click Next to read it.<br>\nAfter that, you can switch to the CSV format details tab to see the first lines of the file and<br>\nprovide the asked values in that tab. Click Next again to parse the file as a CSV file and<br>\nset the glossary fields assignments to different CSV columns.\n</html>");

        testRunCheck.setMnemonic('R');
        testRunCheck.setText("Run in test mode");

        testTipLabel.setText("<html>If you run in test mode, all but actually adding entries and translations will be performed<br>\n(checking file for syntax, reading their first lines, allowing to set import details and actually<br>\nreading the file to check for existing and duplicated entries)</html>");

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(importToGlossaryLabel)
                        .addGap(3, 3, 3)
                        .addComponent(importToGlossaryCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(filenameLabel)
                        .addGap(26, 26, 26)
                        .addComponent(filenamePathField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(testRunCheck)
                            .addComponent(descrip1Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tipImport)
                            .addComponent(testTipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(filenamePathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filenameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importToGlossaryCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(importToGlossaryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testRunCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tipImport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(descrip1Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testTipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
        );

        tabbedPane.addTab("General values", generalPanel);

        fileFormatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        fileFormatPanel.setEnabled(false);

        charsetLabel.setLabelFor(charsetCombo);
        charsetLabel.setText("Charset code:");

        fieldDelimLabel.setLabelFor(fieldDelimField);
        fieldDelimLabel.setText("Field Delimiter:");

        fieldDelimField.setColumns(1);
        fieldDelimField.setText(",");

        textDelimLabel.setLabelFor(textDelimField);
        textDelimLabel.setText("Text Delimiter:");

        textDelimField.setColumns(1);
        textDelimField.setText("'");

        firstLineToImportLabel.setLabelFor(firstLineToImportField);
        firstLineToImportLabel.setText("Import from row:");

        firstLineToImportField.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        firstLineToImportField.setVerifyInputWhenFocusTarget(false);

        allQuotedCheck.setText("All fields are quoted");

        firstRowHeadersCheck.setText("First imported row contains headers");

        sampleFileContentTextArea.setColumns(20);
        sampleFileContentTextArea.setRows(5);
        jScrollPane2.setViewportView(sampleFileContentTextArea);

        descrip2Label.setText("<html>Check the first lines of the file and complete charset codification, delimiter details, etc.<br>\nThen click Next again to parse the file as a CSV file and set the glossary fields assignments<br>\nto different CSV columns. </html>");

        ofFileLinesLabel.setText("(of 0 lines)");

        javax.swing.GroupLayout fileFormatPanelLayout = new javax.swing.GroupLayout(fileFormatPanel);
        fileFormatPanel.setLayout(fileFormatPanelLayout);
        fileFormatPanelLayout.setHorizontalGroup(
            fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileFormatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(fileFormatPanelLayout.createSequentialGroup()
                        .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(charsetLabel)
                            .addComponent(fieldDelimLabel)
                            .addComponent(textDelimLabel))
                        .addGap(23, 23, 23)
                        .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(charsetCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(fileFormatPanelLayout.createSequentialGroup()
                                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fieldDelimField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(textDelimField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileFormatPanelLayout.createSequentialGroup()
                        .addComponent(firstLineToImportLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firstLineToImportField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ofFileLinesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(allQuotedCheck)
                            .addComponent(firstRowHeadersCheck)))
                    .addGroup(fileFormatPanelLayout.createSequentialGroup()
                        .addComponent(descrip2Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 62, Short.MAX_VALUE)))
                .addContainerGap())
        );
        fileFormatPanelLayout.setVerticalGroup(
            fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileFormatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(charsetCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(charsetLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldDelimField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldDelimLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(allQuotedCheck)
                    .addComponent(textDelimLabel)
                    .addComponent(textDelimField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileFormatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstRowHeadersCheck)
                    .addComponent(firstLineToImportField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstLineToImportLabel)
                    .addComponent(ofFileLinesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descrip2Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        tabbedPane.addTab("CSV format details", fileFormatPanel);

        importDetailsPanel.setEnabled(false);

        onExistingTermsLabel.setLabelFor(onExistingTermsCombo);
        onExistingTermsLabel.setText("On existing terms:");

        multipleValuesInTrnsCheck.setText("Trns. column contains multiple values separated by:");
        multipleValuesInTrnsCheck.addActionListener(formListener);
        multipleValuesInTrnsCheck.addFocusListener(formListener);

        multValueSeparatorField.setColumns(1);
        multValueSeparatorField.setText(",");
        multValueSeparatorField.setEnabled(false);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        origTermLabel.setForeground(new java.awt.Color(153, 0, 0));
        origTermLabel.setLabelFor(origTermCombo);
        origTermLabel.setText("Original term:");

        origTermCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        origTermCommentLabel.setLabelFor(origTermCommentLabel);
        origTermCommentLabel.setText("Original term comment:");

        origTermCommentCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        origTermCreationDateLabel.setLabelFor(origTermCreationDateCombo);
        origTermCreationDateLabel.setText("Orig. term creation date:");

        origTermCreationDateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        origTermLastUpdateLabel.setLabelFor(origTermLastUpdateCombo);
        origTermLastUpdateLabel.setText("Orig. term last update:");

        origTermLastUpdateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        origTermPoSLabel.setLabelFor(origTermPoSCombo);
        origTermPoSLabel.setText("Orig. term part of speech:");

        origTermPoSCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        trnsValueLabel.setForeground(new java.awt.Color(153, 0, 0));
        trnsValueLabel.setLabelFor(trnsValueCombo);
        trnsValueLabel.setText("Translation value:");

        trnsValueCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        trnsCommentLabel.setLabelFor(trnsCommentCombo);
        trnsCommentLabel.setText("Translation comment:");

        trnsCommentCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        trnsCreationDateLabel.setLabelFor(trnsCreationDateCombo);
        trnsCreationDateLabel.setText("Translation creation date:");

        trnsCreationDateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        trnsLastUpdateLabel.setLabelFor(trnsLastUpdateCombo);
        trnsLastUpdateLabel.setText("Translation last update:");

        trnsLastUpdateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        trnsLocaleLabel.setForeground(new java.awt.Color(153, 0, 0));
        trnsLocaleLabel.setLabelFor(trnsLocaleCombo);
        trnsLocaleLabel.setText("Translation locale:");

        trnsLocaleCombo.setModel(l10nComboModel);

        jLabel1.setText("Fields labeled in red are the minimum you should specify to be able to import anything");

        javax.swing.GroupLayout FieldAssignmentPanelLayout = new javax.swing.GroupLayout(FieldAssignmentPanel);
        FieldAssignmentPanel.setLayout(FieldAssignmentPanelLayout);
        FieldAssignmentPanelLayout.setHorizontalGroup(
            FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FieldAssignmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FieldAssignmentPanelLayout.createSequentialGroup()
                        .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(origTermLabel)
                            .addComponent(origTermCommentLabel)
                            .addComponent(origTermCreationDateLabel)
                            .addComponent(origTermLastUpdateLabel)
                            .addComponent(origTermPoSLabel)
                            .addComponent(trnsValueLabel)
                            .addComponent(trnsCommentLabel)
                            .addComponent(trnsCreationDateLabel)
                            .addComponent(trnsLastUpdateLabel)
                            .addComponent(trnsLocaleLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(trnsLastUpdateCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 486, Short.MAX_VALUE)
                            .addComponent(trnsCommentCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trnsCreationDateCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trnsValueCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(origTermPoSCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(origTermLastUpdateCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(origTermCreationDateCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(origTermCommentCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(origTermCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trnsLocaleCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(FieldAssignmentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        FieldAssignmentPanelLayout.setVerticalGroup(
            FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FieldAssignmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origTermCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(origTermLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origTermCommentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(origTermCommentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origTermCreationDateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(origTermCreationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origTermLastUpdateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(origTermLastUpdateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origTermPoSCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(origTermPoSLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsValueCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trnsValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsCommentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trnsCommentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsCreationDateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trnsCreationDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsLastUpdateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trnsLastUpdateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldAssignmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsLocaleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trnsLocaleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(FieldAssignmentPanel);

        javax.swing.GroupLayout importDetailsPanelLayout = new javax.swing.GroupLayout(importDetailsPanel);
        importDetailsPanel.setLayout(importDetailsPanelLayout);
        importDetailsPanelLayout.setHorizontalGroup(
            importDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(importDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(importDetailsPanelLayout.createSequentialGroup()
                        .addComponent(onExistingTermsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(onExistingTermsCombo, 0, 1, Short.MAX_VALUE))
                    .addGroup(importDetailsPanelLayout.createSequentialGroup()
                        .addComponent(multipleValuesInTrnsCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(multValueSeparatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        importDetailsPanelLayout.setVerticalGroup(
            importDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, importDetailsPanelLayout.createSequentialGroup()
                .addGroup(importDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(onExistingTermsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(onExistingTermsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(importDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multipleValuesInTrnsCheck)
                    .addComponent(multValueSeparatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        tabbedPane.addTab("Import Details", importDetailsPanel);

        okButton.setText("Next");
        okButton.setMaximumSize(new java.awt.Dimension(81, 25));
        okButton.setMinimumSize(new java.awt.Dimension(81, 25));
        okButton.setPreferredSize(new java.awt.Dimension(81, 25));
        okButton.addActionListener(formListener);
        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(formListener);
        buttonPanel.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.FocusListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == multipleValuesInTrnsCheck) {
                ImportCSVGlossaryDialog.this.multipleValuesInTrnsCheckActionPerformed(evt);
            }
            else if (evt.getSource() == okButton) {
                ImportCSVGlossaryDialog.this.okButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cancelButton) {
                ImportCSVGlossaryDialog.this.cancelButtonActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == multipleValuesInTrnsCheck) {
                ImportCSVGlossaryDialog.this.multipleValuesInTrnsCheckFocusLost(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void multipleValuesInTrnsCheckFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_multipleValuesInTrnsCheckFocusLost
        multValueSeparatorField.setEnabled(multipleValuesInTrnsCheck.isSelected());
    }//GEN-LAST:event_multipleValuesInTrnsCheckFocusLost

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // If the currently selected tab is the first one,
        if (tabbedPane.getSelectedIndex() == 0) {
            // Launch the file reader worker, enable the second tab and activate it
            FileHeadReaderWorker fhrw = new FileHeadReaderWorker(filenamePathField.getSelectedFile(),
                    sampleFileContentTextArea);
            FileLinesCounterWorker flcw = new FileLinesCounterWorker(filenamePathField.getSelectedFile(),
                    ofFileLinesLabel);
            fhrw.execute();
            flcw.execute();
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
            try {
                fileLines = flcw.get(2, TimeUnit.SECONDS);
                ((SpinnerNumberModel) firstLineToImportField.getModel()).setMaximum(fileLines);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                fileLines = -1;
            }
        } else if (tabbedPane.getSelectedIndex() == 1) {
            CSV csvSettings = CSV
                    .separator(fieldDelimField.getText().charAt(0))
                    .quote(textDelimField.getText().charAt(0))
                    .charset((Charset) charsetCombo.getSelectedItem())
                    .skipLines((Integer) firstLineToImportField.getValue() - 1) // UI control counts from 1, file rows counts from 0
                    .create();
            CSVHeaderReaderWorker crw = new CSVHeaderReaderWorker(filenamePathField.getSelectedFile(),
                    csvSettings, headerModelList, firstRowHeadersCheck.isSelected(), this);
            okButton.setText("OK");
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);

            if (multipleValuesInTrnsCheck.isSelected()) {
                multValueSeparatorField.setEnabled(true);
            }
            crw.execute();
        } else {
            CSVImportSettings cis = new CSVImportSettings();
            cis.setFileToImport(filenamePathField.getSelectedFile())
                    .setTestMode(testRunCheck.isSelected())
                    .setGlossary(glosComboModel.getElementAt(importToGlossaryCombo.getSelectedIndex()))
                    .setAllFieldsQuoted(allQuotedCheck.isSelected())
                    .setCharset((Charset) charsetCombo.getSelectedItem())
                    .setFieldDelimiter(fieldDelimField.getText().charAt(0))
                    .setFileLines(fileLines)
                    .setFirstRowHasHeaders(firstRowHeadersCheck.isSelected())
                    .setImportLocale((L10n) l10nComboModel.getSelectedItem())
                    .setMultipeValuesSeparator(multValueSeparatorField.getText().charAt(0))
                    .setMultipleValuesOnTranslation(multipleValuesInTrnsCheck.isSelected())
                    .setOnExistingTerms((OnExistingTermAction) onExistingTermsCombo.getSelectedItem())
                    .setOrigTermColumn(origTermCombo.getSelectedIndex() - 1)
                    .setOrigTermCommentColumn(origTermCommentCombo.getSelectedIndex() - 1)
                    .setOrigTermCreationDateColumn(origTermCreationDateCombo.getSelectedIndex() - 1)
                    .setOrigTermLastUpdateColumn(origTermLastUpdateCombo.getSelectedIndex() - 1)
                    .setOrigTermPoSColumn(origTermPoSCombo.getSelectedIndex() - 1)
                    .setSkippedLines((Integer) firstLineToImportField.getValue() - 1)
                    .setTextDelimiter(textDelimField.getText().charAt(0))
                    .setTrnsCommentColumn(trnsCommentCombo.getSelectedIndex() - 1)
                    .setTrnsCreationDateColumn(trnsCreationDateCombo.getSelectedIndex() - 1)
                    .setTrnsLastUpdateColumn(trnsLastUpdateCombo.getSelectedIndex() - 1)
                    .setTrnsValueColumn(trnsValueCombo.getSelectedIndex() - 1);
            cis.serializeToXML("savedCis.xml");

            CSVImporterWorker ciw = new CSVImporterWorker(cis, statusBar);
            ProgressBarListener pbl = new ProgressBarListener(statusBar, "progress");
            ciw.addPropertyChangeListener(pbl);

            okButton.setText("OK");
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);
            ciw.execute();
            md.setModalDialogResult(true);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void multipleValuesInTrnsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multipleValuesInTrnsCheckActionPerformed
        multValueSeparatorField.setEnabled(multipleValuesInTrnsCheck.isSelected());
    }//GEN-LAST:event_multipleValuesInTrnsCheckActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        md.setModalDialogResult(false);
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FieldAssignmentPanel;
    private javax.swing.JCheckBox allQuotedCheck;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<Charset> charsetCombo;
    private javax.swing.JLabel charsetLabel;
    private javax.swing.JLabel descrip1Label;
    private javax.swing.JLabel descrip2Label;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JTextField fieldDelimField;
    private javax.swing.JLabel fieldDelimLabel;
    private javax.swing.JPanel fileFormatPanel;
    private javax.swing.JLabel filenameLabel;
    private net.localizethat.util.gui.JPathField filenamePathField;
    private javax.swing.JSpinner firstLineToImportField;
    private javax.swing.JLabel firstLineToImportLabel;
    private javax.swing.JCheckBox firstRowHeadersCheck;
    private javax.swing.JPanel generalPanel;
    private net.localizethat.gui.models.ListComboBoxGenericModel<Glossary> glosComboModel;
    private javax.swing.JPanel importDetailsPanel;
    private javax.swing.JComboBox<Glossary> importToGlossaryCombo;
    private javax.swing.JLabel importToGlossaryLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> l10nComboModel;
    private javax.swing.JTextField multValueSeparatorField;
    private javax.swing.JCheckBox multipleValuesInTrnsCheck;
    private javax.swing.JLabel ofFileLinesLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<OnExistingTermAction> onExistingTermsCombo;
    private javax.swing.JLabel onExistingTermsLabel;
    private javax.swing.JComboBox<String> origTermCombo;
    private javax.swing.JComboBox<String> origTermCommentCombo;
    private javax.swing.JLabel origTermCommentLabel;
    private javax.swing.JComboBox<String> origTermCreationDateCombo;
    private javax.swing.JLabel origTermCreationDateLabel;
    private javax.swing.JLabel origTermLabel;
    private javax.swing.JComboBox<String> origTermLastUpdateCombo;
    private javax.swing.JLabel origTermLastUpdateLabel;
    private javax.swing.JComboBox<String> origTermPoSCombo;
    private javax.swing.JLabel origTermPoSLabel;
    private javax.swing.JTextArea sampleFileContentTextArea;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JCheckBox testRunCheck;
    private javax.swing.JLabel testTipLabel;
    private javax.swing.JTextField textDelimField;
    private javax.swing.JLabel textDelimLabel;
    private javax.swing.JLabel tipImport;
    private javax.swing.JComboBox<String> trnsCommentCombo;
    private javax.swing.JLabel trnsCommentLabel;
    private javax.swing.JComboBox<String> trnsCreationDateCombo;
    private javax.swing.JLabel trnsCreationDateLabel;
    private javax.swing.JComboBox<String> trnsLastUpdateCombo;
    private javax.swing.JLabel trnsLastUpdateLabel;
    private javax.swing.JComboBox<L10n> trnsLocaleCombo;
    private javax.swing.JLabel trnsLocaleLabel;
    private javax.swing.JComboBox<String> trnsValueCombo;
    private javax.swing.JLabel trnsValueLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setModalDialogReference(ModalDialog md) {
        this.md = md;
    }
}
