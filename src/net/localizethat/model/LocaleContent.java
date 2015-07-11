/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;

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
}
