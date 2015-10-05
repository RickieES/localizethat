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
     * Returns the Translation Status for this editable locale content
     * @return the translation status of this EditableLocaleContent.
     */
    TranslationStatus getTrnsStatus();

    /**
     * Sets the Translation Status for this editable locale content
     * @param trnsStatus the translation status value to assign to this
     * EditableLocaleContent
     */
    void setTrnsStatus(TranslationStatus trnsStatus);
}
