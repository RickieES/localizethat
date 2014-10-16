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
import net.localizethat.model.Channel;

/**
 *
 * @author rpalomares
 */
public class ChannelListModel extends AbstractListModel<Channel> implements MutableComboBoxModel<Channel> {
    List<Channel> channelList;
    int selectComboItem = -1;

    public ChannelListModel() {
        channelList = new ArrayList<>(5);
    }

    public ChannelListModel(List<Channel> listSource) {
        channelList = listSource;
    }

    /**
     * Implements ListModel missing method
     * @return the list size
     */
    @Override
    public int getSize() {
        return channelList.size();
    }

    /**
     * Implements ListModel missing method
     * @param index index of the element to display
     * @return the element
     */
    @Override
    public Channel getElementAt(int index) {
        return channelList.get(index);
    }

    @Override
    public void addElement(Channel item) {
        channelList.add(item);
        fireIntervalAdded(item, channelList.size() -1, channelList.size() -1);
    }

    @Override
    public void removeElement(Object obj) {
        Channel item = (Channel) obj;
        int index = channelList.indexOf(item);
        if (index != -1) {
            channelList.remove(index);
            fireIntervalRemoved(item, index, index);
        }
    }

    @Override
    public void insertElementAt(Channel item, int index) {
        if (index >= 0 && index < channelList.size()) {
            channelList.add(index, item);
            fireIntervalAdded(item, index, index);
        }
    }

    @Override
    public void removeElementAt(int index) {
        if (index >= 0 && index < channelList.size()) {
            channelList.remove(index);
            fireIntervalRemoved(index, index, index);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        Channel item = (Channel) anItem;
        int index = channelList.indexOf(item);
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
            return channelList.get(selectComboItem);
        } else {
            return null;
        }
    }

    public void clearAll() {
        int end = Math.max(0, channelList.size() - 1);
        channelList.clear();
        fireIntervalRemoved(this, 0, end);
    }

    public void addAll(List<Channel> l) {
        // start marks the first element that will be added
        int start = channelList.size();
        channelList.addAll(l);
        // end marks the last element just added by above addAll
        int end = Math.max(0, channelList.size() - 1);
        fireIntervalAdded(this, start, end);
    }
}
