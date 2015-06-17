/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.components;

import net.localizethat.gui.models.ContentListTableModel;
import net.localizethat.gui.models.XTableColumnModel;
import net.localizethat.model.L10n;

/**
 * Panel containing a JTable to display a list of LocaleContent entries
 * @author rpalomares
 */
public class ContentListTable extends javax.swing.JPanel {
    XTableColumnModel xColumnModel;

    /**
     * Creates new form ContentListTable
     * @param locale
     */
    public ContentListTable() {
        super();
        xColumnModel = new XTableColumnModel();

        initComponents();
        contentTable.setColumnModel(xColumnModel);
        contentTable.createDefaultColumnsFromModel();
    }

    public ContentListTableModel getTableModel() {
        return tableModel;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableModel = new net.localizethat.gui.models.ContentListTableModel();
        auxPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterField = new javax.swing.JTextField();
        rowsInfoLabel = new javax.swing.JLabel();
        rowsInfoText = new javax.swing.JLabel();
        columnsButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        contentTable = new javax.swing.JTable();

        filterLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        filterLabel.setText("Filter:");

        filterField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        filterField.setText("(filter on key/entity or text)");

        rowsInfoLabel.setText("Rows (T/D)");
        rowsInfoLabel.setToolTipText("Rows (total / displayed)");

        rowsInfoText.setText("0 / 0");

        columnsButton.setText("Columns");

        javax.swing.GroupLayout auxPanelLayout = new javax.swing.GroupLayout(auxPanel);
        auxPanel.setLayout(auxPanelLayout);
        auxPanelLayout.setHorizontalGroup(
            auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(auxPanelLayout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
                        .addComponent(columnsButton))
                    .addGroup(auxPanelLayout.createSequentialGroup()
                        .addComponent(rowsInfoLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rowsInfoText)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        auxPanelLayout.setVerticalGroup(
            auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel)
                    .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(columnsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(auxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rowsInfoLabel)
                    .addComponent(rowsInfoText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        contentTable.setModel(tableModel);
        jScrollPane1.setViewportView(contentTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(auxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(auxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel auxPanel;
    private javax.swing.JButton columnsButton;
    private javax.swing.JTable contentTable;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel rowsInfoLabel;
    private javax.swing.JLabel rowsInfoText;
    private net.localizethat.gui.models.ContentListTableModel tableModel;
    // End of variables declaration//GEN-END:variables
}