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
import net.localizethat.model.Glossary;
import net.localizethat.model.L10n;

/**
 *
 * @author rpalomares
 */
public class GlossaryTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_NAME         = 0;
    public static final int COLUMN_HEADER_VERSION      = 1;
    public static final int COLUMN_HEADER_CREATIONDATE = 2;
    public static final int COLUMN_HEADER_LASTUPDATE   = 3;
    public static final int COLUMN_HEADER_L10N_ID      = 4;
    final String[] columnNames = {"Name", "Version", "Creation Date", "Last update", "Master locale"};
    List<Glossary> glossaryList;
    int selectComboItem = -1;

    public GlossaryTableModel() {
        glossaryList = new ArrayList<>(5);
    }

    public GlossaryTableModel(List<Glossary> listSource) {
        glossaryList = listSource;
    }

    @Override
    public int getRowCount() {
        return glossaryList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= glossaryList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_NAME:
                return glossaryList.get(rowIndex).getName();
            case COLUMN_HEADER_VERSION:
                return glossaryList.get(rowIndex).getVersion();
            case COLUMN_HEADER_CREATIONDATE:
                return glossaryList.get(rowIndex).getCreationDate();
            case COLUMN_HEADER_LASTUPDATE:
                return glossaryList.get(rowIndex).getLastUpdate();
            case COLUMN_HEADER_L10N_ID:
                return glossaryList.get(rowIndex).getL10nId();
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
            case COLUMN_HEADER_NAME:
            case COLUMN_HEADER_VERSION:
                return String.class;
            case COLUMN_HEADER_CREATIONDATE:
            case COLUMN_HEADER_LASTUPDATE:
                return Date.class;
            case COLUMN_HEADER_L10N_ID:
                return L10n.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex < COLUMN_HEADER_CREATIONDATE);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= glossaryList.size()) {
            return;
        }

        switch (columnIndex) {
            case COLUMN_HEADER_NAME:
                glossaryList.get(rowIndex).setName(aValue.toString());
                break;
            case COLUMN_HEADER_VERSION:
                glossaryList.get(rowIndex).setVersion(aValue.toString());
                break;
            case COLUMN_HEADER_L10N_ID:
                glossaryList.get(rowIndex).setL10nId((L10n) aValue);
                break;
            default:
        }
        glossaryList.get(rowIndex).setLastUpdate(new Date());
        fireTableCellUpdated(rowIndex, columnIndex);
        fireTableCellUpdated(rowIndex, COLUMN_HEADER_LASTUPDATE);
        fireTableChanged(null);
        fireTableDataChanged();
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (glossaryList.size() > 0) {
            int end = glossaryList.size() - 1;
            glossaryList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<Glossary> lg) {
        int start = glossaryList.size();
        if (glossaryList.addAll(lg)) {
            int end = glossaryList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(Glossary g) {
        glossaryList.add(g);
        int newRow = glossaryList.size() -1;
        fireTableRowsInserted(newRow, newRow);
    }

    public Glossary getElement(int index) {
        return glossaryList.get(index);
    }

    public int getIndexOf(Glossary g) {
        int index = glossaryList.indexOf(g);
        return index;
    }

    public void removeElement(Glossary g) {
        int index = glossaryList.indexOf(g);
        if (index != -1) {
            glossaryList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < glossaryList.size()) {
            glossaryList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
