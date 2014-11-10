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

/**
 * Generic List/ComboBoxModel that implements ListModel, MutableComboBoxModel and adds some
 * additional useful methods
 * @author rpalomares
 * @param <T> The class of objects displayed in the JList or JComboBox
 */
public class ListComboBoxGenericModel<T> extends AbstractListModel<T> implements MutableComboBoxModel<T> {
    List<T> itemList;
    int selectComboItem = -1;

    public ListComboBoxGenericModel() {
        itemList = new ArrayList<>(5);
    }

    public ListComboBoxGenericModel(List<T> listSource) {
        itemList = listSource;
    }

    /**
     * Implements ListModel missing method
     * @return the list size
     */
    @Override
    public int getSize() {
        return itemList.size();
    }

    /**
     * Implements ListModel missing method
     * @param index index of the element to display
     * @return the element
     */
    @Override
    public T getElementAt(int index) {
        return itemList.get(index);
    }

    @Override
    public void addElement(T item) {
        itemList.add(item);
        fireIntervalAdded(item, itemList.size() -1, itemList.size() -1);
    }

    @Override
    public void removeElement(Object obj) {
        T item = (T) obj;
        int index = itemList.indexOf(item);
        if (index != -1) {
            itemList.remove(index);
            fireIntervalRemoved(item, index, index);
        }
    }

    @Override
    public void insertElementAt(T item, int index) {
        if (index >= 0 && index < itemList.size()) {
            itemList.add(index, item);
            fireIntervalAdded(item, index, index);
        }
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= 0 && index < itemList.size()) {
            itemList.remove(index);
            fireIntervalRemoved(index, index, index);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        T item = (T) anItem;
        int index = itemList.indexOf(item);
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
            return itemList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public T getSelectedTypedItem() {
        if (selectComboItem != -1) {
            return itemList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public int getSelectedIndex() {
        return selectComboItem;
    }

    public void clearAll() {
        int end = Math.max(0, itemList.size() - 1);
        itemList.clear();
        fireIntervalRemoved(this, 0, end);
    }

    public void addAll(List<T> l) {
        // start marks the first element that will be added
        int start = itemList.size();
        itemList.addAll(l);
        // end marks the last element just added by above addAll
        int end = Math.max(0, itemList.size() - 1);
        fireIntervalAdded(this, start, end);
    }
}
