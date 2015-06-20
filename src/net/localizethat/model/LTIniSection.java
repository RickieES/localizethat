/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * LocalizeThat class for marking sections in INI files. It is a kind (ie., subclass)
 * of LocaleContent, which in turn implements the LocaleNode interface.
 *
 * LTIniSection has a LocaleContent children list made on the fly (it is not persisted
 * in the DB as such). To build it, the very own INI section is searched in the parent
 * children list, and then every node is considered a children up to the end of the list
 * or the next INISection noe (not included).
 * 
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("LTIniSection")
@XmlRootElement
public class LTIniSection extends LocaleContent {
    transient protected List<LocaleContent> children;
    
    public LTIniSection() {
        super();
        children = new ArrayList<>(5);
        // buildChildrenOnTheFly();
    }
    
    /**
     * Instead of actually maintaining the collection, we re-create it by reading 
     * @return 
     */
    private void buildChildrenOnTheFly() {
        children.clear();
        for(LocaleNode ln : getParent().getChildren()) {
            children.add((LocaleContent) ln);
        }
        Collections.sort(children, LocaleContent.orderInFileComparator);

        // Now we have a copy of the siblings sorted by their position in file
        // We are going to remove the siblings before this ini section (including it)
        Iterator<LocaleContent> it = children.iterator();
        while (it.hasNext()) {
            LocaleContent lc = it.next();
            if (lc.getOrderInFile() <= this.getOrderInFile()) {
                it.remove();
            } else {
                break;
            }
        }

        // And now we are going to remove the siblings starting with the next ini section
        // up to end of the list
        it = children.iterator();
        boolean endOfSectionReached = false;
        while (it.hasNext()) {
            LocaleContent lc = it.next();
            if (!endOfSectionReached && (lc instanceof LTIniSection)) {
                endOfSectionReached = true;
            }

            if (endOfSectionReached) {
                it.remove();
            }
        }
    }

    @Override
    public boolean addChild(LocaleNode node) {
        return false;
    }

    @Override
    public LocaleNode getChildByName(String name) {
        return null;
    }

    @Override
    public LocaleNode getChildByName(String name, boolean matchCase) {
        return null;
    }

    @Override
    public Collection<? extends LocaleNode> getChildren() {
        buildChildrenOnTheFly();
        return children;
    }

    @Override
    public LocaleNode removeChild(String name) {
        return null;
    }

    @Override
    public LocaleNode removeChild(String name, boolean matchCase) {
        return null;
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        return false;
    }

    @Override
    public boolean clearChildren() {
        children.clear();
        return true;
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
