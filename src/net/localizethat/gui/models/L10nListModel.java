/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;
import net.localizethat.model.L10n;

/**
 *
 * @author rpalomares
 */
public class L10nListModel extends AbstractListModel<L10n> implements MutableComboBoxModel<L10n> {
    List<L10n> l10nList;
    int selectComboItem = -1;

    public L10nListModel() {
        l10nList = new ArrayList<>(5);
    }

    public L10nListModel(List<L10n> listSource) {
        l10nList = listSource;
    }

    /**
     * Implements ListModel missing method
     * @return the list size
     */
    @Override
    public int getSize() {
        return l10nList.size();
    }

    /**
     * Implements ListModel missing method
     * @param index index of the element to display
     * @return the element
     */
    @Override
    public L10n getElementAt(int index) {
        return l10nList.get(index);
    }

    @Override
    public void addElement(L10n item) {
        l10nList.add(item);
        fireIntervalAdded(item, l10nList.size() -1, l10nList.size() -1);
    }

    @Override
    public void removeElement(Object obj) {
        L10n item = (L10n) obj;
        int index = l10nList.indexOf(item);
        if (index != -1) {
            l10nList.remove(index);
            fireIntervalRemoved(item, index, index);
        }
    }

    @Override
    public void insertElementAt(L10n item, int index) {
        if (index >= 0 && index < l10nList.size()) {
            l10nList.add(index, item);
            fireIntervalAdded(item, index, index);
        }
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= 0 && index < l10nList.size()) {
            l10nList.remove(index);
            fireIntervalRemoved(index, index, index);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        L10n item = (L10n) anItem;
        int index = l10nList.indexOf(item);
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
            return l10nList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public void clearAll() {
        int end = Math.max(0, l10nList.size() - 1);
        l10nList.clear();
        fireIntervalRemoved(this, 0, end);
    }

    public void addAll(List<L10n> l) {
        // start marks the first element that will be added
        int start = l10nList.size();
        l10nList.addAll(l);
        // end marks the last element just added by above addAll
        int end = Math.max(0, l10nList.size() - 1);
        fireIntervalAdded(this, start, end);
    }
}
