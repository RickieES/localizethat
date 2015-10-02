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
import net.localizethat.model.GlsEntry;
import net.localizethat.model.PartOfSpeech;

/**
 *
 * @author rpalomares
 */
public class GlosEntryTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_TERM         = 0;
    public static final int COLUMN_HEADER_PARTOFSPEECH = 1;
    public static final int COLUMN_HEADER_CREATIONDATE = 2;
    public static final int COLUMN_HEADER_LASTUPDATE   = 3;
    public static final int COLUMN_HEADER_GLOS_ID      = 4;
    final String[] columnNames = {"Term", "Part Of Speech", "Creation Date", "Last update", "Glossary"};
    List<GlsEntry> glsEntryList;
    int selectComboItem = -1;

    public GlosEntryTableModel() {
        glsEntryList = new ArrayList<>(5);
    }

    public GlosEntryTableModel(List<GlsEntry> listSource) {
        glsEntryList = listSource;
    }

    @Override
    public int getRowCount() {
        return glsEntryList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= glsEntryList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_TERM:
                return glsEntryList.get(rowIndex).getTerm();
            case COLUMN_HEADER_PARTOFSPEECH:
                return glsEntryList.get(rowIndex).getPartOfSpeech();
            case COLUMN_HEADER_CREATIONDATE:
                return glsEntryList.get(rowIndex).getCreationDate();
            case COLUMN_HEADER_LASTUPDATE:
                return glsEntryList.get(rowIndex).getLastUpdate();
            case COLUMN_HEADER_GLOS_ID:
                return glsEntryList.get(rowIndex).getGlosId();
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
            case COLUMN_HEADER_TERM:
                return String.class;
            case COLUMN_HEADER_PARTOFSPEECH:
                return PartOfSpeech.class;
            case COLUMN_HEADER_CREATIONDATE:
            case COLUMN_HEADER_LASTUPDATE:
                return Date.class;
            case COLUMN_HEADER_GLOS_ID:
                return Glossary.class;
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
        if (rowIndex < 0  || rowIndex >= glsEntryList.size()) {
            return;
        }

        switch (columnIndex) {
            case COLUMN_HEADER_TERM:
                glsEntryList.get(rowIndex).setTerm(aValue.toString());
                break;
            case COLUMN_HEADER_PARTOFSPEECH:
                glsEntryList.get(rowIndex).setPartOfSpeech(PartOfSpeech.valueOf(aValue.toString().toUpperCase()));
                break;
            default:
        }
        glsEntryList.get(rowIndex).setLastUpdate(new Date());
        fireTableCellUpdated(rowIndex, columnIndex);
        fireTableCellUpdated(rowIndex, COLUMN_HEADER_LASTUPDATE);
        fireTableChanged(null);
        fireTableDataChanged();
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (glsEntryList.size() > 0) {
            int end = glsEntryList.size() - 1;
            glsEntryList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<GlsEntry> lg) {
        int start = glsEntryList.size();
        if (glsEntryList.addAll(lg)) {
            int end = glsEntryList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(GlsEntry ge) {
        glsEntryList.add(ge);
        int newRow = glsEntryList.size() -1;
        fireTableRowsInserted(newRow, newRow);
    }

    public GlsEntry getElement(int index) {
        return glsEntryList.get(index);
    }

    public int getIndexOf(GlsEntry ge) {
        int index = glsEntryList.indexOf(ge);
        return index;
    }

    public void removeElement(GlsEntry ge) {
        int index = glsEntryList.indexOf(ge);
        if (index != -1) {
            glsEntryList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < glsEntryList.size()) {
            glsEntryList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
