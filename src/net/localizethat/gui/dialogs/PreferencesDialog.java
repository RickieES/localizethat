/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.gui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.localizethat.Main;
import net.localizethat.gui.models.ListComboBoxGenericModel;
import net.localizethat.model.L10n;
import net.localizethat.system.AppSettings;
import net.localizethat.util.gui.GuiUtils;
import net.localizethat.util.gui.SimpleFontChooser;

/**
 * The Preferences dialog for LocalizeThat!
 * @author rpalomares
 */
public class PreferencesDialog extends javax.swing.JDialog {
    private final EntityManagerFactory emf;
    private boolean okay;
    private final List<String> availableLafs;
  
    /**
     * Creates new form SetupDialog
     */
    public PreferencesDialog() {
        super(Main.mainWindow, "LocalizeThat! preferences", true);
        emf = Main.emf;
        // The following code is executed inside initComponents()
        // entityManager = emf.createEntityManager();

        availableLafs = GuiUtils.getAvailableLookAndFeels();
        initComponents();
        // JDialogHelper.setupOKCancelHotkeys(this, okButton, cancelButton);
    }

    private void refreshL10nList(ListComboBoxGenericModel<L10n> listModel) {
        TypedQuery<L10n> l10nQuery = entityManager.createNamedQuery("L10n.findAll",
                L10n.class);
        listModel.clearAll();
        listModel.addAll(l10nQuery.getResultList());
    }

/**
 * Shows the dialog (in a modal way)
 */
public void showDialog() {
        L10n defL10n = null;
        AppSettings appSettings = Main.appSettings;
        okay = false;

        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = emf.createEntityManager();
        }
        refreshL10nList(defaultLanguageComboModel);

        // Display Tab
        Font f;
        f = new Font(appSettings.getString(AppSettings.PREF_FONT_EDITPHRASE_NAME),
                     appSettings.getInteger(AppSettings.PREF_FONT_EDITPHRASE_STYLE),
                     appSettings.getInteger(AppSettings.PREF_FONT_EDITPHRASE_SIZE));
        editPhraseFontLabel.setFont(f);
        f = new Font(appSettings.getString(AppSettings.PREF_FONT_TABLEVIEW_NAME),
                     appSettings.getInteger(AppSettings.PREF_FONT_TABLEVIEW_STYLE),
                     appSettings.getInteger(AppSettings.PREF_FONT_TABLEVIEW_SIZE));
        tableViewFontLabel.setFont(f);
        editPhraseFontLabel.repaint();
        tableViewFontLabel.repaint();
        lookAndFeelCombo.setSelectedItem(appSettings.getString(AppSettings.PREF_GUI_LOOK_AND_FEEL));

        tsclUntranslated.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_UNTRANSLATED));
        tsclModified.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_MODIFIED));
        tsclApproximated.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_APPROXIMATED));
        tsclProposed.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_PROPOSED));
        tsclCopied.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_COPIED));
        tsclTranslated.setBackground(appSettings.getColor(AppSettings.PREF_TRNS_STATUS_TRANSLATED));

        String defL10nCode = appSettings.getString(AppSettings.PREF_DEFAULT_ORIGINAL_LANGUAGE);
        for(L10n l : defaultLanguageComboModel.getAll()) {
            if (l.getCode().equals(defL10nCode)) {
                defL10n = l;
                break;
            }
        }
        if (defL10n != null) {
            defaultLanguageCombo.setSelectedItem(defL10n);
        }

        /*
        // Input/Output Tab
        replaceEnUSCheck.setSelected(appSettings.getBoolean(AppSettings.EXPORT_REPLACE_ENUS));
        exportOnlyModifFilesCheck.setSelected(appSettings.getBoolean(AppSettings.EXPORT_ONLY_MODIFIED));
        emptyTrnsAsOriginalCheck.setSelected(appSettings.getBoolean(AppSettings.EXPORT_ENUS_VALUE_ON_EMPTY_TRANSLATIONS));
        baseDirForReposField.setText(appSettings.getString(AppSettings.REPOSITORIES_BASE));

        // Translation Assistance Tab
        useSuggCheckBox.setSelected(appSettings.getBoolean(AppSettings.USE_SUGGESTIONS));
        percentCoincidenceTextField.setValue(appSettings.getInteger(AppSettings.SUGGESTIONS_MATCH_VALUE));
        autoTranslateCheck.setSelected(appSettings.getBoolean(AppSettings.AUTOTRANSLATE_ON_UPDATE));

        // Key Connection Tab
        akeyCaseCheck.setSelected(appSettings.getBoolean(AppSettings.CONN_AKEYS_CASESENSE));
        akeyPatternField.setText(appSettings.getString(AppSettings.CONN_AKEYS_PATTERNS));
        ckeyCaseCheck.setSelected(appSettings.getBoolean(AppSettings.CONN_CKEYS_CASESENSE));
        ckeyPatternField.setText(appSettings.getString(AppSettings.CONN_CKEYS_PATTERNS));
        labelCaseCheck.setSelected(appSettings.getBoolean(AppSettings.CONN_LABEL_CASESENSE));
        labelPatternField.setText(appSettings.getString(AppSettings.CONN_LABEL_PATTERNS));

        // Automated Tests tab
        origDTDEntField.setText(appSettings.getString(AppSettings.QA_DTD_ORIG_ENTITIES_IGNORED));
        trnsDTDEntField.setText(appSettings.getString(AppSettings.QA_DTD_TRNS_ENTITIES_IGNORED));
        endingCheckedCharsField.setText(appSettings.getString(AppSettings.QA_ENDING_CHECKED_CHARS));
        useSuggCheckBox.setSelected(appSettings.getBoolean(AppSettings.USE_SUGGESTIONS));
        pairedCharsListField.setText(appSettings.getString(AppSettings.QA_PAIRED_CHARS_LIST));

        // Data Store tab
        pathGlossaryTextField.setText(appSettings.getString(AppSettings.DATAMODEL_FILENAME));
        useOneFilePerProductCheck.setSelected(appSettings.getBoolean(AppSettings.DATAMODEL_ONE_FILE_PER_PRODUCT));
        */

        setVisible(true);

        if (okay) {
            // Set the new parameters

            // General tab
            f = editPhraseFontLabel.getFont();
            appSettings.setString(AppSettings.PREF_FONT_EDITPHRASE_NAME, f.getFontName());
            appSettings.setInteger(AppSettings.PREF_FONT_EDITPHRASE_SIZE, f.getSize());
            appSettings.setInteger(AppSettings.PREF_FONT_EDITPHRASE_STYLE, f.getStyle());
            f = tableViewFontLabel.getFont();
            appSettings.setString(AppSettings.PREF_FONT_TABLEVIEW_NAME, f.getFontName());
            appSettings.setInteger(AppSettings.PREF_FONT_TABLEVIEW_SIZE, f.getSize());
            appSettings.setInteger(AppSettings.PREF_FONT_TABLEVIEW_STYLE, f.getStyle());
            appSettings.setString(AppSettings.PREF_GUI_LOOK_AND_FEEL, lookAndFeelCombo.getSelectedItem().toString());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_UNTRANSLATED, tsclUntranslated.getBackground());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_MODIFIED, tsclModified.getBackground());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_APPROXIMATED, tsclApproximated.getBackground());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_PROPOSED, tsclProposed.getBackground());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_COPIED, tsclCopied.getBackground());
            appSettings.setColor(AppSettings.PREF_TRNS_STATUS_TRANSLATED, tsclTranslated.getBackground());

            defL10n = defaultLanguageComboModel.getSelectedTypedItem();
            appSettings.setString(AppSettings.PREF_DEFAULT_ORIGINAL_LANGUAGE, defL10n.getCode());

            /*
            // Input/Output Tab
            appSettings.setBoolean(AppSettings.EXPORT_REPLACE_ENUS, replaceEnUSCheck.isSelected());
            appSettings.setBoolean(AppSettings.EXPORT_ONLY_MODIFIED, exportOnlyModifFilesCheck.isSelected());
            appSettings.setBoolean(AppSettings.DATAMODEL_ONE_FILE_PER_PRODUCT, useOneFilePerProductCheck.isSelected());
            appSettings.setString(AppSettings.REPOSITORIES_BASE, baseDirForReposField.getText());

            // Translation Assistance Tab
            appSettings.setBoolean(AppSettings.USE_SUGGESTIONS, useSuggCheckBox.isSelected());
            appSettings.setInteger(AppSettings.SUGGESTIONS_MATCH_VALUE, (Integer) percentCoincidenceTextField.getValue());
            appSettings.setBoolean(AppSettings.AUTOTRANSLATE_ON_UPDATE, autoTranslateCheck.isSelected());
            
            // Key Connection Tab
            appSettings.setBoolean(AppSettings.CONN_AKEYS_CASESENSE, akeyCaseCheck.isSelected());
            appSettings.setString(AppSettings.CONN_AKEYS_PATTERNS, akeyPatternField.getText());
            appSettings.setBoolean(AppSettings.CONN_CKEYS_CASESENSE, ckeyCaseCheck.isSelected());
            appSettings.setString(AppSettings.CONN_CKEYS_PATTERNS, ckeyPatternField.getText());
            appSettings.setBoolean(AppSettings.CONN_LABEL_CASESENSE, labelCaseCheck.isSelected());
            appSettings.setString(AppSettings.CONN_LABEL_PATTERNS, labelPatternField.getText());

            // Automated tests tab
            appSettings.setString(AppSettings.QA_DTD_ORIG_ENTITIES_IGNORED, origDTDEntField.getText());
            appSettings.setString(AppSettings.QA_DTD_TRNS_ENTITIES_IGNORED, trnsDTDEntField.getText());
            appSettings.setString(AppSettings.QA_ENDING_CHECKED_CHARS, endingCheckedCharsField.getText());
            appSettings.setBoolean(AppSettings.USE_SUGGESTIONS, useSuggCheckBox.isSelected());
            appSettings.setString(AppSettings.QA_PAIRED_CHARS_LIST, pairedCharsListField.getText());

            // Data Store tab
            appSettings.setString(AppSettings.DATAMODEL_FILENAME, pathGlossaryTextField.getText());
            appSettings.setBoolean(AppSettings.EXPORT_ENUS_VALUE_ON_EMPTY_TRANSLATIONS, emptyTrnsAsOriginalCheck.isSelected());
            */
        }
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        defaultLanguageComboModel = new net.localizethat.gui.models.ListComboBoxGenericModel<L10n>();
        entityManager = emf.createEntityManager();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        prefDisplayPanel = new javax.swing.JPanel();
        appearanceLabel = new javax.swing.JLabel();
        editPhraseFontLabel = new javax.swing.JLabel();
        editPhraseChooseButton = new javax.swing.JButton();
        tableViewFontLabel = new javax.swing.JLabel();
        tableViewChooseButton = new javax.swing.JButton();
        lookAndFeelLabel = new javax.swing.JLabel();
        lookAndFeelCombo = new javax.swing.JComboBox();
        tsColorsLabel = new javax.swing.JLabel();
        tsclUntranslated = new javax.swing.JLabel();
        tsclModified = new javax.swing.JLabel();
        tsclApproximated = new javax.swing.JLabel();
        tsclProposed = new javax.swing.JLabel();
        tsclCopied = new javax.swing.JLabel();
        tsclTranslated = new javax.swing.JLabel();
        generalLabel = new javax.swing.JLabel();
        defaultLanguageLabel = new javax.swing.JLabel();
        defaultLanguageCombo = new javax.swing.JComboBox<L10n>();
        prefIOPanel = new javax.swing.JPanel();
        importExportLabel = new javax.swing.JLabel();
        replaceEnUSCheck = new javax.swing.JCheckBox();
        exportOnlyModifFilesCheck = new javax.swing.JCheckBox();
        emptyTrnsAsOriginalCheck = new javax.swing.JCheckBox();
        baseDirForReposLabel = new javax.swing.JLabel();
        baseDirForReposField = new javax.swing.JTextField();
        baseDirForReposButton = new javax.swing.JButton();
        jPathField1 = new net.localizethat.util.gui.JPathField();
        prefTrnsAssistPanel = new javax.swing.JPanel();
        translationSuggestionsLabel = new javax.swing.JLabel();
        useSuggCheckBox = new javax.swing.JCheckBox();
        provideSuggFor1Label = new javax.swing.JLabel();
        provideSuggFor2Label = new javax.swing.JLabel();
        autoTranslateCheck = new javax.swing.JCheckBox();
        percentCoincidenceTextField = new javax.swing.JSpinner();
        prefKeyConnPanel = new javax.swing.JPanel();
        keyConnLabel = new javax.swing.JLabel();
        labelPatternLabel = new javax.swing.JLabel();
        labelPatternField = new javax.swing.JTextField();
        labelCaseCheck = new javax.swing.JCheckBox();
        ckeyPatternLabel = new javax.swing.JLabel();
        ckeyPatternField = new javax.swing.JTextField();
        ckeyCaseCheck = new javax.swing.JCheckBox();
        akeyPatternLabel = new javax.swing.JLabel();
        akeyPatternField = new javax.swing.JTextField();
        akeyCaseCheck = new javax.swing.JCheckBox();
        suffixInfoLabel = new javax.swing.JLabel();
        prefAutoTestsPanel = new javax.swing.JPanel();
        descripLabel = new javax.swing.JLabel();
        ignoredOrigEntLabel = new javax.swing.JLabel();
        origDTDEntField = new javax.swing.JTextField();
        ignoredTrnsEntLabel = new javax.swing.JLabel();
        trnsDTDEntField = new javax.swing.JTextField();
        endingCheckedCharsLabel = new javax.swing.JLabel();
        endingCheckedCharsField = new javax.swing.JTextField();
        pairedCharsListLabel = new javax.swing.JLabel();
        pairedCharsListField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        prefDataStorePanel = new javax.swing.JPanel();
        locationAndStructureLabel = new javax.swing.JLabel();
        pathGlossaryLabel = new javax.swing.JLabel();
        pathGlossaryTextField = new javax.swing.JTextField();
        pathGlossaryButton = new javax.swing.JButton();
        useOneFilePerProductCheck = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(54, 27));
        okButton.setMinimumSize(new java.awt.Dimension(54, 27));
        okButton.setPreferredSize(new java.awt.Dimension(54, 27));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonPressed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonPressed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        prefDisplayPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        appearanceLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        appearanceLabel.setText("Appearance");

        editPhraseFontLabel.setText("Font for locale content fields");

        editPhraseChooseButton.setMnemonic('C');
        editPhraseChooseButton.setText("Choose...");
        editPhraseChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPhraseChooseButtonActionPerformed(evt);
            }
        });

        tableViewFontLabel.setText("Font for table views");

        tableViewChooseButton.setMnemonic('h');
        tableViewChooseButton.setText("Choose...");
        tableViewChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableViewChooseButtonActionPerformed(evt);
            }
        });

        lookAndFeelLabel.setDisplayedMnemonic('L');
        lookAndFeelLabel.setLabelFor(lookAndFeelCombo);
        lookAndFeelLabel.setText("Look And Feel");

        lookAndFeelCombo.setModel(new DefaultComboBoxModel(availableLafs.toArray()));
        lookAndFeelCombo.setToolTipText("Graphical appearance of the application");

        tsColorsLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        tsColorsLabel.setText("Translation Status Colors");

        tsclUntranslated.setBackground(java.awt.Color.red);
        tsclUntranslated.setText("Untranslated");
        tsclUntranslated.setToolTipText("Click to change its color");
        tsclUntranslated.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclUntranslated.setOpaque(true);
        tsclUntranslated.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        tsclModified.setBackground(java.awt.Color.pink);
        tsclModified.setText("Modified");
        tsclModified.setToolTipText("Click to change its color");
        tsclModified.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclModified.setOpaque(true);
        tsclModified.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        tsclApproximated.setBackground(java.awt.Color.orange);
        tsclApproximated.setText("Approximated");
        tsclApproximated.setToolTipText("Click to change its color");
        tsclApproximated.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclApproximated.setOpaque(true);
        tsclApproximated.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        tsclProposed.setBackground(java.awt.Color.yellow);
        tsclProposed.setText("Proposed");
        tsclProposed.setToolTipText("Click to change its color");
        tsclProposed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclProposed.setOpaque(true);
        tsclProposed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        tsclCopied.setBackground(java.awt.Color.green);
        tsclCopied.setText("Copied");
        tsclCopied.setToolTipText("Click to change its color");
        tsclCopied.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclCopied.setOpaque(true);
        tsclCopied.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        tsclTranslated.setBackground(java.awt.Color.lightGray);
        tsclTranslated.setText("Translated");
        tsclTranslated.setToolTipText("Click to change its color");
        tsclTranslated.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tsclTranslated.setOpaque(true);
        tsclTranslated.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tsclLabelMouseClicked(evt);
            }
        });

        generalLabel.setText("General");

        defaultLanguageLabel.setText("Default language");

        defaultLanguageCombo.setModel(defaultLanguageComboModel);
        defaultLanguageCombo.setToolTipText("Default language code for new products. Doesn't affect existing products");

        javax.swing.GroupLayout prefDisplayPanelLayout = new javax.swing.GroupLayout(prefDisplayPanel);
        prefDisplayPanel.setLayout(prefDisplayPanelLayout);
        prefDisplayPanelLayout.setHorizontalGroup(
            prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                        .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(appearanceLabel)
                            .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                            .addComponent(lookAndFeelLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lookAndFeelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                            .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(editPhraseFontLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(tableViewFontLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(editPhraseChooseButton)
                                                .addComponent(tableViewChooseButton))))
                                    .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                        .addComponent(defaultLanguageLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(defaultLanguageCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(18, 18, 18)
                        .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tsclApproximated, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(tsclUntranslated, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                    .addComponent(tsclCopied, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tsclModified, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                    .addComponent(tsclProposed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tsclTranslated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                                .addComponent(tsColorsLabel)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                        .addComponent(generalLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        prefDisplayPanelLayout.setVerticalGroup(
            prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appearanceLabel)
                    .addComponent(tsColorsLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(prefDisplayPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tsclUntranslated)
                            .addComponent(tsclModified)))
                    .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(editPhraseFontLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editPhraseChooseButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableViewFontLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableViewChooseButton)
                    .addComponent(tsclApproximated)
                    .addComponent(tsclProposed))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tsclCopied)
                        .addComponent(tsclTranslated))
                    .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lookAndFeelLabel)
                        .addComponent(lookAndFeelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(generalLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(prefDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultLanguageLabel)
                    .addComponent(defaultLanguageCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/pref-display.png")), prefDisplayPanel, "Display"); // NOI18N

        importExportLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        importExportLabel.setText("Import/Export");

        replaceEnUSCheck.setMnemonic('R');
        replaceEnUSCheck.setText("Replace original directories name with target on exporting/writing");
        replaceEnUSCheck.setToolTipText("<html>Subdirectories named after the original language code (e.g.: en-US) <br/>\nwill be renamed to the target language code (e.g.: fr, es-ES, etc.).</html>");
        replaceEnUSCheck.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        exportOnlyModifFilesCheck.setMnemonic('E');
        exportOnlyModifFilesCheck.setText("Export only modified files");
        exportOnlyModifFilesCheck.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        emptyTrnsAsOriginalCheck.setMnemonic('x');
        emptyTrnsAsOriginalCheck.setText("Export empty translations as original value");
        emptyTrnsAsOriginalCheck.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        baseDirForReposLabel.setDisplayedMnemonic('B');
        baseDirForReposLabel.setLabelFor(baseDirForReposField);
        baseDirForReposLabel.setText("Base directory for repositories:");

        baseDirForReposButton.setMnemonic('o');
        baseDirForReposButton.setText("Choose...");
        baseDirForReposButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baseDirForReposButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout prefIOPanelLayout = new javax.swing.GroupLayout(prefIOPanel);
        prefIOPanel.setLayout(prefIOPanelLayout);
        prefIOPanelLayout.setHorizontalGroup(
            prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefIOPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(prefIOPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(exportOnlyModifFilesCheck, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(replaceEnUSCheck, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(emptyTrnsAsOriginalCheck))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prefIOPanelLayout.createSequentialGroup()
                        .addComponent(importExportLabel)
                        .addContainerGap(529, Short.MAX_VALUE))
                    .addGroup(prefIOPanelLayout.createSequentialGroup()
                        .addComponent(baseDirForReposLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(prefIOPanelLayout.createSequentialGroup()
                                .addComponent(jPathField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(prefIOPanelLayout.createSequentialGroup()
                                .addComponent(baseDirForReposField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(baseDirForReposButton)
                                .addGap(25, 25, 25))))))
        );
        prefIOPanelLayout.setVerticalGroup(
            prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prefIOPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importExportLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceEnUSCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportOnlyModifFilesCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emptyTrnsAsOriginalCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(prefIOPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baseDirForReposLabel)
                    .addComponent(baseDirForReposField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baseDirForReposButton))
                .addGap(18, 18, 18)
                .addComponent(jPathField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/pref-io.png")), prefIOPanel, "Input/Output"); // NOI18N

        translationSuggestionsLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        translationSuggestionsLabel.setText("Translation Suggestions");

        useSuggCheckBox.setMnemonic('U');
        useSuggCheckBox.setText("Use translation suggestions on Edit Phrase Dialog");
        useSuggCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useSuggCheckBoxItemStateChanged(evt);
            }
        });

        provideSuggFor1Label.setDisplayedMnemonic('P');
        provideSuggFor1Label.setLabelFor(percentCoincidenceTextField);
        provideSuggFor1Label.setText("Provide suggestions for ");

        provideSuggFor2Label.setText("% coincidence or more");

        autoTranslateCheck.setMnemonic('A');
        autoTranslateCheck.setText("Auto-translate on Product Update");

        percentCoincidenceTextField.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));

        javax.swing.GroupLayout prefTrnsAssistPanelLayout = new javax.swing.GroupLayout(prefTrnsAssistPanel);
        prefTrnsAssistPanel.setLayout(prefTrnsAssistPanelLayout);
        prefTrnsAssistPanelLayout.setHorizontalGroup(
            prefTrnsAssistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefTrnsAssistPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefTrnsAssistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(translationSuggestionsLabel)
                    .addGroup(prefTrnsAssistPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(prefTrnsAssistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(prefTrnsAssistPanelLayout.createSequentialGroup()
                                .addComponent(provideSuggFor1Label)
                                .addGap(4, 4, 4)
                                .addComponent(percentCoincidenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(provideSuggFor2Label))
                            .addComponent(useSuggCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(autoTranslateCheck))))
                .addContainerGap(214, Short.MAX_VALUE))
        );
        prefTrnsAssistPanelLayout.setVerticalGroup(
            prefTrnsAssistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefTrnsAssistPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(translationSuggestionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useSuggCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefTrnsAssistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(provideSuggFor1Label)
                    .addComponent(provideSuggFor2Label)
                    .addComponent(percentCoincidenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoTranslateCheck)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/pref-trns-asist.png")), prefTrnsAssistPanel, "Translation Assistance"); // NOI18N

        keyConnLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        keyConnLabel.setText("Key Connection");

        labelPatternLabel.setDisplayedMnemonic('L');
        labelPatternLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelPatternLabel.setLabelFor(labelPatternField);
        labelPatternLabel.setText("Label suffixes:");
        labelPatternLabel.setMaximumSize(new java.awt.Dimension(152, 17));
        labelPatternLabel.setMinimumSize(new java.awt.Dimension(152, 17));
        labelPatternLabel.setPreferredSize(new java.awt.Dimension(152, 17));

        labelPatternField.setText("Label patterns");

        labelCaseCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelCaseCheck.setText("C.S.");
        labelCaseCheck.setToolTipText("Check to make label suffixes case-sensitive.");
        labelCaseCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ckeyPatternLabel.setDisplayedMnemonic('C');
        ckeyPatternLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ckeyPatternLabel.setLabelFor(ckeyPatternField);
        ckeyPatternLabel.setText("Commandkeys suffixes:");

        ckeyPatternField.setText("Commandkeys patterns");

        ckeyCaseCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        ckeyCaseCheck.setText("C.S.");
        ckeyCaseCheck.setToolTipText("Check to make commandkeys suffixes case-sensitive.");
        ckeyCaseCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        akeyPatternLabel.setDisplayedMnemonic('A');
        akeyPatternLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        akeyPatternLabel.setLabelFor(akeyPatternField);
        akeyPatternLabel.setText("Accesskeys suffixes:");
        akeyPatternLabel.setMaximumSize(new java.awt.Dimension(152, 17));
        akeyPatternLabel.setMinimumSize(new java.awt.Dimension(152, 17));
        akeyPatternLabel.setPreferredSize(new java.awt.Dimension(152, 17));

        akeyPatternField.setText("Accesskeys patterns");

        akeyCaseCheck.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        akeyCaseCheck.setText("C.S.");
        akeyCaseCheck.setToolTipText("Check to make accesskeys suffixes case-sensitive.");
        akeyCaseCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        suffixInfoLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        suffixInfoLabel.setText("<html>\n<p><b>Tip</b>: enter suffixes used for labels (incl. buttons and menu options), accesskeys and commandkeys.</p>\n<ul>\n<li>Separate multiples suffixes with | (vertical pipe).</li>\n<li>Include the empty suffix (useful for labels, don't use for others!) using [:empty:].</li>\n<li>NO leading dot is automatically prepended.</li>\n</ul>\n<p>Example: |.label|.button|.nameCmd||</p>\n</html>\n");

        javax.swing.GroupLayout prefKeyConnPanelLayout = new javax.swing.GroupLayout(prefKeyConnPanel);
        prefKeyConnPanel.setLayout(prefKeyConnPanelLayout);
        prefKeyConnPanelLayout.setHorizontalGroup(
            prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(suffixInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                        .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(keyConnLabel)
                            .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(labelPatternLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(akeyPatternLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(ckeyPatternLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ckeyPatternField, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                                    .addComponent(labelPatternField)
                                    .addComponent(akeyPatternField))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ckeyCaseCheck)
                            .addComponent(akeyCaseCheck)
                            .addComponent(labelCaseCheck))
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        prefKeyConnPanelLayout.setVerticalGroup(
            prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefKeyConnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(keyConnLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPatternField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPatternLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCaseCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ckeyPatternField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ckeyPatternLabel)
                    .addComponent(ckeyCaseCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefKeyConnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(akeyPatternField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(akeyPatternLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(akeyCaseCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(suffixInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/autoassign-ak.png")), prefKeyConnPanel, "Key Connections"); // NOI18N

        descripLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        descripLabel.setText("DTD entities ignored in Check Variables (example: &one;|&two;|&three;)");

        ignoredOrigEntLabel.setDisplayedMnemonic('O');
        ignoredOrigEntLabel.setText("Original:");

        ignoredTrnsEntLabel.setDisplayedMnemonic('T');
        ignoredTrnsEntLabel.setText("Translated:");

        endingCheckedCharsLabel.setDisplayedMnemonic('E');
        endingCheckedCharsLabel.setText("Ending checked chars:");

        endingCheckedCharsField.setFont(new java.awt.Font("DialogInput", 0, 12)); // NOI18N

        pairedCharsListLabel.setDisplayedMnemonic('h');
        pairedCharsListLabel.setText("Character pairs to check:");

        pairedCharsListField.setFont(new java.awt.Font("DialogInput", 0, 12)); // NOI18N
        pairedCharsListField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pairedCharsListFieldFocusLost(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel1.setText("Parameters for other automated tests");

        javax.swing.GroupLayout prefAutoTestsPanelLayout = new javax.swing.GroupLayout(prefAutoTestsPanel);
        prefAutoTestsPanel.setLayout(prefAutoTestsPanelLayout);
        prefAutoTestsPanelLayout.setHorizontalGroup(
            prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ignoredOrigEntLabel)
                            .addComponent(ignoredTrnsEntLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trnsDTDEntField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(origDTDEntField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(descripLabel))
                    .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                                            .addComponent(pairedCharsListLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(pairedCharsListField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(endingCheckedCharsField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(endingCheckedCharsLabel))))))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        prefAutoTestsPanelLayout.setVerticalGroup(
            prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefAutoTestsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descripLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(origDTDEntField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ignoredOrigEntLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trnsDTDEntField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ignoredTrnsEntLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endingCheckedCharsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endingCheckedCharsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefAutoTestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pairedCharsListField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pairedCharsListLabel))
                .addContainerGap(127, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/pref-qa-tests.png")), prefAutoTestsPanel, "Automated Tests"); // NOI18N

        locationAndStructureLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        locationAndStructureLabel.setText("Glossary.zip location and structure");

        pathGlossaryLabel.setLabelFor(pathGlossaryTextField);
        pathGlossaryLabel.setText("Path to Glossary.zip:");

        pathGlossaryButton.setMnemonic('C');
        pathGlossaryButton.setText("Choose...");
        pathGlossaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pathGlossaryButtonActionPerformed(evt);
            }
        });

        useOneFilePerProductCheck.setMnemonic('W');
        useOneFilePerProductCheck.setText("When saving glossary, use one file per product");
        useOneFilePerProductCheck.setToolTipText("Use this to save memory while saving Glossary.zip");

        javax.swing.GroupLayout prefDataStorePanelLayout = new javax.swing.GroupLayout(prefDataStorePanel);
        prefDataStorePanel.setLayout(prefDataStorePanelLayout);
        prefDataStorePanelLayout.setHorizontalGroup(
            prefDataStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefDataStorePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(prefDataStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationAndStructureLabel)
                    .addGroup(prefDataStorePanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(prefDataStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(prefDataStorePanelLayout.createSequentialGroup()
                                .addComponent(pathGlossaryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathGlossaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathGlossaryButton))
                            .addComponent(useOneFilePerProductCheck))))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        prefDataStorePanelLayout.setVerticalGroup(
            prefDataStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prefDataStorePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(locationAndStructureLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(prefDataStorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathGlossaryLabel)
                    .addComponent(pathGlossaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pathGlossaryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useOneFilePerProductCheck)
                .addContainerGap(194, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/pref-db.png")), prefDataStorePanel, "Data Store"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void editPhraseChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPhraseChooseButtonActionPerformed
        SimpleFontChooser sfc = new SimpleFontChooser(Main.mainWindow,
                editPhraseFontLabel.getFont());

        if (sfc.showFontDialog() == SimpleFontChooser.APPROVE_OPTION) {
            editPhraseFontLabel.setFont(sfc.getSelectedFont());
        }
    }//GEN-LAST:event_editPhraseChooseButtonActionPerformed

    private void tableViewChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableViewChooseButtonActionPerformed
        SimpleFontChooser sfc = new SimpleFontChooser(Main.mainWindow,
                tableViewFontLabel.getFont());

        if (sfc.showFontDialog() == SimpleFontChooser.APPROVE_OPTION) {
            tableViewFontLabel.setFont(sfc.getSelectedFont());
        }
    }//GEN-LAST:event_tableViewChooseButtonActionPerformed

    private void okButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonPressed
        okay = true;
        setVisible(false);
    }//GEN-LAST:event_okButtonPressed

    private void cancelButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonPressed
        okay = false;
        setVisible(false);
    }//GEN-LAST:event_cancelButtonPressed

    private void pairedCharsListFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pairedCharsListFieldFocusLost
        if (pairedCharsListField.getText().length() % 2 != 0) {
            JOptionPane.showMessageDialog(this, "Please, specify a string with even "
                    + "length (or 0 to disable)",
                    "String length error", JOptionPane.ERROR_MESSAGE);
            pairedCharsListField.requestFocusInWindow();
        }
    }//GEN-LAST:event_pairedCharsListFieldFocusLost

    private void useSuggCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useSuggCheckBoxItemStateChanged
        this.percentCoincidenceTextField.setEnabled((evt.getStateChange() == ItemEvent.SELECTED));
    }//GEN-LAST:event_useSuggCheckBoxItemStateChanged

    private void tsclLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tsclLabelMouseClicked
        JLabel tsclLabel = (JLabel) evt.getSource();
        Color newColor = JColorChooser.showDialog(this, "Choose Background Color marker for "
                + tsclLabel.getText() + "Untranslated strings", tsclLabel.getBackground());

        if (newColor != null) {
            tsclLabel.setBackground(newColor);
        }
    }//GEN-LAST:event_tsclLabelMouseClicked

    private void baseDirForReposButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baseDirForReposButtonActionPerformed
        File defaultFile;
        JFileChooser chooser;
        int result;

        defaultFile = new File(baseDirForReposField.getText());
        chooser = new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select the directory storing all the repositories used by SCM-based products");
        chooser.setSelectedFile(defaultFile);
        result = chooser.showDialog(this, "Choose");
        if (result == JFileChooser.APPROVE_OPTION) {
            defaultFile = chooser.getSelectedFile();
            this.baseDirForReposField.setText(defaultFile.toString());
        }
    }//GEN-LAST:event_baseDirForReposButtonActionPerformed

    private void pathGlossaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pathGlossaryButtonActionPerformed
        File defaultFile;
        JFileChooser chooser;
        int result;

        defaultFile = new File(pathGlossaryTextField.getText());
        chooser = new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Select the path to Glossary.zip");
        chooser.setSelectedFile(defaultFile);
        result = chooser.showDialog(this, "Choose");
        if (result == JFileChooser.APPROVE_OPTION) {
            defaultFile = chooser.getSelectedFile();
            pathGlossaryTextField.setText(defaultFile.toString());
        }
    }//GEN-LAST:event_pathGlossaryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox akeyCaseCheck;
    private javax.swing.JTextField akeyPatternField;
    private javax.swing.JLabel akeyPatternLabel;
    private javax.swing.JLabel appearanceLabel;
    private javax.swing.JCheckBox autoTranslateCheck;
    private javax.swing.JButton baseDirForReposButton;
    private javax.swing.JTextField baseDirForReposField;
    private javax.swing.JLabel baseDirForReposLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox ckeyCaseCheck;
    private javax.swing.JTextField ckeyPatternField;
    private javax.swing.JLabel ckeyPatternLabel;
    private javax.swing.JComboBox<L10n> defaultLanguageCombo;
    private net.localizethat.gui.models.ListComboBoxGenericModel<L10n> defaultLanguageComboModel;
    private javax.swing.JLabel defaultLanguageLabel;
    private javax.swing.JLabel descripLabel;
    private javax.swing.JButton editPhraseChooseButton;
    private javax.swing.JLabel editPhraseFontLabel;
    private javax.swing.JCheckBox emptyTrnsAsOriginalCheck;
    private javax.swing.JTextField endingCheckedCharsField;
    private javax.swing.JLabel endingCheckedCharsLabel;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JCheckBox exportOnlyModifFilesCheck;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JLabel ignoredOrigEntLabel;
    private javax.swing.JLabel ignoredTrnsEntLabel;
    private javax.swing.JLabel importExportLabel;
    private javax.swing.JLabel jLabel1;
    private net.localizethat.util.gui.JPathField jPathField1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel keyConnLabel;
    private javax.swing.JCheckBox labelCaseCheck;
    private javax.swing.JTextField labelPatternField;
    private javax.swing.JLabel labelPatternLabel;
    private javax.swing.JLabel locationAndStructureLabel;
    private javax.swing.JComboBox lookAndFeelCombo;
    private javax.swing.JLabel lookAndFeelLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField origDTDEntField;
    private javax.swing.JTextField pairedCharsListField;
    private javax.swing.JLabel pairedCharsListLabel;
    private javax.swing.JButton pathGlossaryButton;
    private javax.swing.JLabel pathGlossaryLabel;
    private javax.swing.JTextField pathGlossaryTextField;
    private javax.swing.JSpinner percentCoincidenceTextField;
    private javax.swing.JPanel prefAutoTestsPanel;
    private javax.swing.JPanel prefDataStorePanel;
    private javax.swing.JPanel prefDisplayPanel;
    private javax.swing.JPanel prefIOPanel;
    private javax.swing.JPanel prefKeyConnPanel;
    private javax.swing.JPanel prefTrnsAssistPanel;
    private javax.swing.JLabel provideSuggFor1Label;
    private javax.swing.JLabel provideSuggFor2Label;
    private javax.swing.JCheckBox replaceEnUSCheck;
    private javax.swing.JLabel suffixInfoLabel;
    private javax.swing.JButton tableViewChooseButton;
    private javax.swing.JLabel tableViewFontLabel;
    private javax.swing.JLabel translationSuggestionsLabel;
    private javax.swing.JTextField trnsDTDEntField;
    private javax.swing.JLabel tsColorsLabel;
    private javax.swing.JLabel tsclApproximated;
    private javax.swing.JLabel tsclCopied;
    private javax.swing.JLabel tsclModified;
    private javax.swing.JLabel tsclProposed;
    private javax.swing.JLabel tsclTranslated;
    private javax.swing.JLabel tsclUntranslated;
    private javax.swing.JCheckBox useOneFilePerProductCheck;
    private javax.swing.JCheckBox useSuggCheckBox;
    // End of variables declaration//GEN-END:variables
}
