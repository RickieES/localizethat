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
import net.localizethat.model.LocalePath;

/**
 *
 * @author rpalomares
 */
public class PathTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_PATH    = 0;
    public static final int COLUMN_HEADER_LOCALE  = 1;
    final String[] columnNames = {"Path", "Locale"};
    List<LocalePath> itemList;
    int selectComboItem = -1;

    public PathTableModel() {
        itemList = new ArrayList<>(5);
    }

    public PathTableModel(List<LocalePath> listSource) {
        itemList = listSource;
    }

    @Override
    public int getRowCount() {
        return itemList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= itemList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_PATH:
                return itemList.get(rowIndex).getPath();
            case COLUMN_HEADER_LOCALE:
                return itemList.get(rowIndex).getL10nId().toString();
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
            case COLUMN_HEADER_PATH:
                return String.class;
            case COLUMN_HEADER_LOCALE:
                return L10n.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= itemList.size()) {
            return;
        }

        switch (columnIndex) {
            case COLUMN_HEADER_PATH:
                itemList.get(rowIndex).setPath(aValue.toString());
                break;
            case COLUMN_HEADER_LOCALE:
                itemList.get(rowIndex).setL10nId((L10n) aValue);
                break;
            default:
        }
        itemList.get(rowIndex).setLastUpdate(new Date());
        this.fireTableCellUpdated(rowIndex, columnIndex);
        this.fireTableChanged(null);
        this.fireTableDataChanged();
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (itemList.size() > 0) {
            int end = itemList.size() - 1;
            itemList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<LocalePath> l) {
        int start = itemList.size();
        if (itemList.addAll(l)) {
            int end = itemList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(LocalePath l) {
        itemList.add(l);
        fireTableRowsInserted(itemList.size() - 2, itemList.size() - 1);
    }

    public LocalePath getElement(int index) {
        return itemList.get(index);
    }

    public int getIndexOf(LocalePath l) {
        int index = itemList.indexOf(l);
        return index;
    }

    public void removeElement(LocalePath l) {
        int index = itemList.indexOf(l);
        if (index != -1) {
            itemList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < itemList.size()) {
            itemList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
