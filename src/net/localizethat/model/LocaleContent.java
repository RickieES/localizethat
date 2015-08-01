/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for LocaleNode instances corresponding to contents of LocaleFiles
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

    /**
     * Returns the twin of this node for the supplied Locale
     * @param locale the L10n for which we are looking a twin
     * @return the LocaleContent twin for L10n locale, or null if there is no twin for that L10n
     */
    @Override
    LocaleContent getTwinByLocale(L10n locale);

    /**
     * Returns the default twin for this LocaleContent. It overrides the
     * more general LocaleNode version
     * @return a LocaleContent for the default L10n
     */
    @Override
    LocaleContent getDefLocaleTwin();

    /**
     * Returns the text value (as opposed to the <i>name</i> of the item)
     * @return an empty string if there is no saved text value, or the actual text value
     */
    String getTextValue();

    /**
     * Sets the text value.
     * ExternalEntity does nothing on this method, since the value is not localizable
     *
     * @param value the text value to be set
     */
    void setTextValue(String value);
    
    /**
     * Returns whether a LocaleContent is set to keep the original value. This
     * value is only meaningful on non-default twins.
     * @return true if the localization should always use the text value of the
     * default locale twin; false if the localization must use its own text value
     */
    boolean isKeepOriginal();
    
    /**
     * Sets whether a LocaleContent is set to use the original value of the
     * default twin text value as its own text value. This is only useful if the
     * LocaleContent is a non-default locale twin.
     * @param keepOriginal true to use the text value of the default twin
     * as own
     */
    void setKeepOriginal(boolean keepOriginal);
}
