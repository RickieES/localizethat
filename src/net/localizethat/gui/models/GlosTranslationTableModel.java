/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import net.localizethat.model.GlsTranslation;
import net.localizethat.model.L10n;

/**
 *
 * @author rpalomares
 */
public class GlosTranslationTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_VALUE        = 0;
    public static final int COLUMN_HEADER_LOCALE       = 1;
    public static final int COLUMN_HEADER_CREATIONDATE = 2;
    public static final int COLUMN_HEADER_LASTUPDATE   = 3;
    final String[] columnNames = {"Value", "Locale", "Creation Date", "Last update"};
    List<GlsTranslation> glsTranslationList;
    int selectComboItem = -1;

    public GlosTranslationTableModel() {
        glsTranslationList = new ArrayList<>(5);
    }

    public GlosTranslationTableModel(List<GlsTranslation> listSource) {
        glsTranslationList = listSource;
    }

    @Override
    public int getRowCount() {
        return glsTranslationList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= glsTranslationList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_VALUE:
                return glsTranslationList.get(rowIndex).getValue();
            case COLUMN_HEADER_LOCALE:
                return glsTranslationList.get(rowIndex).getL10nId().toString();
            case COLUMN_HEADER_CREATIONDATE:
                return glsTranslationList.get(rowIndex).getCreationDate();
            case COLUMN_HEADER_LASTUPDATE:
                return glsTranslationList.get(rowIndex).getLastUpdate();
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLUMN_HEADER_VALUE:
            case COLUMN_HEADER_LOCALE:
                return L10n.class;
            case COLUMN_HEADER_CREATIONDATE:
            case COLUMN_HEADER_LASTUPDATE:
                return Date.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == COLUMN_HEADER_VALUE);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= glsTranslationList.size()) {
            return;
        }

        // Sounds weird using a switch with just one case, but I've gone with it
        // in case in the future more fields can be edited directly in the table
        switch (columnIndex) {
            case COLUMN_HEADER_VALUE:
                glsTranslationList.get(rowIndex).setValue(aValue.toString());
                break;
            default:
        }
        glsTranslationList.get(rowIndex).setLastUpdate(new Date());
        fireTableCellUpdated(rowIndex, columnIndex);
        fireTableCellUpdated(rowIndex, COLUMN_HEADER_LASTUPDATE);
        fireTableChanged(null);
        fireTableDataChanged();
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (glsTranslationList.size() > 0) {
            int end = glsTranslationList.size() - 1;
            glsTranslationList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<GlsTranslation> lg) {
        int start = glsTranslationList.size();
        if (glsTranslationList.addAll(lg)) {
            int end = glsTranslationList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(GlsTranslation gt) {
        glsTranslationList.add(gt);
        int newRow = glsTranslationList.size() -1;
        fireTableRowsInserted(newRow, newRow);
    }

    public GlsTranslation getElement(int index) {
        return glsTranslationList.get(index);
    }

    public int getIndexOf(GlsTranslation gt) {
        int index = glsTranslationList.indexOf(gt);
        return index;
    }

    public void removeElement(GlsTranslation gt) {
        int index = glsTranslationList.indexOf(gt);
        if (index != -1) {
            glsTranslationList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < glsTranslationList.size()) {
            glsTranslationList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
