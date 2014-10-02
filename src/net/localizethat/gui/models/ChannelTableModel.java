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
import net.localizethat.model.Channel;

/**
 *
 * @author rpalomares
 */
public class ChannelTableModel extends AbstractTableModel {
    public static final int COLUMN_HEADER_NAME            = 0;
    public static final int COLUMN_HEADER_REPLACEMENTTAG  = 1;
    public static final int COLUMN_HEADER_REPLACEMENTTEXT = 2;
    public static final int COLUMN_HEADER_CREATIONDATE    = 3;
    public static final int COLUMN_HEADER_LASTUPDATE      = 4;
    final String[] columnNames = {"Name", "Tag", "Repl. text", "Creation Date", "Last update"};
    List<Channel> channelList;
    int selectComboItem = -1;

    public ChannelTableModel() {
        channelList = new ArrayList<>(5);
    }

    public ChannelTableModel(List<Channel> listSource) {
        channelList = listSource;
    }

    @Override
    public int getRowCount() {
        return channelList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0  || rowIndex >= channelList.size()) {
            return null;
        }
        switch (columnIndex) {
            case COLUMN_HEADER_NAME:
                return channelList.get(rowIndex).getName();
            case COLUMN_HEADER_REPLACEMENTTAG:
                return channelList.get(rowIndex).getReplacementTag();
            case COLUMN_HEADER_REPLACEMENTTEXT:
                return channelList.get(rowIndex).getReplacementText();
            case COLUMN_HEADER_CREATIONDATE:
                return channelList.get(rowIndex).getCreationDate();
            case COLUMN_HEADER_LASTUPDATE:
                return channelList.get(rowIndex).getLastUpdate();
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
            case COLUMN_HEADER_NAME:
            case COLUMN_HEADER_REPLACEMENTTAG:
            case COLUMN_HEADER_REPLACEMENTTEXT:
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
        if (rowIndex < 0  || rowIndex >= channelList.size()) {
            return;
        }

        switch (columnIndex) {
            case COLUMN_HEADER_NAME:
                channelList.get(rowIndex).setName(aValue.toString());
                break;
            case COLUMN_HEADER_REPLACEMENTTAG:
                channelList.get(rowIndex).setReplacementTag(aValue.toString());
                break;
            case COLUMN_HEADER_REPLACEMENTTEXT:
                channelList.get(rowIndex).setReplacementText(aValue.toString());
                break;
            default:
        }
        channelList.get(rowIndex).setLastUpdate(new Date());
        this.fireTableCellUpdated(rowIndex, columnIndex);
        this.fireTableCellUpdated(rowIndex, COLUMN_HEADER_LASTUPDATE);
        this.fireTableChanged(null);
        this.fireTableDataChanged();
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void clearAll() {
        // If and only if there was previous content in the model (and thus in the JTable)
        if (channelList.size() > 0) {
            int end = channelList.size() - 1;
            channelList.clear();
            fireTableRowsDeleted(0, end);
        }
    }

    public void addAll(List<Channel> l) {
        int start = channelList.size();
        if (channelList.addAll(l)) {
            int end = channelList.size() - 1;
            fireTableRowsInserted(start, end);
        }
    }

    public void addElement(Channel l) {
        channelList.add(l);
        fireTableRowsInserted(channelList.size() - 2, channelList.size() - 1);
    }

    public Channel getElement(int index) {
        return channelList.get(index);
    }

    public int getIndexOf(Channel l) {
        int index = channelList.indexOf(l);
        return index;
    }

    public void removeElement(Channel l) {
        int index = channelList.indexOf(l);
        if (index != -1) {
            channelList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }

    public void removeElementAt(int index) {
        if (index >= 0 && index < channelList.size()) {
            channelList.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
}
