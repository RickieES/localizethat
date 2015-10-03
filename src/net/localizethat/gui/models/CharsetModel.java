/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 *
 * @author rpalomares
 */
public class CharsetModel extends AbstractListModel<Charset> implements MutableComboBoxModel<Charset> {
    List<Charset> charsetList;
    int selectComboItem = -1;

    public CharsetModel() {
        charsetList = new ArrayList<>(5);
    }

    public CharsetModel(List<Charset> listSource) {
        charsetList = listSource;
    }

    public CharsetModel(Collection<Charset> colSource) {
        ArrayList<Charset> lc = new ArrayList<>(colSource.size());
        for(Charset c : colSource) {
            lc.add(c);
        }
        charsetList = lc;
    }

    /**
     * Implements ListModel missing method
     * @return the list size
     */
    @Override
    public int getSize() {
        return charsetList.size();
    }

    /**
     * Implements ListModel missing method
     * @param index index of the element to display
     * @return the element
     */
    @Override
    public Charset getElementAt(int index) {
        return charsetList.get(index);
    }

    @Override
    public void addElement(Charset item) {
        charsetList.add(item);
        fireIntervalAdded(item, charsetList.size() -1, charsetList.size() -1);
    }

    @Override
    public void removeElement(Object obj) {
        Charset item = (Charset) obj;
        int index = charsetList.indexOf(item);
        if (index != -1) {
            charsetList.remove(index);
            fireIntervalRemoved(item, index, index);
        }
    }

    @Override
    public void insertElementAt(Charset item, int index) {
        if (index >= 0 && index < charsetList.size()) {
            charsetList.add(index, item);
            fireIntervalAdded(item, index, index);
        }
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= 0 && index < charsetList.size()) {
            charsetList.remove(index);
            fireIntervalRemoved(index, index, index);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        Charset item = (Charset) anItem;
        int index = charsetList.indexOf(item);
        if (index != -1) {
            selectComboItem = index;
            fireContentsChanged(item, index, index);
        } else {
            selectComboItem = -1;
        }
    }

    @Override
    public Object getSelectedItem() {
        if (selectComboItem != -1) {
            return charsetList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public void clearAll() {
        int end = Math.max(0, charsetList.size() - 1);
        charsetList.clear();
        fireIntervalRemoved(this, 0, end);
    }

    public void addAll(List<Charset> l) {
        // start marks the first element that will be added
        int start = charsetList.size();
        charsetList.addAll(l);
        // end marks the last element just added by above addAll
        int end = Math.max(0, charsetList.size() - 1);
        fireIntervalAdded(this, start, end);
    }
}
