/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

/**
 * Interface for LocaleContent descendents that have a gettable/settable Text Value.
 * For the sake of simplicity, ExternalEntity implements this interface, although it
 * doesn't directly allow to set (update) the text value in the form of System ID
 * @author rpalomares
 */
public interface EditableLocaleContent extends LocaleContent {

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
}
