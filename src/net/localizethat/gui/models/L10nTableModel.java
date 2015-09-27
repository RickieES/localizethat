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
import net.localizethat.model.L10n;

/**
 *
 * @author rpalomares
 */
public class L10nTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_CODE         = 0;
    public static final int COLUMN_HEADER_NAME         = 1;
    public static final int COLUMN_HEADER_TEAMNAME     = 2;
    public static final int COLUMN_HEADER_URL          = 3;
    public static final int COLUMN_HEADER_CREATIONDATE = 4;
    public static final int COLUMN_HEADER_LASTUPDATE   = 5;
    final String[] columnNames = {"Code", "Description", "Team name", "URL", "Creation Date", "Last update"};
    List<L10n> l10nList;
    int selectComboItem = -1;

    public L10nTableModel() {
        l10nList = new ArrayList<>(5);
    }

    public L10nTableModel(List<L10n> listSource) {
        l10nList = listSource;
    }

    @Override
    public int getRowCount() {
        return l10nList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= l10nList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_CODE:
                return l10nList.get(rowIndex).getCode();
            case COLUMN_HEADER_NAME:
                return l10nList.get(rowIndex).getName();
            case COLUMN_HEADER_TEAMNAME:
                return l10nList.get(rowIndex).getTeamName();
            case COLUMN_HEADER_URL:
                return l10nList.get(rowIndex).getUrl();
            case COLUMN_HEADER_CREATIONDATE:
                return l10nList.get(rowIndex).getCreationDate();
            case COLUMN_HEADER_LASTUPDATE:
                return l10nList.get(rowIndex).getLastUpdate();
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.columnNames[columnIndex];
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLUMN_HEADER_CODE:
            case COLUMN_HEADER_NAME:
            case COLUMN_HEADER_TEAMNAME:
            case COLUMN_HEADER_URL:
                return String.class;
            case COLUMN_HEADER_CREATIONDATE:
            case COLUMN_HEADER_LASTUPDATE:
                return Date.class;
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
        if (rowIndex < 0  || rowIndex >= l10nList.size()) {
            return;
        }

        switch (columnIndex) {
            case COLUMN_HEADER_CODE:
                l10nList.get(rowIndex).setCode(aValue.toString());
                break;
            case COLUMN_HEADER_NAME:
                l10nList.get(rowIndex).setName(aValue.toString());
                break;
            case COLUMN_HEADER_TEAMNAME:
                l10nList.get(rowIndex).setTeamName(aValue.toString());
                break;
            case COLUMN_HEADER_URL:
                l10nList.get(rowIndex).setUrl(aValue.toString());
                break;
            default:
        }
        l10nList.get(rowIndex).setLastUpdate(new Date());
        this.fireTableCellUpdated(rowIndex, columnIndex);
        this.fireTableCellUpdated(rowIndex, COLUMN_HEADER_LASTUPDATE);
        this.fireTableChanged(null);
        this.fireTableDataChanged();
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (l10nList.size() > 0) {
            int end = l10nList.size() - 1;
            l10nList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<L10n> l) {
        int start = l10nList.size();
        if (l10nList.addAll(l)) {
            int end = l10nList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(L10n l) {
        l10nList.add(l);
        fireTableRowsInserted(l10nList.size() - 2, l10nList.size() - 1);
    }

    public L10n getElement(int index) {
        return l10nList.get(index);
    }

    public int getIndexOf(L10n l) {
        int index = l10nList.indexOf(l);
        return index;
    }

    public void removeElement(L10n l) {
        int index = l10nList.indexOf(l);
        if (index != -1) {
            l10nList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < l10nList.size()) {
            l10nList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
