/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.components;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.localizethat.Main;
import net.localizethat.gui.models.ContentListTableModel;
import net.localizethat.gui.models.SelectableItem;
import net.localizethat.gui.renderers.SelectableListItem;
import net.localizethat.system.AppSettings;

/**
 * Panel containing a JTable to display a list of LocaleContent entries
 * @author rpalomares
 */
public class ContentListTable extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    private final TableRowSorter<ContentListTableModel> tableRowSorter;
    private Font f;

    /**
     * Creates new form ContentListTable
     */
    public ContentListTable() {
        super();
        
        initComponents();
        columnChooserDialog.pack();
        columnChooserDialog.setLocationRelativeTo(null);
        contentTable.createDefaultColumnsFromModel();
        tableModel.addTableModelListener(contentTable);
        tableModel.addTableModelListener(new ContentTableModelListener());
        tableRowSorter = new TableRowSorter<>(tableModel);
        contentTable.setRowSorter(tableRowSorter);
        filterField.getDocument().addDocumentListener(
                new FilterDocumentListener(tableModel, tableRowSorter, filterField));

        for(TableColumn tc : tableColumnModel.getColumnsAsList(false)) {
            columnChooserModel.addElement(new SelectableItem<>(tc,
                    tableColumnModel.isColumnVisible(tc)));
        }
    }

    public JTable getTable() {
        return this.contentTable;
    }

    public ContentListTableModel getTableModel() {
        return tableModel;
    }
    
    public void activatePanel() {
        f = new Font(Main.appSettings.getString(AppSettings.PREF_FONT_TABLEVIEW_NAME),
                     Main.appSettings.getInteger(AppSettings.PREF_FONT_TABLEVIEW_STYLE),
                     Main.appSettings.getInteger(AppSettings.PREF_FONT_TABLEVIEW_SIZE));
        contentTable.setFont(f);        
    }

    public void addTableListSelectionListener(ListSelectionListener lsl) {
        this.contentTable.getSelectionModel().addListSelectionListener(lsl);
    }

    private void applyFilter(TableModel tm, TableRowSorter trs, String filter) {
        RowFilter<TableModel, Object> rf;
        //If current expression doesn't parse, don't update.
        try {
            // (?i) adds case insensitive flag to the RegEx
            rf = RowFilter.regexFilter("(?i)" + filter, 3, 4, 5);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        trs.setRowFilter(rf);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableModel = new net.localizethat.gui.models.ContentListTableModel();
        tableColumnModel = new net.localizethat.gui.models.XTableColumnModel();
        columnChooserDialog = new javax.swing.JDialog();
        selectColumnsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        columnsList = new javax.swing.JList();
        columnsOkButton = new javax.swing.JButton();
        columnsCancelButton = new javax.swing.JButton();
        columnChooserModel = new net.localizethat.gui.models.ListComboBoxGenericModel<>();
        auxPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterField = new javax.swing.JTextField();
        rowsInfoLabel = new javax.swing.JLabel();
        rowsInfoText = new javax.swing.JLabel();
        columnsButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        contentTable = new javax.swing.JTable();

        columnChooserDialog.setTitle("Select columns");
        columnChooserDialog.setLocationByPlatform(true);
        columnChooserDialog.setMinimumSize(new java.awt.Dimension(150, 100));
        columnChooserDialog.setModal(true);

        selectColumnsLabel.setText("Select columns to display");

        columnsList.setModel(columnChooserModel);
        columnsList.setCellRenderer(new SelectableListItem() {
            @Override
            public String printableText(Object item) {
                return ((TableColumn) item).getHeaderValue().toString();
            }
        });
        columnsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                columnsListMouseClicked(evt);
            }
        });
        columnsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                columnsListKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(columnsList);

        columnsOkButton.setText("OK");
        columnsOkButton.setMaximumSize(new java.awt.Dimension(81, 25));
        columnsOkButton.setMinimumSize(new java.awt.Dimension(81, 25));
        columnsOkButton.setPreferredSize(new java.awt.Dimension(81, 25));
        columnsOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnsOkButtonActionPerformed(evt);
            }
        });

        columnsCancelButton.setText("Cancel");
        columnsCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnsCancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout columnChooserDialogLayout = new javax.swing.GroupLayout(columnChooserDialog.getContentPane());
        columnChooserDialog.getContentPane().setLayout(columnChooserDialogLayout);
        columnChooserDialogLayout.setHorizontalGroup(
            columnChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnChooserDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(columnChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(columnChooserDialogLayout.createSequentialGroup()
                        .addComponent(selectColumnsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(columnChooserDialogLayout.createSequentialGroup()
                        .addComponent(columnsOkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(columnsCancelButton)))
                .addContainerGap())
        );
        columnChooserDialogLayout.setVerticalGroup(
            columnChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(columnChooserDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectColumnsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(columnChooserDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(columnsOkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(columnsCancelButton))
                .addContainerGap())
        );

        filterLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        filterLabel.setText("Filter:");

        filterField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        filterField.setText("(filter on key/entity or text)");
        filterField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filterFieldFocusGained(evt);
            }
        });

        rowsInfoLabel.setText("Rows:");
        rowsInfoLabel.setToolTipText("Rows (total / displayed)");

        rowsInfoText.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        rowsInfoText.setText("0");

        columnsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/16-choose-columns.png"))); // NOI18N
        columnsButton.setToolTipText("Choose columns");
        columnsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout auxPanelLayout = new javax.swing.GroupLayout(auxPanel);
        auxPanel.setLayout(auxPanelLayout);
        auxPanelLayout.setHorizontalGroup(
            auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowsInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rowsInfoText, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                .addComponent(columnsButton)
                .addContainerGap())
        );
        auxPanelLayout.setVerticalGroup(
            auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(filterLabel)
                .addComponent(rowsInfoLabel)
                .addComponent(rowsInfoText))
            .addComponent(columnsButton)
        );

        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 48));

        contentTable.setModel(tableModel);
        contentTable.setMinimumSize(new java.awt.Dimension(90, 100));
        jScrollPane1.setViewportView(contentTable);
        contentTable.setColumnModel(tableColumnModel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(auxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(auxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void filterFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterFieldFocusGained
        filterField.selectAll();
    }//GEN-LAST:event_filterFieldFocusGained

    private void columnsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnsButtonActionPerformed
        columnChooserDialog.setVisible(true);
    }//GEN-LAST:event_columnsButtonActionPerformed

    private void columnsCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnsCancelButtonActionPerformed
        columnChooserDialog.setVisible(false);
        // Restore the previous value of visible/hidden columns
        for(SelectableItem<TableColumn> stc : columnChooserModel.getAll()) {
            TableColumn tc = stc.getItem();
            stc.setSelected(tableColumnModel.isColumnVisible(tc));
        }
    }//GEN-LAST:event_columnsCancelButtonActionPerformed

    private void columnsOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnsOkButtonActionPerformed
        columnChooserDialog.setVisible(false);
        for(SelectableItem<TableColumn> stc : columnChooserModel.getAll()) {
            TableColumn tc = stc.getItem();
            tableColumnModel.setColumnVisible(tc, stc.isSelected());
        }
    }//GEN-LAST:event_columnsOkButtonActionPerformed

    private void columnsListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_columnsListKeyTyped
        JList c = (JList) evt.getComponent();
        char keyCode = evt.getKeyChar();
        int index = c.getLeadSelectionIndex();

        if ((keyCode == KeyEvent.VK_SPACE) || (keyCode == KeyEvent.VK_ENTER)) {
            SelectableItem<TableColumn> item = columnChooserModel.getElementAt(index);
            item.setSelected(!item.isSelected());
            Rectangle rect = columnsList.getCellBounds(index, index);
            columnsList.repaint(rect);
        }

    }//GEN-LAST:event_columnsListKeyTyped

    private void columnsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_columnsListMouseClicked
        int index = columnsList.locationToIndex(evt.getPoint());
        SelectableItem<TableColumn> item = columnChooserModel
                .getElementAt(index);
        item.setSelected(!item.isSelected());
        Rectangle rect = columnsList.getCellBounds(index, index);
        columnsList.repaint(rect);
    }//GEN-LAST:event_columnsListMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel auxPanel;
    private javax.swing.JDialog columnChooserDialog;
    private net.localizethat.gui.models.ListComboBoxGenericModel<SelectableItem<TableColumn>> columnChooserModel;
    private javax.swing.JButton columnsButton;
    private javax.swing.JButton columnsCancelButton;
    private javax.swing.JList columnsList;
    private javax.swing.JButton columnsOkButton;
    private javax.swing.JTable contentTable;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel rowsInfoLabel;
    private javax.swing.JLabel rowsInfoText;
    private javax.swing.JLabel selectColumnsLabel;
    private net.localizethat.gui.models.XTableColumnModel tableColumnModel;
    private net.localizethat.gui.models.ContentListTableModel tableModel;
    // End of variables declaration//GEN-END:variables

    private class ContentTableModelListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            String totalRows = Integer.toString(tableModel.getRowCount());
            rowsInfoText.setText(totalRows);
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
