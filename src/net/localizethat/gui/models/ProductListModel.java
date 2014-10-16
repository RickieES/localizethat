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
import net.localizethat.model.Product;

/**
 *
 * @author rpalomares
 */
public class ProductListModel extends AbstractListModel<Product> implements MutableComboBoxModel<Product> {
    List<Product> productList;
    int selectComboItem = -1;

    public ProductListModel() {
        productList = new ArrayList<>(5);
    }

    public ProductListModel(List<Product> listSource) {
        productList = listSource;
    }

    /**
     * Implements ListModel missing method
     * @return the list size
     */
    @Override
    public int getSize() {
        return productList.size();
    }

    /**
     * Implements ListModel missing method
     * @param index index of the element to display
     * @return the element
     */
    @Override
    public Product getElementAt(int index) {
        return productList.get(index);
    }

    @Override
    public void addElement(Product item) {
        productList.add(item);
        fireIntervalAdded(item, productList.size() -1, productList.size() -1);
    }

    @Override
    public void removeElement(Object obj) {
        Product item = (Product) obj;
        int index = productList.indexOf(item);
        if (index != -1) {
            productList.remove(index);
            fireIntervalRemoved(item, index, index);
        }
    }

    @Override
    public void insertElementAt(Product item, int index) {
        if (index >= 0 && index < productList.size()) {
            productList.add(index, item);
            fireIntervalAdded(item, index, index);
        }
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= 0 && index < productList.size()) {
            productList.remove(index);
            fireIntervalRemoved(index, index, index);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        Product item = (Product) anItem;
        int index = productList.indexOf(item);
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
            return productList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public void clearAll() {
        int end = Math.max(0, productList.size() - 1);
        productList.clear();
        fireIntervalRemoved(this, 0, end);
    }

    public void addAll(List<Product> l) {
        // start marks the first element that will be added
        int start = productList.size();
        productList.addAll(l);
        // end marks the last element just added by above addAll
        int end = Math.max(0, productList.size() - 1);
        fireIntervalAdded(this, start, end);
    }
}
