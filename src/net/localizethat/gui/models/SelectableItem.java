/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.models;

/**
 *
 * @author rpalomares
 * @param <T> a type that can be selected/unselected in a JList or JComboBox
 */
public class SelectableItem<T> {
    final T item;
    boolean selected;

    public SelectableItem(T item, boolean selected) {
        this.item = item;
        this.selected = selected;
    }

    public T getItem() {
        return item;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return item.toString();
    }
}
