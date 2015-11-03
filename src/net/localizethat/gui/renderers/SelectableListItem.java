/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.renderers;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import net.localizethat.gui.models.SelectableItem;

/**
 * Renderer for JList items that can be selected/unselected using a JCheckBox
 * @author rpalomares
 */
public class SelectableListItem extends JCheckBox
        implements ListCellRenderer {

    /**
     * Default constructor for SelectableListItem
     */
    public SelectableListItem() {
      setForeground(UIManager.getColor("List.foreground"));
      setBackground(UIManager.getColor("List.background"));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        SelectableItem item = (SelectableItem) value;
        setEnabled(list.isEnabled());
        setSelected(item.isSelected());
        setFont(list.getFont());
        setText(printableText(item.getItem()));
        if (item.isSelected()) {
            this.setBackground(UIManager.getColor("List.selectionBackground"));
        } else {
            this.setBackground(UIManager.getColor("List.background"));
        }
      return this;
    }

    /**
     * Returns the String representation of the object. This method is public
     * and not final so it can be overriden to display more elaborated texts
     * if needed
     * @param item the item to be dislayed
     * @return a String reprsentation of the item passed as a parameter
     */
    public String printableText(Object item) {
        return item.toString();
    }

}
