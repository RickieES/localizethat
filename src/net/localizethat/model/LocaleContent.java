/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * @author rpalomares
 */
public interface LocaleContent extends LocaleNode, Serializable {

    /**
     * This method returns true if the instance of LTContent can be edited/localized
     * (by default it is). Subclasses of LTContent should override this method returning
     * the appropiate value
     * @return true if the LTContent is intended to be editable/localizable
     */
    boolean isEditable();

    boolean isDontExport();

    void setDontExport(boolean dontExport);

    boolean isMarkedForDeletion();

    void setMarkedForDeletion(boolean markedForDeletion);

    int getOrderInFile();

    void setOrderInFile(int orderInFile);

    /**
     * Sets the parent of this node. This allows travelling up in the tree
     * @param parent a LocaleNode descendent in whose children list exists this node
     */
    void setParent(LocaleFile parent);

    /**
     * Gets the parent of this node.
     * @return a LocaleNode descendent in whose children list exists this node
     */
    @Override
    LocaleFile getParent();

    /**
     * Returns a list of twins of this object. The list is automatically maintained by using
     * setDefLocaleTwin()
     * @return a list of twins of this object
     */
    @Override
    Collection<? extends LocaleContent> getTwins();

}
