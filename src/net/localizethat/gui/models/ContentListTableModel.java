/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;

/**
 * Table model of LocaleContent objects; the list of objects may come from a
 * ParseableFile objects or from LocaleContent lists (from product updates or
 * searches, for instance)
 * @author rpalomares
 */
public class ContentListTableModel extends AbstractTableModel {
    private L10n localizationCode;
    private final List<ContentListObject> list;
    private XTableColumnModel columnModel;
    private final String[] columnHeaders = {"Filename", "Content Type", "Key/Entity",
                                            "Original value", "Translated value"};

    public ContentListTableModel() {
        super();
        this.list = new ArrayList<>(10);
    }

    public ContentListTableModel(L10n localizationCode) {
        super();
        this.localizationCode = localizationCode;
        this.list = new ArrayList<>(10);
    }

    public ContentListTableModel(L10n localizationCode, List<LocaleContent> source) {
        super();
        this.localizationCode = localizationCode;
        this.list = new ArrayList<>(source.size());

        for(LocaleContent lc : source) {
            ContentListObject clo = new ContentListObject(lc);
            list.add(clo);
        }
    }

    public void replaceData(List<LocaleContent> newSource) {
        this.list.clear();
        for(LocaleContent lc : newSource) {
            ContentListObject clo = new ContentListObject(lc);
            list.add(clo);
        }
        this.fireTableDataChanged();
    }

    public L10n getLocalizationCode() {
        return localizationCode;
    }

    public void setLocalizationCode(L10n localizationCode) {
        this.localizationCode = localizationCode;
    }

    public XTableColumnModel getColumnModel() {
        return columnModel;
    }

    public void setColumnModel(XTableColumnModel columnModel) {
        this.columnModel = columnModel;
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnHeaders.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return "";
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnHeaders[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class ContentListObject {
        private final LocaleContent originalNode;
        private LocaleContent siblingNode;
        private final LocaleFile parentFile;

        ContentListObject(LocaleContent originalNode) {
            this.originalNode = originalNode;
            this.siblingNode = originalNode.getTwinByLocale(localizationCode); // This may be null
            this.parentFile = originalNode.getParent();
        }

        public LocaleContent getOriginalNode() {
            return originalNode;
        }

        public LocaleContent getSiblingNode() {
            return siblingNode;
        }

        public LocaleFile getParentFile() {
            return parentFile;
        }

        public void setSiblingNode(LocaleContent siblingNode) {
            this.siblingNode = siblingNode;
        }

    }
}
